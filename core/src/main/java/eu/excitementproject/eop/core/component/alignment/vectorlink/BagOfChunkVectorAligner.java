package eu.excitementproject.eop.core.component.alignment.vectorlink;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
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
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.apache.uima.fit.util.JCasUtil;

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
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.RelationType;
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.VerbOceanLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * Alignment between chunks of T and H using embedded vector based
 * approaches.
 * 
 * @author Madhumita
 * @since July 2015
 */
public class BagOfChunkVectorAligner extends VectorAligner {

	public BagOfChunkVectorAligner(CommonConfig config,
			boolean removeStopWords, Set<String> stopWords)
			throws ConfigurationException, IOException,
			LexicalResourceException{

		// Initialize the vector models and threshold using superclass.
		super(config, removeStopWords, stopWords, "BagOfChunkVectorScoring",
				"chunk");

		// Load the chunker model
		NameValueTable comp = config.getSection("BagOfChunkVectorScoring");
		String chunkerModel = comp.getString("chunkerModelPath");

		if (null == chunkerModel) {
			throw new ConfigurationException(
					"Please specify path for model for chunker.");
		}
		loadChunkerModel(chunkerModel);

		loadChunkVectorModel(config);

		loadWn(config);

		loadVO(config);

	}

	/**
	 * Load the file which contains chunk vectors
	 * @param config Configuration file
	 * @throws ConfigurationException
	 * @throws FileNotFoundException
	 */
	private void loadChunkVectorModel(CommonConfig config)
			throws ConfigurationException, FileNotFoundException {

		NameValueTable comp = config.getSection("BagOfChunkVectorScoring");

		wordVectors = WordVectorSerializer.loadTxtVectors(new File(comp
				.getString("chunkVecModel")));
	}

	/**
	 * Initialization of VerbOcean Lexical Resource to check negative alignment
	 * wrt entailment
	 * 
	 * @param config
	 *            The configuration file
	 * @throws ConfigurationException
	 * @throws LexicalResourceException
	 */
	private void loadVO(CommonConfig config) throws ConfigurationException,
			LexicalResourceException {
		// Get values from configuration file
		NameValueTable comp = config.getSection("BagOfChunkVectorScoring");
		boolean isVO = Boolean.parseBoolean(comp.getString("isVO"));

		if (isVO) {
			// Default values
			String voPath = "/VerbOcean/verbocean.unrefined.2004-05-20.txt";
			double voTh = 1.0;

			if (null != comp.getString("verbOceanFilesPath")) {
				voPath = comp.getString("verbOceanFilesPath");
			}
			if (null != comp.getString("verbOceanThreshold")) {
				voTh = Double.parseDouble(comp.getString("verbOceanThreshold"));
			}

			File voFile = new File(voPath);
			if (!voFile.exists()) {
				throw new ConfigurationException("cannot find VerbOcean at: "
						+ voPath);
			}

			Set<RelationType> voRelSet = new HashSet<RelationType>();
			// If H verb is stronger than, opposite of, or happens before T
			// verb:
			// negative alignment
			voRelSet.add(RelationType.STRONGER_THAN);
			voRelSet.add(RelationType.OPPOSITE_OF);
			logger.info("Loading VerbOcean");

			volr = new VerbOceanLexicalResource(voTh, voFile, voRelSet);

			logger.info("Load VerbOcean done.");
		} else
			volr = null;

	}

