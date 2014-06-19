package eu.excitementproject.eop.distsim.storage;


import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.items.Countable;
import eu.excitementproject.eop.distsim.items.Externalizable;
import eu.excitementproject.eop.distsim.items.Identifiable;
import eu.excitementproject.eop.distsim.items.InvalidCountException;
import eu.excitementproject.eop.distsim.items.InvalidIDException;
import eu.excitementproject.eop.distsim.items.UndefinedKeyException;
import eu.excitementproject.eop.distsim.storage.iterators.TroveBasedValuesIterator;
import eu.excitementproject.eop.distsim.util.Resetable;
import eu.excitementproject.eop.distsim.util.SerializationException;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;


/**
 * An implementation of the {@link CountableIdentifiableStorage} interface, which stored objects with id, based on a map in the memory
 *  
 * The class is Thread-safe, excluding the usage of the iterator in parallel to writing operations
 *
 * @author Meni Adler
 * @since 19/07/2012
 * 
 * @param <T> The type of the data of the storage
 * 
 */
public class MemoryBasedCountableIdentifiableStorage<T extends Externalizable & Countable & Identifiable> 
	extends PersistenceCountableIdentifiableStorage<T>
	implements CountableIdentifiableStorage<T>, Resetable {
	
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(MemoryBasedCountableIdentifiableStorage.class);
	
	public MemoryBasedCountableIdentifiableStorage(ConfigurationParams params) throws LoadingStateException {
		this();
	}

	public MemoryBasedCountableIdentifiableStorage(ConfigurationParams params,PersistenceDevice persistenceDevice) throws LoadingStateException {
		this(persistenceDevice);
	}

	public MemoryBasedCountableIdentifiableStorage() {
		itemkey2id = new TObjectIntHashMap<String>();
		id2item = new TIntObjectHashMap<T>();
	}
	
	public MemoryBasedCountableIdentifiableStorage(PersistenceDevice persistenceDevice) throws LoadingStateException {
		itemkey2id = new TObjectIntHashMap<String>();
		id2item = new TIntObjectHashMap<T>();		
		loadState(persistenceDevice);
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#getData(int)
	 */
	@Override
	public synchronized T getData(int id) throws ItemNotFoundException {
		T ret = id2item.get(id);
		if (ret == null)
			throw new ItemNotFoundException("No item, assigned to id " + id + ", was found");
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#getId(org.excitement.distsim.items.KeyExternalizable)
	 */
	@Override
	public synchronized int getId(T data) throws ItemNotFoundException, UndefinedKeyException {
		String key = data.toKey();
		if (itemkey2id.containsKey(key))
			return itemkey2id.get(key);
		else
			throw new ItemNotFoundException(data.toString());
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#addData(org.excitement.distsim.items.KeyExternalizable)
	 */
	@Override
	public synchronized T addData(T data) throws UndefinedKeyException, InvalidCountException, SerializationException, InvalidIDException {
		return addData(data,1);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#addData(org.excitement.distsim.items.KeyExternalizable, long)
	 */
	@Override
	public synchronized T addData(T data, double count) throws UndefinedKeyException, InvalidCountException, SerializationException, InvalidIDException {
		
		String key = data.toKey();
		if (!itemkey2id.containsKey(key)) {
			int id = getNextId();
			data.setID(id);
			data.incCount(count);
			id2item.put(id, data);
			itemkey2id.put(key, id);
			return data;
		} else {
			int id = itemkey2id.get(key);
			T storedData = id2item.get(id);
			storedData.incCount(count);
			return storedData;
		}
	}


	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#addData(org.excitement.distsim.items.KeyExternalizable)
	 */
	@Override
	public synchronized void add(int id, T data) throws UndefinedKeyException, SerializationException {
		//@tmp
		//try {
			String key = data.toKey();
			itemkey2id.put(key, id);
			data.setID(id);
			id2item.put(id,data);
		//} catch (Exception e) {
			//logger.warn(e.toString());
		//}
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#removeData(org.excitement.distsim.items.KeyExternalizable)
	 */
	/*@Override
	public synchronized boolean removeData(T data)  throws UndefinedKeyException {
		String key = data.toKey();
		if (!itemkey2id.containsKey(key)) {
			return false;
		} else {
			int id = itemkey2id.get(key);
			itemkey2id.remove(key);
			id2item.remove(id);
			return true;
		}
	}*/


	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#size()
	 */
	@Override
	public synchronized int size() {
		return id2item.size();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#iterator()
	 */
	@Override
	public synchronized ImmutableIterator<T> iterator() {
		return new TroveBasedValuesIterator<T>(id2item.iterator());
	}


	/* (non-Javadoc)
	 * @see org.excitement.util.Resetable#reset()
	 */
	@Override
	public synchronized void reset() {
		itemkey2id.clear();
		id2item.clear();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#resetCounts()
	 */
	@Override
	public synchronized void resetCounts() {
		TIntObjectIterator<T> it = id2item.iterator();
		while (it.hasNext()) {
			it.advance();
			it.value().setCount(0);
		}
	}

	
	/**
	 * Get a new unique id, based on the last existing id
	 * 
	 * @return a new unique id
	 */
	protected synchronized int getNextId() throws InvalidIDException{
		/*int last = itemkey2id.size();
		if (last == 0)
			return 1;
		else {
			T lastItem = id2item.get(last);
			return lastItem.getID() + 1;
		}*/
		return itemkey2id.size() + 1;
	}
	
	protected TObjectIntHashMap<String> itemkey2id;
	protected TIntObjectHashMap<T> id2item;

}
