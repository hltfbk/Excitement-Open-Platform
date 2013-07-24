package eu.excitementproject.eop.transformations.datastructures;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.DummySet;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;


/**
 * A value set map that is constructed by wrapping an existing {@link BidirectionalMap}.
 * Once a modification to this map is done - the values from the {@linkplain BidirectionalMap}
 * are copied to a new, real, {@link SimpleValueSetMap}.
 * But as long as no change was done on this map, all the key-value pairs are stored in the
 * {@linkplain BidirectionalMap}. 
 * 
 * @author Asher Stern
 * @since Jan 30, 2011
 *
 * @param <K>
 * @param <V>
 */
public class FromBidirectionalMapValueSetMap<K, V> implements ValueSetMap<K, V>
{
	private static final long serialVersionUID = 323298845591836787L;
	
	public FromBidirectionalMapValueSetMap(BidirectionalMap<K, V> bidirectionalMap)
	{
		this.bidirectionalMap = bidirectionalMap;
		this.valueSetMap = null;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void put(K key, V value)
	{
		if (this.bidirectionalMap!=null)
		{
			flipToValueSetMap();
		}
		this.valueSetMap.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#remove(java.lang.Object)
	 */
	@Override
	public void remove(K key)
	{
		if (this.bidirectionalMap!=null)
		{
			flipToValueSetMap();
		}
		this.valueSetMap.remove(key);
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#removeValue(java.lang.Object)
	 */
	@Override
	public void removeValue(V value)
	{
		if (this.bidirectionalMap!=null)
		{
			flipToValueSetMap();
		}
		this.valueSetMap.removeValue(value);
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#clear()
	 */
	@Override
	public void clear()
	{
		if (this.bidirectionalMap!=null)
		{
			this.bidirectionalMap = null;
			this.valueSetMap = new SimpleValueSetMap<K, V>();
		}
		else
		{
			this.valueSetMap.clear();
		}

		
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#get(java.lang.Object)
	 */
	@Override
	public ImmutableSet<V> get(K key)
	{
		ImmutableSet<V> ret = null;
		if (this.bidirectionalMap!=null)
		{
			Set<V> set = null;
			if (this.bidirectionalMap.leftContains(key))
			{
				V value = this.bidirectionalMap.leftGet(key);
				set = new SingleItemSet<V>(value);
			}
			else
			{
				set = new DummySet<V>();
			}
			ret = new ImmutableSetWrapper<V>(set);
		}
		else
		{
			ret = this.valueSetMap.get(key);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#keySet()
	 */
	@Override
	public ImmutableSet<K> keySet()
	{
		ImmutableSet<K> ret = null;
		if (this.bidirectionalMap!=null)
		{
			ret = this.bidirectionalMap.leftSet();
		}
		else
		{
			ret = this.valueSetMap.keySet();
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#getKeysOf(java.lang.Object)
	 */
	@Override
	public ImmutableSet<K> getKeysOf(V value)
	{
		ImmutableSet<K> ret = null;
		if (this.bidirectionalMap!=null)
		{
			LinkedHashSet<K> set = new LinkedHashSet<K>();
			if (this.bidirectionalMap.rightContains(value))
			{
				K key = this.bidirectionalMap.rightGet(value);
				set.add(key);
			}
			ret = new ImmutableSetWrapper<K>(set);
		}
		else
		{
			ret = this.valueSetMap.getKeysOf(value);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(K key)
	{
		boolean ret = false;
		if (this.bidirectionalMap!=null)
		{
			ret = this.bidirectionalMap.leftContains(key);
		}
		else
		{
			ret = this.valueSetMap.containsKey(key);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(V value)
	{
		boolean ret = false;
		if (this.bidirectionalMap!=null)
		{
			ret = this.bidirectionalMap.rightContains(value);
		}
		else
		{
			ret = this.valueSetMap.containsValue(value);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		boolean ret = false;
		if (this.bidirectionalMap!=null)
		{
			ret = this.bidirectionalMap.isEmpty();
		}
		else
		{
			ret = this.valueSetMap.isEmpty();
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#size()
	 */
	@Override
	public int size()
	{
		int ret = 0;
		if (this.bidirectionalMap!=null)
		{
			ret = this.bidirectionalMap.size();
		}
		else
		{
			ret = this.valueSetMap.size();
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#deepCopy()
	 */
	@Override
	public ValueSetMap<K, V> deepCopy()
	{
		if (this.bidirectionalMap!=null)
		{
			flipToValueSetMap();
		}
		return valueSetMap.deepCopy();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ValueSetMap#values()
	 */
	@Override
	public ImmutableSet<V> values()
	{
		ImmutableSet<V> ret = null;
		if (this.bidirectionalMap!=null)
		{
			ret = this.bidirectionalMap.rightSet();
		}
		else
		{
			ret = this.valueSetMap.values();
		}
		return ret;
	}

	
	
	protected void flipToValueSetMap()
	{
		this.valueSetMap = new SimpleValueSetMap<K, V>();
		for (K key : this.bidirectionalMap.leftSet())
		{
			V value = this.bidirectionalMap.leftGet(key);
			this.valueSetMap.put(key, value);
		}
		this.bidirectionalMap = null;
	}
	
	
	
	protected BidirectionalMap<K, V> bidirectionalMap = null;
	protected ValueSetMap<K, V> valueSetMap = null;


}
