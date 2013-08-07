package eu.excitementproject.eop.distsim.storage;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.items.Countable;
import eu.excitementproject.eop.distsim.items.Identifiable;
import eu.excitementproject.eop.distsim.items.InvalidCountException;
import eu.excitementproject.eop.distsim.items.InvalidIDException;
import eu.excitementproject.eop.distsim.items.UndefinedKeyException;
import eu.excitementproject.eop.distsim.util.SerializationException;

/**
 * An extension of the {@link Storage} interface, for objects that are countable and identifiable
 * 
 * @author Meni Adler
 * @since 19/07/2012
 *
 * 
 * @param <T> The type of the data of the storage
 */
public interface CountableIdentifiableStorage<T extends Serializable & Countable & Identifiable> extends Persistence  {

	/**
	 * Get the object assigned to the given id
	 * 
	 * @param id an id of some stored object
	 * @return the object assigned to the given id
	 * @throws ItemNotFoundException in a case where the given id is not assigned to any stored object
	 */
	T getData(int id) throws ItemNotFoundException, SerializationException;
	
	
	/**
	 * Get the id assigned to the given object
	 * 
	 * @param data a KeyExternalizable object which is about to be stored in the database, under its string key serialization
	 * @return the id assigned to the given object
	 * @throws ItemNotFoundException in a case where the given object was not found in the storage
	 * @throws UndefinedKeyException in a case where the key serialization of the given data object is not defined
	 */
	int getId(T data) throws ItemNotFoundException, UndefinedKeyException, SerializationException;
	
	
	/**
	 * Add a given object to the storage, if it is not already stored (according to its key serialization).
	 * In case the data is already stored (according to its KeyExternalizable), the counting of the stored data will be increased by one.
	 * Otherwise the new id and an initial count (1) will be set at the data object.
	 * 
	 * @param data an object to be added to the storage
	 * @return the stored data
	 * @throws UndefinedKeyException in a case where the key serialization of the given data object is not defined
	 */	
	T addData(T data) throws UndefinedKeyException, InvalidCountException, SerializationException, InvalidIDException;

	
	/**
	 * Add a given object to the storage, if it is not already stored (according to its key serialization).
	 * In case the data is already stored (according to its KeyExternalizable), the counting of the stored data will be increased by the given count.
	 * Otherwise the new id and the given count will be set at the data object.
	 * 
	 * @param data an object to be added to the storage
	 * @param count the number of times the given data was seen
	 * @return the stored data
	 * @throws UndefinedKeyException in a case where the key serialization of the given data object is not defined
	 * @throws ExecutionException 
	 */	
	T addData(T data, double count) throws UndefinedKeyException, InvalidCountException,SerializationException, InvalidIDException, ExecutionException;

	/**
	 * Add a given object to the storage under the given id.
	 * 
	 * @param id a unique id for the data parameter
	 * @param data an object to be added to the storage
	 * @throws UndefinedKeyException in a case where the key serialization of the given data object is not defined
	 */
	void add(int id, T data) throws UndefinedKeyException,SerializationException;

	 
	 /**
	 * Remove a given object from the storage
	 * 
	 * @param data an object to be removed from the storage
	 * @return return true if the data was found in the storage and removed successfully
	 * @throws UndefinedKeyException in a case where the key serialization of the given data object is not defined
	 */
	//boolean removeData(T data) throws UndefinedKeyException;
	 
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
	ImmutableIterator<T> iterator();
	
	/**
	 * reset all count to zero
	 */
	void resetCounts() throws ResetCountsException;
	
}
