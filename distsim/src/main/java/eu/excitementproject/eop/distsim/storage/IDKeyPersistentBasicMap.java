package eu.excitementproject.eop.distsim.storage;

import java.io.Serializable;

/**
 * Defines a basic map which is persistent, i.e., can be stored for long term
 * 
 * @author Meni Adler
 * @since 28/12/2012
 *
 * @param <V> the type of the values in the map
 */
public interface IDKeyPersistentBasicMap<V extends Serializable> extends BasicMap<Integer,V>, Persistence {

}
