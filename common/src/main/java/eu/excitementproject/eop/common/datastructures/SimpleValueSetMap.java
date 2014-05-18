package eu.excitementproject.eop.common.datastructures;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;


/**
 * Implementation of {@link ValueSetMap}. Not thread safe.
 * @author Asher Stern
 *
 * @param <K>
 * @param <V>
 */
public class SimpleValueSetMap<K, V> implements ValueSetMap<K, V>
{
	private static final long serialVersionUID = -4001256762637151472L;

	/////////////////////// PUBLIC PART /////////////////////////

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#clear()
	 */
	public void clear()
	{
		map = this.<K,Set<V>>newMap();
		mapValueToItsKeys = this.<V,Set<K>>newMap();
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#containsKey(java.lang.Object)
	 */
	public boolean containsKey(K key)
	{
		return map.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#containsValue(java.lang.Object)
	 */
	public boolean containsValue(V value)
	{
		return mapValueToItsKeys.containsKey(value);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#get(java.lang.Object)
	 */
	public ImmutableSet<V> get(K key)
	{
		return new ImmutableSetWrapper<V>(map.get(key));
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#getKeysOf(java.lang.Object)
	 */
	public ImmutableSet<K> getKeysOf(V value)
	{
		return new ImmutableSetWrapper<K>(mapValueToItsKeys.get(value));
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#isEmpty()
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#keySet()
	 */
	public ImmutableSet<K> keySet()
	{
		return new ImmutableSetWrapper<K>(map.keySet());
	}
	

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#values()
	 */
	public ImmutableSet<V> values()
	{
		return new ImmutableSetWrapper<V>(mapValueToItsKeys.keySet());
	}
	

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#put(java.lang.Object, java.lang.Object)
	 */
	public void put(K key, V value)
	{
		if (map.containsKey(key)) ;
		else map.put(key, this.<V>newSet());
		
		if (mapValueToItsKeys.containsKey(value)) ;
		else mapValueToItsKeys.put(value, this.<K>newSet());
		
		Set<V> itsSet = map.get(key);
		Set<K> itsKeys = mapValueToItsKeys.get(value);
		itsSet.add(value);
		itsKeys.add(key);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#remove(java.lang.Object)
	 */
	public void remove(K key)
	{
		if (map.containsKey(key))
		{
			Set<V> itsValueSet = map.get(key);
			if (itsValueSet!=null)
			{
				for (V value : itsValueSet)
				{
					Set<K> keys = mapValueToItsKeys.get(value);
					if (keys!=null)
					{
						if (keys.contains(key))
							keys.remove(key);
					}
					if (keys.size()==0)
						mapValueToItsKeys.remove(value);
					
				}
			}
			map.remove(key);
		}

		
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#removeValue(java.lang.Object)
	 */
	public void removeValue(V value)
	{
		if (mapValueToItsKeys.containsKey(value))
		{
			if (mapValueToItsKeys.get(value)!=null)
			{
				Set<K> itsKeys = mapValueToItsKeys.get(value);
				for (K key : itsKeys)
				{
					if (map.containsKey(key)){if (map.get(key)!=null)
					{
						Set<V> setv = map.get(key);
						if (setv.contains(value)) setv.remove(value);
						if (setv.size()==0)
							map.remove(key);
					}}
				}


			}
			mapValueToItsKeys.remove(value);
		}
		
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#size()
	 */
	public int size()
	{
		return map.size();
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#deepCopy()
	 */
	public ValueSetMap<K, V> deepCopy()
	{
		SimpleValueSetMap<K, V> ret = new SimpleValueSetMap<K, V>();
		for (K key : map.keySet())
		{
			if (map.get(key)!=null)
			{
				for (V value : map.get(key))
				{
					ret.put(key, value);
				}
			}
		}
		return ret;
	}
	
	
	
	///////////////// equals() and hashCode() implementations ///////////////////
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		result = prime
				* result
				+ ((mapValueToItsKeys == null) ? 0 : mapValueToItsKeys
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		SimpleValueSetMap other = (SimpleValueSetMap) obj;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		if (mapValueToItsKeys == null) {
			if (other.mapValueToItsKeys != null)
				return false;
		} else if (!mapValueToItsKeys.equals(other.mapValueToItsKeys))
			return false;
		return true;
	}
	
	/////////////////////// PROTECTED PART //////////////////////////

	protected <T> Set<T> newSet(){return new LinkedHashSet<T>();}
	protected  <TK,TV> Map<TK,TV> newMap(){return new LinkedHashMap<TK,TV>();}
	
	protected Map<K,Set<V>> map = this.<K,Set<V>>newMap();
	protected Map<V,Set<K>> mapValueToItsKeys = this.<V,Set<K>>newMap();


}
