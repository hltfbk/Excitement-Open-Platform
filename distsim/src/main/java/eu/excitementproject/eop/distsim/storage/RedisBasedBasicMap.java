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
public class RedisBasedBasicMap<K extends Serializable, V extends Serializable> implements BasicMap<K,V> {

	
	private static final long serialVersionUID = 1L;
	
	public RedisBasedBasicMap(String host, int port) {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), host,port);
		jedis = pool.getResource();
		jedis.connect();
		jedis.getClient().setTimeoutInfinite();
	}

	public RedisBasedBasicMap(ConfigurationParams params) throws ConfigurationException {
		this(params.get(Configuration.REDIS_HOST),params.getInt(Configuration.REDIS_PORT));
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ThinMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public synchronized void put(K key, V value) throws BasicMapException {
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
	public synchronized  V get(K key) throws BasicMapException {
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
	 * @see org.excitement.distsim.storage.BasicMap#keys()
	 */
	@Override
	public ImmutableIterator<Pair<K,V>> iterator() {
		return new RedisBasedIterator<Pair<K,V>>(jedis);
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
