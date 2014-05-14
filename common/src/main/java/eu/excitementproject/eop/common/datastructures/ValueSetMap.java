package eu.excitementproject.eop.common.datastructures;

import java.io.Serializable;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;


/**
 * A Map that its value is a set. Each key has a set of values that
 * is mapped to it.
 * A value can belong to more than one set (i.e. to more than one key).
 * <P>
 * The map supports remove of value as well as keys.
 * <P>
 * When removing a value - if there is a key that that value is its
 * only value, then that key is removed as well.
 * <P>
 * When removing a key - if that key has a value "v" in its value set,
 * and "v" was not in any other set (of any other key), than that value
 * is actually removed, and {@link #containsValue(Object)} will return
 * <code>false</code> on that value.
 * <P>
 * <B><code>null</code> values and <code>null</code> keys behavior is undefined.</B>
 * <P>
 * <B>Not thread safe, unless explicitly specified.</B>
 * @author Asher Stern
 *
 * @param <K>
 * @param <V>
 */
public interface ValueSetMap<K,V> extends Serializable
{
	/**
	 * Put a value in the set of values mapped to the key. 
	 * @param key
	 * @param value
	 */
	public void put(K key, V value);
	
	/**
	 * Removes the specified key. For each value that is in the set
	 * associated to that key, such that that value is not member
	 * in any other set associated to any other key - that value is
	 * removed.
	 * {@link #containsValue(Object)} will return <code>false</code>
	 * on that value.
	 * <BR>
	 * The whole concept is identical to {@link #removeValue(Object)}
	 * @param key
	 */
	public void remove(K key);
	
	
	/**
	 * Removes that value. For any key that that value belongs to the
	 * set of values associated with it, the value is removed from
	 * that set.
	 * If a key has an empty set of values associated with it, due
	 * to the removal, that key is removed as well.
	 * <BR>
	 * The whole concept is identical to {@link #remove(Object)}
	 * @param value
	 */
	public void removeValue(V value);
	
	
	/**
	 * Clears that map. The map becomes empty after calling
	 * this method.
	 */
	public void clear();

	/**
	 * Returns the set of values associated with the given key.
	 * @param key
	 * @return
	 */
	public ImmutableSet<V>  get(K key);
	
	/**
	 * @return a set of all keys in the map.
	 * @see #values() 
	 */
	public ImmutableSet<K> keySet();
	
	/**
	 * @return a set of all values in the map.
	 * @see #keySet()
	 */
	public ImmutableSet<V> values();
	
	/**
	 * Returns a set of all keys that the given value belongs to
	 * the set of values associated with them.
	 * @param value
	 * @return
	 */
	public ImmutableSet<K> getKeysOf(V value);

	/**
	 * Returns <code>true</code> if the map contains that key.
	 * "contains" means that at least one value is associated with it.
	 * @param key
	 * @return
	 */
	public boolean containsKey(K key);

	/**
	 * Returns <code>true</code> if there at least one key that the
	 * given value belongs to its set.
	 * @param value
	 * @return
	 */
	public boolean containsValue(V value);

	
	/**
	 * <code> true </code> if the map is empty.
	 * @return
	 */
	public boolean isEmpty();

	/**
	 * Returns the map's size.
	 * @return
	 */
	public int size();
	
	
	/**
	 * Creates a copy of the map.
	 * Each set is actually copied, such that the set associated
	 * to a key in the copied map, is not the same set associated
	 * with that key in the original map (though those sets are equal).
	 * @return
	 */
	public ValueSetMap<K,V> deepCopy();
	
	public boolean equals(Object obj);
	public int hashCode();
}
