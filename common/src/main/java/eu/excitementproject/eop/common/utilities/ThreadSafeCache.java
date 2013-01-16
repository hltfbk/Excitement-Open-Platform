package eu.excitementproject.eop.common.utilities;

/**
 * Thread safe implementation of {@link Cache}.
 * This implementation merely wraps a non-thread-safe cache and
 * calls its method in a synchronized way.
 *  
 * @author Asher Stern
 *
 * @param <K>
 * @param <V>
 */
public class ThreadSafeCache<K, V> implements Cache<K, V>
{
	/**
	 * The constructor wraps the given cache.
	 * The given <code>realCache</code> is a cache which is not thread safe.
	 * This class merely calls the <code>realCache</code>'s methods in a synchronized
	 * manner.
	 * 
	 * @param realCache the real cache implementation. Should not be null!
	 */
	public ThreadSafeCache(Cache<K, V> realCache)
	{
		if (null==realCache)
			throw new RuntimeException("Caller\'s BUG: The given real cache is null!");
		this.realCache = realCache;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Cache#containsKey(java.lang.Object)
	 */
	public synchronized boolean containsKey(K key)
	{
		return realCache.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Cache#get(java.lang.Object)
	 */
	public synchronized V get(K key)
	{
		return realCache.get(key);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Cache#put(java.lang.Object, java.lang.Object)
	 */
	public synchronized void put(K key, V value)
	{
		realCache.put(key, value);
	}
	
	
	
	
	// protected part 
	
	protected Cache<K, V> realCache;
}
