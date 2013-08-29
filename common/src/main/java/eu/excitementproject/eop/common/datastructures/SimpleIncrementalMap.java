package eu.excitementproject.eop.common.datastructures;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;


/**
 * A simple implementation of {@link IncrementalMap} that uses <code>
 * java.util.LinkedHashMap</code> for the underlying map,
 * and <code>java.util.HashSet</code> for the underlying set of closed keys.
 * 
 * @author Asher Stern
 *
 * @param <K>
 * @param <V>
 */
public class SimpleIncrementalMap<K, V> implements IncrementalMap<K, V>
{
	private static final long serialVersionUID = -4240184220276767706L;
	
	// Exception class
	@SuppressWarnings("serial")
	public static class IncrementalMapInitializationException extends Exception
	{public IncrementalMapInitializationException(String message){super(message);}}
	
	// public constructors and methods
	
	public SimpleIncrementalMap()
	{
		
	}
	
	public SimpleIncrementalMap(Map<K, V> givenMap) throws IncrementalMapInitializationException
	{
		if (null==givenMap) throw new IncrementalMapInitializationException("null==givenMap");
		if (givenMap.isEmpty()) throw new IncrementalMapInitializationException("givenMap is empty.");
		this.realMap = givenMap;
	}
	
	
	public boolean containsKey(K key)
	{
		return realMap.containsKey(key);
	}

	public V get(K key)
	{
		return realMap.get(key);
	}

	public boolean isEmpty()
	{
		return realMap.isEmpty();
	}

	public ImmutableSet<K> keySet()
	{
		return new ImmutableSetWrapper<K>(realMap.keySet());
	}

	public void put(K key, V value) throws IncrementalMapException
	{
		if (realMap.containsKey(key))
			throw new IncrementalMapException("The given key is already mapped.", key);
		if (closedKeys.contains(key))
			throw new IncrementalMapException("The given key is already closed.", key);
		
		realMap.put(key, value);
	}

	public int size()
	{
		return realMap.size();
	}
	
	public void closeKey(K key) throws IncrementalMapException
	{
		if (closedKeys.contains(key))
			throw new IncrementalMapException("The given key is already closed.", key);
		if (realMap.containsKey(key))
			throw new IncrementalMapException("The given key is already mapped.", key);
		
		closedKeys.add(key);
	}
	
	public ImmutableSet<K> getClosedKeys()
	{
		return new ImmutableSetWrapper<K>(this.closedKeys);
	}



	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((closedKeys == null) ? 0 : closedKeys.hashCode());
		result = prime * result + ((realMap == null) ? 0 : realMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		SimpleIncrementalMap other = (SimpleIncrementalMap) obj;
		if (closedKeys == null)
		{
			if (other.closedKeys != null)
				return false;
		} else if (!closedKeys.equals(other.closedKeys))
			return false;
		if (realMap == null)
		{
			if (other.realMap != null)
				return false;
		} else if (!realMap.equals(other.realMap))
			return false;
		return true;
	}








	protected Map<K, V> realMap = new LinkedHashMap<K, V>();
	protected Set<K> closedKeys = new LinkedHashSet<K>();



}
