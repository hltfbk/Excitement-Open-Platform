package eu.excitementproject.eop.distsim.redis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
//import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.common.io.Files;
import com.google.common.io.Resources;

/**
 * This small utility class runs Redis-server with three options. 
 * Port, RDB file dir, RDB file name. 
 * 
 * The class first invokes redis-server binary from Jar (via getResource), and makes one 
 * copy in a temporary disc location (via Google coreIO library), and runs it as a native process. 
 * This copy (of file) is automatically destroyed when the JVM closes down. 
 * 
 * Usage is simple: init a Redis-server process by using the constructor, start running by calling start(), stop its running by calling stop(). 
 * See the unit test class for the code usage in action.   
 * The progress will be reported via log4j INFO level. You can see redis-server output (STDOUT & STDERR) by enabling level.DEBUG of log4j.  
 * 
 * DISCLAIMER: I have borrowed a lot of codes (e.g. the idea of using script enum, or using google IO to get file from resource) 
 * from the following Apache 2.0 licensed library. 
 * 
 * The code itself didn't meet our needs (e.g. it does not support passing of 
 * arguments, configuration, and rdb file); so I extended it fairly for our usage. But still, 
 * this file contains some of the original code. See the following page for original code. 
 * https://github.com/kstyrc/embedded-redis
 * 
 * @author Tae-Gil Noh 
 * 
 * NOTE: designed with REDIS server binary 2.6.16! 
 * NOTE: tested with Linux-64 and OSX 10.9 
 * TODO write "redis-based lexical resource implementation base". This can work also 
 * with "already existing server (by getting server & port)
 * 
 */
public class RedisServerRunner {
	
	/* We won't need this. 
	public RedisServer(File command, Integer port) {
		this.command = command;
		this.port = port;
	}
	*/ 
	/**
	 * This is the main constructor of RedisServerRunner. 
	 * 
	 * @param port port number - on what port this redis-server will serve? (passed as --port argument to redis-server binary)
	 * @param argRDBDir directory of the rdb, on what path the rdb file exist? (without file name, passed as argument --dir to redis-server binary) 
	 * @param argRDBName filename of the rdb file to be served (without path, just the file name. passed as argument --dbfilename ) 
	 * @throws IOException
	 */
	public RedisServerRunner(Integer port, String argRDBDir, String argRDBName) throws IOException {
		this.port = port;
		this.rdbDir = argRDBDir; 
		this.rdbName = argRDBName; 
		this.command = extractExecutableFromJar(RedisRunScriptEnum.getRedisRunScript());		
        logger = Logger.getLogger("eu.excitementproject.eop.distsim.redis"); 
        logger.debug("redis server constructed with " + port.toString() + ", " + rdbDir + ", " + rdbName); 
	}

	public RedisServerRunner(Integer port) throws IOException {
		this(port, null, null); 
	}
	
	private File extractExecutableFromJar(String scriptName) throws IOException {
		File tmpDir = Files.createTempDir();
		tmpDir.deleteOnExit();

		File command = new File(tmpDir, scriptName);
		FileUtils.copyURLToFile(Resources.getResource(scriptName), command);
		command.deleteOnExit();
		command.setExecutable(true);
		
		return command;
	}
	
	public boolean isActive() {
		return active;
	}

	public synchronized void start() throws IOException {
		if (active) {
			throw new RuntimeException("This redis server instance is already running...");
		}
		
		logger.info("starting up redis server on port " + port.toString() + " (--dir:" + rdbDir + ", --dbfilename:" + rdbName + ")"); 

		redisProcess = createRedisProcessBuilder().start();
		portReady = awaitRedisServerReady(); // returns true, if it catches "server is now ready" comment. 
		active = true;
		
		if (portReady)
			logger.info("redis server up and running on port " + port.toString() + " (--dir:" + rdbDir + ", --dbfilename:" + rdbName + ")"); 
		else
		{
			logger.warn("redis server executed, but could not check its running on the designated port! (" + port.toString() + ", --dir:" + rdbDir + ", --dbfilename:" + rdbName + ")."); 
			logger.warn("It may be okay; but more likely a problem! Set log4j level to DEBUG to check the redis-server output. "); 				
		}
	}

	private Boolean awaitRedisServerReady() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(redisProcess.getInputStream()));
		Boolean portReady = false; 
		
		// do we have to put some small "wait" here? what if the process starts, say, 
		// very late? Hmm. or should we wait for first line? 
		try {
			String outputLine = null;
			do {
				outputLine = reader.readLine();
				logger.debug(outputLine); // log4j debug-level output, so we know what goes around, if needed. 
				if (outputLine != null)
				{
					if (outputLine.matches(REDIS_READY_PATTERN))
					{
						portReady = true; // successfully ran the Redis-server. 
						break; 
					}
				}
			} while (outputLine != null); //  && !outputLine.matches(REDIS_READY_PATTERN));
		} finally {
			reader.close();
		}
		return portReady; 
	}

	private ProcessBuilder createRedisProcessBuilder() {
		
		ProcessBuilder pb = null; 
		if ((rdbDir == null) || (rdbName == null))
		{
			pb = new ProcessBuilder(command.getAbsolutePath(), "--port", Integer.toString(port));
			pb.directory(command.getParentFile());
		}
		else
		{
			pb = new ProcessBuilder(command.getAbsolutePath(), "--port", Integer.toString(port), "--dir", rdbDir, "--dbfilename", rdbName);
			pb.directory(command.getParentFile());			
		}
		pb.redirectErrorStream(true); // both STDERR/STDOUT via  getInputStream:  needed for rare cases where run fails due to GLIBC problem, etc. 
		return pb;
	}

	public synchronized void stop() {
		if (active) {
			redisProcess.destroy();
			active = false;
		}
		
		logger.info("redis server process for port " + port.toString() + " destroyed. (--dir:" + rdbDir + ", --dbfilename:" + rdbName + ")"); 
	}
	
	private static enum RedisRunScriptEnum {
		WINDOWS_32("embedded-redis/redis-server.exe"),
		WINDOWS_64("embedded-redis/redis-server-64.exe"),
		LINUX_32("embedded-redis/redis-server"),
		LINUX_64("embedded-redis/redis-server-64"), 
		MACOSX("embedded-redis/redis-server.app");
		
		private final String runScript;

		private RedisRunScriptEnum(String runScript) {
			this.runScript = runScript;
		}
		
		public static String getRedisRunScript() {
			String osName = System.getProperty("os.name");
			String osArch = System.getProperty("os.arch");
			
			if (osName.indexOf("win") >= 0) {
				if (osArch.indexOf("64") >= 0) {
					return WINDOWS_64.runScript;
				} else {
					return WINDOWS_32.runScript;
				}
			} else if (osName.indexOf("nix") >= 0 || osName.indexOf("nux") >= 0 || osName.indexOf("aix") > 0) {
				if (osArch.indexOf("64") >= 0) {
					return LINUX_64.runScript; 
				} else {
					return LINUX_32.runScript;
				}
			} else if ("Mac OS X".equals(osName)) {
				return MACOSX.runScript;
			} else {
				throw new RuntimeException("Unsupported os/architecture...: " + osName + " on " + osArch);
			}
		}
	}
	
	private static final String REDIS_READY_PATTERN = ".*The server is now ready to accept connections on port.*";

	private final File command;
	private final Integer port;
	private final String rdbDir; 
	private final String rdbName; 
	private final Logger logger; 
	
	private volatile boolean active = false;
	private volatile boolean portReady = false; 
	private Process redisProcess;
	
}
