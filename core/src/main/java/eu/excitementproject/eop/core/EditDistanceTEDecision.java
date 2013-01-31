package eu.excitementproject.eop.core;

import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.IEditDistanceTEDecision;

public class EditDistanceTEDecision implements IEditDistanceTEDecision {

	private DecisionLabel decisionLabel = null;
	private String pairId = null;
	private double confidence = IEditDistanceTEDecision.CONFIDENCE_NOT_AVAILABLE;
	
	public EditDistanceTEDecision (DecisionLabel decisionLabel, String pairId) {
		
		this.decisionLabel = decisionLabel;
		this.pairId = pairId;
		
	}
	
	public EditDistanceTEDecision (DecisionLabel decisionLabel, String pairId, double confidence) {
		
		this.decisionLabel = decisionLabel;
		this.pairId = pairId;
		this.confidence = confidence;
		
	}
	
	public DecisionLabel getDecision() {
		
		return decisionLabel;
		
	}
	
	public double getConfidence() {
		
		return this.confidence;
		
	}
	
	public String getPairID() {
		
		return pairId;
		
	}
	
	

}
