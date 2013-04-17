package eu.excitementproject.eop.distsim.storage;

import java.io.IOException;
import java.io.Serializable;

import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.SerializationException;

/**
 * Persistence devices store KeyExternalizable items under unique IDs, in order to support the {@link Storage} interface 
 * 
 * @author Meni Adler
 * @since 22/07/2012
 *
 */
public interface PersistenceDevice {
	
	/**
	 * Close the persistence device
	 * @throws IOException 
	 */
	void open() throws IOException;
	
	/**
	 * Save the given data under the given id
	 * 
	 * @param id a unique id 
	 * @param data an object
	 * @throws SerializationException for any problem with object serialization
	 * @throws IOException 
	 */
	void write(int id, Serializable data) throws SerializationException, IOException;
	
	/**
	 * Read next id-based data from the persistence device
	 * 
	 * @return next item with its id. if no next item was found, returns null.
	 * @throws SerializationException for any problem with object deserialization
	 * @throws IOException 
	 */
	Pair<Integer, Serializable> read() throws SerializationException, IOException; 
	
	/**
	 * Get the type of this persistence device
	 * 
	 * @return the type of the persistence device
	 */
	PersistenceDeviceType getType();
	
	/**
	 * Close the persistence device
	 * @throws IOException 
	 */
	void close() throws IOException;

}
