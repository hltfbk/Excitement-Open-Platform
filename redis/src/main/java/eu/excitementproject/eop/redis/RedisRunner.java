/**
 * 
 */
package eu.excitementproject.eop.redis;

/**
 * Management of Redis running, given a directory of Redis databases
 * 
 * @author Meni Adler
 * @since Nov 4, 2013
 *
 */
public interface RedisRunner {
	/**
	 * Runs a a local Redis server instance for a given database file. The file may not existed (as the case of running Redis on a new database) 
	 * 
	 * @param dbFile An existing or non-existing Redis database file. The parent directory of the given file should have writing permissions
	 * @return the port id of the Redis server instance for the given file in the given directory
	 * @throws RedisRunException
	 */
	int run(final String dbFile) throws RedisRunException;
		
	/**
	 * Stops the running of a given Redis server, specified by the given db file (in case no other references for the running Redis server exists)
	 * 
	 * @param dbFile An existing or non-existing Redis database file. The parent directory of the given file should have writing permissions
	 * 
	 * @throws RedisCloseException
	 */
	void close(final String dbFile) throws RedisCloseException;
}
