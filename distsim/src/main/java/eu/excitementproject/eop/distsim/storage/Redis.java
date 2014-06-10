/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.io.IOException;

import java.io.Serializable;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.Serialization;
import eu.excitementproject.eop.distsim.util.SerializationException;
import eu.excitementproject.eop.redis.BasicRedisRunner;

/**
 * An implementation of of the PersistenceDevice, based on a given Redis storage server 
 *
 * @author Meni Adler
 * @since 24/07/2012
 *
 */
public class Redis implements PersistenceDevice {

	private static Logger logger = Logger.getLogger(Redis.class);
	
	public Redis(String dbFile) {
		this.dbFile = dbFile;
		this.jedis = null;
		this.currID = -1;
		
	}
	
	public Redis(ConfigurationParams params) throws ConfigurationException {
		this.dbFile = params.get(Configuration.REDIS_FILE);
		this.jedis = null;
		this.currID = -1;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.PersistenceDevice#open()
	 */
	@Override
	public void open() throws IOException {
		try {
			int port = BasicRedisRunner.getInstance().run(dbFile);
			JedisPoolConfig config = new JedisPoolConfig();
			JedisPool pool = new JedisPool(config, "localhost",port,10000);
			jedis = pool.getResource();
			jedis.connect();
			jedis.getClient().setTimeoutInfinite();
			//jedis = new Jedis(host,port,10000);
			currID = 0;
		} catch (Exception e) {
			throw new IOException(e);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.PersistenceDevice#save(int, org.excitement.distsim.items.Externalizable)
	 */
	@Override
	public void write(int id, Serializable data) throws SerializationException {
		write(Integer.toString(id), Serialization.serialize(data));
	}

	public void write(String id, String data) throws SerializationException {
		jedis.set(id, data);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.PersistenceDevice#read()
	 */
	@Override
	public Pair<Integer, Serializable> read() throws SerializationException {
		currID++;
		/*String val=null;
		while ((val=jedis.get(Integer.toString(currID))) == null) {
			currID++;			
		}*/
		String val = jedis.get(Integer.toString(currID));
		if (val == null)
			return null;
		else
			return new Pair<Integer, Serializable>(currID,Serialization.deserialize(val));
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.PersistenceDevice#getType()
	 */
	@Override
	public PersistenceDeviceType getType() {
		return PersistenceDeviceType.REDIS;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.PersistenceDevice#close()
	 */
	@Override
	public void close() {
		jedis.save();
		jedis.disconnect();
		jedis = null;
		this.currID = -1;
		try {
			BasicRedisRunner.getInstance().close(dbFile);
		} catch (Exception e) {
			logger.info(e.toString());
		}
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
	
	public void clear() {
		if (jedis != null)
			jedis.flushAll();
	}
	
	public void rpush(String id, String data) {
		jedis.rpush(id, data);
	}
	
	protected Jedis jedis;
	protected int currID;
	
	protected final String dbFile;
}
