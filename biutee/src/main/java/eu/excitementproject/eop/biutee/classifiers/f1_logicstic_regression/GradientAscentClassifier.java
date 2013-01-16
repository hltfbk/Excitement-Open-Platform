package eu.excitementproject.eop.biutee.classifiers.f1_logicstic_regression;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.io.LearningModel;
import eu.excitementproject.eop.common.datastructures.DummySet;
import eu.excitementproject.eop.common.utilities.StringUtil;

/**
 * This linear-classifier uses gradient-ascent method to find the optimal
 * (may be local-optima) values for the parameters, for maximizing the
 * objective-function.
 * 
 * The objective-function is given by the {@link DerivativeCalculator}, given as
 * parameter to the constructor.
 * 
 * @author Asher Stern
 * @since Mar 12, 2012
 *
 */
public class GradientAscentClassifier extends AdjustingVectorLinearWithGammaClassifier implements LinearTrainableStorableClassifier
{
	public static final int NUMBER_OF_INITIALIZATIONS = 3;
	public static final double INITIAL_DELTA_STOP_CONDITION = 0.00001;
	public static final int JUMP_AAFTER_ITERATION = 5000;
	public static final double JUMP_FACTOR = 10.0;
	
	public GradientAscentClassifier(double regularizationFactor,
			double updateFactor,
			DerivativeCalculator derivativeCalculator,
			double gammaCoefficientSigmoid) throws ClassifierException
	{
		super();
		this.regularizationFactor = regularizationFactor;
		this.updateFactor = updateFactor;
		this.derivativeCalculator = derivativeCalculator;
		this.gammaCoefficientSigmoid = gammaCoefficientSigmoid;
		if (gammaCoefficientSigmoid<=0) throw new ClassifierException("gammaCoefficientSigmoid<=0");
	}

	@Override
	public void train(Vector<LabeledSample> samples) throws ClassifierException
	{
		this.samples = adjustAllSamples(samples);
//		if (logger.isDebugEnabled())
//		{
//			logger.debug("Printing normalized samples:");
//			for (LabeledSample sample : this.samples)
//			{
//				logger.debug(ClassifierUtils.printLabeledSample(sample));
//			}
//		}
		
		metaLoop();
	}



	@Override
	public void reset() throws ClassifierException
	{
		numberOfFeatures=0;
		weights=null;
		samples=null;
		initializationOfWeights=null;
		numberOfIterations = 0;
		totalSuccessRate = 0.0;
	}

