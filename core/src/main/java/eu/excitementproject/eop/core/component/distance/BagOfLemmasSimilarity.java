package eu.excitementproject.eop.core.component.distance;

import java.util.Vector;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.distance.DistanceCalculation;
import eu.excitementproject.eop.common.component.distance.DistanceComponentException;
import eu.excitementproject.eop.common.component.distance.DistanceValue;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.core.component.scoring.BagOfLemmasScoring;

/**
 * The <code>BagOfLemmasSimilarity</code> extends the
 * <code>BagOfLemmasScoring</code> class and implements the
 * <code>DistanceCalculation</code> interface. It is very similar to
 * <code>BagOfWordsScoring</code>, except it uses lemmas instead of word forms
 * to calculate the scores.
 * 
 * @author Rui Wang
 * @since November 2012
 */
public class BagOfLemmasSimilarity extends BagOfLemmasScoring implements
		DistanceCalculation {

	@Override
	public String getComponentName() {
		return "BagOfLemmasSimilarity";
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
