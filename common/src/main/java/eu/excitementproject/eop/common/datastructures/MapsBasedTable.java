package eu.excitementproject.eop.common.datastructures;


import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;



/**
 * Implementation of {@link Table} based on <code>java.util.Map</code>.
 * <P>
 * This implementation is efficient from space point of view.
 * The time complexity is O(1) for {@link #get(Object, Object)}
 * and {@link #put(Object, Object, Object)}.
 * <P>
 * As for {@link #allCols()}, {@link #rowsOfCol(Object)} and {@link #colsOfRow(Object)} methods,
 * this implementation is inefficient.
 * @author Asher Stern
 * @since Aug 12, 2010
 *
 * @param <K>
 * @param <V>
 */
public class MapsBasedTable<K,V> implements Table<K,V>
{
	private static final long serialVersionUID = 9000741758377424356L;


	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Table#put(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public void put (K rowKey, K colKey, V value)
	{
		if (!map.containsKey(rowKey))
		{
			map.put(rowKey, new LinkedHashMap<K, V>());
		}
		Map<K,V> colMap = map.get(rowKey);
		colMap.put(colKey, value);
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Table#get(java.lang.Object, java.lang.Object)
	 */
	public V get(K rowKey, K colKey)
	{
		V ret = null;
		if (map.containsKey(rowKey)) { if (map.get(rowKey)!=null)
		{
			Map<K,V> rowMap = map.get(rowKey);
			if (rowMap.containsKey(colKey))
				ret = rowMap.get(colKey);
		}}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Table#allRows()
	 */
	public ImmutableSet<K> allRows()
	{
		return new ImmutableSetWrapper<K>(map.keySet());
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Table#allCols()
	 */
	public ImmutableSet<K> allCols()
	{
		Set<K> setAllCols = new LinkedHashSet<K>();
		for (K rowKey : map.keySet())
		{
			Map<K,V> rowMap = map.get(rowKey);
			if (rowMap!=null)
			{
				for (K colKey : rowMap.keySet())
				{
					setAllCols.add(colKey);
				}
			}
		}
		return new ImmutableSetWrapper<K>(setAllCols);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Table#colsOfRow(java.lang.Object)
	 */
	public ImmutableSet<K> colsOfRow(K rowKey)
	{
		ImmutableSet<K> ret = null;
		if (map.containsKey(rowKey)) { if (map.get(rowKey)!=null)
		{
			ret = new ImmutableSetWrapper<K>(map.get(rowKey).keySet());
		}}
		return ret;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Table#rowsOfCol(java.lang.Object)
	 */
	public ImmutableSet<K> rowsOfCol(K colKey)
	{
		ImmutableSet<K> ret = null;
		Set<K> rows = new LinkedHashSet<K>();
		for (K rowKey : map.keySet())
		{
			if (map.get(rowKey).keySet().contains(colKey))
				rows.add(rowKey);
		}
		if (rows.size()>0)
		{
			ret = new ImmutableSetWrapper<K>(rows);
		}
		return ret;
	}
	
	
	protected Map<K, Map<K,V>> map = new LinkedHashMap<K, Map<K,V>>();
}
