package eu.excitementproject.eop.common.datastructures;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


/**
 * Much like <code>java.util.Map</code>. The difference is followed by the
 * key type which is a {@linkplain Pair}.
 * <P>
 * <B>NOT THREAD SAFE!</B>
 * <P>
 * Since the key type is a {@linkplain Pair} of type <code>K</code>, one
 * type <code>K</code> object may exist in many entries in the map, each
 * time with another partner, forming together one unique key.<BR>
 * Any {@link #put(Pair, V)} operation, in addition to adding a new mapping
 * of the key-value to the map, also keeps tracking on the single elements
 * that form the given pair (i.e. the key). Later, calling
 * to {@link #getPairContaining(K)} will return all of the {@linkplain Pair}s
 * of type <code>K</code> which are keys in the map.
 * <P>
 * Example usage: A Co-reference information may be represented as a collection
 * of pairs (e.g. pair of word in the text), and each pair means that there is
 * a Co-reference relation between those two words, with a specified confidence,
 * that is stored as the value of that pair in the map.
 * 
 * <P>
 * <B>NOT THREAD SAFE!</B>
 * 
 * @author Asher Stern
 *
 */
public class PairMap<K,V> implements Serializable
{
	private static final long serialVersionUID = -7080303890118623500L;

	//////////////////////// PUBLIC PART //////////////////////////
	
	public PairMap()
	{
		
	}
	
	/**
	 * Identical to <code>java.util.Map.put()</code> method.
	 * @param pair
	 * @param value
	 */
	public void put(Pair<K> pair,V value)
	{
		mapPairToValue.put(pair, value);
		for (K single : pair.toSet())
		{
			if (!mapKeyToPairContainingIt.containsKey(single))
			{
				mapKeyToPairContainingIt.put(single, new LinkedHashSet<Pair<K>>());
			}
			Set<Pair<K>> itsSetOfPairsContainingIt = mapKeyToPairContainingIt.get(single);
			itsSetOfPairsContainingIt.add(pair);
		}
	}
	
	/**
	 * Identical to <code>java.util.Map.containsKey()</code> method.
	 * @param pair
	 * @return
	 */
	public boolean containsPair(Pair<K> pair)
	{
		return mapPairToValue.containsKey(pair);
	}

	/**
	 * Identical to <code>java.util.Map.get()</code> method.
	 * @param pair
	 * @return
	 */
	public V getValueOf(Pair<K> pair)
	{
		return mapPairToValue.get(pair);
	}
	
	/**
	 * Returns a set of all pairs that are:
	 * <UL>
	 * <LI>exist as keys in the map</LI>
	 * <LI>contain the given "<code>key</code>"</LI>
	 * </UL>
	 * @param key
	 * @return
	 */
	public eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet<Pair<K>> getPairContaining(K key)
	{
		eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet<Pair<K>> ret = null;
		
		if (mapKeyToPairContainingIt.containsKey(key))
		{
			if (null!=mapKeyToPairContainingIt.get(key))
				ret =  new eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper<Pair<K>>(mapKeyToPairContainingIt.get(key));
		}
		
		return ret;
	}
	
	public void removePair(Pair<K> pair)
	{
		for (K oneKey : pair.toSet())
		{
			if (mapKeyToPairContainingIt.containsKey(oneKey))
			{
				Set<Pair<K>> itsSet = mapKeyToPairContainingIt.get(oneKey);
				if (itsSet!=null)
				{
					if (itsSet.contains(pair))
						itsSet.remove(pair);
				}
			}
		}
		
		mapPairToValue.remove(pair);
	}
	
	public void clear()
	{
		mapKeyToPairContainingIt.clear();
		mapPairToValue.clear();
	}
	
	//////////////////// PROTECTED & PRIVATE PART ///////////////////////
	
	protected Map<K,Set<Pair<K>>> mapKeyToPairContainingIt = new HashMap<K,Set<Pair<K>>>();
	protected Map<Pair<K>,V> mapPairToValue = new HashMap<Pair<K>,V>(); 

}
