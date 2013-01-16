package eu.excitementproject.eop.biutee.classifiers.scaling;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.TrainableClassifier;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.common.datastructures.DummySet;

/**
 * The scaling classifier scales each feature value, x, to fulfill |x|<=1.
 * <P>
 * When the classifier is trained by a new Vector of {@linkplain LabeledSample}s, it calculates,
 * for each feature, the scaling parameters for that feature, such that after scaling, all
 * the values in the training samples will fulfill the condition |x|<=1.
 * <P>
 * When a new feature-vector is given to {@link #classify(Map)}, the feature-vector is first
 * scaled, and then classified.
 * <P>
 * This classifier is used to wrap another {@linkplain TrainableClassifier}, such that the actual
 * classification will be done by the underlying classifier.
 * <P>
 * Note: The scaling assumes that each feature value can get always negative values
 * or always positive values. It also assumes that 0 is a negative number.
 * 
 * @author Asher Stern
 * @since Mar 3, 2011
 *
 */
public abstract class ScalingClassifier implements Classifier
{


	public double classify(Map<Integer, Double> featureVector) throws ClassifierException
	{
		return realClassifier.classify(getScaledFeatureVector(featureVector));
	}

	public boolean classifyBoolean(Map<Integer, Double> featureVector) throws ClassifierException
	{
		return realClassifier.classifyBoolean(getScaledFeatureVector(featureVector));
	}


	public String descriptionOfTraining()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("ScalingClassifier:\nminMap:\n");
		List<Integer> keys = new ArrayList<Integer>(minMap.keySet().size());
		keys.addAll(minMap.keySet());
		Collections.sort(keys);
		for (Integer key : keys)
		{
			sb.append(key);
			sb.append(": ");
			sb.append(String.format("%-3.6f", minMap.get(key)));
			sb.append("\n");
		}
		sb.append("maxMap:\n");
		keys = new ArrayList<Integer>(maxMap.keySet().size());
		keys.addAll(maxMap.keySet());
		Collections.sort(keys);
		for (Integer key : keys)
		{
			sb.append(key);
			sb.append(": ");
			sb.append(String.format("%-3.6f", maxMap.get(key)));
			sb.append("\n");
		}
		sb.append("Wrapping:\n");
		sb.append(realClassifier.descriptionOfTraining());
		
		return sb.toString();
	}

	public void setFeaturesNames(Map<Integer, String> featureNames) throws ClassifierException
	{
		realClassifier.setFeaturesNames(featureNames);
	}
	
	public Map<Integer, String> getFeatureNames() throws ClassifierException
	{
		return realClassifier.getFeatureNames();
	}

	
	public Classifier getRealClassifier()
	{
		return this.realClassifier;
	}
	
	public LabeledSample getNormalizedSample(LabeledSample sample) throws ClassifierException
	{
		if ( (minMap==null) || (maxMap==null) )throw new ClassifierException("Seems that there was not training.");
		return realClassifier.getNormalizedSample(getScaledSample(sample));
	}

	
	
	protected double scale(double value, double min, double max)
	{
		// Must be > (and not >=) such that for features that represent proof steps,
		// "no action", which has the value 0, will be negative.
		// This is crucial to make sure that cost_0<cost_1 if tree_0 precedes tree_1
		// in the proof sequence.
		double sign = (value>0)?1:(-1);
		if (max>min)
		{
			return sign*((Math.abs(value)-min)/(max-min));
		}
		else
		{
			return sign*(Math.abs(value)-min);
		}
	}
	
	protected void findMinMaxMaps(Vector<LabeledSample> samples) throws ClassifierException
	{
		if (samples.size()==0)throw new ClassifierException("Cannot compute on empty training set.");
		maxMap = new LinkedHashMap<Integer, Double>();
		minMap = new LinkedHashMap<Integer, Double>();
		for (Integer key : samples.get(0).getFeatures().keySet())
		{
			maxMap.put(key, Math.abs(samples.get(0).getFeatures().get(key)));
			minMap.put(key, Math.abs(samples.get(0).getFeatures().get(key)));
		}
		for (LabeledSample sample : samples)
		{
			for (Integer key : sample.getFeatures().keySet())
			{
				double value = Math.abs(sample.getFeatures().get(key));
				if (value<minMap.get(key))
					minMap.put(key, value);
				
				if (value>maxMap.get(key))
					maxMap.put(key, value);
			}
		}
	}
	
	protected Vector<LabeledSample> getScaledSamples(Vector<LabeledSample> samples) throws ClassifierException
	{
		Vector<LabeledSample> ret = new Vector<LabeledSample>();
		for (LabeledSample sample : samples)
		{
			ret.add(getScaledSample(sample));
		}
		return ret;
	}
	
	protected LabeledSample getScaledSample(LabeledSample sample) throws ClassifierException
	{
		return new LabeledSample(getScaledFeatureVector(sample.getFeatures()), sample.getLabel());
	}
	
	protected Map<Integer,Double> getScaledFeatureVector(Map<Integer,Double> featureVector) throws ClassifierException
	{
		Map<Integer,Double> ret = new LinkedHashMap<Integer, Double>();
		// sanity
		if (featureVector.size() > minMap.size() || featureVector.size() > maxMap.size())
			throw new ClassifierException("The featureVector is longer than the minMap and/or MaxMap. Possible reasons: (1) your "+ConfigurationParametersNames.RTE_TEST_SAMPLES_FOR_SEARCH_CLASSIFIER+
					" file doesn't match the current configuration parameters. " +
					"(2) The reasonable guess classifier was badly constructed.");
		for (Integer key : featureVector.keySet())
		{
			if (doNotScale.contains(key))
			{
				ret.put(key,featureVector.get(key));
			}
			else
			{
				ret.put(key, scale(featureVector.get(key),minMap.get(key),maxMap.get(key)));
			}
		}
		return ret;
	}
	
	protected Map<Integer,Double> maxMap;
	protected Map<Integer,Double> minMap;
	
	protected Set<Integer> doNotScale = new DummySet<Integer>();
	
	protected Classifier realClassifier;
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ScalingClassifier.class);
}
