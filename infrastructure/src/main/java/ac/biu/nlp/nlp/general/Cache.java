package ac.biu.nlp.nlp.general;

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
	public boolean containsKey(K key);
	
	public V get(K key);
	
	public void put(K key, V value);

}
