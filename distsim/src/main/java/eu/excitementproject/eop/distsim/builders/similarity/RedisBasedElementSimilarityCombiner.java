/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.similarity;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.scoring.combine.SimilarityCombination;
import eu.excitementproject.eop.distsim.storage.PersistenceDevice;
import eu.excitementproject.eop.distsim.storage.RedisBasedIDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.SortUtil;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.hash.TIntDoubleHashMap;


/**
 * An implementation of the {@link ElementSimilarityCombiner} interface, which stores in a Redis server the given set of similarity storages
 * The characteristics of the combination is determined by a given combination method  
 * 
 * @author Meni Adler
 * @since 11/09/2012
 *
 */
public class RedisBasedElementSimilarityCombiner implements ElementSimilarityCombiner {

	private final static Logger logger = Logger.getLogger(MemoryBasedElementSimilarityCombiner.class);
	
	public RedisBasedElementSimilarityCombiner() {
		dbs = null;
		logger.warn("no dbs were provided");
	}

	public RedisBasedElementSimilarityCombiner(ConfigurationParams params) throws ConfigurationException {
		dbs = params.getStringList(Configuration.REDIS_FILE);
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.similarity.ElementSimilarityCombiner#combinedScores(java.util.List, org.excitement.distsim.scoring.combine.SimilarityCombination)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void combinedScores(List<PersistenceDevice> similarityStorageDevices, SimilarityCombination combiner, PersistenceDevice combinedStorage) throws SimilarityCombinationException {

		if (similarityStorageDevices.size()-1 > dbs.size())
			throw new SimilarityCombinationException("the number of provided devices is grater than existed redis servers");
		try {
			List<RedisBasedIDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>>> similarities = new LinkedList<RedisBasedIDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>>>();
			if (similarityStorageDevices.size() > 1) { 
				for (int i=1;i<similarityStorageDevices.size();i++) { 
					RedisBasedIDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>> redis = 
						new RedisBasedIDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>>(
								dbs.get(i-1), 
								similarityStorageDevices.get(i));
					similarities.add(redis);
				}
			}

			PersistenceDevice baseDevice = similarityStorageDevices.get(0);
			Pair<Integer, Serializable> pair = null;
			while ((pair = baseDevice.read()) != null) {
				int entailingElementId = pair.getFirst();
				List<LinkedHashMap<Integer,Double>> similarityMaps = new LinkedList<LinkedHashMap<Integer,Double>>();
				for (RedisBasedIDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>> similarityMap : similarities) {
					LinkedHashMap<Integer,Double> m = similarityMap.get(entailingElementId);
					if (m != null)
						similarityMaps.add(m);
				}
				TIntDoubleMap combinedEntailedsScores = new TIntDoubleHashMap();
				for (Entry<Integer,Double> entailedScore : ((LinkedHashMap<Integer,Double>)pair.getSecond()).entrySet()) {				
					int entailedElementId = entailedScore.getKey();				
					List<Double> scores = new LinkedList<Double>();
					scores.add(entailedScore.getValue());
					for (LinkedHashMap<Integer,Double> similarityMap : similarityMaps) {
						if (similarityMap.containsKey(entailedElementId))
							scores.add(similarityMap.get(entailedElementId));
					}
					double combinedScore = combiner.combine(scores, similarityStorageDevices.size());
					if (combinedScore > 0)
						combinedEntailedsScores.put(entailedElementId, combinedScore);
				}
				try {
					combinedStorage.write(entailingElementId, SortUtil.sortMapByValue(combinedEntailedsScores, true));
				} catch (Exception e) {
					logger.error(ExceptionUtil.getStackTrace(e));
				}
			}
		} catch (Exception e) {
			throw new SimilarityCombinationException(e);
		}
	}
	
	
	protected List<String> dbs;
}