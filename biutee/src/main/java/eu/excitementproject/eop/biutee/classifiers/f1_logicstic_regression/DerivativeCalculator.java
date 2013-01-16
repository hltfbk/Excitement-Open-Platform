package eu.excitementproject.eop.biutee.classifiers.f1_logicstic_regression;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;


/**
 * Calculates the objective function, and its partial derivations, for
 * the given weight-vector (parameters), for the given data-set (set of samples).
 * 
 * Such calculation is required for classifiers that try to find the optimal
 * values for the parameters for maximizing the objective-function (e.g. Gradient Ascent, (L)BFGS, etc.).
 * 
 * @see GradientAscentClassifier
 * 
 * 
 * @author Asher Stern
 * @since Mar 12, 2012
 *
 */
public interface DerivativeCalculator
{
	public void setCurrentState(int numberOfFeatures,double[] weights,List<LabeledSample> samples) throws ClassifierException;
	
	public void calculate() throws ClassifierException;
	
	public double getCurrentValue() throws ClassifierException;
	
	public Map<Integer,Double> getCurrentPartialDerivatives() throws ClassifierException;
	
	public String getDescription();
}
