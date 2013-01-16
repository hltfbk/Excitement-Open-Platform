package eu.excitementproject.eop.biutee.classifiers.linearimplementations;
import java.util.Map;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.TrainableClassifier;

import jnisvmlight.FeatureVector;
import jnisvmlight.LabeledFeatureVector;
import jnisvmlight.SVMLightInterface;
import jnisvmlight.SVMLightModel;
import jnisvmlight.TrainingParameters;

/**
 * A wrapper of SVM-light classifier.
 * <BR>
 * Note that this classifier is no longer used.
 * 
 * <P>
 * This class requires that
 * the "svmlight" shared library (libsvmlight.so or svmlight.dll) will be
 * in the library-path (java.library.path: on windows this is path, on Unix
 * it is the system's library path, that includes /usr/lib and other directories).
 * <P>
 * The "svmlight" shared library is provided here: http://www.mpi-inf.mpg.de/~mtb/
 * and in our JARS it is in $JARS/JNI_SVM-light-6.01
 * under either linux_64 or windows_64.
 * 
 * @author Asher Stern
 * @since Mar 15, 2012
 *
 */
public class SvmLightClassifier implements TrainableClassifier
{
	public static final double MAX_ABS_VALUE_FOR_SCALING = 10.0;
	public static final boolean USE_SCALING = false;
	
