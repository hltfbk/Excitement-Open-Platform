package eu.excitementproject.eop.common;

import eu.excitementproject.eop.common.DecisionLabel;

/**
 * This interface represents the return value for EDA process() results.
 * It only holds the basic information, and EDA implementers are extend
 * to extend it to hold additional information for their EDA. 
 *  
 * @author Gil
 */
public interface TEDecision {
	
	// constants 
	public static final double CONFIDENCE_NOT_AVAILABLE = -1; 
	
	/**
	 * this method returns an object of type DecisionLabel as the entailment decision.
	 */
	public DecisionLabel getDecision(); 

	
	/**
	 * this method returns the associated confidence value for the entailment 
	 * decision. The range is [0,1], and 1 means full confidence. If the value 
	 * is not meaningful for the EDA, it should return a constant number 
	 * CONFIDENCE_NOT_AVAILABLE, which is defined in the interface as a constant.
    */
	public double getConfidence();
	
	/**
	 * this method returns the entailment.Pair id as described in the CAS.
	 */
	public String getPairID(); 
}

