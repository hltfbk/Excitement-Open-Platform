/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.similarity;

import java.io.EOFException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.scoring.combine.IlegalScoresException;
import eu.excitementproject.eop.distsim.scoring.combine.SimilarityCombination;
import eu.excitementproject.eop.distsim.storage.PersistenceDevice;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.SortUtil;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.hash.TIntDoubleHashMap;


/**
 * An implementation of the {@link ElementSimilarityCombiner} interface, which assumes that the elements of the given similarity storages 
 * are ascending sorted. No memory is used the storages.
 * The characteristics of the combination is determined by a given combination method. 

 * 
 * @author Meni Adler
 * @since 11/09/2012
 *
 */
public class OrderedBasedElementSimilarityCombiner implements ElementSimilarityCombiner {

	private final static Logger logger = Logger.getLogger(OrderedBasedElementSimilarityCombiner.class);
	
	public OrderedBasedElementSimilarityCombiner() {
		
	}

	public OrderedBasedElementSimilarityCombiner(ConfigurationParams params) {
		
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.similarity.ElementSimilarityCombiner#combinedScores(java.util.List, org.excitement.distsim.scoring.combine.SimilarityCombination)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void combinedScores(List<PersistenceDevice> similarityStorageDevices, SimilarityCombination combiner, PersistenceDevice combinedStorage) throws SimilarityCombinationException {
		// Assumption: the element-pair similarities are stored in the same ascending order at each of the given similarityStorageDevices
		boolean b = true;
		while (b) { // while there are more similarity scores in the given devices 
			int entailingElementId = -1;
			Map<Integer, List<Double>> entailedsScores = new HashMap<Integer, List<Double>>();
			try {
				// get similarity score for the next element which is included in all devices
				List<Pair<Integer, Serializable>> pairs = moveToNextEqualElementIdData(similarityStorageDevices);
								
				// combined the all similarity scores of the element
				for (Pair<Integer,Serializable> pair : pairs) {
					entailingElementId = pair.getFirst();
					//debug
					//System.out.println("entailingElementId: " + entailingElementId);
					for (Entry<Integer,Double> entailedScore : ((LinkedHashMap<Integer,Double>)pair.getSecond()).entrySet()) {
						int entailedElementId = entailedScore.getKey();
						double score = entailedScore.getValue();
						List<Double> scores = entailedsScores.get(entailedElementId);
						if (scores == null) {
							scores = new LinkedList<Double>();
							entailedsScores.put(entailedElementId, scores);
						}
						scores.add(score);
					}
				}
			} catch (EOFException e) {
				b = false;
			} catch (Exception e) {
				throw new SimilarityCombinationException(e);
			}
			if (b && entailingElementId != -1) {
				TIntDoubleMap combinedEntailedsScores = new TIntDoubleHashMap();
				for (Entry<Integer, List<Double>> entailedScores : entailedsScores.entrySet()) {
					double combinedScore;
					try {
						//debug
						//System.out.println("combined scores of element " + entailedScores.getKey());

						combinedScore = combiner.combine(entailedScores.getValue(), similarityStorageDevices.size());
						//debug
						//System.out.println("combined " + entailedScores.getValue() + " to " + combinedScore);
					} catch (IlegalScoresException e) {
						throw new SimilarityCombinationException (e);
					}
					if (combinedScore > 0)
						combinedEntailedsScores.put(entailedScores.getKey(), combinedScore);
				}
				//debug
				//System.out.println(combinedEntailedsScores);
				try {
					combinedStorage.write(entailingElementId, SortUtil.sortMapByValue(combinedEntailedsScores, true));
				} catch (Exception e) {
					logger.error(ExceptionUtil.getStackTrace(e));
				}
			}
		}
	}
	
	/**
	 * Gets the next element that is included in each of the given devices with its similarity scores
	 * 
	 * @param similarityStorageDevices a list of devices which include element similarity scores
	 * @return similarity scores for the next element which appears in all given devices.
	 *  The list contains for each device a pair of the element id and its scores (Serializable object which is a sort of a map of entailed/entailing element id and its score)
	 *  
	 *  <b>Assumption: the element ids are stored in the same ascending order at each of the given similarityStorageDevices</b>
	 */
	protected List<Pair<Integer, Serializable>> moveToNextEqualElementIdData(List<PersistenceDevice> similarityStorageDevices) throws Exception {
		//The 'state' is composed of the maximal id that was found by now, and the maximal id that was read for each of the devices
		List<Pair<Integer, Serializable>> maxIds = new ArrayList<Pair<Integer, Serializable>>();
		for (int i=0;i<similarityStorageDevices.size();i++)
			maxIds.add(null); 
		Pair<Integer,List<Pair<Integer, Serializable>>> state = new Pair<Integer,List<Pair<Integer, Serializable>>>(Integer.MIN_VALUE,maxIds);
		while (!read(similarityStorageDevices,state));
		return state.getSecond();
	}
	
