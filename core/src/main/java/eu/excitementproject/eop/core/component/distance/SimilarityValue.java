package eu.excitementproject.eop.core.component.distance;

/**
 * The <code>SimilarityValue</code> class extends <code>DistanceValue</code> and
 * force <code>simBased</code> to be true.
 * 
 * @author Rui
 */
public class SimilarityValue extends DistanceValue {

	public SimilarityValue(double distance, double rawValue) {
		super(distance, true, rawValue);
	}

}
