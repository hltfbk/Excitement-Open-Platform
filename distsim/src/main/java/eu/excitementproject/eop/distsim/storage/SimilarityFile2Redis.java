package eu.excitementproject.eop.distsim.storage;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.UndefinedKeyException;
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
public class SimilarityFile2Redis {
	
	private static  CountableIdentifiableStorage<Element> elementStorage;

	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.err.println("Usage: SimilarityFile2Redis <configuration file>");
			System.exit(0);
		}

		Logger logger=null;
		try {
			
			ConfigurationFile confFile = new ConfigurationFile(args[0]);			
			ConfigurationParams loggingParams = confFile.getModuleConfiguration(Configuration.LOGGING);
			
			PropertyConfigurator.configure(loggingParams.get(Configuration.PROPERTIES_FILE));
			logger = Logger.getLogger(SimilarityFile2Redis.class);
						
			final ConfigurationParams confParams = confFile.getModuleConfiguration(Configuration.FILE_TO_REDIS);			

			File elementsFile = new File(new java.io.File(confParams.get(Configuration.ELEMENTS_FILE)),true);
			elementsFile.open();
			elementStorage = new MemoryBasedCountableIdentifiableStorage<Element>(elementsFile);
			File file;
			//try {
				file = (File)Factory.create(confParams.get(Configuration.CLASS),new java.io.File(confParams.get(Configuration.FILE)),true);
			//} catch (ConfigurationException e) {
				//file = new File(new java.io.File(confParams.get(Configuration.SIMILARITY_FILE)),true);
			//}
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
					else {
						int element1Id = pair.getFirst();
						String element1key = getElementKey(element1Id);

						//debug
						//System.out.println(element1key);
						

						@SuppressWarnings("unchecked")
						LinkedHashMap<Integer, Double> sortedElementScores = (LinkedHashMap<Integer, Double>) pair.getSecond();
						for (Entry<Integer,Double> elementScore : sortedElementScores.entrySet()) {
							int element2Id = elementScore.getKey();
							double score = elementScore.getValue();		
							String element2key = getElementKey(element2Id);
							if (element2key.contains(RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER))
								logger.warn("Delimiter '" + RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER +"' already exists in element " + element2key);
							else 
								redis.rpush(element1key, element2key + RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER + score);
							//redis.write(pair.getFirst(),pair.getSecond());
						}
					}
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

	protected static String getElementKey(int element1Id) throws ItemNotFoundException, SerializationException, UndefinedKeyException {		
		return elementStorage.getData(element1Id).toKey();
	}
}
