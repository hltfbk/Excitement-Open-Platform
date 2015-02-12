/**
 * 
 */
package eu.excitementproject.eop.redis;

import java.io.BufferedReader;




import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.OS;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

/*import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.DefaultExecutor;*/

/**
 * A data structure for the basic info of running redis process
 * 
 * Thread-safe
 * 
 * @author Meni Adler
 * @since Nov 5, 2013
 *
 */
class RedisInstanceInfo { 
	
	/**
	 * @param port the port number of this instance of Redis server 
	 * @param process the process handler of this instance of Redis server
	 */
	RedisInstanceInfo(int port, Process process) {
		this.port = port;
		this.process = process;
		this.refCount = 1;
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public synchronized String toString() {
		return "[port: " + port + ", refCount:" + refCount +"]";
	}



	/**
	 * Increments the reference count of this instance of Redis server
	 */
	public synchronized void incRef() {
		refCount++;
	}
	
	/**
	 * Decrements the reference count of this instance of Redis server
	 */
	public synchronized void decRef() {
		refCount--;
	}
	
	/**
	 * Check it is the last reference to this instance of Redis server
	 * 
	 * @return true if there is only one reference to this instance of Redis server
	 */
	public synchronized boolean isLastRef() {
		return refCount == 1;
	}

	/**
	 * Gets the number of references for this instance of Redis server
	 * 
	 * @return the number of references for this instance of Redis server
	 */
	public synchronized int getRef() {
		return refCount;
	}

	/**
	 * Returns the port number of this instance of Redis server 
	 * 
	 * @return the port number of this instance of Redis server
	 */
	public synchronized int getPort() {
		return port;
	}
	
	/**
	 * Returns the process handler of this instance of Redis server
	 * 
	 * @return the process handler of this instance of Redis server
	 */
	public synchronized Process getProcess() {
		return process;
	}
	
	/**
	 * The port of this instance of Redis server
	 */
	private final int port;
	
	/**
	 * The number of resources which are connected to this instance of Redis server 
	 */
	private int refCount;
	
	/**
	 * The redis server process handler
	 */
	private final Process process;
}

/**
 * A general (singleton) implementation of the {@link eu.excitementproject.eop.redis.RedisRunner} interface
 *      
 * 
 * @author Meni Adler
 * @since Nov 4, 2013
 *
 */
public class BasicRedisRunner implements RedisRunner {

	protected static final int MIN_PORT = 6379;
	protected static final int MAX_PORT = 65535;
	protected static final String DEFAULT_TEMPLATE_CONFIGURATION_FILE_NAME = "redis.conf";
	public static final String DEFAULT_REDIS_BIN_DIR = "redis";
	protected static final String PID_FILE = "pidfile";
	protected static final String PORT = "port";
	protected static final String DIR = "dir";
	protected static final String DB_FILE_NAME = "dbfilename";
	protected static final String VM_SWAP_FILE = "vm-swap-file";
	protected static final String CONF_FILE_EXT = ".conf";	
	protected static final String PID_FILE_EXT = ".pid";
	protected static final String VM_SWAP_FILE_EXT = ".swap";
	protected static final String DB_FILE_EXT = ".rdb";
	protected static final String VM_ENABLED = "vm-enabled";
	protected static final String REALY_USE_VM = "really-use-vm";

	protected static RedisRunner instance = null;
	protected static String redisBinDir; 	 //a path to the Redis binary directory
	private static final Logger logger = Logger.getLogger(BasicRedisRunner.class);

	
	/**
	 * Returns a singleton instance of BasicRedisRunner, based on a default location of the Redis binary directory (./redis/) and a default location of the Redis configuration template file (./redis/redis.conf) 
	 * 
	 * @return a singleton instance of BasicRedisRunner
	 * 
	 * @throws RedisRunException in case the default redis binary directory and/or the default configuration file are not existed
	 * or does not support current OS  
	 */
	public synchronized static RedisRunner getInstance() throws RedisRunException {
		if (instance == null) {
			instance = new BasicRedisRunner();
		}
		return instance;
	}
	
	/**
	 * Returns a singleton instance of BasicRedisRunner, based on a default location of the Redis binary directory (./redis/) and a given location of the Redis configuration template file 
	 * 
	 * @param templateConfigurationFile a path to a Redis configuration file
	 * @return a singleton instance of BasicRedisRunner
	 * 
	 * @throws RedisRunException in case the default redis binary directory and/or the default configuration file are not existed, 
	 *  or does not support current OS 
	 */
	public synchronized static RedisRunner getInstance(String templateConfigurationFile) throws RedisRunException {
		if (instance == null) {
			instance = new BasicRedisRunner();
		}
		try {
			((BasicRedisRunner)instance).setRedisConfigurationFileTemplate(templateConfigurationFile);
		} catch (FileNotFoundException e) {
			throw new RedisRunException(e);
		}
		return instance;
	}

