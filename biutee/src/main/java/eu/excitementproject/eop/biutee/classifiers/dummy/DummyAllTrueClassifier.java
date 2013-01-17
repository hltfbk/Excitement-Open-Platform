package eu.excitementproject.eop.biutee.classifiers.dummy;
import java.util.Map;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.TrainableClassifier;


/**
 * An dummy implementation of {@link TrainableClassifier} which returns "true" for each example, and
 * ignores training data.
 * 
 * @author Asher Stern
 * @since Aug 3, 2011
 *
 */
public class DummyAllTrueClassifier implements TrainableClassifier
{
	public void train(Vector<LabeledSample> samples) throws ClassifierException
	{
	}

	public double classify(Map<Integer, Double> featureVector) throws ClassifierException
	{
		return 1;
	}

	public boolean classifyBoolean(Map<Integer, Double> featureVector) throws ClassifierException
	{
		return true;
	}

	public void reset() throws ClassifierException
	{
	}

	public String descriptionOfTraining()
	{
		return "Dummy all=true classifier.";
	}

	public void setFeaturesNames(Map<Integer, String> featureNames) throws ClassifierException
	{
	}

	public LabeledSample getNormalizedSample(LabeledSample sample) throws ClassifierException
	{
		return sample;
	}

	public Map<Integer, String> getFeatureNames() throws ClassifierException
	{
		return null;
	}
}
