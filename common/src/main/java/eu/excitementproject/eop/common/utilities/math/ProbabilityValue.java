package eu.excitementproject.eop.common.utilities.math;

/**
 * A class for probability scores, assuring that the assigned value is a valid
 * probability value [0-1]
 * 
 * @author Shachar Mirkin
 *         2010
 */
public class ProbabilityValue {

	// --- PRIVATE ---
	
	private double _prob;

	// --- PUBLIC ---
	
	public ProbabilityValue(double prob) throws ProbabilityValueException {
		if (prob < 0 || prob > 1) {
			throw new ProbabilityValueException("Invalid probability value. Values should be in the [0-1] range");
		}
		_prob = prob;
	}
	
	public double getProb(){
		return _prob;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Double.toString(_prob);
	}

}
