package eu.excitementproject.eop.core;

import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.IEditDistanceTEDecision;

public class EditDistanceTEDecision implements IEditDistanceTEDecision {

	private DecisionLabel decisionLabel;
	private String pairId;
	//private double score;
	private double confidence;
	
	public EditDistanceTEDecision (DecisionLabel decisionLabel, String pairId) {
		
		this.decisionLabel = decisionLabel;
		this.pairId = pairId;
		//this.score = 0.0;
		this.confidence = IEditDistanceTEDecision.CONFIDENCE_NOT_AVAILABLE;
		
	}
	
	public EditDistanceTEDecision (DecisionLabel decisionLabel, String pairId, double confidence) {
		
		this.decisionLabel = decisionLabel;
		this.pairId = pairId;
		//this.score = score;
		this.confidence = confidence;
		
	}
	
	public DecisionLabel getDecision() {
		
		return decisionLabel;
		
	}
	
	public double getConfidence() {
		
		return this.confidence;
		
	}
	
	//public double getScore() {
		
		//return this.score;
		
	//}
	
	public String getPairID() {
		
		return pairId;
		
	}
	
	

}
