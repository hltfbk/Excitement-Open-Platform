package eu.excitementproject.eop.distsim.storage;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.SerializationException;


/**
 * A program which load a File device into a Redis server
 * 
 * @author Meni Adler
 * @since 27/12/2012
 *
 */
public class File2Redis {
	public static void main(String[] args) {
		
		/*if (args.length != 3) {
			System.err.println("Usage: ElementsFile2PatternBasedRedis <in file> <out redis host> <out redis port>");
			System.exit(0);
		}*/
		
		if (args.length != 1) {
			System.err.println("Usage: File2Redis <configuration file>");
			System.exit(0);
		}

		Logger logger=null;
		try {
			
			ConfigurationFile confFile = new ConfigurationFile(args[0]);			
			ConfigurationParams loggingParams = confFile.getModuleConfiguration(Configuration.LOGGING);
			
			PropertyConfigurator.configure(loggingParams.get(Configuration.PROPERTIES_FILE));
			logger = Logger.getLogger(File2Redis.class);
						
			final ConfigurationParams confParams = confFile.getModuleConfiguration(Configuration.FILE_TO_REDIS);			

			File file;
			try {
				file = (File)Factory.create(confParams.get(Configuration.CLASS),new java.io.File(confParams.get(Configuration.FILE)),true);
			} catch (ConfigurationException e) {
				file = new File(new java.io.File(confParams.get(Configuration.FILE)),true);

			}
			file.open();
			String host = confParams.getString(Configuration.REDIS_HOST);
			int port = confParams.getInt(Configuration.REDIS_PORT);
			Redis redis = new Redis(host,port);
			redis.open();
			redis.clear();
			Pair<Integer,Serializable> pair = null;
			while (true) {
				try {
					pair = file.read();
					if (pair == null)
						break;
					else
						redis.write(pair.getFirst(),pair.getSecond());
				} catch (SerializationException e) {
					logger.error(e.toString());
				}
			}
			file.close();
			redis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