	/**
	 * Read next element similarity for each of the devices, in case it is less than the maximal id that was found
	 * 
	 * @param similarityStorageDevices a list of devices, each contain element similarities
	 * @param state The 'state' is composed of the maximal id that was found by now, and the maximal id that was read for each of the devices
	 * @return true if element, appears in all devices, was found
	 * @throws Exception
	 * 
	 * <b>Assumption: the element ids are stored in the same ascending order at each of the given similarityStorageDevices</b>
	 */
	protected boolean read(List<PersistenceDevice> similarityStorageDevices, Pair<Integer,List<Pair<Integer, Serializable>>> state) throws Exception {
		int iequals=1;
		List<Pair<Integer, Serializable>> maxIds = state.getSecond();
		int maxId = state.getFirst();
		
		//debug
		//System.out.println("state: " + state);
		
		int maxs = 0;
		for (int i = 0; i <similarityStorageDevices.size(); i++) 
			if (maxIds.get(i) != null && maxIds.get(i).getFirst() == maxId)
				maxs++;
		
		for (int i = 0; i <similarityStorageDevices.size(); i++) {
			
			//debug
			//System.out.println("i = " + i);
			
			//debug
			//System.out.println("i = " + i + ", maxId = " + (maxIds.get(i) == null ? null : maxIds.get(i).getFirst()) + ", generalMaxId = " + maxId);
			
			Pair<Integer, Serializable> pair = null;
			if (maxIds.get(i) == null || maxs == similarityStorageDevices.size() || maxIds.get(i).getFirst() < maxId) {
				pair = similarityStorageDevices.get(i).read();

				//debug
				//System.out.println("pair = " + pair);

				if (pair != null) {
					int id = pair.getFirst();
					
					//debug
					//System.out.println("id = " + id);
					//System.out.println(i + ": " + id);
					
					maxIds.set(i,pair);
					maxId = Math.max(maxId,id);
					
					if (i>0) {
						//debug
						//System.out.println("id = " + id +", prev = " + maxIds.get(i-1).getFirst());
						
						if (id == maxIds.get(i-1).getFirst())
							iequals++;
					}
				} else
					throw new EOFException();
			}
		}
		state.setFirst(maxId);
		
		//debug
		//System.out.println("iequals = " + iequals);
		
		return iequals == similarityStorageDevices.size();
	}

/*	public static void main(String[] args) {
		

		PropertyConfigurator.configure("log4j.properties");
		
		if (args.length != 1) {
			System.err.println("Usage: GeneralElementSimilarityCombiner <configuration file>");
			System.exit(0);
		}

		
		try {			
		
			ConfigurationFile confFile = new ConfigurationFile(args[0]);
			
			ConfigurationParams loggingParams = confFile.getModuleConfiguration(Configuration.LOGGING);
			PropertyConfigurator.configure(loggingParams.get(Configuration.PROPERTIES_FILE));
						
			final ConfigurationParams similarityCombinerParams = confFile.getModuleConfiguration(Configuration.ELEMENT_SIMILARITY_COMBINER);			
			
			org.excitement.distsim.storage.File combinedDevice = new org.excitement.distsim.storage.File(new java.io.File(similarityCombinerParams.getString(Configuration.OUT_COMBINED_FILE)),false);
			combinedDevice.open();
			
			SimilarityCombination similarityCombination = (SimilarityCombination)Factory.create(similarityCombinerParams.get(Configuration.SIMILARITY_COMBINATION_CLASS));
			String[] infiles = similarityCombinerParams.getStringArray(Configuration.IN_FILES);
			// build the combined similarity storage
			List<PersistenceDevice> similarityStorageDevices = new LinkedList<PersistenceDevice>();
			for (int i = 0; i<infiles.length; i++) 
				similarityStorageDevices.add(new org.excitement.distsim.storage.File(new File(infiles[i]),true));
			ElementSimilarityCombiner combiner = new OrderedBasedGeneralElementSimilarityCombiner();
			for (PersistenceDevice device : similarityStorageDevices)
				device.open();
			combiner.combinedScores(similarityStorageDevices, similarityCombination,combinedDevice);
			for (PersistenceDevice device : similarityStorageDevices)
				device.close();			
			combinedDevice.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}*/
}