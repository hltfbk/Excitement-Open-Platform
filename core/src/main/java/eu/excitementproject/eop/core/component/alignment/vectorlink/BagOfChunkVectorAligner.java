package eu.excitementproject.eop.core.component.alignment.vectorlink;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.Span;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * Alignment between chunks of T and H using vector based approaches.
 * 
 * @author Madhumita
 * @since July 2015
 */
public class BagOfChunkVectorAligner extends VectorAligner {

	public BagOfChunkVectorAligner(CommonConfig config,
			boolean removeStopWords, Set<String> stopWords)
			throws ConfigurationException, IOException {

		// Initialize the vector models and threshold using superclass.
		super(config, removeStopWords, stopWords, "BagOfChunkVectorScoring",
				"chunk");

		// Load the chunker model
		NameValueTable comp = config.getSection("NemexBagOfChunksScoring");
		String chunkerModel = comp.getString("chunkerModelPath");

		if (null == chunkerModel) {
			throw new ConfigurationException(
					"Please specify path for model for chunker.");
		}
		loadChunkerModel(chunkerModel);

	}

	/**
	 * Loads chunker model file
	 * 
	 * @param chunkerModelPath
	 *            path to the chunker model
	 */
	private void loadChunkerModel(String chunkerModelPath) {
		// initialize the chunker model file
		InputStream modelIn = null;
		ChunkerModel model = null;

		try {
			modelIn = new FileInputStream(chunkerModelPath);
			model = new ChunkerModel(modelIn);
		} catch (IOException e) {
			logger.warn("Could not load Chunker model");
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}

		this.chunker = new opennlp.tools.chunker.ChunkerME(model);

	}

	@Override
	public String getComponentName() {
		return ("BagOfChunkVectorAligner");
	}

	@Override
	public void annotate(JCas aJCas) throws PairAnnotatorComponentException {

		logger.info("annotate() called with a JCas with the following T and H;  ");

		if (null == aJCas)
			throw new AlignmentComponentException(
					"annotate() got a null JCas object.");

		JCas tView;
		JCas hView;
		try {
			tView = aJCas.getView(LAP_ImplBase.TEXTVIEW);
			hView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
		} catch (CASException e) {
			throw new AlignmentComponentException(
					"Failed to access the Two views (TEXTVIEW, HYPOTHESISVIEW)",
					e);
		}

		logger.info("TEXT: " + tView.getDocumentText());
		logger.info("HYPO: " + hView.getDocumentText());

		// Add chunk annotations to the JCas T and H entries.

		chunk(tView);
		chunk(hView);

		// Call to super, which does the actual alignment
		// super.annotate(aJCas);

		//Get T and H chunk annotations
		AnnotationIndex<Annotation> tChunks = tView
				.getAnnotationIndex(Chunk.type);
		AnnotationIndex<Annotation> hChunks = hView
				.getAnnotationIndex(Chunk.type);

		if (null == tChunks) {
			throw new AlignmentComponentException("Could not read text chunks.");
		}

		if (null == hChunks) {
			throw new AlignmentComponentException(
					"Could not read hypothesis chunks.");
		}

		//Map of chunks and their vectors
		HashMap<String, INDArray> vectors = new HashMap<String, INDArray>();
		createChunkVecMap(tView, tChunks, vectors);
		createChunkVecMap(hView, hChunks, vectors);
		
		//Find similarity between all T and H chunks
		for (FSIterator<Annotation> hIter = hChunks.iterator(); hIter.hasNext();) {
			Annotation curHAnnot = hIter.next();
			String hStr = curHAnnot.getCoveredText().toLowerCase();

			for (FSIterator<Annotation> tIter = tChunks.iterator(); tIter
					.hasNext();) {
				Annotation curTAnnot = tIter.next();
				String tStr = curTAnnot.getCoveredText().toLowerCase();

				double sim = 0d;
				
				if(hStr.equals(tStr))
					sim = 1.0;
				else
					sim = calculateSimilarity(vectors,tStr,hStr);
				
				logger.info("Similarity between, " + tStr + " and " + hStr
						+ " is: " + sim);

				int compare = Double.compare(sim, threshold);
				if (compare == 0 || compare > 0) {
					// if similarity >= threshold, add alignment link
					logger.info("Adding alignment link between, " + tStr
							+ " and " + hStr);
					addAlignmentLink(tView, hView, curTAnnot, curHAnnot, sim);
				}

			}
		}
	}

