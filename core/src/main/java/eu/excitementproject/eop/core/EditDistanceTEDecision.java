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
 * 
 */
public class EditDistanceTEDecision implements TEDecision {

	/**
	 * the decision label
	 */
	private DecisionLabel decisionLabel;
	
	/**
	 * the ID of the T-H pair
	 */
	private String pairId;
	
	/**
	 * the confidence value
	 */
	private double confidence;
	
	
	/**
	 * the constructor
	 * @param decisionLabel the decision label
	 * @param pairId the ID of the t-h pair
	 */
	public EditDistanceTEDecision (DecisionLabel decisionLabel, String pairId) {
		
		this.decisionLabel = decisionLabel;
		this.pairId = pairId;
		this.confidence = TEDecision.CONFIDENCE_NOT_AVAILABLE;
		
	}
	
	
	/**
	 * the constructor
	 * @param decisionLabel the decision label
	 * @param confidence the confidence value
	 * @param pairId the ID of the t-h pair
	 */
	public EditDistanceTEDecision (DecisionLabel decisionLabel, String pairId, double confidence) {
		
		this.decisionLabel = decisionLabel;
		this.pairId = pairId;
		this.confidence = confidence;
		
	}
	
	
	@Override
	public DecisionLabel getDecision() {
		
		return decisionLabel;
		
	}
	
	
	@Override
	public double getConfidence() {
		
		return this.confidence;
		
	}
	
	
	@Override
	public String getPairID() {
		
		return pairId;
		
	}
	

}
