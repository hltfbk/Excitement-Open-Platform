package eu.excitementproject.eop.distsim.builders.similarity;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.SortUtil;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class MemoryBasedRight2LeftSimilarities {
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
			TIntObjectMap<TIntDoubleMap> leftSimilarities = new TIntObjectHashMap<TIntDoubleMap>();
			rightSimilaritiesFile.open();
			
			// set the right similarities at the left similarities map
			logger.info("Loading right similarities");
			Pair<Integer, Serializable> pair = null;
			while ((pair = rightSimilaritiesFile.read()) != null) {
				int rightElementId = pair.getFirst();
				@SuppressWarnings("unchecked")
				LinkedHashMap<Integer,Double> similarities = (LinkedHashMap<Integer,Double>)pair.getSecond();
				for (Entry<Integer,Double> entry : similarities.entrySet()) {
					int leftElementId = entry.getKey();
					double score = entry.getValue();
					TIntDoubleMap scores = leftSimilarities.get(leftElementId);
					if (scores == null) {
						scores = new TIntDoubleHashMap();
						leftSimilarities.put(leftElementId, scores);
					}
					scores.put(rightElementId, score);
				}
			}
			rightSimilaritiesFile.close();
			
			// save the right element similarity file
			java.io.File outfile = new File(confParams.get(Configuration.OUTFILE));
			logger.info("Saving right similarities to file: " + outfile.getPath());			
			eu.excitementproject.eop.distsim.storage.File leftSimilaritiesFile = new eu.excitementproject.eop.distsim.storage.File(outfile,false);
			leftSimilaritiesFile.open();
			TIntObjectIterator<TIntDoubleMap> it = leftSimilarities.iterator();
			while (it.hasNext()) {				
				it.advance();
				leftSimilaritiesFile.write(it.key(), SortUtil.sortMapByValue(it.value(),true));
			}			
			leftSimilaritiesFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}


