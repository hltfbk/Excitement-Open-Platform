package eu.excitementproject.eop.distsim.storage;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.items.Externalizable;
import eu.excitementproject.eop.distsim.items.UndefinedKeyException;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.SerializationException;

/**
 * Defines the basic functionality for id-based storage of objects that can be represented by unique strings 
 * 
 * @author Meni Adler
 * @since 19/07/2012
 * 
 * @param <T> The type of the data of the storage
 */
public interface Storage<T extends Externalizable> extends Persistence {
	

	/**
	 * Get the object assigned to the given id
	 * 
	 * @param id an id of some stored object
	 * @return the object assigned to the given id
	 * @throws ItemNotFoundException in a case where the given id is not assigned to any stored object
	 * @throws SerializationException for any problem of object deserialization
	 */
	T getData(int id) throws ItemNotFoundException, SerializationException;
	
	
	/**
	 * Get the id assigned to the given object
	 * 
	 * @param data a KeyExternalizable object which is about to be stored in the database, under its string key serialization
	 * @return the id assigned to the given object
	 * @throws ItemNotFoundException in a case where the given object was not found in the storage
	 * @throws UndefinedKeyException in a case where the key serialization of the given data object is not defined
	 * @throws SerializationException for any problem of object serialization
	 */
	int getId(T data) throws ItemNotFoundException, UndefinedKeyException, SerializationException;
	
	
	/**
	 * Add a given object to the storage, in case it is not already stored (according to its key serialization), and return its id.
	 * 
	 * @param data an object to be added to the storage
	 * @return the id assigned to the data in the storage
	 * @throws UndefinedKeyException in a case where the key serialization of the given data object is not defined
	 * @throws SerializationException for any problem of object serialization
	 */
	int addData(T data) throws UndefinedKeyException, SerializationException;
	 
	/**
	 * Add a given object to the storage under the given id.
	 * 
	 * @param id a unique id for the data parameter
	 * @param data an object to be added to the storage
	 * @throws UndefinedKeyException in a case where the key serialization of the given data object is not defined
	 * @throws SerializationException for any problem of object serialization
	 */
	void add(int id, T data) throws UndefinedKeyException, SerializationException;
	
	
	 /**
	 * Remove a given object from the storage
	 * 
	 * @param data an object to be removed from the storage
	 * @return return true if the data was found in the storage and removed successfully
	 * @throws UndefinedKeyException in a case where the key serialization of the given data object is not defined
	 */
	boolean removeData(T data) throws UndefinedKeyException;
	 
	/**
	 * Get the number of objects in the storage
	 * 
	 * @return the number of unique objects in the storage
	 */
	int size();

	
	/**
	 * Get an iterator for the storage data
	 * 
	 * @return an iterator for the stored objects and their ids
	 */
	ImmutableIterator<Pair<Integer,T>> iterator();
}