	public SvmLightClassifier(TrainingParameters trainingParameters) throws ClassifierException
	{
		if (null==trainingParameters) throw new ClassifierException("null parameters");
		this.trainingParameters = trainingParameters;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.classifiers.Classifier#train(java.util.Vector)
	 */
	public void train(Vector<LabeledSample> samples) throws ClassifierException
	{
		trainer = new SVMLightInterface();
		LabeledFeatureVector[] dataset = new LabeledFeatureVector[samples.size()];
		int index=0;
		for (LabeledSample sample : samples)
		{
			dataset[index] = fromLabeledSample(sample);
			index++;
		}
		this.svmLightModel = trainer.trainModel(dataset, this.trainingParameters);
		
		if (USE_SCALING)
			findScaling(samples);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.classifiers.Classifier#classify(java.util.Map)
	 */
	public double classify(Map<Integer, Double> featureVector) throws ClassifierException
	{
		double classificationBySvmJni = getSvmClassificationValue(featureVector);
		
		double scaledValue = classificationBySvmJni;
		if (USE_SCALING)
		{
			scaledValue = scaledValue/this.scaling;
		}
		
		return ClassifierUtils.sigmoid(scaledValue);
		//return normalizeTo01(classificationBySvmJni);
	}
	

	public boolean classifyBoolean(Map<Integer, Double> featureVector) throws ClassifierException
	{
		double classificationBySvmJni = getSvmClassificationValue(featureVector);
		return (classificationBySvmJni>=0);
	}

	
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.classifiers.Classifier#reset()
	 */
	public void reset() throws ClassifierException
	{
		this.trainer = null;
	}
	
	public String descriptionOfTraining()
	{
		if (null==this.trainer)
			return "SVM not trained!";
		else
		{
			StringBuffer sb = new StringBuffer();
			sb.append("SVM Light.\n");
			sb.append(this.svmLightModel.toString());
			sb.append("\n");
			double[] linearWeights = this.svmLightModel.getLinearWeights();
			if (linearWeights!=null)
			{
				if (featureNames!=null)
				{
					for (int index=0;index<linearWeights.length;++index)
					{
						String featureName = featureNames.get(index);
						sb.append((featureName!=null)?featureName:"(not set)");
						sb.append(": ");
						sb.append(linearWeights[index]);
						sb.append("\n");
					}
				}
				else
				{
					for (int index=0;index<linearWeights.length;++index)
					{
						sb.append(index);
						sb.append(": ");
						sb.append(linearWeights[index]);
						sb.append("\n");
					}
				}
//				String[] featuresNamesArray = null;
//				if (featureNames!=null)
//				{
//					Integer[] featureNamesKeys = featureNames.keySet().toArray(new Integer[0]);
//					Arrays.sort(featureNamesKeys);
//					featuresNamesArray = new String[featureNamesKeys.length+1];
//					featuresNamesArray[0]="Zero";
//					//int index=1;
//					//for (Integer featureIndex : featureNamesKeys)
//					for (int index=0;index<featureNamesKeys.length;++index)
//					{
//						Integer featureIndex = featureNamesKeys[index];
//						featuresNamesArray[index+1]=featureNames.get(featureIndex);
//						//++index;
//					}
//				}
//				
//				sb.append("Linear weights: ");
//				int index=0;
//				for (double weight : linearWeights)
//				{
//					if (featuresNamesArray!=null)
//					{
//						sb.append("\n");
//						sb.append(featuresNamesArray[index]);
//						sb.append(":");
//					}
//					else
//					{
//						sb.append(" ");
//					}
//					
//					sb.append(String.format("%-5.5f",weight));
//					++index;
//				}
			}
			else
			{
				sb.append("Linear weights of training model are not available.");
			}
			sb.append("Value of constant USE_SCALING is (i.e. the values returned by classify() method will be scaled, this is not a scaling on the feature values, but only on a sigmoid function's result.)\n");
			sb.append("USE_SCALING =");
			sb.append(USE_SCALING);
			sb.append("\n");
			if (USE_SCALING)
			{
				sb.append("Scaling factor (values are divided by that factor) = ");
				sb.append(String.format("%-6.6f", this.scaling));
			}
			return sb.toString();
		}
	}
	
	public void setFeaturesNames(Map<Integer, String> featureNames)
	{
		this.featureNames = featureNames;
	}
	
	public Map<Integer, String> getFeatureNames() throws ClassifierException
	{
		return this.featureNames;
	}

	
	public LabeledSample getNormalizedSample(LabeledSample sample) throws ClassifierException
	{
		return sample;
	}



	
	
	@SuppressWarnings("unused")
	private static double normalizeTo01(double minus1To1)
	{
		return (1+minus1To1)/2;
	}
	
	
	private static FeatureVector fromMap(Map<Integer,Double> map)
	{
		int[] featuresNumbers = new int[map.size()];
		double[] featuresValues = new double[map.size()];
		int index=0;
		for (Integer featureNumber : map.keySet())
		{
			featuresNumbers[index] = featureNumber;
			featuresValues[index] = map.get(featureNumber);
			index++;
		}
		
		return new FeatureVector(featuresNumbers, featuresValues);
	}
	
	private static LabeledFeatureVector fromLabeledSample(LabeledSample sample)
	{
		double label = -1;
		if (true==sample.getLabel())
			label = +1;
		else
			label = -1;
		
		Map<Integer,Double> map = sample.getFeatures();
		int[] featuresNumbers = new int[sample.getFeatures().size()];
		double[] featuresValues = new double[sample.getFeatures().size()];
		int index=0;
		for (Integer featureNumber : map.keySet())
		{
			featuresNumbers[index] = featureNumber;
			featuresValues[index] = map.get(featureNumber);
			index++;
		}
		LabeledFeatureVector ret = new LabeledFeatureVector(label, featuresNumbers, featuresValues);
		return ret;
	}
	
	private double getSvmClassificationValue(Map<Integer, Double> featureVector) throws ClassifierException
	{
		if (null==this.trainer) throw new ClassifierException("Not trained");
		FeatureVector featureVectorForSvmJni = fromMap(featureVector);
		double classificationBySvmJni = svmLightModel.classify(featureVectorForSvmJni);
		//double classificationBySvmJni = this.trainer.classifyNative(featureVectorForSvmJni);
		// it seems that classifyNative causes memory leaks in the native code.

		return classificationBySvmJni;
	}
	
	private void findScaling(Vector<LabeledSample> samples) throws ClassifierException
	{
		double min = 0;
		double max = 0;
		boolean minSet = false;
		boolean maxSet = false;
		for (LabeledSample sample : samples)
		{
			double classificationBySvmJni = getSvmClassificationValue(sample.getFeatures());
			if (classificationBySvmJni<0)
			{
				if (!minSet)
				{
					minSet = true;
					min = classificationBySvmJni;
				}
				else
				{
					if (classificationBySvmJni<min)
						min = classificationBySvmJni;
				}
			}
			else
			{
				if (!maxSet)
				{
					maxSet = true;
					max = classificationBySvmJni;
				}
				else
				{
					if (max<classificationBySvmJni)
						max = classificationBySvmJni;
				}
			}
		}
		
		double scalingForNegatives = 1.0;
		double absMin = Math.abs(min);
		if (absMin>MAX_ABS_VALUE_FOR_SCALING)
		{
			scalingForNegatives = absMin/MAX_ABS_VALUE_FOR_SCALING;
		}
		
		double scalingForPositives = 1.0;
		if (max>MAX_ABS_VALUE_FOR_SCALING)
		{
			scalingForPositives= max/MAX_ABS_VALUE_FOR_SCALING;
		}
		
		if (scalingForNegatives>scalingForPositives)
			this.scaling = scalingForNegatives;
		else
			this.scaling = scalingForPositives;
		
		if (this.scaling<1.0)
			throw new ClassifierException("Bug in SvmLightClassifier. scaling < 1.0. The calculated scaling is: "+String.format("%-6.6f",this.scaling));
	}
	
	private TrainingParameters trainingParameters;
	private SVMLightInterface trainer = null;
	private SVMLightModel svmLightModel = null;
	private Map<Integer, String> featureNames = null;

	private double scaling = 1.0;
}
