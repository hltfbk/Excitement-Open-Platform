package eu.excitementproject.eop.biutee.classifiers.linearimplementations;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.io.LearningModel;
import eu.excitementproject.eop.biutee.classifiers.io.StorableClassifier;
import eu.excitementproject.eop.common.datastructures.DummySet;
import eu.excitementproject.eop.common.utilities.Utils;


/**
 * Implementation of logistic regression classifier <B>Caution!!! I am not sure about the regularization factor formula.</B>
 * This logistic regression classifier <B>is not stochastic</B>.
 * <P>
 * The features index should not contain negative indices.
 * An additional constant feature is appended to each sample automatically in the
 * training process and in each classification ( {@link #classify(Map)} ).
 * <P>
 * I am not sure about the formula of regularization factor. As long as the regularization
 * factor is 0, it should work fine. I think the regularization factor formula is fine, however.
 * 
 * 
 * @author Asher Stern
 * @since Jan 6, 2011
 *
 */
public class LogisticRegressionClassifier extends LinearClassifierWithLeadingConstant implements LinearTrainableStorableClassifier, StorableClassifier
{
	public static final double DEFAULT_LEARNING_RATE = 0.05;
	
	public LogisticRegressionClassifier()
	{
	}
	
	
	public LogisticRegressionClassifier(double learningRate, double regularizationFactor)
	{
		this.learningRate = learningRate;
		this.regularizationFactor = regularizationFactor;
	}

	/**
	 * Instead of training - this method makes the classifier as if it was trained.
	 * The classifier can be used after calling this method, without training. This method
	 * puts the appropriate values to the parameters - artificially.
	 * <P>
	 * Note the the "featuresParameters" argument should identical to maps that represent
	 * samples - i.e. should not include the "constant feature".
	 * 
	 * @param constantFeatureParameter
	 * @param featuresParameters
	 */
	public void setReasonableGuess(double constantFeatureParameter, Map<Integer, Double> featuresParameters) throws ClassifierException
	{
		Map<Integer, Double> theParameters = addConstantFeature(featuresParameters);
		theParameters.put(0,constantFeatureParameter);
		this.parameters = theParameters;
		trainedOrModelLoaded = true;
	}
	
