package eu.excitementproject.eop.biutee.utilities.unigram;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @deprecated {@link #containsValue(Object)} is problematic.
 * @author Asher Stern
 * @since May 13, 2013
 *
 * @param <K>
 */
@Deprecated
public class CounterMap<K> implements Map<K, Integer>
{
	public CounterMap(Map<K, Integer> realMap)
	{
		super();
		realMap.clear();
		this.realMap = realMap;
	}

	@Override
	public int size()
	{
		return realMap.size();
	}

	@Override
	public boolean isEmpty()
	{
		return realMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key)
	{
		return realMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		if (zero.equals(value)) return true;
		return realMap.containsValue(value);
	}

	@Override
	public Integer get(Object key)
	{
		if (realMap.containsKey(key))
		{
			return realMap.get(key);
		}
		else
		{
			return zero;
		}
	}

	@Override
	public Integer put(K key, Integer value)
	{
		return realMap.put(key, value);
	}

	@Override
	public Integer remove(Object key)
	{
		return realMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends Integer> m)
	{
		realMap.putAll(m);
	}

	@Override
	public void clear()
	{
		realMap.clear();
	}

	@Override
	public Set<K> keySet()
	{
		return realMap.keySet();
	}

	@Override
	public Collection<Integer> values()
	{
		Collection<Integer> rmValues = realMap.values();
		if (!rmValues.contains(zero))
		{
			rmValues.add(zero);
		}
		return rmValues;
		
	}

	@Override
	public Set<java.util.Map.Entry<K, Integer>> entrySet()
	{
		// TODO Auto-generated method stub
		return null;
	}

	private final Map<K, Integer> realMap;
	
	private final Integer zero = new Integer(0);
}
