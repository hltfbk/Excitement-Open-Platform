package eu.excitementproject.eop.biutee.classifiers;
import java.util.Map;
import java.util.Vector;

/**
 * NOT USED
 * 
 * @author Asher Stern
 * @since Feb 18, 2011
 *
 */
public class SynchronizedClassifier implements TrainableClassifier
{
	public SynchronizedClassifier(TrainableClassifier realClassifier)
	{
		this.realClassifier = realClassifier;
	}

	public synchronized void train(Vector<LabeledSample> samples) throws ClassifierException
	{
		realClassifier.train(samples);
	}

	public synchronized double classify(Map<Integer, Double> featureVector) throws ClassifierException
	{
		return realClassifier.classify(featureVector);
	}

	public synchronized boolean classifyBoolean(Map<Integer, Double> featureVector) throws ClassifierException
	{
		return realClassifier.classifyBoolean(featureVector);
	}

	public synchronized void reset() throws ClassifierException
	{
		realClassifier.reset();

	}

	public synchronized String descriptionOfTraining()
	{
		return realClassifier.descriptionOfTraining();
	}

	public synchronized void setFeaturesNames(Map<Integer, String> featureNames) throws ClassifierException
	{
		realClassifier.setFeaturesNames(featureNames);
	}
	
	public synchronized Map<Integer, String> getFeatureNames() throws ClassifierException
	{
		return realClassifier.getFeatureNames();
	}


	public synchronized LabeledSample getNormalizedSample(LabeledSample sample) throws ClassifierException
	{
		return realClassifier.getNormalizedSample(sample);
	}

	private TrainableClassifier realClassifier;
}