	/**
	 * Optionally called before {@link #train(Vector)}, to force the training algorithm to
	 * avoid values smaller than <tt>minimumValue</tt> to the parameters.
	 * @param minimumValue
	 * @param notRestricted
	 */
	public void restrict(double minimumValue, Set<Integer> notRestricted)
	{
		this.restricted = true;
		this.restrictionMinimumValue = minimumValue;
		if (null==notRestricted)
		{
			this.notRestricted = new DummySet<Integer>();
		}
		else
		{
			this.notRestricted = notRestricted;
		}
	}

	


	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.classifiers.Classifier#train(java.util.Vector)
	 */
	public void train(Vector<LabeledSample> samples) throws ClassifierException
	{
		Vector<LabeledSample> originalSamples = samples;
		
		Vector<LabeledSample> samplesWithConstantFeature = new Vector<LabeledSample>();
		for (LabeledSample sample : samples)
		{
			samplesWithConstantFeature.add(addConstantFeature(sample));
		}
		samples = samplesWithConstantFeature;
		
		Map<Integer,Double> initialParameters = generateInitialParameters(samples);
		mainLoop(initialParameters, samples);
		
		trainedOrModelLoaded = true;
		
		trainingSetAccuracy = ClassifierUtils.accuracyOf(this, originalSamples);
	}




	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.classifiers.Classifier#reset()
	 */
	public void reset() throws ClassifierException
	{
		this.parameters = null;
		this.trainedOrModelLoaded = false;
		this.weightsForGetWeightsMethod = null;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.classifiers.Classifier#descriptionOfTraining()
	 */
	@Override
	public String descriptionOfTraining()
	{
		StringBuffer ret = new StringBuffer();
		try
		{
			ret.append("Logistic regression.\n");
			ret.append("Parameters: regularization = ");
			ret.append(this.regularizationFactor);
			ret.append(" learning rate = ");
			ret.append(this.learningRate);
			ret.append("\nNumber of iterations = ");
			ret.append(this.numberOfIterations);
			ret.append("\nFeatures:\n");
			ret.append(getFeatures());
			ret.append("\n");
			if (this.trainingSetAccuracy==null)
			{
				ret.append("Accuracy on training set was not computed.");
			}
			else
			{
				ret.append(String.format("Accuracy on training set was: %-4.4f", trainingSetAccuracy.doubleValue()));
			}
		}
		catch(ClassifierException e)
		{
			ret.append("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\nAn Error occurred in descriptionOfTraining!");
		}
		return ret.toString();
	}
	
	
	@Override
	public LearningModel store() throws ClassifierException
	{
		if (!trainedOrModelLoaded) throw new ClassifierException("Not trained");
		return new LearningModelForLinearClassifierWithLeadingConstant(
				this.parameters,
				LinearClassifierWithLeadingConstantLoadable.class.getName(),
				null,this.descriptionOfTraining());
	}

	
	public int getNumberOfIterations() throws ClassifierException
	{
		if (!trainedOrModelLoaded) throw new ClassifierException("not trained");
		return this.numberOfIterations;
	}
	
	
	/**
	 * For each parameter that has negative value - this method sets its value to zero.<BR>
	 * For those parameters that are included in <code>doNotChagne</code> - the method
	 * does not change the value (i.e. although their value is negative, it is not changed).<BR>
	 * The function does not change the value of the constant parameter.
	 *  
	 * @param doNotChange Set of parameters that will not be changed although their values are negative.
	 */
	public void setToZeroNegativeParametersBut(Set<Integer> doNotChange) throws ClassifierException
	{
		Map<Integer,Double> newParameters = new LinkedHashMap<Integer, Double>();
		for (Integer parameterIndex : parameters.keySet())
		{
			if (parameterIndex!=0) // don't touch the constant parameter
			{
				int parameterIndexAsForUser = parameterIndex-1;
				if (!doNotChange.contains(parameterIndexAsForUser))
				{
					double parameterValue = this.parameters.get(parameterIndex);
					if (parameterValue<0)
						parameterValue=0;
					newParameters.put(parameterIndex,parameterValue);
				}
				else
				{
					newParameters.put(parameterIndex, this.parameters.get(parameterIndex));
				}
			}
			else
			{
				newParameters.put(parameterIndex, this.parameters.get(parameterIndex));
			}
		}
		
		this.parameters = newParameters;
	}
	
	public void increaseAllButConstantByBut(double by, Set<Integer> doNotChange) throws ClassifierException
	{
//		if (by<=1.0)throw new ClassifierException("increaseAllButConstantByBut work only for >0");
//		
//		double minimumValueOfPositives = 0;
//		boolean firstIteration = true;
//		for (Integer featureIndex : parameters.keySet())
//		{
//			if ( (featureIndex.intValue()!=0) && (!doNotChange.contains(featureIndex)) && (parameters.get(featureIndex)>0) )
//			{
//				if (firstIteration)
//				{
//					firstIteration=false;
//					minimumValueOfPositives = parameters.get(featureIndex);
//				}
//				else
//				{
//					double currentParameter = parameters.get(featureIndex);
//					if (currentParameter<minimumValueOfPositives)
//						minimumValueOfPositives = currentParameter;
//				}
//				
//			}
//		}
//		
//		if (minimumValueOfPositives<=0.0)throw new ClassifierException("bug");
//		
//		Map<Integer,Double> newParameters = new LinkedHashMap<Integer, Double>();
//
//		// For all parameters. This actually changes nothing:
//		// w*x<>0 iff c*w*x<>0 for any positive c
//		for (Integer featureIndex : parameters.keySet())
//		{
//			Double value = parameters.get(featureIndex);
//			newParameters.put(featureIndex,value*by);
//		}
//
//		// Those that were 0 (and they are 0 in the original parameters as well as the newParameters),
//		// will be assigned minimumValueOfPositives
//		for (Integer featureIndex : parameters.keySet())
//		{
//			if ( (featureIndex.intValue()!=0) && (!doNotChange.contains(featureIndex)) && (parameters.get(featureIndex)==0.0) )
//			{
//				newParameters.put(featureIndex,minimumValueOfPositives);
//			}
//		}
		
		Map<Integer,Double> newParameters = new LinkedHashMap<Integer, Double>();
		for (Integer featureIndex : this.parameters.keySet())
		{
			Double value = this.parameters.get(featureIndex);
			if ( (featureIndex.intValue()!=0) && (!doNotChange.contains(featureIndex)) )
			{
				value+= by;
			}
			newParameters.put(featureIndex,value);
		}
		
		
		this.parameters = newParameters;
	}
	



	////////////////////// PROTECTED ///////////////////////////////
	
	protected static final double CONVERGENCE_VALUE = 0.01;
	protected static final int CONVERGENCE_RELAX_ITERATIONS = 1000;
	protected static final double CONVERGENCE_RELAX_MULTIPLIER = 10;
	
	
	
	protected LabeledSample addConstantFeature(LabeledSample sample) throws ClassifierException
	{
		Map<Integer,Double> newFeatureVector = addConstantFeature(sample.getFeatures());
		return new LabeledSample(newFeatureVector, sample.getLabel());
	}
	

	
	protected Map<Integer,Double> generateInitialParameters(Vector<LabeledSample> samples)
	{
		Map<Integer,Double> ret = new LinkedHashMap<Integer, Double>();
		Set<Integer> indices = new LinkedHashSet<Integer>();
		for (LabeledSample sample : samples)
		{
			indices.addAll(sample.getFeatures().keySet());
		}
		
		Integer[] indicesArray = Utils.collectionToArray(indices, new Integer[0]);
		Arrays.sort(indicesArray);
		
		double initialParameterValue = 0.0;
		if (this.restricted)
		{
			initialParameterValue = Math.max(0.0, this.restrictionMinimumValue);
		}
		for (Integer index : indicesArray)
		{
			ret.put(index,initialParameterValue);
		}
		return ret;
	}
	
	

	
	
	/**
	 * repeat until convergence...
	 * 
	 * @param initialParameters
	 * @param samples
	 */
	protected void mainLoop(Map<Integer,Double> initialParameters,Vector<LabeledSample> samples)
	{
		parameters = initialParameters;
		Map<Integer,Double> oldParameters = null;
		numberOfIterations = 0;
		do
		{
			oldParameters = parameters;
			parametersUpdate(samples);
			numberOfIterations++;
		}while(!convergence(oldParameters,numberOfIterations));
	}
	
	/**
	 * true if converged
	 * @param oldParameters
	 * @return
	 */
	protected boolean convergence(Map<Integer,Double> oldParameters, int numberOfIterations)
	{
		double convergenceThreshold = CONVERGENCE_VALUE;
		for (int mulIndex=0;mulIndex<(numberOfIterations/CONVERGENCE_RELAX_ITERATIONS);++mulIndex)
		{
			convergenceThreshold*=CONVERGENCE_RELAX_MULTIPLIER;
		}
		boolean notConverged = false;
		for (Integer parameterIndex : parameters.keySet())
		{
			if (Math.abs(parameters.get(parameterIndex)-oldParameters.get(parameterIndex))>convergenceThreshold)
				notConverged = true;
		}
		return (!notConverged);
		
	}
	
	/**
	 * theta_j = theta_j + alpha*SIGMA...
	 * @param samples
	 */
	protected void parametersUpdate(Vector<LabeledSample> samples)
	{
		Map<Integer,Double> newParameters = new LinkedHashMap<Integer, Double>();
		for (Integer featureIndex : parameters.keySet()) // featureIndex is "j"
		{
			double newParameterValue = 0;
			if (featureIndex==0)
			{
				newParameterValue = parameters.get(featureIndex) + learningRate*oneFeatureSum(samples, featureIndex);
			}
			else
			{
				newParameterValue = parameters.get(featureIndex) + learningRate*(oneFeatureSum(samples, featureIndex)-regularizationFactor*parameters.get(featureIndex));
				newParameterValue = restrictedParameterValue(featureIndex, newParameterValue);
			}
			
			
			newParameters.put(featureIndex, newParameterValue);
			
//			newParameters.put(featureIndex,
//					parameters.get(featureIndex) + learningRate*(oneFeatureSum(samples, featureIndex)-regularizationFactor*parameters.get(featureIndex))
//			);

		}
		parameters = newParameters;
	}

	/**
	 * SIGMA_i=1^m (y^i - h(x^i))x_j^i
	 * @param samples X
	 * @param featureIndex j
	 * @return
	 */
	protected double oneFeatureSum(Vector<LabeledSample> samples,Integer featureIndex)
	{
		double sum = 0;
		for (LabeledSample sample : samples)
		{
			double realClassification = 0;
			if (sample.getLabel()==true)
				realClassification = 1;
			
			double classifierClassification = classifierClassify(sample.getFeatures());
			
			double featureValue = sample.getFeatures().get(featureIndex);
			sum+=(realClassification-classifierClassification)*featureValue;
		}
		return sum;
	}
	
	protected double restrictedParameterValue(Integer featureIndex, double parameterValue)
	{
		if (!this.restricted)
		{
			return parameterValue;
		}
		else
		{
			if (this.notRestricted.contains(featureIndex))
			{
				return parameterValue;
			}
			else
			{
				return Math.max(this.restrictionMinimumValue, parameterValue);
			}
		}
	}
	

	
	
	

	
	
	
	
	protected double learningRate = DEFAULT_LEARNING_RATE;
	protected double regularizationFactor = 0;
	protected int numberOfIterations = 0;

	/**
	 * If <tt>true</tt>, then the immediately following the training process
	 * (in the {@link #train(Vector)} method), the accuracy on training-set will be calculated,
	 * and when {@link #descriptionOfTraining()} will be called, the description will include that
	 * accuracy. 
	 */
	protected boolean descriptionIncludesTrainingSetAccuracy = true;
	protected Double trainingSetAccuracy = null;
	
	protected boolean restricted = false;
	protected double restrictionMinimumValue = 0;
	protected Set<Integer> notRestricted = null;
}
