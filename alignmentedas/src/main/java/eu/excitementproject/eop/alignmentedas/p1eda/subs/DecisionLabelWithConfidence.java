package eu.excitementproject.eop.alignmentedas.p1eda.subs;

import eu.excitementproject.eop.common.DecisionLabel;

/**
 * Classification result for single TE instance, on EDAClassifierAbstraction 
 * 
 * @author Tae-Gil Noh 
 */
public class DecisionLabelWithConfidence {

	public DecisionLabelWithConfidence(DecisionLabel label, double confidence) {
		this.label = label;
		this.confidence = confidence; 
	}	
	
	public DecisionLabel getLabel()
	{
		return label; 
	}
	
	public double getConfidence()
	{
		return confidence; 
	}

	private final DecisionLabel label; 
	private final double confidence; 
}
