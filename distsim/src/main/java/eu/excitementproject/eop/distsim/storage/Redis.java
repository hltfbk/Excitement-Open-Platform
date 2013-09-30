/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.io.Serializable;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.Serialization;
import eu.excitementproject.eop.distsim.util.SerializationException;

/**
 * An implementation of of the PersistenceDevice, based on a given Redis storage server 
 *
 * @author Meni Adler
 * @since 24/07/2012
 *
 */
public class Redis implements PersistenceDevice {

	public Redis(String host, int port) {
		this.host = host;
		this.port = port;
		this.jedis = null;
		this.currID = -1;
		
	}
	
	public Redis(ConfigurationParams params) throws ConfigurationException {
		this.host = params.get("redis-host");
		this.port = params.getInt("redis-port");
		this.jedis = null;
		this.currID = -1;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.PersistenceDevice#open()
	 */
	@Override
	public void open() {
		JedisPoolConfig config = new JedisPoolConfig();
		JedisPool pool = new JedisPool(config, host,port,10000);
		jedis = pool.getResource();
		jedis.connect();
		jedis.getClient().setTimeoutInfinite();
		//jedis = new Jedis(host,port,10000);
		currID = 0;
		
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
			return new Pair<Integer, Serializable>(currID,(Serializable)Serialization.deserialize(val));
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
		jedis = null;;
		this.currID = -1;
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
	
	protected final String host;
	protected final int port;
}
