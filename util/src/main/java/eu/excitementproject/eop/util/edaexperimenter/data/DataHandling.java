package eu.excitementproject.eop.util.edaexperimenter.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.stanford.nlp.util.StringUtils;
import eu.excitementproject.eop.lap.implbase.RawDataFormatReader.PairXMLData;

//@SuppressWarnings("unused")
public class DataHandling {
	
	/**
	 * Split data into training and testing, with the given ratio
	 * 
	 * @param data
	 * @param ratio
	 * @return
	 */
	public static HashMap<String, Set<PairXMLData>> splitData(
			HashMap<String, HashMap<String, Set<PairXMLData>>> data,
			double ratio, 
			int xval,
			String split) {
		
		if (split.matches("pure")) {
			return splitDataByCluster(data, ratio, xval);
		}
		
		HashMap<String, Set<PairXMLData>> dataSplit = new HashMap<String, Set<PairXMLData>>();

		for(String cluster: data.keySet()) {
			dataSplit = splitClusters(dataSplit, data, cluster, xval, ratio);
		}
		
		// if it is only training-testing, then rename the clusters accordingly (the larger one "train")
		if (xval == 2) { 
			Object[] names = dataSplit.keySet().toArray();
			if (dataSplit.get(names[0]).size() > dataSplit.get(names[1]).size()) {
				dataSplit.put("train", dataSplit.get(names[0]));
				dataSplit.put("test", dataSplit.get(names[1]));

				dataSplit.remove(names[0]);
				dataSplit.remove(names[1]);
			} else {
				
				dataSplit.put("train", dataSplit.get(names[1]));
				dataSplit.put("test", dataSplit.get(names[0]));

				dataSplit.remove(names[0]);
				dataSplit.remove(names[1]);				
			}
		}

		return dataSplit;
	}


	/**
	 * Splits instances in clusters among the required folds
	 * For N folds, put in each of the N-1 folds size_of_cluster * ratio instances (for each class),
	 * and all that remains put in the N-th cluster
	 * 
	 * @param dataSplit
	 * @param data
	 * @param cluster
	 * @param xval
	 * @param ratio
	 * @return
	 */
	private static HashMap<String, Set<PairXMLData>> splitClusters(
			HashMap<String, Set<PairXMLData>> dataSplit,
			HashMap<String, HashMap<String, Set<PairXMLData>>> data,
			String cluster, int xval, double ratio) {
		
		Logger logger = Logger.getLogger("eu.excitementproject.eda-exp.experimenter.DataHandling / splitClusters");
		
		String[] keys = new String[xval+1];
		Double[] ratios = new Double[xval+1];
		boolean initializeSplits = dataSplit.isEmpty();
		int size = 0;
		
		for(int i = 1; i <= xval; i++) {
			keys[i] = i + "";
			ratios[i] = ratio;
			if (initializeSplits) {
				Set<PairXMLData> newSet = new HashSet<PairXMLData>();
				dataSplit.put(keys[i], newSet);
			} else {
				size += dataSplit.get(keys[i]).size();
			}
		}
		
		// this mostly for when we have training/testing split only
		ratios[xval] = 1 - (xval-1) * ratio;
		Double random;
		int randomInt;
		boolean assigned = false;
		
		for(String cls: data.get(cluster).keySet()) {
			size += data.get(cluster).get(cls).size();
			
			logger.info("\t processing cluster " + cluster + ", class " + cls + " (" + data.get(cluster).get(cls).size() + " instances)");
//			System.out.println("\t processing cluster " + cluster + ", class " + cls + " (" + data.get(cluster).get(cls).size() + " instances)");
			
			for(PairXMLData p: data.get(cluster).get(cls)) {
				
				assigned = false;
				while (! assigned) {
					random = Math.random() * (xval) + 1 ;
					randomInt = random.intValue();
//					System.out.println("\t\t generated cluster id: " + randomInt);
					if (dataSplit.get(randomInt + "").size() <= size * ratios[randomInt]) {
						Set<PairXMLData> set = dataSplit.get(randomInt + "");
						set.add(p);
						dataSplit.put(randomInt + "", set);
						assigned = true;
//						System.out.println("\t\t\t assigned!");
					}
				}
			}
		}
		
		return dataSplit;
	}


