package eu.excitementproject.eop.common.utilities;

/**
 * A cache is like a <code>java.util.Map</code> except that
 * it has a capacity.<BR>
 * The size of a cache is limited, and therefore if
 * the cache reached that limit, any {@linkplain #put(Object, Object)}
 * operation will cause the cache to throw out at least one member.
 * 
 * <B>A {@linkplain Cache} is not thread safe, unless the opposite is defined
 * explicitly.</B>
 * 
 * @author Asher Stern
 *
 * @param <K>
 * @param <V>
 */
public interface Cache<K,V>
{
	/**
	 * Answers whether the cache contains the key.
	 * <P>
	 * Be aware of race conditions here: a {@link #get(Object)} will not return
	 * <code>null</code> if it immediately follows a {@link #containsKey(Object)}
	 * that returned <tt>true</tt>. This, however, is not guaranteed if another
	 * cache method was called in between. In a multi-threading environment
	 * more attention should be given to this property.
	 * 
	 * @param key a key which the user wants to find out whether it exists in
	 * the cache or not.
	 * @return <tt>true</tt> if the cache contains the key.
	 */
	public boolean containsKey(K key);
	
	/**
	 * Returns the value stored in the cache for the given key. Calling this method
	 * for a given key also makes it the "newest" key in the cache. This affects the
	 * time when this key (and its value) is dropped out of the cache.
	 * <BR>
	 * <code>null</code> will be returned if the key does not exist in the cache,
	 * <B>or if the cache supports null values, and a null value was stored for
	 * the given key</B>.
	 * <P>
	 * Since cache might support null values, it is strongly recommended to use
	 * {@link #containsKey(Object)} method to detect whether an element exists
	 * in the cache, and not to depend on the return value of this method.
	 *
	 * @param key a key, for which the user wants to get the value, stored in the cache.
	 * @return the value stored in the cache for this key.
	 */
	public V get(K key);
	
	/**
	 * Puts the given key-value in the cache. If they are already stored in the cache,
	 * this method only affects the time when this key-value element is dropped out
	 * of the cache.
	 * @param key
	 * @param value
	 */
	public void put(K key, V value);

}
