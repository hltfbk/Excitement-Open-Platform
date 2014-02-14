package eu.excitementproject.eop.distsim.application;

import eu.excitementproject.eop.distsim.redis.BasicRedisRunner;

/**
 * Runs a Redis similarity resources on local host, given Redis file and port
 * 
 * Assumption: There's a redis dir in the running directory, contain redis-server binary file and redis.conf template file
 * 
 * @author Meni Adler
 * @since Feb 11, 2014
 *
 */
public class RunRedisResources {
	public static void main(String[] args) throws Exception {
		
		if (args.length != 2) {
			System.out.println("Usage: java eu.excitementproject.eop.distsim.application.RunRedisResources <redis file name> <port>");
		}
		String redisFile = args[0];
		int port = Integer.parseInt(args[1].trim());
		String redisConfFile = BasicRedisRunner.generateConfigurationFile(redisFile, port);
		Runtime.getRuntime().exec(new String[]{BasicRedisRunner.DEFAULT_REDIS_BIN_DIR + "/" + BasicRedisRunner.REDIS_SERVER_CMD,redisConfFile});		
	}
}
