package eu.excitementproject.eop.core;

public class EditDistanceTEDecision implements IEditDistanceTEDecision {

	private DecisionLabel decisionLabel = null;
	private String pairId = null;
	
	public EditDistanceTEDecision (DecisionLabel decisionLabel, String pairId) {
		
		this.decisionLabel = decisionLabel;
		this.pairId = pairId;
		
	}
	
	public DecisionLabel getDecision() {
		
		return decisionLabel;
		
	}
	
	public double getConfidence() {
		
		return CONFIDENCE_NOT_AVAILABLE;
		
	}
	
	public String getPairID() {
		
		return pairId;
		
	}
	
	

}
