package eu.excitementproject.eop.biutee.classifiers.f1_logicstic_regression;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;


/**
 * Implementation of {@link DerivativeCalculator} for maximizing the F\alpha
 * (F1, for example, in which \alpha=0.5) measure
 * In other words: the objective function is F\alpha.
 * 
 * @author Asher Stern
 * @since Mar 12, 2012
 *
 */
public class F_alpha_DerivativeCalculator implements DerivativeCalculator
{
	public F_alpha_DerivativeCalculator(double alg_gamma_sigmoid_coefficient)
	{
		super();
		this.alg_gamma_sigmoid_coefficient = alg_gamma_sigmoid_coefficient;
	}
	
	public F_alpha_DerivativeCalculator(double alpha, double alg_gamma_sigmoid_coefficient)
	{
		super();
		this.alg_gamma_sigmoid_coefficient = alg_gamma_sigmoid_coefficient;
		this.alpha = alpha;
	}

	@Override
	public void setCurrentState(int numberOfFeatures, double[] weights,
			List<LabeledSample> samples) throws ClassifierException
	{
		this.numberOfFeatures = numberOfFeatures;
		this.weights = weights;
		this.samples = samples;
		
		valuesSet = true;
	}

	@Override
	public void calculate() throws ClassifierException
	{
		if (!valuesSet) throw new ClassifierException("values not set");
		
		F_and_dF_calculator calculator = new F_and_dF_calculator(numberOfFeatures,weights,samples,alpha,alg_gamma_sigmoid_coefficient);
		calculator.calculate();
		this.currentValue = calculator.get_F();
		this.partialDerivations = calculator.get_dF();
		
		calculated = true;
		valuesSet = false;
	}

	@Override
	public double getCurrentValue() throws ClassifierException
	{
		if (!calculated) throw new ClassifierException("not calculated");
		return this.currentValue;
	}

	@Override
	public Map<Integer, Double> getCurrentPartialDerivatives()
			throws ClassifierException
	{
		if (!calculated) throw new ClassifierException("not calculated");
		return this.partialDerivations;
	}
	
	@Override
	public String getDescription()
	{
		return F_alpha_DerivativeCalculator.class.getSimpleName()+" alpha = "+String.format("%-4.4f", alpha);
	}

	
	
	 
	private double alpha = 0.5;
	private double alg_gamma_sigmoid_coefficient;

	private int numberOfFeatures;
	private double[] weights;
	private List<LabeledSample> samples;
	
	private double currentValue;
	private Map<Integer, Double> partialDerivations;
	
	private boolean valuesSet = false;
	private boolean calculated = false;
}
