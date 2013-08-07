package eu.excitementproject.eop.distsim.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class Sampler<K> {
		
	/**
	 * Sampling of unique objects from a given map of objects and their count, in linear time
	 * @param objectCounts a map of objects and their counts
	 * @param samplesNum
	 * @return a set of amplesNum unique sampled objects 
	 */
	public Set<K> sample(Map<K,Integer> objectCounts, int samplesNum) {	
		
		Set<K> samples = new HashSet<K>();
		
		long total = 0;
		for (Integer count : objectCounts.values())
		   total +=count;
		int size = Math.min(objectCounts.size(),samplesNum);
		while (samples.size() < size) 
			samples .add(sample(objectCounts,total));
		return samples;
	}

	/**
	 * sampling of one object from a given map of objects and their count, in linear time
	 * @param a map of objects and their counts
	 * @return a key sampled from the distribution of objects
	 */
	protected K sample(Map<K,Integer> objectCounts, long total) {
	
		Random rand = new Random();
				
		int randNum = rand.nextInt((int)total);
		
		int sum = 0;
		for (Entry<K,Integer>  objectCount  : objectCounts.entrySet()) {
			sum += objectCount.getValue();
			if (sum > randNum)
				return objectCount.getKey();
		}
		//should not be reached
		return null;
	}
}
