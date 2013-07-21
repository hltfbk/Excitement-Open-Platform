package eu.excitementproject.eop.common.utilities;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Implementation of {@link Cache} using two <code>java.util.Map</code>.
 * The maps are maintained such that their size does not exceed the "capacity"
 * declared for the cache.
 * <P>
 * The logic is as follows: each key-value element is stored in the "used-map".
 * If a new element has to be stored in the cache, but the size of the "used-map"
 * reached the capacity, then this map is changed to be "previous-map", and a new
 * empty map is created for "used-map". Eventually, when the new "used-map" reaches
 * the capacity too, then the previous map is discarded, the "used-map" becomes
 * "previous-map", and a new empty "used-map" is created.
 * 
 * 
 * @author Asher Stern
 * 
 *
 * @param <K>
 * @param <V>
 */
public class TwoMapsCache<K,V> implements Cache<K, V>
{
	public static final int MINIMUM_CAPACITY = 10;
	
	public TwoMapsCache()
	{
		
	}
	public TwoMapsCache(int capacity)
	{
		if (capacity>MINIMUM_CAPACITY)
			this.maxCapacity = capacity;
	}
	
	/*
	 * (non-Javadoc)
	 * @see eu.excitementproject.eop.common.utilities.Cache#containsKey(java.lang.Object)
	 */
	public boolean containsKey(K key)
	{
		boolean ret = false;
		if (usedMap.containsKey(key))
			ret = true;
		else if (previousMap.containsKey(key))
			ret = true;
		
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see eu.excitementproject.eop.common.utilities.Cache#get(java.lang.Object)
	 */
	public V get(K key)
	{
		V ret = null;
		if (usedMap.containsKey(key))
			ret = usedMap.get(key);
		else if (previousMap.containsKey(key))
		{
			ret = previousMap.get(key);
			if (usedMap.keySet().size()>=maxCapacity)
			{
				previousMap = usedMap;
				usedMap = new LinkedHashMap<K, V>();
			}
			usedMap.put(key,ret);
		}
		
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see eu.excitementproject.eop.common.utilities.Cache#put(java.lang.Object, java.lang.Object)
	 */
	public void put(K key, V value)
	{
		if (usedMap.containsKey(key))
			;
		else
		{
			if (usedMap.keySet().size()>=maxCapacity)
			{
				previousMap = usedMap;
				usedMap = new LinkedHashMap<K, V>();
			}
			usedMap.put(key,value);
		}
		
		
		
		
	}

	protected Map<K, V> usedMap = new LinkedHashMap<K, V>();
	protected Map<K, V> previousMap = new LinkedHashMap<K, V>();
	protected int maxCapacity = MINIMUM_CAPACITY;
	
}
