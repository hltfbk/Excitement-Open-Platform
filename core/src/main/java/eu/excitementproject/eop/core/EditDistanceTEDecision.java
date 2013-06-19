package eu.excitementproject.eop.core;

import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.DecisionLabel;

/**
 * The <code>EditDistanceTEDecision</code> class implements the
 * <code>TEDecision</code> interface.
 * 
 * It supports two kinds of constructors, with <code>confidence</code> value or
 * without.
 * 
 * @author Roberto Zanoli
 */
public class EditDistanceTEDecision implements TEDecision {

	private DecisionLabel decisionLabel;
	private String pairId;
	private double confidence;
	
	public EditDistanceTEDecision (DecisionLabel decisionLabel, String pairId) {
		
		this.decisionLabel = decisionLabel;
		this.pairId = pairId;
		this.confidence = TEDecision.CONFIDENCE_NOT_AVAILABLE;
		
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