	public static HashMap<String, Set<PairXMLData>> splitDataByCluster(
			HashMap<String, HashMap<String, Set<PairXMLData>>> data, 
			double ratio,
			int xval) {
		
		Logger logger =  Logger.getLogger("eu.excitementproject.eda_exp.data.DataHandling / splitDataByCluster");
		logger.info("Splitting data by cluster:  xval = " + xval + " / ratio = " + ratio);
		logger.info("Data size: " + data.size() + " number of keys: " + data.keySet().size());
		
		int nClusters = data.size();
		if (nClusters < xval) {
			logger.error("Cannot do cluster-based split when there are fewer clusters than Xval folds!");
			System.exit(1);
		}
		
		HashMap<String, Set<PairXMLData>> dataSplit = new HashMap<String, Set<PairXMLData>>();
		if (nClusters == xval) {
			for(String cluster: data.keySet()) {
				Set<PairXMLData> inst = new HashSet<PairXMLData>();
				for(String cls: data.get(cluster).keySet()) {
					inst.addAll(data.get(cluster).get(cls));
				}
				dataSplit.put(cluster, inst);
			}
		} else {
			
			// split the clusters among the folds
			
			HashMap<String,Integer> clusterFoldMap = makeMap(data, xval, ratio);
			
			for(String cluster: clusterFoldMap.keySet()) {
				Set<PairXMLData> pairs;
				String fold = clusterFoldMap.get(cluster).toString();
				if (dataSplit.containsKey(fold)) {
					pairs = dataSplit.get(fold);
				} else {
					pairs = new HashSet<PairXMLData>();
				}
				
				for(String cls: data.get(cluster).keySet()) {
					pairs.addAll(data.get(cluster).get(cls));
				}
				dataSplit.put(fold, pairs);
			}			
		}
		
		logger.info("data key set size: " + dataSplit.keySet().size());
		String[] names = new String[dataSplit.keySet().size()];
		int i = 0;
		for(String s: dataSplit.keySet()) {
			logger.info("key: " + s);
			names[i++] = s;
		}
		
		// if it is only training-testing, then rename the clusters accordingly (the larger one "train")
		if (xval == 2) { 
//			String[] names = (String[]) dataSplit.keySet().toArray();
			if (dataSplit.get(names[0]).size() > dataSplit.get(names[1]).size()) {
				dataSplit.put("train", dataSplit.get(names[0]));
				dataSplit.put("test", dataSplit.get(names[1]));

				dataSplit.remove(names[0]);
				dataSplit.remove(names[1]);
			} else {
				
				dataSplit.put("train", dataSplit.get(names[1]));
				dataSplit.put("test", dataSplit.get(names[0]));

				dataSplit.remove(names[0]);
				dataSplit.remove(names[1]);		
			}
		}
		
		return dataSplit;
	}


	private static HashMap<String, Integer> makeMap(
			HashMap<String, HashMap<String, Set<PairXMLData>>> data, int xval, double ratio) {

		Logger logger = Logger.getLogger("eu.excitementproject.eda-exp.experimenter.DataHandling / makeMap");
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		SortedMap<Integer, Set<String>> sizes = new TreeMap<Integer,Set<String>>();
		HashMap<String,Integer> clsSizes = new HashMap<String,Integer>();
		
		for(String cluster: data.keySet()) {
			logger.info("Processing cluster " + cluster);
			int size = 0;
			for(String cls: data.get(cluster).keySet()) {
				size += data.get(cluster).get(cls).size();
			}
			clsSizes.put(cluster, size);
			Set<String> x = new HashSet<String>();
			x.add(cluster);
			sizes.put(size, x);
		}

		SortedMap<Integer, Set<String>> folds = new TreeMap<Integer,Set<String>> ();
		// initialize the folds with the largest clusters
		for(int i = 1; i <= xval; i++) {
			int max = sizes.lastKey();
			folds.put(max, sizes.get(max));
			sizes.remove(max);
	
			logger.info("Initializing a fold " + folds.get(max) + " of size " + max);
		}
		
		// increase the smallest fold by adding the biggest cluster
		while(! sizes.isEmpty()) {
			int min = folds.firstKey();
			int max = sizes.lastKey();
			Set<String> clusters = folds.get(min);
	
			logger.info("Adding " + max + " (" + StringUtils.join(sizes.get(max).toArray(), " ") + ") to fold " + min + " (" + StringUtils.join(clusters.toArray(), " ") + ")");
			
			clusters.addAll(sizes.get(max));
			sizes.remove(max);

			folds.remove(min); 
			min += max;
			
			// what if we obtain a fold of a previously seen size?
			while (folds.containsKey(min)) { min--; }
			folds.put(min,clusters);
		}
		
		logger.info("Distribution of clusters into folds done! " + folds.size() + " folds");
		
		folds = enforceRatio(folds, ratio);
		
		int i = 1;
		for(int size: folds.keySet()) {
			for(String cluster: folds.get(size)) {
				map.put(cluster, i);
				logger.info("Cluster " + i + " : " + cluster + " (" + clsSizes.get(cluster) + ")");
			}
			i++;
		}
		
		return map;
	}


