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
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.scoring.combine.SimilarityCombination;
import eu.excitementproject.eop.distsim.storage.PersistenceDevice;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.SortUtil;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * An implementation of the {@link ElementSimilarityCombiner} interface, which stores in the memory the given set of similarity storages
 * The characteristics of the combination is determined by a given combination method  
 * 
 * @author Meni Adler
 * @since 11/09/2012
 *
 */
public class MemoryBasedElementSimilarityCombiner implements ElementSimilarityCombiner {

	private final static Logger logger = Logger.getLogger(MemoryBasedElementSimilarityCombiner.class);
	
	public MemoryBasedElementSimilarityCombiner() {
		
	}

	public MemoryBasedElementSimilarityCombiner(ConfigurationParams params) {
		
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.similarity.ElementSimilarityCombiner#combinedScores(java.util.List, org.excitement.distsim.scoring.combine.SimilarityCombination)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void combinedScores(List<PersistenceDevice> similarityStorageDevices, SimilarityCombination combiner, PersistenceDevice combinedStorage) throws SimilarityCombinationException {

		try {
			List<TIntObjectMap<TIntDoubleMap>> similarities = new LinkedList<TIntObjectMap<TIntDoubleMap>>();
			if (similarityStorageDevices.size() > 1) {
				for (int i=1;i<similarityStorageDevices.size();i++) 
					similarities.add(device2map(similarityStorageDevices.get(i)));
			}

			PersistenceDevice baseDevice = similarityStorageDevices.get(0);
			Pair<Integer, Serializable> pair = null;
			while ((pair = baseDevice.read()) != null) {
				int entailingElementId = pair.getFirst();
				List<TIntDoubleMap> similarityMaps = new LinkedList<TIntDoubleMap>();
				for (TIntObjectMap<TIntDoubleMap> similarityMap : similarities) {
					TIntDoubleMap m = similarityMap.get(entailingElementId);
					if (m != null)
						similarityMaps.add(m);
				}
				TIntDoubleMap combinedEntailedsScores = new TIntDoubleHashMap();
				for (Entry<Integer,Double> entailedScore : ((LinkedHashMap<Integer,Double>)pair.getSecond()).entrySet()) {				
					int entailedElementId = entailedScore.getKey();				
					List<Double> scores = new LinkedList<Double>();
					scores.add(entailedScore.getValue());
					for (TIntDoubleMap similarityMap : similarityMaps) {
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
	
	protected TIntObjectMap<TIntDoubleMap> device2map(PersistenceDevice device) throws Exception {
		TIntObjectMap<TIntDoubleMap> ret = new TIntObjectHashMap<TIntDoubleMap>();
		Pair<Integer, Serializable> pair = null;
		while ((pair = device.read()) != null) {
			TIntDoubleMap map = new TIntDoubleHashMap();
			@SuppressWarnings("unchecked")
			LinkedHashMap<Integer, Double> linkedhashmap = (LinkedHashMap<Integer,Double>)pair.getSecond();
			for (Entry<Integer,Double> entry : linkedhashmap.entrySet())
				map.put(entry.getKey(), entry.getValue());
			ret.put(pair.getFirst(),map);
		}
		return ret;
	}
}