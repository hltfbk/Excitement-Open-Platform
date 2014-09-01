/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.io.Serializable;


import org.apache.log4j.Logger;

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
import eu.excitementproject.eop.redis.BasicRedisRunner;
import eu.excitementproject.eop.redis.RedisRunException;

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

	private static Logger logger = Logger.getLogger(RedisBasedBasicMap.class);
	
	private static final long serialVersionUID = 1L;
	
	public RedisBasedBasicMap(String dbFile, boolean bVM) throws RedisRunException {
		init(dbFile,bVM);
	}

	public RedisBasedBasicMap(ConfigurationParams params) throws ConfigurationException, RedisRunException {
		boolean bVM = false;
		try {
			bVM = params.getBoolean(eu.excitementproject.eop.redis.Configuration.REDIS_VM);
		} catch (ConfigurationException e) {}
		init(params.get(Configuration.REDIS_FILE),bVM);
	}

	protected void init(String dbFile, boolean bVM) throws RedisRunException {
		this.dbFile = dbFile;
		int port = BasicRedisRunner.getInstance().run(dbFile,bVM);
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost",port);
		jedis = pool.getResource();
		jedis.connect();
		jedis.getClient().setTimeoutInfinite();
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
	protected String dbFile;
	protected boolean bVM;

}