	@Override
	public String descriptionOfTraining()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Gradient ascent classifier with derivation calculator of "+this.derivativeCalculator.getDescription()+"\n");
		if (this.weights != null)
		{
			sb.append("weights are:\n");
			printWeights(this.weights, sb);
			sb.append("Total success rate = "+String.format("%-4.4f",this.totalSuccessRate));
		}
		else
		{
			sb.append("Not trained!");
		}
		return sb.toString();
	}


	@Override
	public LearningModel store() throws ClassifierException
	{
		return new LearningModelForAdjustingVectorLinearWithGammaClassifier(AdjustingVectorLinearWithGammaLoadableClassifier.class.getName(),null,weights,gammaCoefficientSigmoid,descriptionOfTraining());
	}

	
	
	
	
	/**
	 * Optionally called before {@link #train(Vector)}, to force the training algorithm to
	 * avoid values smaller than <tt>minimumValue</tt> to the parameters.
	 * @param minimumValue
	 * @param notRestricted
	 */
	public void restrict(double minimumValue, Set<Integer> notRestricted)
	{
		this.doConstrain = true;
		this.constrainValueForConstrainedFeatures = minimumValue;
		if (null==notRestricted)
		{
			this.notConstrainedFeatures = new DummySet<Integer>();
		}
		else
		{
			this.notConstrainedFeatures = shiftSet(notRestricted);
			this.notConstrainedFeatures.add(0);
		}
	}

	
	
	
	
	private void metaLoop() throws ClassifierException
	{
		initialWeights();
		Vector<WeightsAndAccuracy> allLearnedWeights = new Vector<WeightsAndAccuracy>();
		for (Map<Integer,Double> initialWeight : initializationOfWeights)
		{
			this.weights = convertMapToArray(initialWeight);
			this.numberOfFeatures = this.weights.length;
			double successRate = mainLoop();
			allLearnedWeights.add(new WeightsAndAccuracy(this.weights,successRate,numberOfIterations));
		}
		
		if (logger.isDebugEnabled())
		{
			StringBuffer sb = new StringBuffer();
			sb.append("Printing all learned weights...\n");
			for (WeightsAndAccuracy oneLearnedWeight : allLearnedWeights)
			{
				printWeights(oneLearnedWeight.getWeights(), sb);
				sb.append("\nThe success rate was: ").append(String.format("%-4.4f\n", oneLearnedWeight.getAccuracy()));
				sb.append(String.format("Number of iterations: %d\n", oneLearnedWeight.getNumberOfIterations()));
				sb.append(StringUtil.generateStringOfCharacter('-', 50));
				sb.append("\n");
			}
			logger.debug(sb.toString());
		}
		
		WeightsAndAccuracy best = null;
		boolean firstLoop = true;
		for (WeightsAndAccuracy learned : allLearnedWeights)
		{
			if (firstLoop){firstLoop=false;best=learned;}
			else
			{
				if (best.getAccuracy()<learned.getAccuracy())
				{
					best = learned;
				}
			}
		}
		totalSuccessRate = best.getAccuracy();
		this.weights = best.getWeights();
	}
	
	private double mainLoop() throws ClassifierException
	{
		numberOfIterations = 0;
		double deltaStopCondition = INITIAL_DELTA_STOP_CONDITION;
		derivativeCalculator.setCurrentState(numberOfFeatures, weights, samples);
		derivativeCalculator.calculate();
		double previousValue = getCurrentSuccessRate(); //derivativeCalculator.getCurrentValue();
		updateSingleIteration(derivativeCalculator.getCurrentPartialDerivatives());
		++numberOfIterations;
		derivativeCalculator.setCurrentState(numberOfFeatures, weights, samples);
		derivativeCalculator.calculate();
		double currentValue = getCurrentSuccessRate(); // derivativeCalculator.getCurrentValue();
		while (Math.abs(currentValue - previousValue)>deltaStopCondition)
		{
			// logger.debug(String.format("%-4.4f", currentValue));
			previousValue = currentValue;
			updateSingleIteration(derivativeCalculator.getCurrentPartialDerivatives());
			++numberOfIterations;
			derivativeCalculator.setCurrentState(numberOfFeatures, weights, samples);
			derivativeCalculator.calculate();
			currentValue = getCurrentSuccessRate(); // derivativeCalculator.getCurrentValue();
			
			if (0==(numberOfIterations%JUMP_AAFTER_ITERATION))
			{
				deltaStopCondition *= JUMP_FACTOR;
				if (logger.isDebugEnabled()){logger.debug("Jumping with delta (stop-condition) to: "+String.format("%-4.10f",deltaStopCondition));}
			}
		}
		
		if (logger.isDebugEnabled()){logger.debug("Number of iterations = "+numberOfIterations);}
		return currentValue;
	}
	
	private final double getCurrentSuccessRate() throws ClassifierException
	{
		double ret = derivativeCalculator.getCurrentValue();
		if (this.regularizationFactor!=0)
		{
			ret -= regularizationFactor*normSquare();
		}
		return ret;
	}
	
	private final double normSquare()
	{
		double ret = 0;
		for (int index=0;index<this.weights.length;++index)
		{
			ret += this.weights[index]*this.weights[index];
		}
		return ret;
	}
	
	
	private void initialWeights()
	{
		initializationOfWeights = new Vector<Map<Integer,Double>>();
		double lower = -2.0;
		double upper = 2.0;
		int length = convertMapToArray(samples.iterator().next().getFeatures()).length;
		for (int index=0; index< NUMBER_OF_INITIALIZATIONS; ++index)
		{
			double current = lower+index*((upper-lower)/(NUMBER_OF_INITIALIZATIONS-1));
			double restrictedCurrent = Math.max(current, constrainValueForConstrainedFeatures);
			Map<Integer,Double> currentWeight = new LinkedHashMap<Integer, Double>();
			for (int key = 0; key < length; ++key)
			{
				boolean isRestricted = false;
				if (doConstrain){if (!notConstrainedFeatures.contains(key)){isRestricted=true;}}
				
				if (isRestricted)
				{
					currentWeight.put(key, restrictedCurrent);
				}
				else
				{
					currentWeight.put(key, current);
				}
			}
			initializationOfWeights.add(currentWeight);
		}
		
		if (logger.isDebugEnabled())
		{
			StringBuffer sb = new StringBuffer();
			sb.append("initializationOfWeights:\n");
			for (Map<Integer,Double> initializationWeightVector : initializationOfWeights)
			{
				ClassifierUtils.appendFeatureVector(initializationWeightVector,", ",sb);
				sb.append("\n");
			}
			logger.debug(sb.toString());
		}

	}
	
	@SuppressWarnings("unused")
	private void initialWeightsOld()
	{
		initializationOfWeights = new Vector<Map<Integer,Double>>();
		Map<Integer,Double> minMap = new LinkedHashMap<Integer, Double>();
		Map<Integer,Double> maxMap = new LinkedHashMap<Integer, Double>();
		for (LabeledSample sample : samples)
		{
			Map<Integer,Double> features = sample.getFeatures();
			for (Integer featureIndex : features.keySet())
			{
				if (!minMap.containsKey(featureIndex))
				{
					minMap.put(featureIndex, features.get(featureIndex));
				}
				else
				{
					Double value = minMap.get(featureIndex);
					if (value>features.get(featureIndex))
					{
						minMap.put(featureIndex, features.get(featureIndex));
					}
				}
			}
			
			for (Integer featureIndex : features.keySet())
			{
				if (!maxMap.containsKey(featureIndex))
				{
					maxMap.put(featureIndex, features.get(featureIndex));
				}
				else
				{
					Double value = maxMap.get(featureIndex);
					if (value<features.get(featureIndex))
					{
						maxMap.put(featureIndex, features.get(featureIndex));
					}
				}
			}
		}
		
		for (Integer key : minMap.keySet())
		{
			if (minMap.get(key).doubleValue()==maxMap.get(key).doubleValue())
			{
				double orig = minMap.get(key).doubleValue();
				minMap.put(key, orig-1.0);
				maxMap.put(key, orig+1.0);
			}
		}
		
		for (Integer key : minMap.keySet())
		{
			minMap.put(key,-minMap.get(key));
		}
		for (Integer key : maxMap.keySet())
		{
			maxMap.put(key,-maxMap.get(key));
		}
		
		for (int index=0;index<NUMBER_OF_INITIALIZATIONS;++index)
		{
			Map<Integer,Double> oneInit = new LinkedHashMap<Integer, Double>();
			for (Integer featureIndex : minMap.keySet())
			{
				double sub = minMap.get(featureIndex)-maxMap.get(featureIndex); // remember that now min > max
				double relative = sub/(NUMBER_OF_INITIALIZATIONS-1);
				oneInit.put(featureIndex, maxMap.get(featureIndex)+(index*relative));
			}
			initializationOfWeights.add(oneInit);
		}
		if (logger.isDebugEnabled())
		{
			StringBuffer sb = new StringBuffer();
			sb.append("initializationOfWeights:\n");
			for (Map<Integer,Double> initializationWeightVector : initializationOfWeights)
			{
				ClassifierUtils.appendFeatureVector(initializationWeightVector,", ",sb);
				sb.append("\n");
			}
			logger.debug(sb.toString());
		}
	}
	
	private void updateSingleIteration(Map<Integer,Double> derivatives)
	{
		double[] newWeights = new double[weights.length];
		for (int index = 0;index<numberOfFeatures;++index)
		{
			double weight = weights[index];
			double regularizedDerivativeValue = derivatives.get(index) - 2*regularizationFactor*Math.abs(weight);
			double newWeight = weight+regularizedDerivativeValue*updateFactor;
			if (this.doConstrain)
			{
				if (!notConstrainedFeatures.contains(index))
				{
					newWeight = Math.max(newWeight, constrainValueForConstrainedFeatures);
				}
				else
				{}
			}
			newWeights[index] = newWeight;
		}
		this.weights = newWeights;
	}
	
	private Vector<LabeledSample> adjustAllSamples(Vector<LabeledSample> samples) throws ClassifierException
	{
		Vector<LabeledSample> ret = new Vector<LabeledSample>();
		for (LabeledSample sample : samples)
		{
			ret.add(adjustSample(sample));
		}
		
		return ret;
	}
	
	@Override
	protected void printWeights(Map<Integer,Double> weights, StringBuffer sb)
	{
		for (Integer key : weights.keySet())
		{
			String name = null; 
			if (featureNames!=null)
				name = featureNames.get(key);
			if (null==name)
			{
				name = String.valueOf(key.intValue());
				if (featureNames!=null)
				{
					name = "? "+name;
				}
			}
			sb.append(name);
			if (this.doConstrain){ if (!this.notConstrainedFeatures.contains(key))
			{
				sb.append(" (constrained)");
			}}
			sb.append(" : ");
			sb.append(String.format("%-4.4f", weights.get(key)));
			sb.append("\n");
		}

	}

	
	

	
	private static class WeightsAndAccuracy
	{
		
		public WeightsAndAccuracy(double[] weights_, double accuracy_, int numberOfIterations_)
		{
			super();
			this.weights_ = weights_;
			this.accuracy_ = accuracy_;
			this.numberOfIterations_ = numberOfIterations_;
		}
		
		
		public double[] getWeights()
		{
			return weights_;
		}
		public double getAccuracy()
		{
			return accuracy_;
		}
		public int getNumberOfIterations()
		{
			return numberOfIterations_;
		}


		private double[] weights_;
		private double accuracy_;
		private int numberOfIterations_;
	}
	
	
	private int numberOfFeatures;
	private List<LabeledSample> samples;
	
	private double regularizationFactor;
	private boolean doConstrain = false;
	private Set<Integer> notConstrainedFeatures = new DummySet<Integer>();
	private double constrainValueForConstrainedFeatures = 0;
	private double updateFactor; // learning rate
	
	
	private Vector<Map<Integer,Double>> initializationOfWeights;
	private int numberOfIterations = 0;
	private double totalSuccessRate = 0.0;
	
	private DerivativeCalculator derivativeCalculator;
	
	
	
	private static final Logger logger = Logger.getLogger(GradientAscentClassifier.class);
}
