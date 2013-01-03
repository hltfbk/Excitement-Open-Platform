package ac.biu.nlp.nlp.general;

import java.util.HashMap;
import java.util.Map;


/**
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
	
	public boolean containsKey(K key)
	{
		boolean ret = false;
		if (usedMap.containsKey(key))
			ret = true;
		else if (previousMap.containsKey(key))
			ret = true;
		
		return ret;
	}

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
				usedMap = new HashMap<K, V>();
			}
			usedMap.put(key,ret);
		}
		
		return ret;
	}

	public void put(K key, V value)
	{
		if (usedMap.containsKey(key))
			;
		else
		{
			if (usedMap.keySet().size()>=maxCapacity)
			{
				previousMap = usedMap;
				usedMap = new HashMap<K, V>();
			}
			usedMap.put(key,value);
		}
		
		
		
		
	}

	protected Map<K, V> usedMap = new HashMap<K, V>();
	protected Map<K, V> previousMap = new HashMap<K, V>();
	protected int maxCapacity = MINIMUM_CAPACITY;
	
}
