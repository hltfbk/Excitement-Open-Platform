package eu.excitementproject.eop.core.component.alignment.vectorlink;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.Span;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
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
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
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
			throws ConfigurationException, IOException,
			LexicalResourceException {

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

		loadWn(config);

	}

	/**
	 * Initialization of Wordnet Lexical Resource
	 * 
	 * @param config
	 *            The configuration file
	 * @throws ConfigurationException
	 * @throws LexicalResourceException
	 */
	private void loadWn(CommonConfig config) throws ConfigurationException,
			LexicalResourceException {
		// Default values
		boolean useFirstSenseOnlyLeft = false;
		boolean useFirstSenseOnlyRight = false;
		String wnPath = "/ontologies/EnglishWordNet-dict/";

		// Get values from configuration file
		NameValueTable comp = config.getSection("BagOfChunkVectorScoring");

		if (null != comp.getString("useFirstSenseOnlyLeft")
				&& Boolean
						.parseBoolean(comp.getString("useFirstSenseOnlyLeft"))) {
			useFirstSenseOnlyLeft = true;
		}
		if (null != comp.getString("useFirstSenseOnlyRight")
				&& Boolean.parseBoolean(comp
						.getString("useFirstSenseOnlyRight"))) {
			useFirstSenseOnlyRight = true;
		}
		if (null != comp.getString("wordNetFilesPath")) {
			wnPath = comp.getString("wordNetFilesPath");
		}

		File wnFile = new File(wnPath);
		if (!wnFile.exists()) {
			throw new ConfigurationException("cannot find WordNet at: "
					+ wnPath);
		}

		Set<WordNetRelation> wnRelSet = new HashSet<WordNetRelation>();
		wnRelSet.add(WordNetRelation.ANTONYM);

		logger.info("Loading WordNet");

		wnlr = new WordnetLexicalResource(wnFile, useFirstSenseOnlyLeft,
				useFirstSenseOnlyRight, wnRelSet);

		logger.info("Load WordNet done.");
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

		// Get T and H chunk annotations
		Collection<Chunk> tChunks = JCasUtil.select(tView, Chunk.class);
		Collection<Chunk> hChunks = JCasUtil.select(hView, Chunk.class);

		if (null == tChunks) {
			throw new AlignmentComponentException("Could not read text chunks.");
		}

		if (null == hChunks) {
			throw new AlignmentComponentException(
					"Could not read hypothesis chunks.");
		}

		chunkVecMap = new HashMap<String, INDArray>();
		createMap(tView, tChunks);
		createMap(hView, hChunks);

		// Find similarity between all T and H chunk vectors
		for (Iterator<Chunk> hIter = hChunks.iterator(); hIter.hasNext();) {

			Annotation curHChunk = hIter.next();
			String hStr = curHChunk.getCoveredText();

			// Get vector for H chunk.
			INDArray curHChunkVec = chunkVecMap.get(hStr);

			for (Iterator<Chunk> tIter = tChunks.iterator(); tIter.hasNext();) {
				Annotation curTChunk = tIter.next();
				String tStr = curTChunk.getCoveredText();

				double sim = 0d;

				// Similarity 1.0 for identical chunks
				if (hStr.equals(tStr))
					sim = 1.0;
				else {
					// Get vector for T chunk.
					INDArray curTChunkVec = chunkVecMap.get(tStr);

					sim = calculateSimilarity(curHChunkVec, curTChunkVec);
				}
				logger.info("Similarity between, " + tStr + " and " + hStr
						+ " is: " + sim);

				int compare = Double.compare(sim, threshold);
				if (compare == 0 || compare > 0) {
					// if similarity >= threshold, add alignment link
					logger.info("Adding alignment link between, " + tStr
							+ " and " + hStr);

					HashSet<String> tTokenSet = new HashSet<String>();
					HashSet<String> hTokenSet = new HashSet<String>();

					createLemmaSet(tView, curTChunk, tTokenSet);
					createLemmaSet(hView, curHChunk, hTokenSet);
					
					boolean antonyms = false;
					
					//check for negative alignments
					try {
						antonyms = checkforAntonym(tTokenSet, hTokenSet);
					} catch (LexicalResourceException e) {
						e.getMessage();
					}

					addAlignmentLink(tView, hView, curTChunk, curHChunk, sim,antonyms);
				}

			}
		}
	}

	/**
	 * Create set of all token lemma strings covered by given chunk in given view
	 * @param view Current JCas view
	 * @param curChunk Current chunk annotation
	 * @param tokenSet Set of token lemma
	 */
	private void createLemmaSet(JCas view, Annotation curChunk,
			HashSet<String> tokenSet) {

		for (Token t : JCasUtil.selectCovered(view, Token.class,
				curChunk.getBegin(), curChunk.getEnd())) {
			String curLemma = t.getLemma().getValue();
			if (!tokenSet.contains(curLemma))
				tokenSet.add(curLemma);
		}

	}

	/**
	 * Check if one of the text token lemmas is an antonym of one of the hypothesis token lemmas
	 * @param tTokenSet Set of lemmas of tokens in T
	 * @param hTokenSet Set of lemmas of tokens in H
	 * @return true if antonym is present
	 * @throws LexicalResourceException
	 */
	private boolean checkforAntonym(HashSet<String> tTokenSet,
			HashSet<String> hTokenSet) throws LexicalResourceException {
		// Iterating over all h token strings
		for (Iterator<String> hIter = hTokenSet.iterator(); hIter.hasNext();) {
			//Get all antonyms for current h token string
			for (LexicalRule<? extends RuleInfo> rule : wnlr.getRulesForLeft(
					hIter.next(), null)) {
				//If antonym present in t token string set, negative alignment
				if (tTokenSet.contains(rule.getRLemma()))
					return true;
			}
		}

		return false;
	}

	/**
	 * Create map of chunk text and corresponding vector
	 * 
	 * @param view
	 *            text or hypothesis view for current chunk
	 * @param chunks
	 *            chunk annotations.
	 */
	private void createMap(JCas view, Collection<Chunk> chunks) {

		for (Iterator<Chunk> chunkIter = chunks.iterator(); chunkIter.hasNext();) {
			Chunk curChunk = chunkIter.next();
			String curChunkText = curChunk.getCoveredText();
			if (!chunkVecMap.containsKey(curChunkText))
				chunkVecMap.put(curChunkText, getChunkVec(view, curChunk));
		}

	}

	/**
	 * Calculate similarity between two vectors.
	 * 
	 * @param vec1
	 *            First vector
	 * @param vec2
	 *            Second vector
	 * @return similarity between vec1 and vec2.
	 */
	@SuppressWarnings("unchecked")
	private double calculateSimilarity(INDArray vec1, INDArray vec2) {
		if (vec1 == null || vec2 == null)
			return -1;

		return Nd4j.getBlasWrapper().dot(vec1, vec2);

	}

	/**
	 * Return vector for the given chunk, calculated by summing vectors for all
	 * tokens in the chunk.
	 * 
	 * @param view
	 *            View which contains required chunk annotations.
	 * @param chunks
	 *            Chunk annotations on given view.
	 * @return vector for given chunk.
	 */
	private INDArray getChunkVec(JCas view, Annotation curChunk) {

		INDArray curVec = null;

		// Get all tokens covered under Chunk annotation.
		Collection<Token> coveredTokens = JCasUtil.selectCovered(view,
				Token.class, curChunk.getBegin(), curChunk.getEnd());

		if (coveredTokens.size() == 0)
			logger.warn("No tokens covered under the current chunk annotation.");

		// Iterate over all tokens
		for (Iterator<Token> iter = coveredTokens.iterator(); iter.hasNext();) {
			Token curToken = iter.next();
			String curPos = curToken.getPos().getPosValue();
			String curTokenText = curToken.getCoveredText();

			// If token is a symbol, number, 's, determiner (a/an/the) or the
			// stopword "in", skip it for
			// chunk vector calculation because it does not add any new
			// information.

			if (ignorePosSet.contains(curPos)
					|| curTokenText.equalsIgnoreCase("a")
					|| curTokenText.equalsIgnoreCase("an")
					|| curTokenText.equalsIgnoreCase("the")
					|| curTokenText.equalsIgnoreCase("in")) {
				continue;
			}

			// Lower case if token is not proper noun.
			if (!curPos.startsWith("NNP"))
				curTokenText = curTokenText.toLowerCase();

			if (curVec == null) {
				// First token in given chunk
				// Check if word present in vector model file
				if (vec.hasWord(curTokenText))
					curVec = vec.getWordVectorMatrix(curTokenText);
			} else {
				// Sum vectors for all tokens to get equivalent chunk vector
				// Check if word present in vector model file
				if (vec.hasWord(curTokenText))
					curVec = curVec.add(vec.getWordVectorMatrix(curTokenText));
			}

		}

		if (null == curVec)
			return null;

		return Transforms.unitVec(curVec);

	}

	/**
	 * Map of chunk strings and corresponding vectors.
	 */
	private HashMap<String, INDArray> chunkVecMap;

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
			Token token = iter.next();
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
	 * WordNet lexical resource
	 */
	private WordnetLexicalResource wnlr;

	/**
	 * Logger
	 */
	private final static Logger logger = Logger
			.getLogger(BagOfChunkVectorAligner.class.getName());

}
