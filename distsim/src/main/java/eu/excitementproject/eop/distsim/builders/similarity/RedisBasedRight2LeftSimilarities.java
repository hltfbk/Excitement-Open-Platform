package eu.excitementproject.eop.distsim.builders.similarity;


import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.storage.BasicMapException;
import eu.excitementproject.eop.distsim.storage.RedisBasedIDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.SortUtil;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;


public class RedisBasedRight2LeftSimilarities {
	public static void main(String[] args) {
				
		if (args.length != 1) {
			System.err.println("Usage: Right2LeftSimilarities <configuration file>");
			System.exit(0);
		}


		
		try {			
			
			//ConfigurationFile confFile = new ConfigurationFile(args[0]);		
			ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(args[0])));
			
			ConfigurationParams loggingParams = confFile.getModuleConfiguration(Configuration.LOGGING);
			PropertyConfigurator.configure(loggingParams.get(Configuration.PROPERTIES_FILE));
			final Logger logger = Logger.getLogger(MemoryBasedRight2LeftSimilarities.class);
						
			final ConfigurationParams confParams = confFile.getModuleConfiguration(Configuration.RIGHT_TO_LEFT_SIMILARITIES);			

			eu.excitementproject.eop.distsim.storage.File rightSimilaritiesFile = new eu.excitementproject.eop.distsim.storage.File(new File(confParams.get(Configuration.INFILE)),true);
			rightSimilaritiesFile.open();

			RedisBasedIDKeyPersistentBasicMap<HashMap<Integer,Double>> leftSimilarities = new RedisBasedIDKeyPersistentBasicMap<HashMap<Integer,Double>>(confParams.getString(Configuration.REDIS_FILE));
			leftSimilarities.clear();
			
			// set the right similarities at the left similarities map
			logger.info("Loading right similarities");
			Pair<Integer, Serializable> pair = null;
			int i=0;
			
			TIntObjectMap<HashMap<Integer,Double>> tmpLeftSimilarities = new TIntObjectHashMap<HashMap<Integer,Double>>();
			
			while ((pair = rightSimilaritiesFile.read()) != null) {
				int rightElementId = pair.getFirst();
				
				//System.out.println("rightElementId = " + rightElementId);
				
				
				@SuppressWarnings("unchecked")
				HashMap<Integer,Double> similarities = (HashMap<Integer,Double>)pair.getSecond();
				
				//System.out.println("left similarities: " + similarities.size());
				
				for (Entry<Integer,Double> entry : similarities.entrySet()) {
					int leftElementId = entry.getKey();					
					double score = entry.getValue();
					
					//System.out.println("\t" + leftElementId + "\t" + score);


					HashMap<Integer, Double> scores = tmpLeftSimilarities.get(leftElementId);
					if (scores == null)
						scores = leftSimilarities.get(leftElementId);
					
					if (scores == null) 						
						scores = new HashMap<Integer,Double>();
					scores.put(rightElementId, score);
					
					//System.out.println("\t" + scores);
					
					tmpLeftSimilarities.put(leftElementId, scores);
				}
				i++;
				if (i % 1000 == 0)
					logger.info(i);
				
				if (!memoryLeft()) {
					logger.info("writing data to redis");
					writeToRedis(tmpLeftSimilarities,leftSimilarities);
					System.gc();
					Thread.sleep(10000);
				}
			}
			
			logger.info("writing data to redis");
			writeToRedis(tmpLeftSimilarities,leftSimilarities);
			
			rightSimilaritiesFile.close();
			
			// save the right element similarity file
			java.io.File outfile = new File(confParams.get(Configuration.OUTFILE));
			logger.info("Saving right similarities to file: " + outfile.getPath());			
			eu.excitementproject.eop.distsim.storage.File leftSimilaritiesFile = new eu.excitementproject.eop.distsim.storage.File(outfile,false);
			leftSimilaritiesFile.open();
			ImmutableIterator<Pair<Integer, HashMap<Integer, Double>>> it = leftSimilarities.iterator();
			while (it.hasNext()) {				
				Pair<Integer, HashMap<Integer, Double>> idData = it.next();
				leftSimilaritiesFile.write(idData.getFirst(), SortUtil.sortMapByValue(idData.getSecond(),true));
			}			
			leftSimilaritiesFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	static void writeToRedis(TIntObjectMap<HashMap<Integer,Double>> tmpLeftSimilarities,RedisBasedIDKeyPersistentBasicMap<HashMap<Integer,Double>> leftSimilarities) throws BasicMapException {		
		TIntObjectIterator<HashMap<Integer, Double>> it = tmpLeftSimilarities.iterator();
		while (it.hasNext()) {
			it.advance();
			leftSimilarities.put(it.key(), it.value());
			it.remove();
		}
	}
	static boolean memoryLeft() {
		//System.out.println("total: " + Runtime.getRuntime().totalMemory() / Math.pow(2,20) + "MB");
		//System.out.println("free: " + Runtime.getRuntime().freeMemory() / Math.pow(2,20) + "MB");
		return (Runtime.getRuntime().freeMemory() > Math.pow(2,30));	
	}
}


