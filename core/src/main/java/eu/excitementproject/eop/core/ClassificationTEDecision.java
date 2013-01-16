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
 * @author Rui
 */

public class ClassificationTEDecision implements TEDecision {

	private DecisionLabel decision = null;
	private double confidence = CONFIDENCE_NOT_AVAILABLE;
	private String pairId = null;

	public ClassificationTEDecision(DecisionLabel decision, String pairId) {
		super();
		this.decision = decision;
		this.confidence = CONFIDENCE_NOT_AVAILABLE;
		this.pairId = pairId;
	}

	public ClassificationTEDecision(DecisionLabel decision, double confidence,
			String pairId) {
		super();
		this.decision = decision;
		this.confidence = confidence;
		this.pairId = pairId;
	}

	@Override
	public DecisionLabel getDecision() {
		return decision;
	}

	@Override
	public double getConfidence() {
		return confidence;
	}

	@Override
	public String getPairID() {
		return pairId;
	}

}
