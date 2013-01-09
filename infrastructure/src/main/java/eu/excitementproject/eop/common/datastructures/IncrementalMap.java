package eu.excitementproject.eop.common.datastructures;

import java.io.Serializable;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;



/**
 * Incremental map is a map with the restriction that a key can get a value only once.
 * Once a value was put for a particular key, that value cannot be changed. Trying
 * to change the value of a key that already has a value causes an exception to be thrown.
 * <P>
 * In addition to the normal operation of putting values into keys, the incremental map
 * also exposes operation for "closing" a key. "Closing" a key means that no value
 * can be put for that key, much like as if a value was set for that key.
 * Trying to put a value to a closed key causes an exception to be thrown.
 * <P>
 * The {@link IncrementalMap} methods are similar to <code>java.util.Map</code>
 * methods.
 * 
 *  
 * @author Asher Stern
 *
 * @param <K>
 * @param <V>
 */
public interface IncrementalMap<K,V> extends Serializable
{
    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.
     */
	public boolean containsKey(K key);
	
    /**
     * Returns the value to which this map maps the specified key.
     */
	public V get(K key);
	
	
	
    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     * Closed keys has no impact on this method. (i.e. it will return <tt>true</tt>
     * though there are closed keys, if there are no keys mapped to a value).
     *
     * @return <tt>true</tt> if this map contains no key-value mappings.
     */
	public boolean isEmpty();
	
	public ImmutableSet<K> keySet();
	
	
    /**
     * Associates the specified value with the specified key in this map.
     * No value can be associated with that key later. If {@link #put(Object, Object)}
     * will be called on this key later, an {@link IncrementalMapException} will be thrown.
     * @param key
     * @param value
     */ 
	public void put(K key, V value) throws IncrementalMapException;
	
	
	/**
	 * Closes a key. No value can be associated with that key later.
	 * If {@link #put(Object, Object)} will be called on this key later, an
	 * {@link IncrementalMapException} will be thrown.
	 * @param key
	 * @throws IncrementalMapException
	 */
	public void closeKey(K key) throws IncrementalMapException;
	
	/**
	 * Returns a set of the closed keys.
	 * @return a set of the closed keys.
	 */
	public ImmutableSet<K> getClosedKeys();

	
    /**
     * Returns the number of key-value mappings in this map.
     * The closed keys are not counted here.
     *
     * @return the number of key-value mappings in this map.
     */
	public int size();

	
	public boolean equals(Object obj);
	public int hashCode();

}
