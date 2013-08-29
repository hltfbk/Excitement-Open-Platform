package eu.excitementproject.eop.core;

import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.TEDecision;

/**
 * The <code>ClassificationTEDecision</code> class implements the
 * <code>TEDecision</code> interface.
 * 
 * It supports two kinds of constructors, with <code>confidence</code> value or
 * without.
 * 
 * @author Rui Wang
 * @since November 2012
 */

public class ClassificationTEDecision implements TEDecision {

	/**
	 * the decision label
	 */
	private DecisionLabel decision = null;
	
	/**
	 * the confidence value
	 */
	private double confidence = CONFIDENCE_NOT_AVAILABLE;
	
	/**
	 * the ID of the T-H pair
	 */
	private String pairId = null;

	/**
	 * the constructor with decision label and T-H pair ID
	 * @param decision the decision label
	 * @param pairId the ID of the T-H pair
	 */
	public ClassificationTEDecision(DecisionLabel decision, String pairId) {
		super();
		this.decision = decision;
		this.confidence = CONFIDENCE_NOT_AVAILABLE;
		this.pairId = pairId;
	}

	/**
	 * the constructor with decision label, T-H pair ID, and confidence value
	 * @param decision the decision label
	 * @param confidence the confidence value
	 * @param pairId the ID of the T-H pair
	 */
	public ClassificationTEDecision(DecisionLabel decision, double confidence,
			String pairId) {
		super();
		this.decision = decision;
		this.confidence = confidence;
		this.pairId = pairId;
	}

	@Override
	public final DecisionLabel getDecision() {
		return decision;
	}

	@Override
	public final double getConfidence() {
		return confidence;
	}

	@Override
	public final String getPairID() {
		return pairId;
	}

}
