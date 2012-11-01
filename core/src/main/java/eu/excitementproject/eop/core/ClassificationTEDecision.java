package eu.excitementproject.eop.core;

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