	// not sure how to do this ... it is used (for now) to enforce the training/testing split ratio when clusters are not split (inside)
	private static SortedMap<Integer, Set<String>> enforceRatio(
			SortedMap<Integer, Set<String>> folds, double ratio) {

		Logger logger = Logger.getLogger("eu.excitementproject.eda-exp.experimenter.DataHandling / enforceRatio");
		logger.setLevel(Level.INFO);
		
		for(Integer i: folds.keySet()) {
			logger.info(" key=" + i + " val=" + folds.get(i));
		}
		
		return folds;
	}


	// filter the data if the "balance" option was given
	public static HashMap<String,HashMap<String,Set<PairXMLData>>> filterData(
			HashMap<String,HashMap<String,Set<PairXMLData>>> data, boolean balance) {
		
		if (! balance) {
			return data;
		}
		
		HashMap<String,HashMap<String,Set<PairXMLData>>> dataFiltered = new HashMap<String,HashMap<String,Set<PairXMLData>>>();
		for(String cluster: data.keySet()) {
			dataFiltered.put(cluster, balanceCluster(data, cluster));
		}
		
		return dataFiltered;
	}

	// balances the positive/negative instances within a cluster by undersampling the majority class
	/**
	 * 
	 * @param cluster the name of the cluster to be balanced
	 */
	private static HashMap<String,Set<PairXMLData>> balanceCluster(HashMap<String,HashMap<String,Set<PairXMLData>>> dataRaw, String cluster) {

		Logger logger = Logger.getLogger("eu.excitementproject.eda-exp.experimenter.DataHandling / balanceCluster");
		logger.setLevel(Level.INFO);
		
		HashMap<String,Set<PairXMLData>> oldCluster = dataRaw.get(cluster);
		HashMap<String,Set<PairXMLData>> balancedCluster = new HashMap<String,Set<PairXMLData>>();
		
		logger.info("Balancing clusters!");
		
		// adjust how close to the 50/50 ratio we should get with undersampling. 
		double ratio = 1.0;
		int min = Integer.MAX_VALUE;
		
		if (oldCluster.keySet().size() <= 1) {
			min = 0;
		} else {
			for(String cls: oldCluster.keySet()) {
				int size = oldCluster.get(cls).size();
				if ( size < min) {
					min = size;
				}
			}
		}
		
		for(String cls: oldCluster.keySet()) {
			
			logger.info("Balancing cluster " + cls + ", size = " + oldCluster.get(cls).size());
			if (oldCluster.get(cls).size() == min) {
				balancedCluster.put(cls,oldCluster.get(cls));
			} else {
				balancedCluster.put(cls, undersample(oldCluster.get(cls), min * ratio));
			}
			
			logger.info("done processing " + cls + ", size = " + balancedCluster.get(cls).size());
			
		}		
		return balancedCluster;
	}

	// undersample randomly a given set of instances
	private static Set<PairXMLData> undersample(Set<PairXMLData> set, double d) {
		
		Logger logger = Logger.getLogger("eu.excitementproject.eda-exp.experimenter.DataHandling / balanceCluster");
		logger.setLevel(Level.INFO);
		
		logger.info("\tundersampling to size " + d);
		
		List<PairXMLData> setX = new ArrayList<PairXMLData>();
		setX.addAll(set);
		Collections.shuffle(setX);

		Set<PairXMLData> newSet = new HashSet<PairXMLData>();
		
		for(PairXMLData p: setX) {
			if (newSet.size() < d) {
				newSet.add(p);
			} else {
				continue;
			}
		}
				
		logger.info("\tnew set size: " + newSet.size());
		
		return newSet;
	}
}
