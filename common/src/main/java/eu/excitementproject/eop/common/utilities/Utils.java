package eu.excitementproject.eop.common.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;


/**
 * A collection of general utilities.
 * 
 * @author Asher Stern
 * 
 *
 */
public class Utils
{
	public static final long MEGA = 1048576;
	public static final String COMPILED_CLASS_EXTENSION = ".class";
	
	/**
	 * Return a sorted list with the collection's elements.
	 * 
	 * @param collection
	 * @return
	 */
	public static <T extends Comparable<T>> List<T> getSorted(Collection<T> collection)
	{
		List<T> ret = new ArrayList<T>(collection.size());
		ret.addAll(collection);
		Collections.sort(ret);
		return ret;
	}
	
	/**
	 * Constructs a new java.util.Collection that will contain the given array elements.
	 * 
	 * @param <E>
	 * @param <C>
	 * @param array
	 * @param collection
	 * @return
	 */
	public static <E,C extends Collection<E>> C arrayToCollection(E[] array,C collection)
	{
		collection.clear();
		for (E e : array)
		{
			collection.add(e);
		}
		return collection;
	}
	
	/**
	 * Creates an array that will contain the given collection's elements.
	 * @param <E>
	 * @param <C>
	 * @param collection
	 * @param array
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E,C extends Collection<? extends E>> E[] collectionToArray(C collection, E[] array)
	{
		E[] ret = null;
		if (collection!=null)
		{
			if (array.length==collection.size())
			{
				ret = array;
			}
			else
			{
				ret = (E[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), collection.size());
			}
			int index=0;
			for (E element : collection)
			{
				ret[index]=element;
				++index;
			}
		}
		else
		{
			ret = (E[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), 0);
		}
		return ret;

		
		
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public static <E,C extends Collection<? extends E>> E[] collectionToArray(C collection)
	{
		E[] ret = null;
		if (collection!=null)
		{
			ret = (E[]) new Object[collection.size()];
			int index=0;
			for (E element : collection)
			{
				ret[index]=element;
				++index;
			}
		}
		else
		{
			ret = (E[]) new Object[0];
		}
		return ret;
	}
	
	
	
	/**
	 * Given a collection - returns one of its elements, chosen randomly.
	 * @param <E>
	 * @param <C>
	 * @param collection
	 * @param random
	 * @return
	 */
	public static <E,C extends Collection<? extends E>> E getRandomElementOfCollection(C collection, Random random)
	{
		E ret = null;
		if (collection!=null) { if (collection.size()>0)
		{
			int randomIndex = random.nextInt(collection.size());
			int iterIndex = 0;
			Iterator<? extends E> iterator = collection.iterator();
			E element;
			for (element = iterator.next();iterator.hasNext() && iterIndex!=randomIndex;++iterIndex,element = iterator.next())
			{
			}
			ret = element;
			
			
//			E[] array = collectionToArray(collection);
//			int index = random.nextInt(array.length);
//			ret = array[index];
		}}
		return ret;
	}
	
	/**
	 * compute moving averages of lists, initializing with either the <i>Neighbor</i> formula or the <i>regular</i> formula
	 * @param data a list of numbers we want a moving average for
	 * @param window the width of the window - how many cells to the right and left we look at
	 * @param base the exponential decay base.
	 * @param useNeighborFormula determines whether to initialize the windowSum and weightSum with the <i>Neighbor</i> formula or the regular one
	 * @return a smoothed list.
	 */
	public static List<Double> exponentialMovingAverageSmoother(List<Double> data, int window, double base, boolean useNeighborFormula) {
		
		List<Double> result = new LinkedList<Double>();
				
		for(int i = 0; i < data.size(); i++) {
			
			double windowSum;						
			double weightSum;
			
			if (useNeighborFormula)
			{
				windowSum = 0.0;			
				weightSum = 0.0;				
			}
			else
			{
				windowSum = data.get(i);						
				weightSum = 1;
			}			
			
			for(int j = 1; j <= window; ++j) {
				double currWeight = Math.pow(base, j);
				if(i-j>=0) {
					windowSum+=currWeight*data.get(i-j);
					weightSum+=currWeight;
				}
				if(i+j<data.size()) {
					windowSum+=currWeight*data.get(i+j);
					weightSum+=currWeight;
				}
			}
			result.add(windowSum / weightSum);
		}
		return result;
	}
	
	/**
	 * Return a new Collection containing the elements of Collection a minus elements of Collection b 
	 * @param <T>
	 * @param primaryCollection
	 * @param toBeRemovedCollection
	 * @param target a collection that will be returned by this method. I will be cleared by this method before inserting the required elements. 
	 * @return a new Collection containing the elements of Collection a minus elements of Collection b
	 */
	public static <T> Collection<T> minus(Collection<T> primaryCollection, Collection<T> toBeRemovedCollection,	Collection<T> target)
	{
		target.clear();
		
		for(T elem : primaryCollection)
		{
			if(!toBeRemovedCollection.contains(elem)){
				target.add(elem);
			}
		}
		return target;
	}
	