	/**
	 * Initializes the redisBinDir static field 
	 * 
	 * @param redisBinDir a path to the Redis binary directory
	 */
	public synchronized static void setRedisBinDir(String redisBinDir) throws  RedisRunException {
		if (instance != null)
			logger.info("A singleton instance of redis runner, based on previous binary, is already run");
		else
			BasicRedisRunner.redisBinDir = redisBinDir;
	}

	
	/**
	 * Returns a singleton instance of BasicRedisRunner, based on the given configuration parameters.
	 * 
	 * @param params Defines the Redis binary directory and the Redis configuration file, as follows:
 	 * <ul>
	 * <li>redis-binary-dir: a path to the Redis binary directory (default, current directory)</li>
	 * <li>redis-configuration-template-file: a path to a Redis configuration file (default, desc/redis.conf) 
	 * </ul>
	 * 
	 * @return  a singleton instance of BasicRedisRunner
	 * 
	 * @throws RedisRunException in case the configured redis binary directory and/or configuration template file are not existed 
	 * or does not support current OS 
	 */
	public synchronized static RedisRunner getInstance(ConfigurationParams params) throws RedisRunException {
		if (instance == null) {			
			try {
				BasicRedisRunner.redisBinDir = params.get(Configuration.REDIS_BIN_DIR);
			} catch (ConfigurationException e) {			
			}
			instance = new BasicRedisRunner();
		}
		
		try {
			((BasicRedisRunner)instance).setRedisConfigurationFileTemplate(params.get(Configuration.REDIS_CONFIGURATION_TEMPLATE_FILE));
		} catch (FileNotFoundException e) {
			throw new RedisRunException(e);
		} catch (ConfigurationException e) {			
		}
		
		return instance;
	}
	
	/**
	 * Constructs BasicRedisRunner, based on a default location of the Redis binary directory (.) and a default location of the Redis configuration template file (desc/redis.conf) 
	 * 
	 * @throws RedisRunException in case the default redis binary directory and/or the default configuration file are not existed, 
	 * or does not support current OS  
	 */			
	private BasicRedisRunner() throws RedisRunException {
		this.templateConfigurationFile = redisBinDir + "/" + DEFAULT_TEMPLATE_CONFIGURATION_FILE_NAME;
		this.mapDir2FileInstanceInfo = new HashMap<String,RedisInstanceInfo>();
		this.usedPorts = new HashSet<Integer>();		
		if (!new File(redisBinDir + "/" + getRedisServerCmd()).exists())
			throw new RedisRunException("Redis server executable was not found: " + redisBinDir + "/" + getRedisServerCmd() + ". Recheck the redis server directory argument");
		//this.executor = new DefaultExecutor();
	}
	
	/**
	 * Initializes static fields 
	 * 
	 * @param templateConfigurationFileName a path to a Redis configuration file
	 */
	public synchronized void setRedisConfigurationFileTemplate(String templateConfigurationFile) throws FileNotFoundException {
		if (!new File(templateConfigurationFile).exists())
			throw new FileNotFoundException(templateConfigurationFile);
		this.templateConfigurationFile = templateConfigurationFile;
	}


	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.redis.RedisRunner#run(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized int run(final String dbFile, boolean bVM) throws RedisRunException {
	
