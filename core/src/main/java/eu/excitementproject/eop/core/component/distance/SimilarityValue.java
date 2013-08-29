package eu.excitementproject.eop.core.component.distance;

import eu.excitementproject.eop.common.component.distance.DistanceValue;

/**
 * The <code>SimilarityValue</code> class extends <code>DistanceValue</code> and
 * force <code>simBased</code> to be true.
 * 
 * @author Rui Wang
 * @since November 2012
 */
public class SimilarityValue extends DistanceValue {

	/**
	 * the similarity value
	 * @param distance
	 * @param rawValue
	 */
	public SimilarityValue(double distance, double rawValue) {
		super(distance, true, rawValue);
	}

}
