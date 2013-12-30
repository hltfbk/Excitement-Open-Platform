package eu.excitementproject.eop.distsim.storage;


import java.io.Serializable;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;

/**
 * The BasicSet interface defines the basic functionality of Set: add, iterator 
 * 
 * @author Meni Adler
 * @since 15/08/2012
 *
 * 
 * @param <V>  the type of the values of the set
 */
public interface BasicSet<V extends Serializable> extends Serializable {
	/**
	 * Associates the specified value with the specified key in this map (optional operation). If the map previously contained a mapping for this key, the old value is replaced by the specified value.
	 * 
	 * @param value value to be associated with the specified key.
	 */
	void add(V value);
	
	/**
	 * Gets an (immutable) iterator for the set values
	 * 
	 * @return an immutable iterator for the set values
	 */
	ImmutableIterator<V> iterator();
	
	/**
	 * Gets the size of the set
	 * 
	 * @return the size of the set
	 */
	int size();
	
	/**
	 * Checks if the given item is included in the set
	 * 
	 * @param item some item
	 * @return true if the set contains the given item
	 */
	boolean contains(V item);
}
