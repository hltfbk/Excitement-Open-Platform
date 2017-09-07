/**
 * 
 */
package eu.excitementproject.eop.core.component.scoring;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.entailment.EntailmentMetadata;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.alignment.nemex.NemexBagOfLemmasAligner;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * @author Madhumita
 * 
 */
public class NemexBagOfLemmasScoring implements ScoringComponent {

	public NemexBagOfLemmasScoring(CommonConfig config)
			throws ConfigurationException {

		NameValueTable comp = config.getSection("NemexBagOfLemmasScoring");

		this.removeStopWords = Boolean.valueOf(comp
				.getString("removeStopWords"));

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

		this.aligner = new NemexBagOfLemmasAligner(config, removeStopWords,
				stopWords);
	}

	/**
	 * Returns the number of feature values in the scorer.
	 * 
	 * @return number of features
	 */
	
	public int getNumOfFeats() {
		return numOfFeats;
	}

	@Override
	public String getComponentName() {
		return "NemexBagOfLemmasScoring";
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

		Collection<Link> links = null;

		links = JCasUtil.select(hView, Link.class);

		double numOfLinks = links.size();
		if (0 == links.size())
			logger.warn("No alignment links found");

		if (this.removeStopWords) {

			for (final Iterator<Token> tokensIter = tTokens.iterator(); tokensIter
					.hasNext();) {
				Token token = tokensIter.next();
				String curToken = token.getCoveredText().toLowerCase();

				if (stopWords.contains(curToken.toLowerCase()))
					numOfTTokens--;

			}

			for (final Iterator<Token> tokensIter = hTokens.iterator(); tokensIter
					.hasNext();) {
				Token token = tokensIter.next();
				String curToken = token.getCoveredText().toLowerCase();

				if (stopWords.contains(curToken.toLowerCase()))
					numOfHTokens--;

			}
			
			for (final Iterator<Link> iter = links.iterator(); iter.hasNext();) {
				Link link = iter.next();
				
				int tStartOffset = link.getTSideTarget().getBegin();
				int tEndOffset = link.getTSideTarget().getEnd();
				int hStartOffset = link.getHSideTarget().getBegin();
				int hEndOffset = link.getHSideTarget().getEnd();

				List<Token> tLinkCoveredTokens = JCasUtil.selectCovered(
						tView, Token.class, tStartOffset, tEndOffset);
				
				if (tLinkCoveredTokens.size() == 0)
					logger.warn("No tokens covered under aligned data in TEXT.");

				List<Token> hLinkCoveredTokens = JCasUtil.selectCovered(
						hView, Token.class, hStartOffset, hEndOffset);

				if (hLinkCoveredTokens.size() == 0)
					logger.warn("No tokens covered under aligned data in HYPOTHESIS.");
				
				if(stopWords.contains(tLinkCoveredTokens.get(0)) && stopWords.contains(hLinkCoveredTokens.get(0)))
					numOfLinks--;
				
			}
		}

		Vector<Double> returnValue = new Vector<Double>();

		returnValue.add(numOfLinks / numOfTTokens);
		returnValue.add(numOfLinks / numOfHTokens);
		returnValue.add(numOfLinks * numOfLinks / numOfTTokens / numOfHTokens);

		return returnValue;
	}

	private int numOfFeats = 7;

	private NemexBagOfLemmasAligner aligner;

	private Set<String> stopWords;
	private boolean removeStopWords;

	public final static Logger logger = Logger
			.getLogger(NemexBagOfLemmasScoring.class);

}
