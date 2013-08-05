package eu.excitementproject.eop.core.component.distance;

import java.util.Vector;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.distance.DistanceCalculation;
import eu.excitementproject.eop.common.component.distance.DistanceComponentException;
import eu.excitementproject.eop.common.component.distance.DistanceValue;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.core.component.scoring.BagOfWordsScoring;

/**
 * The <code>BagOfWordsSimilarity</code> extends the
 * <code>BagOfWordsScoring</code> class and implements the
 * <code>DistanceCalculation</code> interface. As the
 * <code>BagOfWordsScoring</code> already calculates several similarity scores,
 * the return value of the <code>BoWSimilarityValue</code> (extends
 * <code>DistanceValue</code>) will be the first score in the vector after
 * normalization.
 * 
 * @author Rui Wang
 * @since November 2012
 */
public class BagOfWordsSimilarity extends BagOfWordsScoring implements
		DistanceCalculation {

	@Override
	public String getComponentName() {
		return "BagOfWordsSimilarity";
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	@Override
	public DistanceValue calculation(JCas aCas)
			throws DistanceComponentException {
		// (1 - (T&H/H))
		double distance = 0.0d;

		// (T&H/H)
		double unnormalized = 0.0d;

		// all the values: (T&H/H), (T&H/T), and ((T&H/H)*(T&H/T))
		Vector<Double> scoresVector;
		try {
			scoresVector = calculateScores(aCas);
		} catch (ScoringComponentException e) {
			throw new DistanceComponentException(e.getMessage());
		}
		unnormalized = scoresVector.get(0);
		distance = 1.0d - unnormalized;

		return new SimilarityValue(distance, unnormalized);
	}

}
