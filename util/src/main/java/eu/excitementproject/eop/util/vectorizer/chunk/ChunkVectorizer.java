package eu.excitementproject.eop.util.vectorizer.chunk;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.Span;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;
import eu.excitementproject.eop.util.runner.LAPRunner;

/**
 * Calculate chunk vectors from word vectors and store the vector file.
 * @author Madhumita
 * @since September 2015
 */
public class ChunkVectorizer {

	public ChunkVectorizer(String chunkerModel, String vecModelType,
			String vecModel, String ignorePos) throws ConfigurationException,
			IOException {

		loadChunkerModel(chunkerModel);

		// load the vector model
		initializeWordVecModel(vecModelType, vecModel);

		// initialize ignore POS set
		intializeIgnorePosSet(ignorePos);

		chunkVecMap = new HashMap<String, INDArray>();
	}

	/**
	 * Initialize set of POS tags to ignore for vector alignment
	 * 
	 * @param ignorePos
	 *            File with list of POS tags to ignore
	 * @throws ConfigurationException
	 */
	private void intializeIgnorePosSet(String ignorePos)
			throws ConfigurationException {

		// create set of POS tags to ignore
		ignorePosSet = new HashSet<String>();
		try {
			for (String str : (Files.readAllLines(Paths.get(ignorePos),
					Charset.forName("UTF-8"))))
				ignorePosSet.add(str);
		} catch (IOException e1) {
			logger.error("Could not read POS tags file");
		}

	}

	/**
	 * Initialize word vector model file
	 * 
	 * @param config
	 *            Configuration file
	 * @param sectionName
	 *            Section containing required configuration
	 * @throws ConfigurationException
	 * @throws IOException
	 */
	private void initializeWordVecModel(String modelType, String vecModel)
			throws ConfigurationException, IOException {

		if (null == vecModel) {
			logger.warn("Please specify the vector model file path.");
		}

		if (modelType.equalsIgnoreCase("google")) {

			File modelFile = new File(vecModel);
			vec = WordVectorSerializer.loadGoogleModel(modelFile, true);
			if (null == vec) {
				throw new IOException("Could not load Google model file.");
			}

		}

		else {
			logger.warn("Please specify the correct model type to load");
		}

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

	/**
	 * Find chunk vectors for T and H chunks.
	 * @param cas a (T,H) pair
	 * @throws CASException
	 */
	private void vectorize(JCas cas) throws CASException {

		logger.info("vectorize() called with a JCas with the following T and H;  ");

		if (null == cas) {
			logger.error("vectorize() got a null JCas object.");
			throw new CASException();
		}

		JCas tView;
		JCas hView;

		try {
			tView = cas.getView(LAP_ImplBase.TEXTVIEW);
			hView = cas.getView(LAP_ImplBase.HYPOTHESISVIEW);
		} catch (CASException e) {
			logger.error("Failed to access the Two views (TEXTVIEW, HYPOTHESISVIEW)");
			throw new CASException();
		}

		logger.info("TEXT: " + tView.getDocumentText());
		logger.info("HYPO: " + hView.getDocumentText());

		//chunking T and H
		chunk(tView);
		chunk(hView);

		// Get T and H chunk annotations
		Collection<Chunk> tChunks = JCasUtil.select(tView, Chunk.class);
		Collection<Chunk> hChunks = JCasUtil.select(hView, Chunk.class);

		if (null == tChunks) {
			logger.error("Could not read text chunks.");
		}

		if (null == hChunks) {
			logger.error("Could not read hypothesis chunks.");
		}

		logger.info("Getting vectors for text chunks");
		createMap(tView, tChunks);
		logger.info("Getting vectors for hypothesis chunks");
		createMap(hView, hChunks);
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
			logger.info("current chunk: " + curChunkText);
			if (!chunkVecMap.containsKey(curChunkText))
				chunkVecMap.put(curChunkText, getChunkVec(view, curChunk));
		}

	}

