package eu.excitementproject.eop.common.utilities;

import java.util.*;


/**
 * Static utility functions for extracting samples from collections, either arbitrarily or at random. 
 *
 * @author Erel Segal Halevi
 * @since 2012-09-19
 */
public class SamplingUtils {
	
	
	/**
	 * <p>Choose an arbitrary element from the given set (NOT at random). 
	 * <p>This is useful for quickly peeking inside a collection, to get an idea of what's inside.
	 * @param <T>
	 * @see SamplingUtils#chooseSome(Collection, int, Object[])
	 * @see SamplingUtils#pickRandomElement(List, Object)
	 */
	public static <T> T chooseOne(Collection<T> theSet) {
		if (theSet==null) return null;
		for (T element: theSet)
			return element;
		return null;
	}

	/**
	 * Choose arbitrary elements from the given set (NOT at random), and insert into the given array.
	 * The number of elements selected will be theChoice.length.
	 * @param theSet [input] 
	 * @param theCount [input] 
	 * @param theChoice [output]
	 * @return theChoice
	 * @see SamplingUtils#pickRandomSample(List, int)
	 */
	public static <T> T[] chooseSome(Collection<T> theSet, int theCount, T[] theChoice) {
		int count=0;
		for (T element: theSet) {
			if (theCount <= count)
				break;
			theChoice[count] = element;
			count++;
		}
		return theChoice;
	}

	/**
	 * @return a random element picked from list. If the list is empty or null, return defaultValue. 
	 */
	public static <T> T pickRandomElement(List<T> list, T defaultValue) {
		if (list==null || list.isEmpty())
			return defaultValue;
		else
			return list.get(random.nextInt(list.size()));
	}

	/**
	 * @return a random element picked from list. If the list is empty or null, return null. 
	 */
	public static <T> T pickRandomElement(List<T> list) {
		return pickRandomElement(list, null);
	}

	/**
	 * @return a random element picked from collection. If the collection is empty or null, return defaultValue. 
	 * @note this method may take time O(list.size()). If "collection" is actually a list, use {@link #pickRandomElement(List)}
	 */
	public static <T> T pickRandomElement(Collection<T> collection, T defaultValue) {
		if (collection==null || collection.isEmpty()) {
			return null;
		} else {
			int index = random.nextInt(collection.size());
			for (T element: collection) {
				if (index==0)
					return element;
				index--;
			}
			throw new InternalError("no element found - should not get here");
		}
	}
	
	/**
	 * @return a random element picked from collection. If the collection is empty or null, return null. 
	 */
	public static <T> T pickRandomElement(Collection<T> collection) {
		return pickRandomElement(collection, null);
	}

	/**
	 * <p>Get a random sample of the given size from the given list. 
	 * <p>Average complexity is O(m), which may be much smaller than shuffling the list and picking the first elements.
	 * @return a set of size m, randomly selected from items
	 * @note uses an algorithm attributed to Floyd.
	 * @see http://eyalsch.wordpress.com/2010/04/01/random-sample/
	 */
	public static <T> Set<T> pickRandomSample(List<T> items, int sampleSize) {   
		int n = items.size();
		if (sampleSize>=n)
			return new LinkedHashSet<T>(items);  // return all items as sample
		LinkedHashSet<T> res = new LinkedHashSet<T>(sampleSize); 
		for(int i=n-sampleSize;i<n;i++) {
			int pos = random.nextInt(i+1);
			T item = items.get(pos);
			if (res.contains(item))
				res.add(items.get(i));
			else
				res.add(item);
		}
		return res;
	}
	
	/**
	 * <p>Change the random number generator used for creating the samples. 
	 * <p>Useful for generating repeatable samples.
	 * @param newRandom
	 */
	public static void setRandom(Random newRandom) {
		random = newRandom;
	}
	
	

	
	
	/*
	 * PRIVATE ZONE
	 */

	private static Random random = new Random();



	/*
	 * demo program
	 */
	public static void main(String[] args) {
		List<Integer> numbers = Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8,9,10});
		System.out.println("Changing sample: "+SamplingUtils.pickRandomSample(numbers, 3));
		
		SamplingUtils.setRandom(new Random(1));
		System.out.println("Constant sample: "+SamplingUtils.pickRandomSample(numbers, 3));
	}

}
