/**
 * 
 */
package eu.excitementproject.eop.core.component.scoring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.entailment.EntailmentMetadata;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.alignment.nemex.NemexBagOfChunksAligner;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * @author Madhumita
 * 
 */
public class NemexBagOfChunksScoring implements ScoringComponent {

	public NemexBagOfChunksScoring(CommonConfig config)
			throws ConfigurationException {

		NameValueTable comp = config.getSection("NemexBagOfChunksScoring");

		this.direction = comp.getString("direction");
		this.useCoverageFeats = Boolean.valueOf(comp
				.getString("useCoverageFeats"));
		this.coverageFeats = comp.getString("coverageFeats").split(",");

		if (useCoverageFeats)
			numOfFeats += coverageFeats.length;

		this.aligner = new NemexBagOfChunksAligner(config, this.direction);
	}

	/**
	 * Returns the number of feature values in the scorer.
	 * 
	 * @return number of features
	 */

	public int getNumOfFeats() {
		return numOfFeats;
	}

	public String[] getCoverageFeats() {
		return this.coverageFeats;
	}

	@Override
	public String getComponentName() {
		return "NemexBagOfChunksScoring";
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	@Override
	public Vector<Double> calculateScores(JCas cas)
			throws ScoringComponentException {
		// all the values: (T&H/H), (T&H/T), and ((T&H/H)*(T&H/T))

		Vector<Double> scoresVector = new Vector<Double>();

		try {
			this.aligner.annotate(cas);

			JCas tView = cas.getView(LAP_ImplBase.TEXTVIEW);

			JCas hView = cas.getView(LAP_ImplBase.HYPOTHESISVIEW);

			if (null != tView && null != hView) {

				scoresVector.addAll(calculateSimilarity(tView, hView));
			}

			String task = JCasUtil.select(cas, EntailmentMetadata.class)
					.iterator().next().getTask();
			if (null == task) {
				scoresVector.add(0d);
				scoresVector.add(0d);
				scoresVector.add(0d);
				scoresVector.add(0d);
			} else {
				scoresVector.add(NemexScorerUtility.isTaskIE(task));
				scoresVector.add(NemexScorerUtility.isTaskIR(task));
				scoresVector.add(NemexScorerUtility.isTaskQA(task));
				scoresVector.add(NemexScorerUtility.isTaskSUM(task));
			}

		} catch (PairAnnotatorComponentException | CASException e) {
			throw new ScoringComponentException(e.getMessage());
		}
		return scoresVector;

	}

	/**
	 * 
	 * @param tView
	 * @param hView
	 * @return vector of feature scores
	 */
	private Vector<Double> calculateSimilarity(JCas tView, JCas hView) {

		Vector<Double> scoreValues = new Vector<Double>();

		Collection<Chunk> tChunks = JCasUtil.select(tView, Chunk.class);

		int tChunkNum = tChunks.size();

		if (0 == tChunkNum) {
			logger.warn("No chunks found for T");
		}

		Collection<Chunk> hChunks = JCasUtil.select(hView, Chunk.class);
		int hChunkNum = hChunks.size();

		if (0 == hChunkNum) {
			logger.warn("No chunks found for H");
		}

		Collection<Link> links = null;

		links = JCasUtil.select(hView, Link.class);

		double numOfLinks = links.size();

		if (0 == links.size()) {
			logger.warn("No alignment links found");
		}

		if (tChunkNum != 0 && hChunkNum != 0) {
			scoreValues.add(numOfLinks / tChunkNum);
			scoreValues.add(numOfLinks / hChunkNum);
			scoreValues.add(numOfLinks * numOfLinks / tChunkNum / hChunkNum);
		}
		else {
			scoreValues.add(0d);
			scoreValues.add(0d);
			scoreValues.add(0d);
		}

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

			//Iterating over all alignment links
			for (final Iterator<Link> iter = links.iterator(); iter.hasNext();) {
				Link link = iter.next();

				int tStartOffset = link.getTSideTarget().getBegin();
				int tEndOffset = link.getTSideTarget().getEnd();
				int hStartOffset = link.getHSideTarget().getBegin();
				int hEndOffset = link.getHSideTarget().getEnd();

				//T tokens covered under alignment
				Collection<Token> tLinkCoveredTokens = JCasUtil.selectCovered(
						tView, Token.class, tStartOffset, tEndOffset);

				if (tLinkCoveredTokens.size() == 0)
					logger.warn("No tokens covered under aligned data in TEXT.");

				//H tokens covered under alignment
				Collection<Token> hLinkCoveredTokens = JCasUtil.selectCovered(
						hView, Token.class, hStartOffset, hEndOffset);

				if (hLinkCoveredTokens.size() == 0)
					logger.warn("No tokens covered under aligned data in HYPOTHESIS.");

				//Map of all word and POS wrt frequency, covered under alignment
				NemexScorerUtility.addToWordsAndPosMap(tWordsMap, tPosMap,
						tLinkCoveredTokens);
				NemexScorerUtility.addToWordsAndPosMap(hWordsMap, hPosMap,
						hLinkCoveredTokens);

			}

			if (direction == "TtoH")
				scoreValues.addAll(NemexScorerUtility.calculateOverlap(
						tWordsMap, tPosMap, coverageFeats, tCovFeatMap));
			else
				scoreValues.addAll(NemexScorerUtility.calculateOverlap(
						hWordsMap, hPosMap, coverageFeats, hCovFeatMap));
		}

		return scoreValues;
	}

	private int numOfFeats = 7;

	private NemexBagOfChunksAligner aligner;

	private String direction;
	private boolean useCoverageFeats;
	private String[] coverageFeats;

	public final static Logger logger = Logger
			.getLogger(NemexBagOfChunksScoring.class);

}
