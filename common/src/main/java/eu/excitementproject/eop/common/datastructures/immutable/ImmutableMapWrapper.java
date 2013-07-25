package eu.excitementproject.eop.common.datastructures.immutable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * An implementation of {@link ImmutableMap}. The implementation
 * wraps a <code>java.util.Map</code>, such that the user who has
 * this object can change nothing in the map.
 * However - the one who has the original <code>java.util.Map</code>,
 * <B>can</B> change the map - and the changes will take effect on this
 * map as well.
 * 
 * @author Asher Stern
 *
 * @param <K> the map's key type
 * @param <V> the map's elements type
 */
public class ImmutableMapWrapper<K,V> implements ImmutableMap<K,V>
{
	private static final long serialVersionUID = 4290850758144965446L;


	public ImmutableMapWrapper(Map<K,V> realMap)
	{
		this.realMap = realMap;
	}

	
	public boolean containsKey(Object key)
	{
		if (this.realMap!=null) return this.realMap.containsKey(key);
		else return false;
	}

	public boolean containsValue(Object value)
	{
		if (this.realMap!=null) return this.realMap.containsValue(value);
		else return false; 
	}

	public V get(Object key)
	{
		if (this.realMap!=null) return this.realMap.get(key);
		else return null;
	}

	public boolean isEmpty()
	{
		if (this.realMap!=null) return this.realMap.isEmpty();
		else return true;
	}

	public ImmutableSet<K> keySet()
	{
		if (null==this.realMap)
			return new ImmutableSetWrapper<K>(Collections.<K>emptySet());
		else
			return new ImmutableSetWrapper<K>(this.realMap.keySet());
	}
	
	public int size()
	{
		if (null==this.realMap)
			return 0;
		else
			return this.realMap.size();
	}

	public ImmutableCollection<V> values()
	{
		if (null==this.realMap)
		{
			return new ImmutableListWrapper<V>(new ArrayList<V>());
		}
		else
		{
			return new ImmutableListWrapper<V>(new ArrayList<V>(this.realMap.values()));
		}
	}
	
	public ImmutableMap<K, V> getImmutableCopy()
	{
		return new ImmutableMapWrapper<K,V>(this.getMutableCopy());
	}


	@SuppressWarnings("unchecked")
	public Map<K, V> getMutableCopy()
	{
		if (null==this.realMap)
			return new LinkedHashMap<K, V>();
		else
		{
			Map<K,V> copy = null;
			try
			{
				copy = this.realMap.getClass().newInstance();
				copy.putAll(this.realMap);
			}
			catch (Exception e)
			{
				copy = new HashMap<K, V>();
				copy.putAll(this.realMap);
			}
			return copy;
		}
	}

	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
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
		ImmutableMapWrapper other = (ImmutableMapWrapper) obj;
		if (realMap == null) {
			if (other.realMap != null)
				return false;
		} else if (!realMap.equals(other.realMap))
			return false;
		return true;
	}


	protected Map<K,V> realMap;
}