		RedisInstanceInfo instanceInfo = mapDir2FileInstanceInfo.get(dbFile);
		if (instanceInfo == null) {
			int port;
			try {
				port = getAvailablePort();
			} catch (NoAvailablePortException e) {
				throw new RedisRunException(e);
			}
			run1(dbFile,port,bVM);
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						close(dbFile);
					} catch (RedisCloseException e) {
						//logger.info("The redis server was already closed: " + e.toString());
					}
				}
			});
			return port;
		} else {
			//logger.info("A redis server instance for this database is already run: " + instanceInfo);
			instanceInfo.incRef();
			return instanceInfo.getPort();
		}		
	}

	
	/**
	 * Run, if needed, a local Redis server for a given Redis database file in a given directory
	 * 
	 * @param dbFile An existing or non-existing Redis database file. The parent directory of @param dbFile should have writing permissions
	 * @param port a port for the redis server to be run
	 * @param bVM should the redis server run on low memory usage mode (virtual memory).
	 * @throws RedisRunException
	 */
	public synchronized int run1(final String dbFile, int port, boolean bVM) throws RedisRunException {
		
		logger.debug("running redis server for file " + dbFile);
		
		//Assumption: dbFile is a Redis db file (.rdb) or non existing file		
		try {
			String confFile = generateConfigurationFile(dbFile,port,templateConfigurationFile,bVM);
			
			logger.info("A new instance is about to run on port " + port + " according to " + confFile + " configuration");
			
			/*executor.execute(new CommandLine(REDIS_SERVER_CMD + " " + confFile), null, new ExecuteResultHandler() {
				public void onProcessComplete(int exitCode) {						
					logger.info("Redis server for database " + dbDir + "/" + dbFile + " was terminated with exit code " + exitCode);
				}
				public void onProcessFailed(ExecuteException e) {
					logger.warn(e.toString());						
				}					
			});*/
			Process process = Runtime.getRuntime().exec(new String[]{redisBinDir + "/" + getRedisServerCmd(),confFile});
			waitForRedisInitializing(process);
			
			logger.info("The redis server on port " + port + ", based on " + confFile + " configuration, is ready now");
			
			usedPorts.add(port);				
			mapDir2FileInstanceInfo.put(dbFile,new RedisInstanceInfo(port, process));
			return port;
		} catch (Exception e) {				
			try {
				close(dbFile);
			} catch (RedisCloseException e1) {
				//logger.error(e1.toString());
			}
			throw new RedisRunException(e);
		}
	}

	/**
	 * Waits until the given redis-server process ends its initialization, by identifying the 'ready' message in the process's output stream 
	 * 
	 * @param process A handler of a redis-server process
	 * @throws IOException
	 */
	protected synchronized void waitForRedisInitializing(Process process) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = null;
		while ((line = reader.readLine())!= null) {
			logger.info(line);
			if (line.contains("The server is now ready")) {
				reader.close();
				return;
			}
		}
		if (reader != null)
			reader.close();
		throw new IOException("The redis-server output was ended before a 'ready' message was identified");
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.redis.RedisRunner#close(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void close(final String dbFile) throws RedisCloseException {
		
		RedisInstanceInfo instanceInfo = mapDir2FileInstanceInfo.get(dbFile);
		if (instanceInfo == null)
			throw new RedisCloseException("No open redis instance was found for the given database file: " + dbFile);
		if (close1(dbFile, instanceInfo))
			mapDir2FileInstanceInfo.remove(dbFile);		
	}

	/**
	 * Stop the running of a given Redis server, specified by the given db directory and file (in case no other references for the running Redis server exists)
	 * 
	 * @param dbFile An existing Redis database file. The parent directory of the given dbFile should have writing permissions
	 * @param instanceInfo An information about the running Redis server process of the given database file
	 * 
	 * @return true if the server process was terminated
	 */
	protected synchronized boolean close1(String dbFile, RedisInstanceInfo instanceInfo) {
		if (instanceInfo.isLastRef()) {
			
			logger.info("Last reference for database " + dbFile + ", closing the redis server instance");			

			instanceInfo.getProcess().destroy();		
			usedPorts.remove(instanceInfo.getPort());
			cleanConfigurationFile(dbFile);
			return true;
		} else {
			instanceInfo.decRef();
			
			//logger.info("There are still " + instanceInfo.getRef() + " references for database " + dbFile + ", the redis server instance is not closed yet");
			
			return false;
		}
			
		
	}
			
	/**
	 * Generate a configuration file for the Redis server, based on a template of configuration, 
	 * a given directory of a database, and a given port 
	 * 
	 * @param dbDir an existing directory  with writing permissions.
	 * @param dbFile a path to an existing or non-existing Redis database file, located at @param dbDir directory
	 * @param port an available port for Redis connection
	 * @param bVM should the redis server run on low memory usage mode (virtual memory).
	 * @return the path to the new generated configuration file
	 * @throws IOException 
	 */
	public synchronized static String generateConfigurationFile(String dbfile, int port, boolean bVM) throws IOException {
		return generateConfigurationFile(dbfile,port,DEFAULT_REDIS_BIN_DIR + "/" + DEFAULT_TEMPLATE_CONFIGURATION_FILE_NAME,bVM);
	}
	
	/**
	 * Generate a configuration file for the Redis server, based on a template of configuration, 
	 * a given directory of a database, and a given port 
	 * 
	 * @param dbDir an existing directory  with writing permissions.
	 * @param dbFile a path to an existing or non-existing Redis database file, located at @param dbDir directory
	 * @param port an available port for Redis connection
	 * @param templateConfigurationFile a template of redis configuration file
	 * @param bVM should the redis server run on low memory usage mode (virtual memory).
	 * @return the path to the new generated configuration file
	 * @throws IOException 
	 */
	public synchronized static String generateConfigurationFile(String dbfile, int port, String templateConfigurationFile, boolean bVM) throws IOException {
		String confFile = dbfile + CONF_FILE_EXT;
		String dbDir = new File(dbfile).getParent();
		BufferedReader reader = new BufferedReader(new FileReader(templateConfigurationFile));
		PrintWriter writer = new PrintWriter(new FileWriter(confFile));
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith(PID_FILE)) {
				writer.println(PID_FILE + " " + dbfile + PID_FILE_EXT);
			} else if (line.startsWith(PORT)) {
				writer.println(PORT + " " + port);
			} else if (line.startsWith(DIR)) {
				writer.println(DIR + " " + dbDir);
			} else if (line.startsWith(DB_FILE_NAME)) {
				writer.println(DB_FILE_NAME + " " + new File(dbfile).getName());
			} else if (line.startsWith(VM_SWAP_FILE)) {
				writer.println(VM_SWAP_FILE + " " + dbfile + VM_SWAP_FILE_EXT);
			} else if (line.startsWith(VM_ENABLED)) {
				writer.println(VM_ENABLED + " " + (bVM ? "yes" : "no"));
				if (bVM)
					writer.println(REALY_USE_VM + " yes");
			} else {
				writer.println(line);
			}
		}
		reader.close();
		writer.flush();
		writer.close();
		return confFile;
	}
	
	/**
	 * Deletes the generated configuration file for the given db dir and file, and all relevant temp files
	 * 
	 * @param dbFile a path to an existing Redis database file. The parent directory should have a writing permissions
	 */
	protected synchronized void cleanConfigurationFile(String dbFile) {
		String confFile = dbFile + CONF_FILE_EXT;
		if (!new File(confFile).delete()) 
			logger.warn("Cannot delete the temporary redis configuration file: " + confFile + CONF_FILE_EXT);
	}
	
	
	/**
	 * Finds an available port for new Redis server instance
	 * 
	 * @return an available port for new Redis server instance
	 * 
	 * @throws NoAvailablePortException in case no available port was found
	 */
	protected synchronized int getAvailablePort() throws NoAvailablePortException  {
		for (int port = MIN_PORT; port <= MAX_PORT; port++) 
			if (availablePort(port)) 
				return port;
		throw new NoAvailablePortException();
	}
	
	/**
	 * Checks whether the given port is available, i.e., not used by some local server
	 * 
	 * @param port a candidate port for Redis server
	 * 
	 * @return true if the port is available
	 */
	protected synchronized boolean availablePort(int port) {
		if (usedPorts.contains(port))
			return false;
		
		boolean ret;
		try {
			/*Jedis jedis = new Jedis("localhost",port);
			jedis.connect();
			ret = (!jedis.isConnected());
			jedis = null;*/
			new Socket("localhost",port);
			
			//logger.info("Socket connection on port " + port + " succeeded: " + s);
			
			ret = false;
		} catch (Exception e) {
			ret = true;
		}
		return ret;
	}
	
	protected Map<String,RedisInstanceInfo> mapDir2FileInstanceInfo;
	protected Set<Integer> usedPorts; // a set of port which are currently used for Redis server connections
	public String templateConfigurationFile; // a path to a Redis configuration file	
	//protected Executor executor;
	
	public static void main(String[] args) {
		/*
		 * args
		 * [0]: redis bin directory
		 * [1]: redis configuration file template
		 * [2]: redis db file
		 */
		try {
			BasicRedisRunner.setRedisBinDir(args[0]);
			RedisRunner runner = BasicRedisRunner.getInstance(args[1]);
			runner.run(args[2],false);
			runner.run(args[2],false);
			runner.run(args[2],false);
			
			Thread.sleep(10000);			
			runner.close(args[2]);
			Thread.sleep(10000);			
			runner.close(args[2]);
			Thread.sleep(10000);			
			runner.close(args[2]);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	public static String getRedisServerCmd() throws RedisRunException {
		if (OS.isLinux() || OS.isUnix() || OS.isMac())
			return "redis-server";
		if (OS.isWindows()) 
			return "redis-server.exe";
		throw new RedisRunException("Current operating system does not support Redis servewr execution");
	}
	
	static {
		redisBinDir = DEFAULT_REDIS_BIN_DIR;		
	}
}
