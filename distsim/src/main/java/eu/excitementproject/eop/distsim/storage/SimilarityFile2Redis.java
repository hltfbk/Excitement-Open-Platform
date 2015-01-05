package eu.excitementproject.eop.distsim.storage;

import java.io.Serializable;





import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.UndefinedKeyException;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.SerializationException;
import eu.excitementproject.eop.redis.RedisBasedStringListBasicMap;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;;

/**
 * A program which load a File device into a Redis server
 * 
 * @author Meni Adler
 * @since 27/12/2012
 *
 */
public class SimilarityFile2Redis {
	
	private static CountableIdentifiableStorage<Element> elementStorage = null;
	private static CountableIdentifiableStorage<Feature> featureStorage = null;
	private static boolean bElementTypeFound = false;
	private static boolean bFirstOrder = false;
	private static Redis redis = null;

	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.err.println("Usage: java SimilarityFile2Redis <configuration file>");
			System.exit(0);
		}

		Logger logger=null;
		try {
			
			//ConfigurationFile confFile = new ConfigurationFile(args[0]);	
			ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new java.io.File(args[0])));
			ConfigurationParams loggingParams = confFile.getModuleConfiguration(Configuration.LOGGING);
			
			PropertyConfigurator.configure(loggingParams.get(Configuration.PROPERTIES_FILE));
			logger = Logger.getLogger(SimilarityFile2Redis.class);
						
			final ConfigurationParams confParams = confFile.getModuleConfiguration(Configuration.FILE_TO_REDIS);			
			
			try {
				bFirstOrder = confParams.getBoolean(Configuration.FIRST_ORDER);
			} catch (ConfigurationException e) {
				
			}
			
			int maxSimilaritiesNum = -1;
			try {
				maxSimilaritiesNum = confParams.getInt(Configuration.MAX_SIMILARITIES_PER_ELEMENT);	
			} catch (ConfigurationException e) {}
			
			File elementsFile = new File(new java.io.File(confParams.get(Configuration.ELEMENTS_FILE)),true);
			elementsFile.open();
			elementStorage = new MemoryBasedCountableIdentifiableStorage<Element>(elementsFile);
			elementsFile.close();
			if (bFirstOrder) {
				File featuresFile = new File(new java.io.File(confParams.get(Configuration.FEATURES_FILE)),true);
				featuresFile.open();
				featureStorage = new MemoryBasedCountableIdentifiableStorage<Feature>(featuresFile);
				featuresFile.close();
			}
		
			File file;
			//try {
				file = (File)Factory.create(confParams.get(Configuration.CLASS),new java.io.File(confParams.get(Configuration.FILE)),true);
			//} catch (ConfigurationException e) {
				//file = new File(new java.io.File(confParams.get(Configuration.SIMILARITY_FILE)),true);
			//}
			file.open();
			
			redis = new Redis(confParams.getString(Configuration.REDIS_FILE),false);
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
						
						String element1key = getElement1Key(element1Id);						
						//tmp
						/*String element1key = null;						
						try {
							element1key = getElementKey(element1Id);
						} catch (ItemNotFoundException e) {
							continue;
						}*/

						@SuppressWarnings("unchecked")
						LinkedHashMap<Integer, Double> sortedElementScores = (LinkedHashMap<Integer, Double>) pair.getSecond();
						int i=0;
						for (Entry<Integer,Double> elementScore : sortedElementScores.entrySet()) {							
							int element2Id = elementScore.getKey();
							double score = elementScore.getValue();		
							String element2key = getElement2Key(element2Id);
							//tmp
							/*String element2key = null;						
							try {
								element2key = getElementKey(element2Id);
							} catch (ItemNotFoundException e) {
								continue;
							}*/
							
							if (element2key.contains(RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER))
								logger.info("Element " + element2key + " contains the delimiter '" + RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER + "', and considered insignificant. The element is filtered from the model");
							else { 
								redis.rpush(element1key, element2key + RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER + score);
								i++;
								if (maxSimilaritiesNum != -1 & i == maxSimilaritiesNum)
									break;
							}
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

	protected static String getElement2Key(int element2Id) throws ItemNotFoundException, SerializationException, UndefinedKeyException {
		if (!bFirstOrder)
			return getElement1Key(element2Id);
		else {
			Feature feature = featureStorage.getData(element2Id);
			return feature.toKey();
		}
	}
	
	protected static String getElement1Key(int element1Id) throws ItemNotFoundException, SerializationException, UndefinedKeyException {
		Element element = elementStorage.getData(element1Id);
		if (!bElementTypeFound && redis != null) {
			redis.write(RedisBasedStringListBasicMap.ELEMENT_CLASS_NAME_KEY,element.getClass().getName());
			bElementTypeFound = true;
		}
		return element.toKey();
	}
}