	/**
	 * Return vector for the given chunk, calculated by summing vectors for all
	 * tokens in the chunk, except the ones whose POS tags need to be ignored.
	 * 
	 * @param view
	 *            View which contains required chunk annotations.
	 * @param chunks
	 *            Chunk annotations on given view.
	 * @return vector for given chunk.
	 */
	private INDArray getChunkVec(JCas view, Chunk curChunk) {
		logger.info("Calculating vector");
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

			// If token is a symbol, ignore it
			if (ignorePosSet.contains(curPos)) {
				continue;
			}

			// Lower case if token is not proper noun.
			if (!curPos.startsWith("NNP"))
				curTokenText = curTokenText.toLowerCase();

			if (null == curVec) {
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
	 * Chunk the content in given view and add chunk annotations.
	 * 
	 * @param view
	 *            JCas view for text or hypothesis.
	 */
	private void chunk(JCas view) {

		logger.info("Chunking");

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
		Span[] chunk = chunker.chunkAsSpans(
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
	 * Write the chunk vector file to the disk
	 * @param fname Chunk vector model filename
	 * @throws IOException
	 */
	private void serializeChunkVecModel(String fname) throws IOException {

		BufferedWriter write = new BufferedWriter(new FileWriter(
				new File(fname), false));

		//Iterating over  all chunk strings and their vectors
		for (Iterator<Entry<String, INDArray>> chunkIter = chunkVecMap
				.entrySet().iterator(); chunkIter.hasNext();) {
			Entry<String, INDArray> curEntry = chunkIter.next();
			String word = curEntry.getKey();

			if (null == word) {
				continue;
			}

			StringBuilder sb = new StringBuilder();
			
			//String for chunk text
			sb.append(word.replaceAll("\\s+","_"));
			sb.append(" ");
			INDArray wordVector = curEntry.getValue();
			
			//Null vector - all tokens in chunk need to be ignored
			if(null == wordVector) 
				continue;
			
			for (int j = 0; j < wordVector.length(); j++) {
				sb.append(wordVector.getDouble(j));
				if (j < wordVector.length() - 1) {
					sb.append(" ");
				}
			}
			sb.append("\n");
			write.write(sb.toString());

		}

		write.flush();
		write.close();
	}

	public static void main(String[] args) throws ConfigurationException,
			LAPException, CASException, IOException {
		LAPRunner lapRunner = new LAPRunner(
				"eu.excitementproject.eop.lap.dkpro.MaltParserEN", "EN");

		String inputFile = args[0]; // Input file in RTE3 format
		String casOutputDir = args[1]; // Directory to store processed CAS as
										// XMI
		String outputVecFile = args[2]; // File to write chunk vector map to

		String chunkerModel = args[3]; //model file for chunker
		String vecModelType = args[4]; //google vector or not
		String vecModel = args[5]; //word vector model file path
		String ignorePosTags = args[6]; //file with list of token pos tags to ignore

		//Generate xmi files for all (T,H) pairs, with annotations
		lapRunner.runLAPOnFile(inputFile, casOutputDir); // run LAP on inputFile

		//directory to write the xmi files to
		File f = new File(casOutputDir);
		if (f.exists() == false) {
			throw new ConfigurationException("casOutputDIR:"
					+ f.getAbsolutePath() + " not found!");
		}

		//calculate chunk vectors
		ChunkVectorizer vectorizer = new ChunkVectorizer(chunkerModel,
				vecModelType, vecModel, ignorePosTags);

		for (File xmi : (f.listFiles())) {
			if (!xmi.getName().endsWith(".xmi")) {
				continue;
			}
			JCas cas = PlatformCASProber.probeXmi(xmi, null);
			vectorizer.vectorize(cas); // create chunk vector for chunks in each
										// cas
		}

		//write final vectors to chunk vector file
		vectorizer.serializeChunkVecModel(outputVecFile); 
	}

	/**
	 * Chunker
	 */
	private ChunkerME chunker;

	/**
	 * Map of chunk strings and corresponding vectors.
	 */
	private HashMap<String, INDArray> chunkVecMap;

	/**
	 * Set of POS tags to ignore for chunk vector calculation
	 */
	private HashSet<String> ignorePosSet;

	/**
	 * Word2Vec model
	 */
	private Word2Vec vec;

	/**
	 * Logger
	 */
	private final static Logger logger = Logger.getLogger(ChunkVectorizer.class
			.getName());

}
