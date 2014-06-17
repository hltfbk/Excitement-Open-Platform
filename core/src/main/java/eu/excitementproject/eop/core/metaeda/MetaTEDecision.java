package eu.excitementproject.eop.core.metaeda;

import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.TEDecision;

public class MetaTEDecision implements TEDecision {
	
	private DecisionLabel decision;
	private double confidence;
	private String pairID;

	public MetaTEDecision(DecisionLabel dLabel) {
		super();
		this.decision = dLabel;
	}

	public MetaTEDecision(DecisionLabel dLabel, Double confidence, String pairID) {
		this(dLabel, pairID);
		this.confidence = confidence;
	}

	public MetaTEDecision(DecisionLabel dLabel, String pairID) {
		this(dLabel);
		this.pairID = pairID;
	}

	@Override
	public DecisionLabel getDecision() {
		return this.decision;
	}

	@Override
	public double getConfidence() {
		return this.confidence;
	}

	@Override
	public String getPairID() {
		return this.pairID;
	}

}
