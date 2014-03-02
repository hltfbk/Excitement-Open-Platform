/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.redis.BasicRedisRunner;
import eu.excitementproject.eop.distsim.redis.RedisRunException;
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
	
	private static Logger logger = Logger.getLogger(RedisBasedStringListBasicMap.class);
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	public static final String ELEMENT_CLASS_NAME_KEY = "element-class-name";
	
	public RedisBasedStringListBasicMap(String dbFile, String redisDir) throws FileNotFoundException, RedisRunException {
		init(dbFile, redisDir);

	}
	
	public RedisBasedStringListBasicMap(String dbFile) throws FileNotFoundException, RedisRunException {
		init(dbFile);
	}

	public RedisBasedStringListBasicMap(String host, int port) {
		this.dbFile = null;
		init(host,port);
	}
	
	public RedisBasedStringListBasicMap(ConfigurationParams params) throws ConfigurationException, FileNotFoundException, RedisRunException {
		String dbFile = params.get(Configuration.REDIS_FILE);
		String redisDir = null;
		try {
			redisDir = params.get(Configuration.REDIS_BIN_DIR);
			init(dbFile,redisDir);
		} catch (ConfigurationException e) {
			init(dbFile);
		}
		
	}
	
	protected void init(String dbFile) throws FileNotFoundException, RedisRunException {
		this.dbFile = dbFile;
		int port = BasicRedisRunner.getInstance().run(dbFile);
		init("localhost",port);

	}

	protected void init(String dbFile, String redisDir) throws FileNotFoundException, RedisRunException {
		this.dbFile = dbFile;
		BasicRedisRunner.setRedisBinDir(redisDir);
		int port = BasicRedisRunner.getInstance().run(dbFile);
		init("localhost",port);

	}
	
	protected void init(String host, int port) {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), host,port);
		jedis = pool.getResource();
		jedis.connect();
		jedis.getClient().setTimeoutInfinite();
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

	public List<String> getTopN(String key, long n) {
		//long t1 = System.currentTimeMillis();
		List<String> ret= jedis.lrange(key, 0, n-1);
		//long t2 = System.currentTimeMillis();
	//	System.out.println("getTopN time: " + (t2-t1) + " ms");
		return ret;
	}
	
	/**
	 * Gets the name of the type of the elements in the database, based on the assumption that the type name is stored in the database under a well-defined key. 
	 * 
	 * @return the name of the element type. in case the name is not stored in the database, null value will be returned
	 */
	public String getElementClassName() {
		return  jedis.get(ELEMENT_CLASS_NAME_KEY);
	}

	/**
	 * Sets the name of the element types in the database, to the database under a well-defined key
	 * 
	 * @param elementClass The type of the elements in the database
	 */
	public void setElementClassName(String elementClassName) {
		jedis.set(ELEMENT_CLASS_NAME_KEY, elementClassName);
	}

	/**
	 * Close and release Redis processes
	 */
	public void close() {
		try {
			if (dbFile != null)
				BasicRedisRunner.getInstance().close(dbFile);
		} catch (Exception e) {
			logger.info(e.toString());
		}
	}
	
	
	
	protected String dbFile;
	protected Jedis jedis;
}
