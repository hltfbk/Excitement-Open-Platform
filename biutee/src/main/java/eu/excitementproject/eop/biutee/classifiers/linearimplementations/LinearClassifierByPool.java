package eu.excitementproject.eop.biutee.classifiers.linearimplementations;
import java.util.Map;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.io.LearningModel;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;

/**
 * Wrapper of {@link LinearTrainableStorableClassifier} which uses an already-exist
 * {@link LinearTrainableStorableClassifier} if available.
 * <BR>
 * This class uses a pool - {@link LinearClassifierPool} - which stores
 * actual {@link LinearTrainableStorableClassifier}s, and if the required classifier
 * already exists in the pool, this class just wraps this classifier.
 * Otherwise, the pool creates a new classifier, which is then wrapped
 * by this class.
 * <BR>
 * A classifier already-exists if in the {@link #train(Vector)} method
 * the given samples were already given for training, so a classifier
 * trained by the same samples already exists in the pool.
 * <P>
 * Note that this solution is not a very nice solution. It is a
 * compromise for efficiency.
 * 
 * @see LinearClassifierPool
 * @author Asher Stern
 * @since Jan 18, 2012
 *
 */
public class LinearClassifierByPool<T extends LinearTrainableStorableClassifier> implements LinearTrainableStorableClassifier
{
	public LinearClassifierByPool(LinearClassifierPool<T> pool)
	{
		this.pool = pool;
	}
	
	public void train(Vector<LabeledSample> samples) throws ClassifierException
	{
		this.realClassifier = pool.getClassifier(samples);
		if (this.deferredFeatureNames!=null)
		{
			this.realClassifier.setFeaturesNames(this.deferredFeatureNames);
		}
	}

	public double classify(Map<Integer, Double> featureVector)
			throws ClassifierException
	{
		return this.realClassifier.classify(featureVector);
	}

	public boolean classifyBoolean(Map<Integer, Double> featureVector)
			throws ClassifierException
	{
		return this.realClassifier.classifyBoolean(featureVector);
	}

	public void reset() throws ClassifierException
	{
		this.realClassifier.reset();
	}

	public String descriptionOfTraining()
	{
		return this.realClassifier.descriptionOfTraining();
	}

	public void setFeaturesNames(Map<Integer, String> featureNames) throws ClassifierException
	{
		if (null==this.realClassifier)
		{
			this.deferredFeatureNames = featureNames;
		}
		else
		{
			this.realClassifier.setFeaturesNames(featureNames);
		}
	}

	public LabeledSample getNormalizedSample(LabeledSample sample) throws ClassifierException
	{
		return this.realClassifier.getNormalizedSample(sample);
	}

	public ImmutableMap<Integer, Double> getWeights() throws ClassifierException
	{
		return this.realClassifier.getWeights();
	}

	public double getThreshold() throws ClassifierException
	{
		return this.realClassifier.getThreshold();
	}

	public double getProduct(Map<Integer, Double> featureVector) throws ClassifierException
	{
		return this.realClassifier.getProduct(featureVector);
	}

	
	public Map<Integer, String> getFeatureNames() throws ClassifierException
	{
		if (null==this.realClassifier)
		{
			return this.deferredFeatureNames;
		}
		else
		{
			return this.realClassifier.getFeatureNames();
		}
	}
	
	@Override
	public LearningModel store() throws ClassifierException
	{
		return this.realClassifier.store();
	}


	protected LinearClassifierPool<T> pool;
	protected T realClassifier = null;
	protected Map<Integer, String> deferredFeatureNames = null;
}
