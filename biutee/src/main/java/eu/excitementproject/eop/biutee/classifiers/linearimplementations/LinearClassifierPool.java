package eu.excitementproject.eop.biutee.classifiers.linearimplementations;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.common.utilities.Cache;
import eu.excitementproject.eop.common.utilities.CacheFactory;

/**
 * Stores in a cache (a pool) trained linear classifiers.
 * This class is used to save time when two or more classifiers,
 * of the same type, are to be trained with the same vector of samples.
 * This class returns the same classifier (the same object) to
 * the consumers.
 * 
 * @see LinearClassifierByPool
 * @author Asher Stern
 * @since Jan 18, 2012
 *
 */
public abstract class LinearClassifierPool<T extends LinearTrainableStorableClassifier>
{
	public static final int POOL_CACHE_CAPACITY = 10;
	public T getClassifier(Vector<LabeledSample> samples) throws ClassifierException
	{
		T ret = null;
		synchronized(this)
		{
			if (pool.containsKey(samples))
			{
				logger.info("Classifier exists in pool");
				ret =  pool.get(samples);
			}
		}
		if (null==ret)
		{
			logger.info("Creating a new classifier, since it does not exist in pool");
			ret = createClassifier();
			ret.train(samples);
			synchronized(this)
			{
				pool.put(samples,ret);
			}
		}
		return ret;
	}
	
	protected abstract T createClassifier() throws ClassifierException;
	
	private Cache<Vector<LabeledSample>, T> pool = new CacheFactory<Vector<LabeledSample>, T>().getCache(POOL_CACHE_CAPACITY);
	
	private static final Logger logger = Logger.getLogger(LinearClassifierPool.class);
}
