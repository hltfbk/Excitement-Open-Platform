package eu.excitementproject.eop.alignmentedas.p1eda.subs;

import java.util.Vector;

import eu.excitementproject.eop.common.DecisionLabel;

public class LabeledInstance {

	public LabeledInstance(DecisionLabel goldLabel, Vector<FeatureValue> featureVector) {
		
		this.label = goldLabel; 
		this.featureVector = featureVector; 
	}
	
	public DecisionLabel getLabel()
	{
		return this.label; 
	}
	
	public Vector<FeatureValue> getFeatureVector()
	{
		return this.featureVector; 
	}

	private final DecisionLabel label; 
	private final Vector<FeatureValue> featureVector; 
}
