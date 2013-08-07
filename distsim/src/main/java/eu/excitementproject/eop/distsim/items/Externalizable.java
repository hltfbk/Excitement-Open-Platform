package eu.excitementproject.eop.distsim.items;

import java.io.Serializable;
import java.util.Set;

/**
 * The unique properties of an <i>Externalizable<\i> object can be represented by a string,
 * and its state can be serialized and se-serialized to/from a string.
 * 
 * @author Meni Adler
 * @since 19/06/2012
 *
 *
 */
public interface Externalizable extends Serializable {
	/**
	 * Encoding of the unique properties of the object into a string
	 * 
	 * @return a unique key representation for the object
	 * 
	 * @throws UndefinedKeyException for any key encoding problem
	 */
	String toKey() throws UndefinedKeyException;
	
	/**
	 * Encoding the variation of the properties of the object into a set of strings
	 * 
	 * @return a unique key representation for the object
	 * 
	 * @throws UndefinedKeyException for any key encoding problem
	 */
	Set<String> toKeys() throws UndefinedKeyException;
	
	/* 
	 * Construct 'this' object by decoding its properties from a given key encoding
	 * 
	 * @param key an encoding of the object properties
	 * 
	 * @throws UndefinedKeyException for any key encoding problem
	 */
	void fromKey(String key) throws UndefinedKeyException;

}
