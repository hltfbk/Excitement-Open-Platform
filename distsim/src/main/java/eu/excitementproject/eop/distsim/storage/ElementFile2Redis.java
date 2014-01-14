package eu.excitementproject.eop.distsim.storage;

import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.UndefinedKeyException;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.SerializationException;
//import org.apache.log4j.Logger;


/**
 *
 * A program which load a given elements File device into a Redis server 
 * 
 *  
 * @author Meni Adler
 * @since 27/12/2012
 *
 */
public class ElementFile2Redis {
	public static void main(String[] args) throws LoadingStateException, IOException, SerializationException {
		
		/*if (args.length != 3) {
			System.err.println("Usage: ElementsFile2PatternBasedRedis <in file> <out redis host> <out redis port>");
			System.exit(0);
		}*/
		if (args.length != 1) {
			System.err.println("Usage: ElementsFile2PatternBasedRedis <configuration file>");
			System.exit(0);
		}
		
		try {
			//ConfigurationFile confFile = new ConfigurationFile(args[0]);		
			ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new java.io.File(args[0])));
			ConfigurationParams loggingParams = confFile.getModuleConfiguration(Configuration.LOGGING);
			
			PropertyConfigurator.configure(loggingParams.get(Configuration.PROPERTIES_FILE));
			//Logger logger = Logger.getLogger(File2Redis.class);
						
			final ConfigurationParams confParams = confFile.getModuleConfiguration(Configuration.FILE_TO_REDIS);			
	
			File file;
			try {
				file = (File)Factory.create(confParams.get(Configuration.CLASS),new java.io.File(confParams.get(Configuration.FILE)),true);
			} catch (ConfigurationException e) {
				file = new File(new java.io.File(confParams.get(Configuration.FILE)),true);
			}

			
			file.open();
			String dbFile = confParams.getString(Configuration.REDIS_FILE);
			RedisBasedCountableIdentifiableStorage<Element> elementStorage = new RedisBasedCountableIdentifiableStorage<Element>(dbFile);
			elementStorage.clear();
			Pair<Integer,Serializable> pair = null;
			while ((pair = file.read())!=null) {
				try {
					elementStorage.add(pair.getFirst(),(Element)pair.getSecond());
				} catch (UndefinedKeyException e) {
					System.out.println(e.toString());
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
