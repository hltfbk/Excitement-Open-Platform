package eu.excitementproject.eop.biutee.classifiers.linearimplementations;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.TrainableClassifier;




/**
 * <B>------------ NOT USED ------------</B>
 * 
 * 
 * 
 * @author Asher Stern
 * @since Mar 3, 2011
 *
 */
public class ParametersExpanderClassifier implements TrainableClassifier
{
	public ParametersExpanderClassifier(Set<Set<Integer>> expandScheme,
			TrainableClassifier realClassifier) throws ClassifierException
	{
		super();
		this.expandScheme = expandScheme;
		this.realClassifier = realClassifier;
	}

	public void train(Vector<LabeledSample> samples) throws ClassifierException
	{
		realClassifier.train(expandSamples(samples));
	}

	public double classify(Map<Integer, Double> featureVector) throws ClassifierException
	{
		return realClassifier.classify(expandFeatureVector(featureVector));
	}

	public boolean classifyBoolean(Map<Integer, Double> featureVector) throws ClassifierException
	{
		return realClassifier.classifyBoolean(expandFeatureVector(featureVector));
	}

	public void reset() throws ClassifierException
	{
		realClassifier.reset();
	}

	public String descriptionOfTraining()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("ParametersExpanderClassifier with expanding parameters:\n");
		boolean firstIteration = true;
		for (Integer expandFeature : allExpandings())
		{
			if (firstIteration)
			{
				firstIteration=false;
			}
			else
			{
				sb.append(", ");
			}
			sb.append(expandFeature);
		}
		sb.append("\nWrapping classifier:\n");
		sb.append(realClassifier.descriptionOfTraining());
		 
		return sb.toString();
	}

	public void setFeaturesNames(Map<Integer, String> featureNames) throws ClassifierException
	{
		realClassifier.setFeaturesNames(expandFeatureNames(featureNames));
	}
	
	public Map<Integer, String> getFeatureNames() throws ClassifierException
	{
		return realClassifier.getFeatureNames();
	}

	
	public TrainableClassifier getRealClassifier()
	{
		return this.realClassifier;
	}
	
	public LabeledSample getNormalizedSample(LabeledSample sample) throws ClassifierException
	{
		return realClassifier.getNormalizedSample(expandLabledSample(sample));
	}

	
	public Vector<LabeledSample> expandSamples(Vector<LabeledSample> samples) throws ClassifierException
	{
		Vector<LabeledSample> ret = new Vector<LabeledSample>();
		for (LabeledSample sample : samples)
		{
			ret.add(expandLabledSample(sample));
		}
		return ret;
	}
	
	public LabeledSample expandLabledSample(LabeledSample sample) throws ClassifierException
	{
		LabeledSample ret = new LabeledSample(expandFeatureVector(sample.getFeatures()), sample.getLabel());
		return ret;
	}
	
	
	public Map<Integer,Double> expandFeatureVector(Map<Integer,Double>featureVector) throws ClassifierException
	{
		if (!validFeatureVector(featureVector)) throw new ClassifierException("invalid feature vector");
		Map<Integer,Double> cleanedFeatureVector = removeExpandingFeatures(featureVector);
		for (Set<Integer> set : expandScheme)
		{
			Map<Integer,Double> newFeatureIndex = new LinkedHashMap<Integer, Double>();
			int maxKey = maxKey(cleanedFeatureVector);
			int expandingIndex=0;
			for (Integer expandingFeatureIndex : set)
			{
				for (Integer key : cleanedFeatureVector.keySet())
				{
					newFeatureIndex.put((maxKey+1)*expandingIndex+key, cleanedFeatureVector.get(key)*featureVector.get(expandingFeatureIndex));
				}
				
				
				++expandingIndex;
			}
			cleanedFeatureVector = newFeatureIndex;
		}
		return cleanedFeatureVector;
	}
	
	
	public Map<Integer,String> expandFeatureNames(Map<Integer,String>featureVector) throws ClassifierException
	{
		if (!validFeatureVector(featureVector)) throw new ClassifierException("invalid feature vector");
		Map<Integer,String> cleanedFeatureVector = removeExpandingFeatures(featureVector);
		for (Set<Integer> set : expandScheme)
		{
			Map<Integer,String> newFeatureIndex = new LinkedHashMap<Integer, String>();
			int maxKey = maxKey(cleanedFeatureVector);
			int expandingIndex=0;
			for (Integer expandingFeatureIndex : set)
			{
				for (Integer key : cleanedFeatureVector.keySet())
				{
					newFeatureIndex.put((maxKey+1)*expandingIndex+key, featureVector.get(expandingFeatureIndex)+"-"+cleanedFeatureVector.get(key));
				}
				
				
				++expandingIndex;
			}
			cleanedFeatureVector = newFeatureIndex;
		}
		return cleanedFeatureVector;
	}
	
	protected int maxKey(Map<Integer,?> map)
	{
		int ret = map.keySet().iterator().next();
		for (Integer key : map.keySet())
		{
			if (ret<key)ret=key;
		}
		return ret;
	}
	
	protected boolean validFeatureVector(Map<Integer,?> featureVector)
	{
		boolean ret = true;
		for (Integer key : featureVector.keySet())
		{
			if (key.intValue()<=0)
			{
				ret=false;
				break;
			}
		}
		return ret;
	}
	
	protected Set<Integer> allExpandings()
	{
		Set<Integer> ret = new LinkedHashSet<Integer>();
		for (Set<Integer> set : expandScheme)
		{
			for (Integer i : set)
			{
				ret.add(i);
			}
		}
		return ret;
	}
	
	protected <T> Map<Integer,T> removeExpandingFeatures(Map<Integer,T> featureVector)
	{
		Set<Integer> toRemove = allExpandings();
		Map<Integer,T> ret = new LinkedHashMap<Integer, T>();
		for (Integer key : featureVector.keySet())
		{
			if (!toRemove.contains(key))
			{
				ret.put(key,featureVector.get(key));
			}
		}
		return ret;
	}

	protected Set<Set<Integer>> expandScheme;
	protected TrainableClassifier realClassifier;
}