	/**
	 * return a new Collection with the elements which appear both in a and b 
	 * @param <T>
	 * @param collA
	 * @param collB
	 * @param target a collection that will be returned by this method. I will be cleared by this method before inserting the required elements. 
	 * @return a new Collection with the elements which appear both in a and b 
	 */
	public static <T> Collection<T> intersect(Collection<T> collA, Collection<T> collB, Collection<T> target)
	{
		target.clear();
		
		for(T elem : collA){
			if(collB.contains(elem)){
				target.add(elem);
			}
		}
		return target;
	}
	
	/**
	 * Returns the amount of memory currently used by the JVM, in megabytes.
	 * @return
	 */
	public static long memoryUsedInMB()
	{
		return (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/MEGA;
	}
	
	/**
	 * Returns the amount of memory currently used by the JVM, in megabytes, as a string.
	 * @return
	 */
	public static String stringMemoryUsedInMB()
	{
		String ret = String.format("%,d MB",Utils.memoryUsedInMB());
		return ret;
	}
	
	public static <X,Y,Z> BidirectionalMap<X, Z> concatBidirectionalMaps(BidirectionalMap<X, Y> first, BidirectionalMap<Y, Z> second)
	{
		BidirectionalMap<X, Z> ret = new SimpleBidirectionalMap<X, Z>();
		for (X x : first.leftSet())
		{
			Y y = first.leftGet(x);
			if (second.leftContains(y))
			{
				Z z = second.leftGet(y);
				ret.put(x, z);
			}
		}
		
		return ret;
	}
	
	public static <K,V> Map<K, Set<V>> mapsIntersection(Map<K, Set<V>> map1, Map<K, Set<V>> map2)
	{
		Map<K, Set<V>> ret = new LinkedHashMap<K, Set<V>>();
		if ( (map1!=null) && (map2!=null) )
		{
			for (K k : map1.keySet())
			{
				if (map2.containsKey(k))
				{
					Set<V> set1 = map1.get(k);
					Set<V> set2 = map2.get(k);
					if ( (set1!=null) && (set2!=null) )
					{
						Set<V> retSet = new LinkedHashSet<V>();
						for (V v : set1)
						{
							if (set2.contains(v))
							{
								retSet.add(v);
								
							}
						}
						ret.put(k, retSet);
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * Takes a map that its value is comparable (e.g. Integer, Double), and returns a list
	 * of its keys, such that the list order is the order of the sorted values.
	 * <P>
	 * For example: Map<String,Integer> "a"->10, "b"->5", "c"->15. The list will be
	 * "b","a","c".
	 * <P>
	 * @param <K>
	 * @param <V>
	 * @param map
	 * @return
	 */
	public static <K,V extends Comparable<V>> ArrayList<K> getSortedByValue(Map<K,V> map)
	{
		@SuppressWarnings("unchecked")
		KeyWithComparable<K,V>[] asArray = (KeyWithComparable<K,V>[]) new KeyWithComparable[map.keySet().size()];
		int index=0;
		for (K k : map.keySet())
		{
			V v = map.get(k);
			asArray[index] = new KeyWithComparable<K, V>(k, v);
			index++;
		}
		Arrays.sort(asArray);
		ArrayList<K> ret = new ArrayList<K>(asArray.length);
		for (int i=0;i<asArray.length;++i)
		{
			ret.add(asArray[i].getK());
		}
		return ret;
	}
	
	
	/**
	 * Generates a random permutation of the numbers {0,1,...,(length-1)}.
	 * Runs in linear time and space.
	 * @param length
	 * @return
	 */
	public static int[] randomPermutation(int length)
	{
		return randomPermutation(length, new Random());
	}
	
	public static int[] randomPermutation(int length, Random random)
	{
		int[] ret = new int[length];
		for (int index=0;index<length;index++){ret[index]=index;}
		for (int index=(length-1);index>0;index--)
		{
			int randomIndex = random.nextInt((index+1));
			int temp = ret[index];
			ret[index] = ret[randomIndex];
			ret[randomIndex] = temp;
		}
		return ret;
	}
	
	
	
	/**
	 * Makes a partition of a given list into several lists. The number
	 * of the returned lists (the "parts") is given by the parameter <code>numberOfParts</code>.
	 * 
	 * @param <T> The type of the list elements
	 * @param list the list to be partitioned.
	 * @param numberOfParts number of parts into which the list will be partitioned.
	 * 
	 * @return A list of lists, each list is a sub-list of the original one,
	 * and the concatenation of them is the original list itself. The elements are
	 * divided such that each list has more-or-less equal size, up-to -+1.
	 */
	public static <T> ArrayList<List<T>> fairPartition(List<T> list, int numberOfParts)
	{
		ArrayList<List<T>> ret = new ArrayList<List<T>>(numberOfParts);
		Iterator<T> iterator = list.iterator();
		double dnumberOfParts = (double)numberOfParts;
		double total = (double) list.size();
		
		for (int partsIndex=0;partsIndex<numberOfParts;++partsIndex)
		{
			double currentPartFractional = total/dnumberOfParts;
			int currentPart = 0;
			if ((currentPartFractional-(int)currentPartFractional)>=0.5)
			{
				currentPart =(int)currentPartFractional+1;
			}
			else
			{
				currentPart =(int)currentPartFractional;
			}
			List<T> currentPartList = new ArrayList<T>(currentPart);
			for (int index=0;index<currentPart;++index)
			{
				currentPartList.add(iterator.next());
			}
			ret.add(currentPartList);
			total-=(double)currentPart;
			dnumberOfParts--;
		}
		
		
		return ret;
	}
	
	/**
	 * Reverse a {@link Map} into a {@link ValueSetMap}, where each original value is mapped to the set of all its original keys.
	 * 
	 * @param mapPredToType
	 * @return
	 */
	public static <K, V> ValueSetMap<V, K> reverseMapIntoValueSetMap(Map<K, V> map) {
		ValueSetMap<V, K> reversedMap = new SimpleValueSetMap<V, K>();
		for (K origKey : map.keySet())
		{
			V origValue = map.get(origKey);
			reversedMap.put(origValue, origKey);
		}
		return reversedMap;
	}
	
	
	///////////////////////////// PRIVATE PART ///////////////////////////////////////
	
	
	/**
	 * Private nested class used by {@link Utils#getSortedByValue(Map)}.
	 * @author Asher Stern
	 * @since Aug 29, 2010
	 *
	 * @param <K>
	 * @param <V>
	 */
	private static class KeyWithComparable<K,V extends Comparable<V>> implements Comparable<KeyWithComparable<K,V>>
	{
		public KeyWithComparable(K k, V v)
		{
			super();
			this.k = k;
			this.v = v;
		}

		public int compareTo(KeyWithComparable<K, V> o)
		{
			return this.v.compareTo(o.getV());
		}
		
		public K getK()
		{
			return k;
		}
		public V getV()
		{
			return v;
		}

		private final K k;
		private final V v;
	}

	
	/**
	 * Convert the stack trace of the given exception to a string.
	 */
	public static String stackTraceToString(Throwable ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return ex.getMessage()+"\n"+sw.toString();
	}

	
	/**
	 * Serialize the given object to a byte-array.
	 * @see http://www.velocityreviews.com/forums/t561666-how-to-serialize-a-object-to-a-string-or-byte.html
	 */
	public static byte[] serializeObjectToByteArray(Object theObject) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ObjectOutputStream output = new ObjectOutputStream(baos);
		output.writeObject(theObject);
		byte[] serialized = baos.toByteArray();
		return serialized;
	}
	
	/**
	 * Deserialize the object that was serialized into the given byte-array.
	 * @see http://www.velocityreviews.com/forums/t561666-how-to-serialize-a-object-to-a-string-or-byte.html
	 */
	public static Object deserializeObjectFromByteArray(byte[] theSerializedVersion) throws IOException, ClassNotFoundException {
		final ByteArrayInputStream bais = new ByteArrayInputStream(theSerializedVersion);
		final ObjectInputStream input = new ObjectInputStream(bais);
		return input.readObject();
	}
	
	/**
	 * Return the source - typically the JAR file - from which a given class is loaded to the JVM
	 * <BR>
	 * Works for Windows and Linux, and I think it works for all OSs. 
	 * @param cls
	 * @return
	 */
	public static String getSourceOfClass(Class<?> cls)
	{
		if (null==cls) return "null class";
		else	if (null==cls.getClassLoader())
			return "Class loader of "+cls.getName()+" is null. Cannot retrieve its source.";
		else
			return cls.getClassLoader().getResource(cls.getName().replace(".", "/")+COMPILED_CLASS_EXTENSION).getPath();
	}

	/**
	 * Return a list of size <code>size</code> which has elements from the
	 * given list, chosen randomly.
	 * 
	 * @param list the original list
	 * @param size the size of the returned list
	 * @return a list of <code>size</code> elements chosen randomly from the original list.
	 */
	public static <T> List<T> randomlyPickElements(List<T> list, int size)
	{
		@SuppressWarnings("unchecked")
		T[] array = (T[]) list.toArray();
		
		Random random = new Random();
		
		for (int index=0;index<size;++index)
		{
			int chooseIndex = random.nextInt(array.length);
			T temp = array[index];
			array[index] = array[chooseIndex];
			array[chooseIndex] = temp;
		}
		
		List<T> returnList = new ArrayList<T>(size);
		for (int index=0;index<size;++index)
		{
			returnList.add(array[index]);
		}
		
		return returnList;
	}

}
