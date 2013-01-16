package eu.excitementproject.eop.common.utilities;

/**
 * 
 * @author Asher Stern
 *
 * @param <K>
 * @param <V>
 */
public class CacheFactory<K,V>
{
	public Cache<K, V> getCache()
	{
		//return new ThreadSafeCache<K, V>(new ListMapCache<K, V>(ListMapCache.MINIMUM_CAPACITY));
		return new ThreadSafeCache<K, V>(new TwoMapsCache<K, V>());
	}
	
	public Cache<K,V> getCache(int capacity)
	{
		//return new ThreadSafeCache<K, V>(new ListMapCache<K, V>(capacity));
		return new ThreadSafeCache<K, V>(new TwoMapsCache<K, V>(capacity));
	}
	
	public Cache<K,V> getThreadSafeCache(int capacity)
	{
		return new ThreadSafeCache<K, V>(new TwoMapsCache<K, V>(capacity));
	}

}
