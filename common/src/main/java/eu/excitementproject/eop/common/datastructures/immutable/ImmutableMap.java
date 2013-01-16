package eu.excitementproject.eop.common.datastructures.immutable;

import java.io.Serializable;
import java.util.Map;

/**
 * Represents an immutable map.
 * <P>
 * This interface is similar to <code>java.util.Map</code>
 * except that it has no methods to change any thing in the map.
 * <P>
 * <B>Note:</B> an ImmutableMap may be non-thread-safe. For example, when using
 * {@linkpalin ImmutableMapWrapper}, and the underlying map is LinkedHashMap, and it
 * is access-ordered, then even read operations may be non-thread-safe.
 * 
 * @author Asher Stern
 *
 * @param <K> the map's key type
 * @param <V> the map's elements type
 */
public interface ImmutableMap<K,V> extends Serializable
{
	/**
	 * @see java.util.Map
	 */
	public boolean containsKey(Object key);

	/**
	 * @see java.util.Map
	 */
	public boolean containsValue(Object value);

	/**
	 * @see java.util.Map
	 */
	public V get(Object key);

	/**
	 * @see java.util.Map
	 */
	public boolean isEmpty();

	/**
	 * @see java.util.Map
	 */
	public ImmutableSet<K> keySet();
	
	/**
	 * @see java.util.Map
	 */
	public int size();
	
	/**
	 * @see java.util.Map
	 */
	public ImmutableCollection<V> values();
	
	
	/**
	 * Returns a mutable copy of the map. That copy is
	 * hard copy. Changing it does not change the original map.
	 * <P>
	 * There is no guarantee about the returned map's type.
	 * It is only known that it implements <code>java.util.Map</code>
	 * 
	 * @return a <code>java.util.Map</code> which is a deep copy
	 * of this map.
	 */
	public Map<K,V> getMutableCopy();
	
	
	/**
	 * Returns a <B>deep copy</B> of the map.
	 * After calling this method, the caller has two maps, and
	 * the elements' reference exist twice in each map.
	 *  
	 * @return an ImmutableMap which is copy of this map.
	 */
	public ImmutableMap<K,V> getImmutableCopy();


}
