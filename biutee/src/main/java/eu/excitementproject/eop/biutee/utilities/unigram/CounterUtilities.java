package eu.excitementproject.eop.biutee.utilities.unigram;

import java.util.Map;

/**
 * 
 * @author Asher Stern
 * @since May 13, 2013
 *
 */
public class CounterUtilities
{
	public static <K> void add(Map<K, Long> map, K key, long value)
	{
		Long currentObj = map.get(key);
		long current = 0;
		if (currentObj!=null)
		{
			current+=currentObj.longValue();
		}
		map.put(key, value+current);
	}
	
	public static <K> long valueOf(Map<K,Long> map, K key)
	{
		Long currentObj = map.get(key);
		if (null==currentObj)
		{
			return 0;
		}
		else
		{
			return currentObj.longValue();
		}
	}

}
