package eu.excitementproject.eop.biutee.classifiers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * Collection of utility-functions used by classifiers, or users-of-classifiers.
 * @author Asher Stern
 * @since Jan 9, 2011
 *
 */
public class ClassifierUtils
{
	public static final double SIGMOID_BIG_VALUE = 500;
	public static final double SIGMOID_SMALL_VALUE = -500;

	/**
	 * Given a classifier and a list of {@link LabeledSample}s - calculates the
	 * accuracy of the classifier on this list (i.e., for how many samples the classifier
	 * classifies correctly).
	 * @param classifier
	 * @param samples
	 * @return
	 * @throws ClassifierException
	 */
	public static double accuracyOf(Classifier classifier, Vector<LabeledSample> samples) throws ClassifierException
	{
		int correct = 0;
		for (LabeledSample sample : samples)
		{
			double classifierClassification = classifier.classify(sample.getFeatures());
			if (((sample.getLabel()==true)&&(classifierClassification>=0.5))
				||
				((sample.getLabel()==false)&&(classifierClassification<0.5)))
				correct++;
		}
		return ((double)correct/(double)samples.size());
	}
	
	/**
	 * Since all classifiers of type {@link Classifier} return the result
	 * as a double - this function converts it to boolean. Note that
	 * also {@link Classifier#classifyBoolean(Map)} does the same.
	 * @param classifierResult
	 * @return
	 */
	public static boolean classifierResultToBoolean(double classifierResult)
	{
		if (classifierResult>=0.5)
			return true;
		else
			return false;
	}
	

