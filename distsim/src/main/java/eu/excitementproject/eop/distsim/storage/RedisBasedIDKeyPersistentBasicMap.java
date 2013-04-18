/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.io.Serializable;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.storage.iterators.RedisBasedIterator;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.Serialization;
import eu.excitementproject.eop.distsim.util.SerializationException;

/**
 * An implementation of the BasicMap interface for integer keys, based on Redis
 *
 * The thread is safe for parallel put/get operation, but not safe for the parallel put/iterator operation
 * 
 * @author Meni Adler
 * @since 12/08/2012
 *
 */
public class RedisBasedIDKeyPersistentBasicMap<V extends Serializable> extends DefaultPersistentBasicMap<V> implements PersistentBasicMap<V> {

	
	private static final long serialVersionUID = 1L;
	
	public RedisBasedIDKeyPersistentBasicMap(String host, int port) {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), host,port);
		jedis = pool.getResource();
		jedis.connect();
		jedis.getClient().setTimeoutInfinite();
	}

	public RedisBasedIDKeyPersistentBasicMap(ConfigurationParams params) throws ConfigurationException {
		this(params.get(Configuration.REDIS_HOST),params.getInt(Configuration.REDIS_PORT));
	}

	public RedisBasedIDKeyPersistentBasicMap(String host, int port, PersistenceDevice device) throws LoadingStateException {
		this(host,port);
		loadState(device);
	}
	
	public RedisBasedIDKeyPersistentBasicMap(ConfigurationParams params, PersistenceDevice device) throws ConfigurationException, LoadingStateException {
		this(params);
		loadState(device);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ThinMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public synchronized void put(Integer key, V value) throws BasicMapException {
		try {
			jedis.set(key.toString(),Serialization.serialize(value));
		} catch (SerializationException e) {
			throw new BasicMapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ThinMap#get(java.lang.Object)
	 */
	@Override
	public synchronized  V get(Integer key) throws BasicMapException {
		String val = jedis.get(key.toString());
		if (val == null)
			return null;
		else
			try {
				return Serialization.deserialize(val);
			} catch (SerializationException e) {
				throw new BasicMapException(e);
			}
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.Persistence#loadState()
	 */
	@Override
	public void loadState(PersistenceDevice... devices) throws LoadingStateException {
		jedis.flushAll();
		super.loadState(devices);
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.BasicMap#keys()
	 */
	@Override
	public ImmutableIterator<Pair<Integer,V>> iterator() {
		return new RedisBasedIterator<Pair<Integer,V>>(jedis);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.BasicMap#size()
	 */
	@Override
	public int size() {
		return (int)(long)jedis.dbSize();
	}

	public void clear() {
		jedis.flushAll();
	}

	protected Jedis jedis;

}
