package eu.excitementproject.eop.biutee.classifiers.f1_logicstic_regression;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMapWrapper;

/**
 * 
 * @author Asher Stern
 * @since Dec 26, 2012
 *
 */
public abstract class AdjustingVectorLinearWithGammaClassifier implements LinearClassifier
{
	public AdjustingVectorLinearWithGammaClassifier(){}
	
	@Override
	public double classify(Map<Integer, Double> featureVector) throws ClassifierException
	{
		Map<Integer,Double> adjustedFeatureVector = adjustVector(featureVector);
		double[] adjustedAsArray = convertMapToArray(adjustedFeatureVector);
		if (adjustedAsArray.length!=this.weights.length) throw new ClassifierException("BUG");
		double sum = 0;
		for (int index=0;index<adjustedAsArray.length;++index)
		{
			sum += adjustedAsArray[index]*this.weights[index];
		}
		return ClassifierUtils.sigmoid(this.gammaCoefficientSigmoid*sum);
	}

	@Override
	public boolean classifyBoolean(Map<Integer, Double> featureVector) throws ClassifierException
	{
		return ClassifierUtils.classifierResultToBoolean(classify(featureVector));
	}
	
	
	
	@Override
	public void setFeaturesNames(Map<Integer, String> featureNames) throws ClassifierException
	{
		this.originalFeatureNames = featureNames;
		this.featureNames = shiftVectorT(featureNames, "Constant");
	}
	
	public Map<Integer, String> getFeatureNames() throws ClassifierException
	{
		return originalFeatureNames;
	}


	@Override
	public LabeledSample getNormalizedSample(LabeledSample sample) throws ClassifierException
	{
		// return normalizeSample(sample);
		return sample;
	}

	@Override
	public ImmutableMap<Integer, Double> getWeights() throws ClassifierException
	{
		return new ImmutableMapWrapper<Integer, Double>(convertArrayToMap(this.weights));
	}

	@Override
	public double getThreshold() throws ClassifierException
	{
		return this.weights[0];
	}

	@Override
	public double getProduct(Map<Integer, Double> featureVector) throws ClassifierException
	{
		double[] features = convertMapToArray(adjustVector(featureVector));
		if (features.length!=this.weights.length) throw new ClassifierException("Bad feature vector. Length = "+features+" while length should be "+this.weights.length);
		double sum = 0.0;
		for (int index=1;index<features.length;++index)
		{
			sum += this.weights[index]*features[index];
		}
		return sum;
	}
	
	@Override
	public String descriptionOfTraining()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(AdjustingVectorLinearWithGammaClassifier.class.getSimpleName());
		sb.append(" classifier.\n");
		if (this.weights != null)
		{
			sb.append("weights are:\n");
			printWeights(this.weights, sb);
		}
		else
		{
			sb.append("Not trained!");
		}
		return sb.toString();
	}
	
	protected LabeledSample adjustSample(LabeledSample sample) throws ClassifierException
	{
		return new LabeledSample(adjustVector(sample.getFeatures()), sample.getLabel());
	}
	
	protected Map<Integer,Double> shiftVector(Map<Integer,Double> vector) throws ClassifierException
	{
		return shiftVectorT(vector,1.0);
//		Map<Integer,Double> ret = new LinkedHashMap<Integer, Double>();
//		ret.put(0,1.0);
//		for (Integer key : vector.keySet())
//		{
//			if (key.intValue()<0) throw new ClassifierException("key "+key.intValue()+" < 0");
//			ret.put(key+1,vector.get(key));
//		}
//		return ret;
	}

	protected <T> Map<Integer,T> shiftVectorT(Map<Integer,T> vector, T valueForZero) throws ClassifierException
	{
		Map<Integer,T> ret = new LinkedHashMap<Integer, T>();
		ret.put(0,valueForZero);
		for (Integer key : vector.keySet())
		{
			if (key.intValue()<0) throw new ClassifierException("key "+key.intValue()+" < 0");
			ret.put(key+1,vector.get(key));
		}
		return ret;
	}
	
	protected Set<Integer> shiftSet(Set<Integer> originalSet)
	{
		Set<Integer> ret = new LinkedHashSet<Integer>();
		for (Integer i : originalSet)
		{
			ret.add(i+1);
		}
		return ret;
		
	}

	
	protected Map<Integer,Double> adjustVector(Map<Integer,Double> vector) throws ClassifierException
	{
		vector = shiftVector(vector);
		int maxKey = vector.keySet().iterator().next();
		for (Integer key : vector.keySet())
		{
			if (key.intValue()>maxKey)
			{
				maxKey = key.intValue();
			}
		}
		for (int index=0;index<=maxKey;++index)
		{
			if (!vector.containsKey(index))
			{
				vector.put(index, 0.0);
			}
		}
		return vector;
	}

	protected Map<Integer,Double> convertArrayToMap(double[] array)
	{
		Map<Integer,Double> ret = new LinkedHashMap<Integer, Double>();
		for (int index=0;index<array.length;++index)
		{
			ret.put(index,array[index]);
		}
		return ret;
	}
	
	protected double[] convertMapToArray(Map<Integer,Double> map)
	{
		double[] ret = new double[map.keySet().size()];
		for (Integer key : map.keySet())
		{
			ret[key] = map.get(key);
		}
		return ret;
	}



	
	protected void printWeights(double[] weights, StringBuffer sb)
	{
		printWeights(convertArrayToMap(weights), sb);
	}
	
	
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
			sb.append(" : ");
			sb.append(String.format("%-4.4f", weights.get(key)));
			sb.append("\n");
		}

	}

	
	

	protected Map<Integer, String> featureNames;
	protected Map<Integer, String> originalFeatureNames;

	protected double[] weights;
	protected double gammaCoefficientSigmoid;
}