	/**
	 * Calculate similarity between two chunk strings
	 * @param vectors Map for chunk strings and vectors.
	 * @param tStr tChunk string.
	 * @param hStr hChunk string.
	 * @return similarity between vectors for tStr and hStr.
	 */
	private double calculateSimilarity(HashMap<String, INDArray> vectors,
			String tStr, String hStr) {
		INDArray tVec = vectors.get(tStr);
        INDArray hVec = vectors.get(hStr);
        
        if(tVec == null || hVec == null)
            return -1;
        
        return  Nd4j.getBlasWrapper().dot(tVec, hVec);
		
	}

	/**
	 * Create a Map of chunk annotation text and corresponding vector for the
	 * chunk. The resultant vector is calculated by summing vectors for all
	 * tokens in the chunk.
	 * 
	 * @param view
	 *            View which contains required chunk annotations.
	 * @param chunks
	 *            Chunk annotations on given view.
	 * @param vectors
	 *            Map which stores vectors for chunks.
	 */
	private void createChunkVecMap(JCas view,
			AnnotationIndex<Annotation> chunks,
			HashMap<String, INDArray> vectors) {

		// Iterate over all chunks
		for (FSIterator<Annotation> tIter = chunks.iterator(); tIter.hasNext();) {
			Annotation curAnnot = tIter.next();
			
			//Vector for required chunk string has been calculated already
			if(vectors.containsKey(curAnnot.getCoveredText().toLowerCase()))
				continue;

			// Get all tokens covered under Chunk annotation.
			Collection<Token> coveredTokens = JCasUtil.selectCovered(view,
					Token.class, curAnnot.getBegin(), curAnnot.getEnd());

			if (coveredTokens.size() == 0)
				logger.warn("No tokens covered under the current chunk annotation.");

			INDArray curVec = null;
			// Iterate over all tokens
			for (Iterator<Token> iter = coveredTokens.iterator(); iter
					.hasNext();) {
				if (curVec == null)
					// First token in given chunk
					curVec = vec.getWordVectorMatrix(iter.next()
							.getCoveredText().toLowerCase());
				else
					// Sum vectors for all tokens to get equivalent chunk vector
					curVec = curVec.add(vec.getWordVectorMatrix(iter.next()
							.getCoveredText().toLowerCase()));
			}

			// Store resulting vector in map of chunk string vs. vector
			vectors.put(curAnnot.getCoveredText().toLowerCase(), Transforms.unitVec(curVec));

		}

	}

	/**
	 * Chunk the content in given view and add chunk annotations.
	 * 
	 * @param view
	 *            JCas view for text or hypothesis.
	 */
	private void chunk(JCas view) {

		Collection<Token> annots = JCasUtil.select(view, Token.class);

		if (null == annots) {
			logger.warn("Token annotations in view: " + view + " not found");
		}

		/*
		 * Creating list of tokenText, and tags for chunking using chunker
		 * model. Creating list of start and end offset to identify actual start
		 * and end offset of chunk given start and end token num.
		 */
		List<String> tokenTexts = new ArrayList<String>();
		List<String> tags = new ArrayList<String>();
		List<Integer> tokenStartOffsets = new ArrayList<Integer>();
		List<Integer> tokenEndOffsets = new ArrayList<Integer>();

		for (Iterator<Token> iter = annots.iterator(); iter.hasNext();) {
			Token token = (Token) iter.next();
			tokenTexts.add(token.getCoveredText().toLowerCase());
			tags.add(token.getPos().getPosValue());
			tokenStartOffsets.add(token.getBegin());
			tokenEndOffsets.add(token.getEnd());
		}

		// Generating chunks
		Span[] chunk = this.chunker.chunkAsSpans(
				tokenTexts.toArray(new String[tokenTexts.size()]),
				tags.toArray(new String[tags.size()]));

		// Iterating over all chunks
		for (int i = 0; i < chunk.length; i++) {

			// starting token number in given chunk, based on list used
			// initially
			int start = chunk[i].getStart();

			// end token number in given chunk
			int end = chunk[i].getEnd();

			// String for total chunk
			String chunkStr = "";

			// Iterating over all tokens in chunk
			for (int j = start; j < end; j++) {
				chunkStr += tokenTexts.get(j);
			}

			// Actual start and end offset of chunk
			int chunkStartOffset = tokenStartOffsets.get(start);
			int chunkEndOffset = tokenEndOffsets.get(end - 1);

			// Creating and adding chunk annotation
			Chunk chunkAnnot = new Chunk(view, chunkStartOffset, chunkEndOffset);
			chunkAnnot.setChunkValue(chunkStr);
			chunkAnnot.addToIndexes();
		}

	}

	/**
	 * Chunker
	 */
	private ChunkerME chunker;

	/**
	 * Logger
	 */
	private final static Logger logger = Logger
			.getLogger(BagOfChunkVectorAligner.class.getName());

}