	/**
	 * Returns a string representation of {@link LabeledSample}.
	 * @param sample
	 * @return
	 */
	public static String printLabeledSample(LabeledSample sample)
	{
		return printLabeledSample(sample,", ");
	}
	
	
	/**
	 * Returns a string representation of {@link LabeledSample}.
	 * @param sample
	 * @param delimiter Each feature-value is separated from others by this
	 * delimiter (e.g., a comma)
	 * @return
	 */
	public static String printLabeledSample(LabeledSample sample, String delimiter)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%-7s", sample.getLabel())  );
		appendFeatureVector(sample.getFeatures(),delimiter,sb);
		return sb.toString();
	}

	/**
	 * Returns a string representation of {@link LabeledSample}.
	 * @param sample
	 * @param delimiter Each feature-value is separated from others by this
	 * delimiter (e.g., a comma)
	 * @return
	 */
	public static void appendFeatureVector(Map<Integer,Double> featureVector, String delimiter, StringBuffer sb)
	{
		List<Integer> sortedFeatureIndexes = new ArrayList<Integer>(featureVector.keySet().size());
		sortedFeatureIndexes.addAll(featureVector.keySet());
		Collections.sort(sortedFeatureIndexes);
		for (Integer featureIndex : sortedFeatureIndexes)
		{
			sb.append(delimiter);
			sb.append(featureIndex);
			sb.append(":");
			sb.append(String.format("%-6.8f", featureVector.get(featureIndex)));
		}
	}

	
	/**
	 * Returns a string which looks like a feature vector, and each
	 * value in the feature vector is the average value of the corresponding
	 * feature in the given samples. This is done separately for positive and
	 * negative samples (i.e., the function returns two "feature vectors", one
	 * for the positives and one for the negatives).
	 * @param samples
	 * @return
	 */
	public static String printAvarages(List<LabeledSample> samples)
	{
		int numberOfTrue = 0;
		int numberOfFalse = 0;
		Map<Integer,Double> avaragesTrue = new LinkedHashMap<Integer, Double>();
		Map<Integer,Double> avaragesFalse = new LinkedHashMap<Integer, Double>();
		
		for (LabeledSample sample : samples)
		{
			Map<Integer,Double> avarages = null;
			if (sample.getLabel()==true)
			{
				numberOfTrue++;
				avarages = avaragesTrue;
			}
			else
			{
				numberOfFalse++;
				avarages = avaragesFalse;
			}
			
			for (Integer featureIndex : sample.getFeatures().keySet())
			{
				if (avarages.containsKey(featureIndex))
				{
					avarages.put(featureIndex, avarages.get(featureIndex)+sample.getFeatures().get(featureIndex));
				}
				else
				{
					avarages.put(featureIndex, sample.getFeatures().get(featureIndex));
				}
			}
		}
		avaragesTrue = avarageOfValues(avaragesTrue,numberOfTrue);
		avaragesFalse = avarageOfValues(avaragesFalse, numberOfFalse);
		
		
		
		StringBuffer sb = new StringBuffer();
		LabeledSample trueAvarageSample = new LabeledSample(avaragesTrue, true);
		LabeledSample falseAvarageSample = new LabeledSample(avaragesFalse, false);
		sb.append(printLabeledSample(trueAvarageSample));
		sb.append("\n");
		sb.append(printLabeledSample(falseAvarageSample));
		return sb.toString();
	}
	
	/**
	 * Calculates the average values, given the sum.
	 * @param map
	 * @param numberOfSamples
	 * @return
	 */
	public static Map<Integer,Double> avarageOfValues(Map<Integer,Double> map, int numberOfSamples)
	{
		Map<Integer,Double> ret = new LinkedHashMap<Integer, Double>();
		for (Integer key : map.keySet())
		{
			ret.put(key, map.get(key)/((double)numberOfSamples));
		}
		return ret;
		
	}
	
	/**
	 * Prints to the given logger the list of samples as used in the input-files of
	 * SVM-light tool (http://svmlight.joachims.org/).
	 * @param samples
	 * @param logger
	 */
	public static void printSamplesAsSvmLightInput(List<LabeledSample> samples, Logger logger)
	{
		logger.info(printSamplesAsSvmLightInput(samples,true).toString());
	}
	
	/**
	 * Returns a StringBuffer that holds a string that contains a string representation
	 * of the given samples as used by the input-files of the SVM-light
	 * tool (http://svmlight.joachims.org/).
	 * @param samples
	 * @param startWithNewline use <tt>false</tt>. <tt>true</tt> is needed only for {@link #printSamplesAsSvmLightInput(List, Logger)}
	 * @return
	 */
	public static StringBuffer printSamplesAsSvmLightInput(List<LabeledSample> samples, boolean startWithNewline)
	{
		StringBuffer allSamplesStringBuffer = new StringBuffer();
		if (startWithNewline) allSamplesStringBuffer.append("\n");
		boolean firstIteration=true;
		for (LabeledSample sample : samples)
		{
			if (firstIteration)firstIteration=false;
			else allSamplesStringBuffer.append("\n");
			StringBuffer sb = new StringBuffer();
			if (true==sample.getLabel())
				sb.append("+1");
			else
				sb.append("-1");
			
			
			for (Integer featureIndex : sample.getFeatures().keySet())
			{
				sb.append(" ");
				sb.append(featureIndex);
				sb.append(":");
				sb.append(String.format("%-6.10f", sample.getFeatures().get(featureIndex)));
			}
			allSamplesStringBuffer.append(sb.toString());
			
		}
		return allSamplesStringBuffer;
	}
	
	/**
	 * Calculates the sigmoid function 1/(1+e^(-z))
	 * @param z
	 * @return
	 */
	public static double sigmoid(double z)
	{
		if (z>SIGMOID_BIG_VALUE)
			z=SIGMOID_BIG_VALUE;
		else if (z<SIGMOID_SMALL_VALUE)
			z=SIGMOID_SMALL_VALUE;
		
		return 1/(1+(Math.exp(-z)));
	}
	
	public static final double INVERSE_SIGMOID_BIG = 0.99;
	public static final double INVERSE_SIGMOID_SMALL = 0.01;
	
	public static double inverseSigmoid(double y)
	{
		if (y>INVERSE_SIGMOID_BIG) y = INVERSE_SIGMOID_BIG;
		if (y<INVERSE_SIGMOID_SMALL) y = INVERSE_SIGMOID_SMALL;
		return -Math.log((1-y)/y);
		
	}

	
	/**
	 * returns in [0,1]
	 * @param minimum
	 * @param maximum
	 * @param value
	 * @return
	 */
	public static double normalize(double minimum, double maximum, double value)
	{
		if (maximum==minimum) return 0.5;
		else return (value-minimum)/(maximum-minimum);
	}
	
	/**
	 * Returns a map from feature-index to a string that represent it.
	 * The function is given such a map, and extends it - it adds new feature-indexes,
	 * starting from the (1+highest-in-given-map) which are mapped to the given
	 * knowledge-resources names.
	 * 
	 * 
	 * @param featureNames
	 * @param moreFeatures
	 * @return
	 */
	public static Map<Integer, String> extendFeatureNames(Map<Integer,String> featureNames, LinkedHashSet<String> moreFeatures)
	{
		if (featureNames==null) throw new NullPointerException("featureNames is null");
		if (moreFeatures==null) throw new NullPointerException("moreFeatures is null");
		Map<Integer, String> ret = new LinkedHashMap<Integer, String>();
		Integer max = 0;
		boolean firstIteration = true;
		for (Integer featureIndex : featureNames.keySet())
		{
			if (firstIteration)
			{
				max = featureIndex;
				firstIteration = false;
			}
			else
			{
				if (max.intValue()<featureIndex.intValue())
				{
					max = featureIndex;
				}
			}
			ret.put(featureIndex,featureNames.get(featureIndex));
		}
		int index=max.intValue()+1;
		for (String additionalFeatureName : moreFeatures)
		{
			ret.put(index, additionalFeatureName);
			++index;
		}
		
		return ret;
	}
	
	
	/**
	 * Takes a value in the interval [0,1], and makes it "closer" to 0.5.
	 * The <tt>exponent</tt> must be greater than 0.0
	 * <tt>exponent=1.0</tt> means no change to the original number.
	 * The larger the exponent - the greater the effect.
	 * 
	 * @param original the original number between 0 to 1.0 (if original is 0 or 1 - there is no effect).
	 * @param exponent the exponent determines how closer to 0.5 the returned number will be
	 * @return a number closer to 0.5 than the original, but the sign of (returned-value - 0.5) is
	 * equal to the sign of (original - 0.5).
	 */
	public static double relief(double original, double exponent)
	{
		double base = Math.abs((original-0.5)*2);
		double newBase = Math.pow(base, exponent);
		double ret = (original>0.5)?((newBase/2.0)+0.5):(0.5-(newBase/2.0));
		
		return ret;
	}
	
	/**
	 * Gets a list of string, each string is a line identical to the lines used in
	 * SVM-light tool input files (http://svmlight.joachims.org/), and returns
	 * a vector of labeled samples - each labeled sample correspond to a line.
	 * @param svmLightFormatSample
	 * @return
	 * @throws TeEngineMlException
	 */
	public static Vector<LabeledSample> fromSvmLightFormatToLabeledSamples(List<String> svmLightFormatSample) throws TeEngineMlException
	{
		Vector<LabeledSample> ret = new Vector<LabeledSample>(svmLightFormatSample.size());
		int lineIndex=0;
		for (String svmLightLine : svmLightFormatSample)
		{
			boolean label = false;
			Map<Integer, Double> featureVector = new LinkedHashMap<Integer, Double>();
			String[] components = svmLightLine.split("\\s");
			String labelString = components[0];
			if (labelString.startsWith("+"))
			{
				labelString = labelString.substring(1);
			}
			int labelInt = Integer.parseInt(labelString);
			if (labelInt==1)
				label = true;
			else if (labelInt==(-1))
				label = false;
			else throw new TeEngineMlException("bad label: "+components[0]+" on line "+lineIndex);
			
			int componentIndex=1;
			boolean stop = false;
			while (!stop)
			{
				if (components.length<=componentIndex)
					stop=true;
				else
				{
					String componentString = components[componentIndex];
					if (componentString.startsWith("#"))
						stop=true;
					else
					{
						String[] featureString = componentString.split(":");
						if (featureString.length!=2)throw new TeEngineMlException("bad "+componentString+"in line"+lineIndex);
						int featureIndex = Integer.parseInt(featureString[0]);
						double featureValue = Double.parseDouble(featureString[1]);

						featureVector.put(featureIndex,featureValue);
					}

					componentIndex++;
				}
			}
			
			ret.add(new LabeledSample(featureVector, label));
			
			lineIndex++;
		}
		return ret;
	}
	
	/**
	 * converts feature-vector to a string.
	 * @param vector
	 * @return
	 */
	public static String vectorAsString(Map<Integer,Double> vector)
	{
		List<Integer> keys = new ArrayList<Integer>(vector.keySet().size());
		keys.addAll(vector.keySet());
		Collections.sort(keys);
		StringBuffer sb = new StringBuffer();
		for (Integer key : keys)
		{
			sb.append(key);
			sb.append(": ");
			sb.append(String.format("%-4.4f",vector.get(key)));
			sb.append("\n");
		}
		return sb.toString();
	}
	
	

	

}
