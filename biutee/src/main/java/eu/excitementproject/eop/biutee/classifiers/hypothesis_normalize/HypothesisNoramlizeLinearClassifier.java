package eu.excitementproject.eop.biutee.classifiers.hypothesis_normalize;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.Feature;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;

/**
 * Wraps another classifier while normalizing all features in hypothesis length.<BR>
 * Each feature is multiplied by the "inverse-hypothesis-length" feature.
 * 
 * @author Asher Stern
 * @since 3 July, 2012
 *
 */
public abstract class HypothesisNoramlizeLinearClassifier implements LinearClassifier
{
//	public HypothesisNoramlizeLinearClassifier(LinearClassifier realClassifier)
//	{
//		super();
//		this.realClassifier = realClassifier;
//	}



	@Override
	public double classify(Map<Integer, Double> featureVector) throws ClassifierException
	{
		return realClassifier.classify(normalizeFeatureVector(featureVector));
	}

	@Override
	public boolean classifyBoolean(Map<Integer, Double> featureVector) throws ClassifierException
	{
		return realClassifier.classifyBoolean(normalizeFeatureVector(featureVector));
	}


	@Override
	public String descriptionOfTraining()
	{
		return "hypothesis normalize classifier, wraps:\n"+realClassifier.descriptionOfTraining();
	}

	@Override
	public void setFeaturesNames(Map<Integer, String> featureNames) throws ClassifierException
	{
		realClassifier.setFeaturesNames(featureNames);
	}

	@Override
	public Map<Integer, String> getFeatureNames() throws ClassifierException
	{
		return realClassifier.getFeatureNames();
	}

	@Override
	public LabeledSample getNormalizedSample(LabeledSample sample) throws ClassifierException
	{
		return realClassifier.getNormalizedSample(normalizeLabeledSample(sample));
	}

	@Override
	public ImmutableMap<Integer, Double> getWeights() throws ClassifierException
	{
		return realClassifier.getWeights();
	}

	@Override
	public double getThreshold() throws ClassifierException
	{
		return realClassifier.getThreshold();
	}

	@Override
	public double getProduct(Map<Integer, Double> featureVector) throws ClassifierException
	{
		return realClassifier.getProduct(normalizeFeatureVector(featureVector));
	}
	
	
	//////////////////////// PROTECTED AND PRIVATE //////////////////////// 
	
	protected Map<Integer, Double> normalizeFeatureVector(Map<Integer, Double> originalFeaureVector) throws ClassifierException
	{
		Double inverseHypothesisLength = originalFeaureVector.get(Feature.INVERSE_HYPOTHESIS_LENGTH.getFeatureIndex());
		if (null==inverseHypothesisLength) throw new ClassifierException("null inverse hypothesis length");
		if (inverseHypothesisLength.doubleValue()==0) throw new ClassifierException("0 inverse hypothesis length");
		double inverseHypothesisLengthPrimitive = inverseHypothesisLength.doubleValue();
		
		Map<Integer, Double> ret = new LinkedHashMap<Integer, Double>();
		for (Integer key : originalFeaureVector.keySet())
		{
			if (Feature.INVERSE_HYPOTHESIS_LENGTH.getFeatureIndex()==key.intValue())
			{
				ret.put(key, 0.0);
			}
			else
			{
				ret.put(key,originalFeaureVector.get(key).doubleValue()*inverseHypothesisLengthPrimitive);
			}
		}
		
		return ret;
	}
	
	protected LabeledSample normalizeLabeledSample(LabeledSample sample) throws ClassifierException
	{
		return new LabeledSample(normalizeFeatureVector(sample.getFeatures()), sample.getLabel());
	}
	
	protected Vector<LabeledSample> normalizeVectorSamples(Vector<LabeledSample> samples) throws ClassifierException
	{
		Vector<LabeledSample> ret = new Vector<LabeledSample>(samples.size());
		for (LabeledSample sample : samples)
		{
			ret.add(normalizeLabeledSample(sample));
		}
		return ret;
	}

	protected LinearClassifier realClassifier;
}
