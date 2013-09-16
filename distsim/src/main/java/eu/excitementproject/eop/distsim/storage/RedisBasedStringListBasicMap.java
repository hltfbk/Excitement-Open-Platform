/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Configuration;

/**
 * An implementation of the BasicMap interface for integer keys, based on Redis
 *
 * The thread is safe for parallel put/get operation, but not safe for the parallel put/iterator operation
 * 
 * @author Meni Adler
 * @since 12/08/2012
 *
 */
public class RedisBasedStringListBasicMap {

	
	public static final String ELEMENT_SCORE_DELIMITER = "%";
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	
	public RedisBasedStringListBasicMap(String host, int port) {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), host,port);
		jedis = pool.getResource();
		jedis.connect();
		jedis.getClient().setTimeoutInfinite();
	}

	public RedisBasedStringListBasicMap(ConfigurationParams params) throws ConfigurationException {
		this(params.get(Configuration.REDIS_HOST),params.getInt(Configuration.REDIS_PORT));
	}


	public synchronized  List<String> get(String key) throws BasicMapException {
		return jedis.lrange(key, 0, -1);
	}

	public synchronized  String get(String key1, String key2) throws BasicMapException {
		key2 = key2 + ELEMENT_SCORE_DELIMITER;		
		List<String> lst = jedis.lrange(key1, 0, -1);
		for (String s : lst)
			if (s.startsWith(key2))
				return s.substring(key2.length());
		return null;
		
	}

	public List<String> getTopN(String key, int n) {
		//long t1 = System.currentTimeMillis();
		List<String> ret= jedis.lrange(key, 0, n-1);
		//long t2 = System.currentTimeMillis();
	//	System.out.println("getTopN time: " + (t2-t1) + " ms");
		return ret;
	}

	protected Jedis jedis;
}
