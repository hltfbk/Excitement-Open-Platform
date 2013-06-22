package eu.excitementproject.eop.distsim.items;

import java.io.Serializable;

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
	 */
	String toKey() throws UndefinedKeyException;
}
