package eu.excitementproject.eop.core.component.scoring;

import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.alignment.vectorlink.BagOfWordVectorAligner;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * Calculate similarity scores between T and H based on alignment of words
 * through word vectors.
 * 
 * @author Madhumita
 * @since July 2015
 */
public class BagOfWordVectorScoring implements ScoringComponent {

	public BagOfWordVectorScoring(CommonConfig config)
			throws ConfigurationException, IOException {

		this.aligner = new BagOfWordVectorAligner(config);

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

		// num of words in text
		int tSize = JCasUtil.select(tView, Token.class).size();
		if (0 == tSize) {
			logger.warn("No tokens found for text.");
		}

		// num of words in hypothesis
		int hSize = JCasUtil.select(hView, Token.class).size();
		if (0 == hSize) {
			logger.warn("No tokens found for hypothesis");
		}

		// num of alignment links between text and hypothesis
		int numOfLinks = JCasUtil.select(hView, Link.class).size();

		// Scores: num of alignments/num of T tokens, num of alignments/num of H
		// tokens, product of the two.
		scoresVector.add((double) numOfLinks / tSize);
		scoresVector.add((double) numOfLinks / hSize);
		scoresVector.add((double) numOfLinks * numOfLinks / tSize / hSize);

		return scoresVector;
	}

	@Override
	public String getComponentName() {
		return "BagOfWordVectorAligner";
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	/**
	 * 
	 * @return number of features
	 */
	public int getNumOfFeats() {
		return numOfFeats;
	}

	/**
	 * number of features
	 */
	int numOfFeats = 3;

	/**
	 * aligner to add alignment links between T and H.
	 */
	BagOfWordVectorAligner aligner;

	/**
	 * The logger
	 */
	public final static Logger logger = Logger
			.getLogger(BagOfWordVectorScoring.class);

}
