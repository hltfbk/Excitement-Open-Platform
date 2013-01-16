package eu.excitementproject.eop.biutee.classifiers.linearimplementations;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMapWrapper;

/**
 * 
 * @author Asher Stern
 * @since Dec 23, 2012
 *
 */
public class LinearClassifierWithLeadingConstant implements LinearClassifier
{
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.classifiers.Classifier#classify(java.util.Map)
	 */
	@Override
	public double classify(Map<Integer, Double> featureVector) throws ClassifierException
	{
		if (!trainedOrModelLoaded) throw new ClassifierException("not trained");
		featureVector = addConstantFeature(featureVector);
		
		return classifierClassify(featureVector);
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.classifiers.Classifier#classifyBoolean(java.util.Map)
	 */
	@Override
	public boolean classifyBoolean(Map<Integer, Double> featureVector) throws ClassifierException
	{
		double classificationValue = classify(featureVector);
		return (classificationValue>=0.5);
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
			ret.append(LinearClassifierWithLeadingConstant.class.getSimpleName()).append("\n");
			ret.append("\nFeatures:\n");
			ret.append(getFeatures());
			ret.append("\n");
		}
		catch(ClassifierException e)
		{
			ret.append("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\nAn Error occurred in descriptionOfTraining!");
		}
		return ret.toString();
	}
	
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.classifiers.Classifier#getNormalizedSample(ac.biu.nlp.nlp.engineml.classifiers.LabeledSample)
	 */
	@Override
	public LabeledSample getNormalizedSample(LabeledSample sample) throws ClassifierException
	{
		return sample;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.classifiers.Classifier#setFeaturesNames(java.util.Map)
	 */
	@Override
	public void setFeaturesNames(Map<Integer, String> featureNames)
	{
		this.featureNames = featureNames;
		
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.classifiers.Classifier#getFeatureNames()
	 */
	@Override
	public Map<Integer, String> getFeatureNames() throws ClassifierException
	{
		return this.featureNames;
	}


	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.classifiers.LinearClassifier#getWeights()
	 */
	@Override
	public ImmutableMap<Integer, Double> getWeights() throws ClassifierException
	{
		if (!trainedOrModelLoaded) throw new ClassifierException("Not trained");
		if (null==weightsForGetWeightsMethod)
		{
			synchronized(this)
			{
				if (null==weightsForGetWeightsMethod)
				{
					LinkedHashMap<Integer, Double> weightsForGetWeightsMethodMutable = new LinkedHashMap<Integer, Double>();
					for (Integer index : parameters.keySet())
					{
						if (index!=0)
						{
							weightsForGetWeightsMethodMutable.put(index-1, parameters.get(index));
						}
					}
					weightsForGetWeightsMethod = new ImmutableMapWrapper<Integer, Double>(weightsForGetWeightsMethodMutable);
				}
			}
		}
		return weightsForGetWeightsMethod;
	}


	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.classifiers.LinearClassifier#getThreshold()
	 */
	@Override
	public double getThreshold() throws ClassifierException
	{
		if (!trainedOrModelLoaded) throw new ClassifierException("Not trained");
		return parameters.get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.classifiers.LinearClassifier#getProduct(java.util.Map)
	 */
	@Override
	public double getProduct(Map<Integer,Double> featureVector) throws ClassifierException
	{
		featureVector = addConstantFeature(featureVector);
		double sum = 0;
		for (Integer featureIndex : parameters.keySet())
		{
			if (featureIndex!=0) // does not include the threshold
			{
				sum += parameters.get(featureIndex)*featureVector.get(featureIndex);
			}
		}
		return sum;
	}

	
	
	
	
	public String getFeatures() throws ClassifierException
	{
		if (!trainedOrModelLoaded) throw new ClassifierException("not trained");
		StringBuffer sb = new StringBuffer();
		sb.append("C:");
		sb.append(String.format("%4.4f", parameters.get(0)));
		
		List<Integer> keys = new ArrayList<Integer>(parameters.keySet().size());
		keys.addAll(parameters.keySet());
		Collections.sort(keys);
		for (Integer index : keys)
		{
			if (index!=0)
			{
				
				boolean featureNameAdded = false;
				if (featureNames!=null){ if (featureNames.get(index-1)!=null)
				{
					sb.append("\n");
					sb.append(featureNames.get(index-1));
					featureNameAdded=true;
				}}
				if (!featureNameAdded)
				{
					sb.append("\n");
					sb.append(index-1);
				}
				sb.append(": ");
				sb.append(String.format("%4.4f", parameters.get(index)));
			}
		}
		return sb.toString();
	}
	
	
	
	protected Map<Integer,Double> addConstantFeature(Map<Integer,Double> features) throws ClassifierException
	{
		Map<Integer,Double> newFeatureVector = new LinkedHashMap<Integer, Double>();
		newFeatureVector.put(0, 1.0);

		for (Integer featureIndex : features.keySet())
		{
			Double featureValue = features.get(featureIndex);
			Integer artificialFeatureIndex = featureIndex+1;
			
			if (artificialFeatureIndex.intValue()==0)throw new ClassifierException("bad features indices.");
			newFeatureVector.put(artificialFeatureIndex,featureValue);
		}
		
		return newFeatureVector; 
	}

	
	protected double classifierClassify(Map<Integer, Double> featureVector)
	{
		double sum = 0;
		for (Integer featureIndex : parameters.keySet())
		{
			sum += parameters.get(featureIndex)*featureVector.get(featureIndex);
		}
		return ClassifierUtils.sigmoid(sum);
	}

	
	
	
	protected Map<Integer,Double> parameters;
	protected Map<Integer, String> featureNames = null;
	protected ImmutableMap<Integer, Double> weightsForGetWeightsMethod = null;

	protected boolean trainedOrModelLoaded = false;
}
