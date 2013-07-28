package eu.excitementproject.eop.transformations.datastructures;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;


/**
 * A fast implementation of java.util.Map<Integer,V>.<BR>
 * ================================<BR>
 * <B>Warning - not tested yet!</B><BR>
 * ================================<BR>
 * Assumes that the original map has no <tt>null</tt> values.
 * 
 * @author Asher Stern
 * @since Mar 2, 2011
 *
 * @param <V>
 */
public class FastIntegerKeyMap<V> implements Map<Integer, V>
{
	public static int MAX_KEY_ALLOWED = 100;
	
	@SuppressWarnings("serial")
	public static class FastIntegerKeyMapException extends Exception
	{public FastIntegerKeyMapException(String message){super(message);}}
	
	
	@SuppressWarnings("unchecked")
	public FastIntegerKeyMap(Map<Integer,V> otherMap, Class<V> valueClass) throws FastIntegerKeyMapException
	{
		int max = 0;
		for (Integer key : otherMap.keySet())
		{
			if (key<0)throw new FastIntegerKeyMapException("negative key");
			if (key>MAX_KEY_ALLOWED)throw new FastIntegerKeyMapException("to large key: "+key.toString());
			if (key>max)
				max = key;
		}
		
		values= (V[]) java.lang.reflect.Array.newInstance(valueClass,max+1);
		
		for (Integer key : otherMap.keySet())
		{
			values[key]=otherMap.get(key);
		}
		
		mapSize = otherMap.keySet().size();
	}

	public int size()
	{
		return mapSize;
	}

	public boolean isEmpty()
	{
		return (mapSize==0);
	}

	public boolean containsKey(Object key)
	{
		boolean ret = false;
		if (key instanceof Integer)
		{
			Integer keyInteger = (Integer) key;
			if (keyInteger!=null)
			{
				if ( (keyInteger<values.length) && (keyInteger>=0))
				{
					if (values[keyInteger]!=null)
						ret = true;
				}
			}
		}
		return ret;
	}

	public boolean containsValue(Object value)
	{
		boolean ret = false;
		for (V v : values)
		{
			if (v==null)
			{
				if (value==null)
				{
					ret = true;
					break;
				}
			}
			else
			{
				if (v.equals(value))
				{
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	public V get(Object key)
	{
		V ret = null;
		if (key!=null)
		{
			if (key instanceof Integer)
			{
				Integer keyInteger = (Integer) key;
				if (keyInteger<values.length)
				{
					ret = values[keyInteger];
				}
			}
		}
		return ret;
	}

	public V put(Integer key, V value)
	{
		throw new UnsupportedOperationException("FastIntegerKeyMap does not support modifications.");
	}

	public V remove(Object key)
	{
		throw new UnsupportedOperationException("FastIntegerKeyMap does not support modifications.");
	}

	public void putAll(Map<? extends Integer, ? extends V> m)
	{
		throw new UnsupportedOperationException("FastIntegerKeyMap does not support modifications.");
	}

	public void clear()
	{
		throw new UnsupportedOperationException("FastIntegerKeyMap does not support modifications.");
	}

	public Set<Integer> keySet()
	{
		Set<Integer> ret = new LinkedHashSet<Integer>();
		for (int index=0;index<values.length;++index)
		{
			if (values[index]!=null)
				ret.add(index);
		}
		return ret;
	}

	public Collection<V> values()
	{
		LinkedList<V> ret = new LinkedList<V>();
		for (int index=0;index<values.length;++index)
		{
			if (values[index]!=null)
				ret.add(values[index]);
		}
		return ret;
	}

	public Set<java.util.Map.Entry<Integer, V>> entrySet()
	{
		Set<java.util.Map.Entry<Integer, V>> ret = new LinkedHashSet<Map.Entry<Integer,V>>(); // will be NonModifiableHashSet
		for (int index=0;index<values.length;++index)
		{
			if (values[index]!=null)
			{
				final Integer finalIndex = index;
				ret.add(
						new java.util.Map.Entry<Integer, V>()
						{
							{
								key = finalIndex;
								value = values[finalIndex];
							}

							public Integer getKey()
							{
								return key;
							}

							public V getValue()
							{
								return value;
							}

							public V setValue(V value)
							{
								throw new UnsupportedOperationException("FastIntegerKeyMap does not support modifications.");
							}

							private Integer key;
							private V value;
						});
			}

		}
		ret = new NonModifiableHashSet<Map.Entry<Integer,V>>(ret);
		return ret;
	}
	
	
	
	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + mapSize;
		result = prime * result + Arrays.hashCode(values);
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
		FastIntegerKeyMap<?> other = (FastIntegerKeyMap<?>) obj;
		if (mapSize != other.mapSize)
			return false;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}





	protected V[] values;
	protected int mapSize = 0;
}
