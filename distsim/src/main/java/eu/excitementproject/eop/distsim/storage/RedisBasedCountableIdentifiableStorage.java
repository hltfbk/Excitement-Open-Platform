package eu.excitementproject.eop.distsim.storage;

import java.util.HashSet;

import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.items.Countable;
import eu.excitementproject.eop.distsim.items.Externalizable;
import eu.excitementproject.eop.distsim.items.Identifiable;
import eu.excitementproject.eop.distsim.items.InvalidCountException;
import eu.excitementproject.eop.distsim.items.UndefinedKeyException;
import eu.excitementproject.eop.distsim.storage.iterators.RedisBasedIterator;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Resetable;
import eu.excitementproject.eop.distsim.util.Serialization;
import eu.excitementproject.eop.distsim.util.SerializationException;
import eu.excitementproject.eop.redis.BasicRedisRunner;
import eu.excitementproject.eop.redis.RedisRunException;

import org.apache.log4j.Logger;
/**
 * An implementation of the {@link CountableIdentifiableStorage} interface, which stored objects with id, based on Redis DB
 * 
 * Thread-safe
 * 
 * @author Meni Adler
 * @since 19/07/2012
 * 
 * @param <T> The type of the data of the storage
 * 
  */
public class RedisBasedCountableIdentifiableStorage<T extends Externalizable & Countable & Identifiable> 
	extends PersistenceCountableIdentifiableStorage<T>
	implements PatternBasedCountableIdentifiableStorage<T>, Resetable {
	
	private static final Logger logger = Logger.getLogger(RedisBasedCountableIdentifiableStorage.class);

	
	/*public RedisBasedCountableIdentifiableStorage(String host, int port) {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), host,port);
		jedis = pool.getResource();
		jedis.connect();
		jedis.getClient().setTimeoutInfinite();
	}*/

	public RedisBasedCountableIdentifiableStorage(String dbFile) throws RedisRunException {
		this.dbFile = dbFile;
		int port = BasicRedisRunner.getInstance().run(dbFile);
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost",port);
		jedis = pool.getResource();
		jedis.connect();
		jedis.getClient().setTimeoutInfinite();
	}
	
	public RedisBasedCountableIdentifiableStorage(ConfigurationParams params) throws ConfigurationException, RedisRunException {
		this(params.getString(Configuration.REDIS_FILE));
	}

	public RedisBasedCountableIdentifiableStorage(String dbFile, PersistenceDevice device) throws LoadingStateException, RedisRunException {
		this(dbFile);
		loadState(device);
	}
	
	public RedisBasedCountableIdentifiableStorage(ConfigurationParams params, PersistenceDevice device) throws ConfigurationException, LoadingStateException, RedisRunException {
		this(params);
		loadState(device);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#getData(int)
	 */
	@Override
	public T getData(int id) throws ItemNotFoundException, SerializationException {
		String val = jedis.get(Integer.toString(id));
		if (val == null)
			throw new ItemNotFoundException("No item, assigned to id " + id + ", was found");
		//tmp
		//return (T) new LemmaPosBasedElement(new LemmaPos("tmplemma",CanonicalPosTag.V));
		return Serialization.deserialize(val);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#getId(org.excitement.distsim.items.KeyExternalizable)
	 */
	@Override
	public int getId(T data) throws ItemNotFoundException, UndefinedKeyException {
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
	public T addData(T data) throws UndefinedKeyException, InvalidCountException, SerializationException {
		return addData(data,1);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#addData(org.excitement.distsim.items.KeyExternalizable, long)
	 */
	@Override
	public T addData(T data, double count) throws UndefinedKeyException, InvalidCountException, SerializationException {
		String key = data.toKey();
		String sID = jedis.get(key);
		if (sID == null) {
			int id = getNextId();
			data.setID(id);
			data.incCount(count);
			jedis.set(Integer.toString(id), Serialization.serialize(data));
			jedis.set(key, Integer.toString(id));
			return data;
		} else {
			T storedData = Serialization.deserialize(jedis.get(sID));
			storedData.incCount(count);
			jedis.set(sID,Serialization.serialize(storedData));
			return storedData;
		}
	}


	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#addData(org.excitement.distsim.items.KeyExternalizable)
	 */
	@Override
	public void add(int id, T data) throws UndefinedKeyException, SerializationException {
		String key = data.toKey();
		String sID = Integer.toString(id);
		jedis.set(key, sID);
		jedis.set(sID,Serialization.serialize(data));
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#removeData(org.excitement.distsim.items.KeyExternalizable)
	 */
	/*@Override
	public boolean removeData(T data)  throws UndefinedKeyException {
		String key = data.toKey();
		String sID = jedis.get(key);
		if (sID == null) {
			return false;
		} else {
			return jedis.del(sID,key) > 1;
		}
	}*/

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#size()
	 */
	@Override
	public int size() {
		//@TOTHINK
		return (int)(long)jedis.dbSize() / 2;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#iterator()
	 */
	@Override
	public ImmutableIterator<T> iterator() {
		return new RedisBasedIterator<T>(jedis,2);
	}


	/* (non-Javadoc)
	 * @see org.excitement.util.Resetable#reset()
	 */
	@Override
	public void reset() {
		jedis.flushAll();
	}

	
	/**
	 * Get a new unique id, based on the last existing id
	 * 
	 * @return a new unique id
	 */
	protected int getNextId() {
		return size() + 1;
	}

	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.PatternBasedCountableIdentifiableStorage#getItems(java.lang.String)
	 */
	@Override
	public Set<Integer> getItemIds(String regularExpr) {
		Set<Integer> ret = new HashSet<Integer>();
		
		//for (String key : jedis.keys(regularExpr)) 
			//ret.add(Integer.parseInt(jedis.get(key)));
			
		
		//@@tmp
		//System.out.println("regularExpr: " + regularExpr);
		ret.add(Integer.parseInt(jedis.get(regularExpr)));
		
		return ret;
	}

	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.PersistenceCountableIdentifiableStorage#saveState(org.excitement.distsim.storage.PersistenceDevice)
	 */
	@Override
	public void saveState(PersistenceDevice... devices) throws SavingStateException {
		if (beSavesOrLoaded(devices))
			super.saveState(devices);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.CountableIdentifiableStorage#resetCounts()
	 */
	@Override
	public void resetCounts() throws ResetCountsException {
		ImmutableIterator<T> it = iterator();
		while (it.hasNext()) {
			T item = it.next();
			item.setCount(0);
			try {
				jedis.set(Integer.toString(item.getID()), Serialization.serialize(item));
			} catch (Exception e) {
				throw new ResetCountsException(e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.PersistenceCountableIdentifiableStorage#loadState(org.excitement.distsim.storage.PersistenceDevice)
	 */
	@Override
	public void loadState(PersistenceDevice... devices) throws LoadingStateException {
		if (beSavesOrLoaded(devices))
			super.loadState(devices);		
	}

	public void clear() {
		jedis.flushAll();
	}

	protected boolean beSavesOrLoaded(PersistenceDevice... devices) {
		for (PersistenceDevice device : devices)
			if (device.getType() != PersistenceDeviceType.REDIS)
				return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() {
		try {
			BasicRedisRunner.getInstance().close(dbFile);
		} catch (Exception e) {
			logger.info(e.toString());
		}
	}
	
	protected Jedis jedis;
	protected final String dbFile;


}
