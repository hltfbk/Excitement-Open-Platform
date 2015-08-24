package eu.excitementproject.eop.core.component.scoring;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.alignment.vectorlink.BagOfChunkVectorAligner;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * Calculate similarity scores between T and H based on alignment of chunks
 * through vectors.
 * 
 * @author Madhumita
 * @since July 2015
 */
public class BagOfChunkVectorScoring implements ScoringComponent {

	public BagOfChunkVectorScoring(CommonConfig config)
			throws ConfigurationException, IOException,
			LexicalResourceException {

		NameValueTable comp = config.getSection("BagOfWordVectorScoring");

		this.removeStopWords = Boolean.valueOf(comp
				.getString("removeStopWords"));

		/*
		 * If remove stop words is true, create a stopwords set.
		 */
		if (removeStopWords) {
			this.stopWords = new HashSet<String>();
			try {
				for (String str : (Files.readAllLines(
						Paths.get(comp.getString("stopWordPath")),
						Charset.forName("UTF-8"))))
					this.stopWords.add(str.toLowerCase());
			} catch (IOException e1) {
				logger.error("Could not read stop words file");
			}
		}

		this.useCoverageFeats = Boolean.valueOf(comp
				.getString("useCoverageFeats"));
		this.coverageFeats = comp.getString("coverageFeats").split(",");

		if (useCoverageFeats)
			numOfFeats += coverageFeats.length;

		this.aligner = new BagOfChunkVectorAligner(config, removeStopWords,
				stopWords);

	}

	@Override
	public Vector<Double> calculateScores(JCas cas)
			throws ScoringComponentException {

		try {
			// add alignment links to cas pair.
			this.aligner.annotate(cas);
		} catch (PairAnnotatorComponentException e) {
			throw new ScoringComponentException(
					"Could not add alignment links to T and H pair.");
		}

		Vector<Double> scoresVector = new Vector<Double>();

		// Read text and hypothesis views
		if (null == cas)
			throw new ScoringComponentException(
					"calculateScores() got a null JCas object.");

		JCas tView;
		JCas hView;
		try {
			tView = cas.getView(LAP_ImplBase.TEXTVIEW);
			hView = cas.getView(LAP_ImplBase.HYPOTHESISVIEW);
		} catch (CASException e) {
			throw new ScoringComponentException(
					"Failed to access the Two views (TEXTVIEW, HYPOTHESISVIEW)",
					e);
		}

		logger.info("TEXT: " + tView.getDocumentText());
		logger.info("HYPO: " + hView.getDocumentText());

		// num of Chunks in text
		int tSize = JCasUtil.select(tView, Chunk.class).size();
		if (0 == tSize) {
			logger.warn("No chunks found for text.");
		}

		// num of Chunks in hypothesis
		int hSize = JCasUtil.select(hView, Chunk.class).size();
		if (0 == hSize) {
			logger.warn("No chunks found for hypothesis");
		}

		// Get all alignment links
		int negLink = 0;
		Collection<Link> links = JCasUtil.select(hView, Link.class);
		for (Link link : links) {
			if (link.getLinkInfo().equalsIgnoreCase("negative")) {
				logger.info("Found negative link");
				negLink++;
			}
		}
		// num of alignment links between text and hypothesis
		int posLink = links.size() - negLink;

		// Scores: num of alignments/num of T chunks, num of alignments/num of H
		// chunks, product of the two.
		// scoresVector.add((double) numOfLinks / tSize);

		// Separate scores for negative and positive alignments
		scoresVector.add((double) negLink / hSize);
		scoresVector.add((double) posLink / hSize);
		// scoresVector.add((double) numOfLinks * numOfLinks / tSize / hSize);

		if (useCoverageFeats) {

			// Get T and H tokens

			Collection<Token> tTokens = JCasUtil.select(tView, Token.class);
			int numOfTTokens = tTokens.size();

			if (numOfTTokens == 0) {
				logger.warn("No tokens found for TEXT");
			}

			Collection<Token> hTokens = JCasUtil.select(hView, Token.class);
			int numOfHTokens = hTokens.size();

			if (numOfHTokens == 0) {
				logger.warn("No tokens found for HYPOTHESIS");
			}

			// Initialization of hash maps
			HashMap<String, Integer> tWordsMap = new HashMap<String, Integer>();
			HashMap<String, Integer> tPosMap = new HashMap<String, Integer>();

			HashMap<String, Integer> hWordsMap = new HashMap<String, Integer>();
			HashMap<String, Integer> hPosMap = new HashMap<String, Integer>();

			HashMap<String, Integer> tCovFeatMap = new HashMap<String, Integer>();
			HashMap<String, Integer> hCovFeatMap = new HashMap<String, Integer>();

			// Create map of content words, verbs and proper nouns wrt frequency
			// in given token set of T/H
			NemexScorerUtility.createCovPosMap(tTokens, tCovFeatMap);
			NemexScorerUtility.createCovPosMap(hTokens, hCovFeatMap);

			// Iterating over all alignment links
			for (final Iterator<Link> iter = links.iterator(); iter.hasNext();) {
				Link link = iter.next();

				int tStartOffset = link.getTSideTarget().getBegin();
				int tEndOffset = link.getTSideTarget().getEnd();
				int hStartOffset = link.getHSideTarget().getBegin();
				int hEndOffset = link.getHSideTarget().getEnd();

				// T tokens covered under alignment
				Collection<Token> tLinkCoveredTokens = JCasUtil.selectCovered(
						tView, Token.class, tStartOffset, tEndOffset);

				if (tLinkCoveredTokens.size() == 0)
					logger.warn("No tokens covered under aligned data in TEXT.");

				// H tokens covered under alignment
				Collection<Token> hLinkCoveredTokens = JCasUtil.selectCovered(
						hView, Token.class, hStartOffset, hEndOffset);

				if (hLinkCoveredTokens.size() == 0)
					logger.warn("No tokens covered under aligned data in HYPOTHESIS.");

				// Map of all word and POS wrt frequency, covered under
				// alignment
				NemexScorerUtility.addToWordsAndPosMap(tWordsMap, tPosMap,
						tLinkCoveredTokens);
				NemexScorerUtility.addToWordsAndPosMap(hWordsMap, hPosMap,
						hLinkCoveredTokens);

			}

			//calculating overlap scores
			scoresVector.addAll(NemexScorerUtility.calculateOverlap(hWordsMap,
					hPosMap, coverageFeats, hCovFeatMap));

		}

		return scoresVector;
	}

	@Override
	public String getComponentName() {
		return "BagOfChunkVectorScoring";
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	/**
	 * @return num of features.
	 */
	public int getNumOfFeats() {
		return numOfFeats;
	}

	/**
	 * num of features
	 */
	int numOfFeats = 2;

	/**
	 * Stopwords set
	 */
	private Set<String> stopWords;

	/**
	 * whether stopwords should be removed
	 */
	private boolean removeStopWords;

	/**
	 * Use coverage features like percentage of named entities, content words,
	 * etc. covered under positively aligned chunks.
	 */
	private boolean useCoverageFeats;

	/**
	 * Which coverage features to use: word coverage, content word coverage,
	 * verb coverage and proper noun coverage.
	 */
	private String[] coverageFeats;

	/**
	 * aligner to add alignment links on T and H pairs
	 */
	BagOfChunkVectorAligner aligner;

	/**
	 * The logger.
	 */
	public final static Logger logger = Logger
			.getLogger(BagOfChunkVectorScoring.class);

}
