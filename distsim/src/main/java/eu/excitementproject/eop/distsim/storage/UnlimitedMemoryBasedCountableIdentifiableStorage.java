package eu.excitementproject.eop.distsim.storage;


import java.io.PrintStream;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.items.Countable;
import eu.excitementproject.eop.distsim.items.Externalizable;
import eu.excitementproject.eop.distsim.items.Identifiable;
import eu.excitementproject.eop.distsim.items.InvalidCountException;
import eu.excitementproject.eop.distsim.items.InvalidIDException;
import eu.excitementproject.eop.distsim.items.UndefinedKeyException;
import eu.excitementproject.eop.distsim.storage.iterators.TroveBasedValuesIterator;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Resetable;
import eu.excitementproject.eop.distsim.util.Serialization;
import eu.excitementproject.eop.distsim.util.SerializationException;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
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
public class UnlimitedMemoryBasedCountableIdentifiableStorage<T extends Externalizable & Countable & Identifiable> 
	extends PersistenceCountableIdentifiableStorage<T>
	implements CountableIdentifiableStorage<T>, Resetable {
	
	protected static final long MIN_FREE_MEMORY_BYTES = 1000000;
	private static final Logger logger = Logger.getLogger(UnlimitedMemoryBasedCountableIdentifiableStorage.class);;
	
	public UnlimitedMemoryBasedCountableIdentifiableStorage(ConfigurationParams params) throws LoadingStateException {
		this();
		try {
			initRedis(params.get(Configuration.REDIS_HOST),Integer.parseInt(params.get(Configuration.REDIS_PORT)));
			tmpDIR = params.get(Configuration.TMP_CONTENT_DIR);
			tmpFileIndex = 0;
		} catch (Exception e) {
			throw new LoadingStateException(e);
		}
	}

	public UnlimitedMemoryBasedCountableIdentifiableStorage(ConfigurationParams params,PersistenceDevice persistenceDevice) throws LoadingStateException {
		this(persistenceDevice);
		try {
			initRedis(params.get(Configuration.REDIS_HOST),Integer.parseInt(params.get(Configuration.REDIS_PORT)));
			tmpDIR = params.get(Configuration.TMP_CONTENT_DIR);
			tmpFileIndex = 0;
		} catch (Exception e) {
			throw new LoadingStateException(e);
		}
	}

	protected void initRedis(String host,int port) {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), host,port);
		jedis = pool.getResource();
		jedis.connect();
		jedis.getClient().setTimeoutInfinite();
		jedis.flushAll();

	}
	protected UnlimitedMemoryBasedCountableIdentifiableStorage() {
		id2item = new TIntObjectHashMap<T>();
	}
	
	protected UnlimitedMemoryBasedCountableIdentifiableStorage(PersistenceDevice persistenceDevice) throws LoadingStateException {
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
		String val = jedis.get(data.toKey());
		if (val != null)
			return Integer.parseInt(val);
		else
			throw new ItemNotFoundException(data.toKey());
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
		String sID = jedis.get(key);
		if (sID == null) {
			int id = getNextId();
			data.setID(id);
			data.incCount(count);
			jedis.set(key, Integer.toString(id));
			id2item.put(id, data);
			return data;
		} else {
			checkMemoryLimit();
			int id = Integer.parseInt(sID);
			T storedData = id2item.get(id);
			if (storedData != null) {
				storedData.incCount(count);
				return storedData;
			} else {
				data.setID(id);
				data.incCount(count);
				id2item.put(id, data);
				return data;
			}
		}
	}


	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#addData(org.excitement.distsim.items.KeyExternalizable)
	 */
	@Override
	public synchronized void add(int id, T data) throws UndefinedKeyException, SerializationException {
		checkMemoryLimit();		
		String key = data.toKey();
		String sID = Integer.toString(id);
		jedis.set(key, sID);
		data.setID(id);
		id2item.put(id,data);
	}

	protected void checkMemoryLimit() {
		if (testMemoryLimit()) 
			dumpMap();
	}

	protected void dumpMap() {
		try {
			tmpFileIndex++;
			String outfile = tmpDIR + "/" + tmpFileIndex + ".tmp";
			logger.info("dumping memory to file: " + outfile);
			PrintStream tmpContentFile = new PrintStream(outfile);
			TIntObjectIterator<T> it = id2item.iterator();
			while (it.hasNext()) {
				it.advance();
				T item = it.value();
				tmpContentFile.print(item.getID());
				tmpContentFile.print("\t");
				tmpContentFile.print(Serialization.serialize(item));
				tmpContentFile.print("\n");
				it.remove();
			}
			tmpContentFile.close();
		} catch (Exception e) {
			logger.error(e.toString());
		}		
	}

	protected boolean testMemoryLimit() {
		Runtime runtime = Runtime.getRuntime(); 
		return (runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory())) < MIN_FREE_MEMORY_BYTES;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#size()
	 */
	@Override
	public synchronized int size() {
		return (int)(long)jedis.dbSize();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#iterator()
	 */
	@Override
	public ImmutableIterator<T> iterator() {
		return new TroveBasedValuesIterator<T>(id2item.iterator());
	}


	/* (non-Javadoc)
	 * @see org.excitement.util.Resetable#reset()
	 */
	@Override
	public synchronized void reset() {
		jedis.flushAll();
		id2item.clear();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#resetCounts()
	 */
	@Override
	public void resetCounts() {
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
		return size() + 1;
	}
	
	protected TIntObjectHashMap<T> id2item;
	protected Jedis jedis;
	protected String tmpDIR;
	protected int tmpFileIndex;
}