	/**
	 * Initialization of Wordnet Lexical Resource to query antomyms to detect
	 * negative alignments wrt entailment.
	 * 
	 * @param config
	 *            The configuration file
	 * @throws ConfigurationException
	 * @throws LexicalResourceException
	 */
	private void loadWn(CommonConfig config) throws ConfigurationException,
			LexicalResourceException {
		// Get values from configuration file
		NameValueTable comp = config.getSection("BagOfChunkVectorScoring");
		boolean isWN = Boolean.parseBoolean(comp.getString("isWN"));

		if (isWN) {

			// Default values
			boolean useFirstSenseOnlyLeft = false;
			boolean useFirstSenseOnlyRight = false;
			String wnPath = "/ontologies/EnglishWordNet-dict/";

			if (null != comp.getString("useFirstSenseOnlyLeft")
					&& Boolean.parseBoolean(comp
							.getString("useFirstSenseOnlyLeft"))) {
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
		} else
			wnlr = null;
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

		// Find similarity between all T and H chunk vectors
		for (Iterator<Chunk> hIter = hChunks.iterator(); hIter.hasNext();) {

			Annotation curHChunk = hIter.next();
			String hStr = curHChunk.getCoveredText().replaceAll("\\s+", "_");
			
			//check if hStr - hypothesis chunk is present in vocab 
			if(!wordVectors.hasWord(hStr))
				continue;

			for (Iterator<Chunk> tIter = tChunks.iterator(); tIter.hasNext();) {
				Annotation curTChunk = tIter.next();
				String tStr = curTChunk.getCoveredText().replaceAll("\\s+", "_");
				
				//check if tStr - text chunk is present in vocab
				if(!wordVectors.hasWord(tStr))
					continue;
				
				//calculate similarity between T chunk vector and H chunk vector
				double sim = calculateSimilarity(
						hStr, tStr);
				
				logger.info("Similarity between, " + tStr + " and " + hStr
						+ " is: " + sim);

				int compare = Double.compare(sim, threshold);
				if (compare == 0 || compare > 0) {
					// if similarity >= threshold, add alignment link
					logger.info("Adding alignment link between, " + tStr
							+ " and " + hStr);

					HashSet<String> tLemmaSet = new HashSet<String>();
					HashSet<String> hLemmaSet = new HashSet<String>();

					createLemmaSet(tView, curTChunk, tLemmaSet);
					createLemmaSet(hView, curHChunk, hLemmaSet);

					boolean negative = false;

					// check for negative alignments
					try {
						if (null != wnlr)
							negative = checkforAntonym(tLemmaSet, hLemmaSet);

						if (!negative) {
							if (null != volr)
								negative = checkNegVerbStrength(tLemmaSet,
										hLemmaSet);
						}
					} catch (LexicalResourceException e) {
						e.getMessage();
					}

					addAlignmentLink(tView, hView, curTChunk, curHChunk, sim,
							negative);
				}

			}
		}
	}
	

	/**
	 * Create set of all token lemma strings covered by given chunk in given
	 * view
	 * 
	 * @param view
	 *            Current JCas view
	 * @param curChunk
	 *            Current chunk annotation
	 * @param tokenSet
	 *            Set of token lemma
	 */
	private void createLemmaSet(JCas view, Annotation curChunk,
			HashSet<String> tokenSet) {

		for (Token t : JCasUtil.selectCovered(view, Token.class,
				curChunk.getBegin(), curChunk.getEnd())) {
			String curLemma = t.getLemma().getValue();
			if (!tokenSet.contains(curLemma))
				tokenSet.add(curLemma.toLowerCase());
		}

	}

        /**
         * Returns the similarity of 2 words
         * @param word the first word
         * @param word2 the second word
         * @return a normalized similarity (cosine similarity)
         */
        public double calculateSimilarity(String word,String word2) {
            if(word.equals(word2))
                return 1.0;

            INDArray vector = this.wordVectors.getWordVectorMatrix(word);
            INDArray vector2 = this.wordVectors.getWordVectorMatrix(word2);
            if(vector == null || vector2 == null)
                return -1;
            return  Nd4j.getBlasWrapper().dot(vector, vector2);
        }
        
	/**
	 * Check if one of the text token lemmas is an antonym of one of the
	 * hypothesis token lemmas
	 * 
	 * @param tTokenSet
	 *            Set of lemmas of tokens in T
	 * @param hTokenSet
	 *            Set of lemmas of tokens in H
	 * @return true if antonym is present
	 * @throws LexicalResourceException
	 */
	private boolean checkforAntonym(HashSet<String> tTokenSet,
			HashSet<String> hTokenSet) throws LexicalResourceException {
		// Iterating over all h token strings
		for (Iterator<String> hIter = hTokenSet.iterator(); hIter.hasNext();) {
			String curLemma = hIter.next();
			// Get all antonyms for current h token string
			for (LexicalRule<? extends RuleInfo> rule : wnlr.getRulesForLeft(
					curLemma, null)) {
				String antonym = rule.getRLemma().toLowerCase();
				logger.info("Checking if anotonym: " + antonym + " of "
						+ curLemma + " in H chunk is present in text chunk");
				// If antonym present in t token string set, negative alignment
				if (tTokenSet.contains(antonym)) {
					logger.info("Antonym match between T and H, negative alignment");
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 
	 * @param tTokenSet
	 *            Set of lemmas of tokens in T
	 * @param hTokenSet
	 *            Set of lemmas of tokens in H
	 * @return true if negative relation wrt entailment
	 * @throws LexicalResourceException
	 */
	private boolean checkNegVerbStrength(HashSet<String> tTokenSet,
			HashSet<String> hTokenSet) throws LexicalResourceException {
		// Iterating over all h token strings
		for (Iterator<String> hIter = hTokenSet.iterator(); hIter.hasNext();) {
			String curLemma = hIter.next();
			// Get all opposites, stronger verbs and verbs that happen before
			// current verb for current h token string
			for (LexicalRule<? extends RuleInfo> rule : volr.getRulesForLeft(
					curLemma, null)) {
				String vRelation = rule.getRLemma().toLowerCase();
				logger.info("Checking if verb relation: " + vRelation + " of "
						+ curLemma + " in H chunk is present in T chunk");
				// If the relation is present in t token string set, negative
				// alignment
				if (tTokenSet.contains(vRelation)) {
					logger.info("Relation match between T and H, negative alignment");
					return true;
				}
			}
		}
		return false;
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
	 * VerbOcean lexical resource
	 */
	private VerbOceanLexicalResource volr;

	/**
	 * chunk vectors
	 */
	WordVectors wordVectors;

	/**
	 * Logger
	 */
	private final static Logger logger = Logger
			.getLogger(BagOfChunkVectorAligner.class.getName());

}
