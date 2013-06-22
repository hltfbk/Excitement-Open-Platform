package eu.excitementproject.eop.distsim.storage;


import java.io.Serializable;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.SerializationException;


/**
 * The BasicMap interface defines the basic functionality of maps: put, get, iterator
 * 
 * @author Meni Adler
 * @since 15/08/2012
 *
 * 
 * @param <K> the type of keys maintained by this map
 * @param <V>  the type of mapped values
 */
public interface BasicMap<K, V> extends Serializable {
	/**
	 * Associates the specified value with the specified key in this map (optional operation). If the map previously contained a mapping for this key, the old value is replaced by the specified value.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @param value value to be associated with the specified key.
	 * @throws SerializationException 
	 */
	void put(K key, V value) throws BasicMapException;
	
	/**
	 * Returns the value to which this map maps the specified key. Returns null if the map contains no mapping for this key. A return value of null does not necessarily indicate that the map contains no mapping for the key; it's also possible that the map explicitly maps the key to null.
	 * 
	 * @param key key whose associated value is to be returned.
	 * @return the value to which this map maps the specified key, or null if the map contains no mapping for this key.
	 * @throws SerializationException 
	 */
	V get(K key) throws BasicMapException;
	
	
	/**
	 * Gets an (immutable) iterator for the key-value pairs of the map
	 * 
	 * @return an immutable iterator for the key-value pairs of the map
	 */
	ImmutableIterator<Pair<K,V>> iterator();
	

	/**
	 * @return the number of entries in the map
	 */
	int size();
}
