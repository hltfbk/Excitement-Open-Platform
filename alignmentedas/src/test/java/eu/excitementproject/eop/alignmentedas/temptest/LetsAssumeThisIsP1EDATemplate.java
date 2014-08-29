package eu.excitementproject.eop.alignmentedas.temptest;

import java.util.List;

import eu.excitementproject.eop.alignmentedas.p1eda.subs.ParameterValue;

public class LetsAssumeThisIsP1EDATemplate {

	public LetsAssumeThisIsP1EDATemplate() {
		
		someValue = 33; 
		ParameterValue p1 = new ParameterValue(3.0); 
		ParameterValue p2 = new ParameterValue(2.0); 
		ParameterValue p3 = new ParameterValue(1.0); 
	}

	
	protected int someValue; 
	protected List<ParameterValue> paramList; 
}
