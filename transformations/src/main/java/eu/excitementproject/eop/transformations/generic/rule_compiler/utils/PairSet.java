/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.utils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.V;
import eu.excitementproject.eop.common.datastructures.Pair;


/**
 * 
 * 	/**
	 * Much like <code>java.util.Set</code>. The difference is followed by the
	 * key type which is a {@linkplain Pair}.
	 * <P>
	 * <B>NOT THREAD SAFE!</B>
	 * <P>
	 * Since the key type is a {@linkplain Pair} of type <code>K</code>, one
	 * type <code>K</code> object may exist in many entries in the Set, each
	 * time with another partner, forming together one unique key.<BR>
	 * Any {@link #put(Pair, V)} operation, in addition to adding a new mapping
	 * of the key-value to the Set, also keeps tracking on the single elements
	 * that form the given pair (i.e. the key). Later, calling
	 * to {@link #getPairContaining(K)} will return all of the {@linkplain Pair}s
	 * of type <code>K</code> which are keys in the set.
	 * <P>
	 * Example usage: A Co-reference information may be represented as a collection
	 * of pairs (e.g. pair of word in the text), and each pair means that there is
	 * a Co-reference relation between those two words, with a specified confidence,
	 * that is stored as the value of that pair in the map.
	 * 
	 * <P>
	 * <B>NOT THREAD SAFE!</B>
	 * 
	 * @author Asher Stern and Amnon Lotan
	 *
	 */
@SuppressWarnings("serial")
public class PairSet<K> implements Serializable, Iterable<Pair<K>>
{
	//////////////////////// PUBLIC PART //////////////////////////
	
	public PairSet()
	{
		
	}
	
	/**
	 * Identical to <code>java.util.set.put()</code> method.
	 * @param pair
	 * @param value
	 */
	public void put(Pair<K> pair)
	{
		pairsSet.add(pair);
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
	 * Removes all pairs that contain the given single value
	 * @param key
	 * @return
	 */
	public boolean removePairsContaining(K key)
	{
		Set<Pair<K>> pairs = mapKeyToPairContainingIt.remove(key);
		boolean ret = pairsSet.removeAll(pairs);
		
		// search and remove all the pairs that contain this key and are mapped by other keys. 
		Set<K> keysToRemove = new LinkedHashSet<K>();
		for (Entry<K, Set<Pair<K>>> entry : mapKeyToPairContainingIt.entrySet())
		{
			Set<Pair<K>> pairs2 = entry.getValue();
			Set<Pair<K>> pairsContainingTheKey = new HashSet<Pair<K>>();
			for (Pair<K> pair : pairs2)
				if (pair.contains(key))
					pairsContainingTheKey.add(pair);
			pairs2.removeAll(pairsContainingTheKey);
			// if the key to this pair is mapped to no other pairs, remove this key too
			if (pairs2.isEmpty())
				keysToRemove.add(entry.getKey());
		}
		for (K keyToRemove : keysToRemove)
			mapKeyToPairContainingIt.remove(keyToRemove);
		
		return ret;
	}
	
	
	/**
	 * Identical to <code>java.util.set.containsKey()</code> method.
	 * @param pair
	 * @return
	 */
	public boolean containsPair(Pair<K> pair)
	{
		return pairsSet.contains(pair);
	}
	
	/**
	 * Returns a set of all pairs that are:
	 * <UL>
	 * <LI>exist as keys in the set</LI>
	 * <LI>contain the given "<code>key</code>"</LI>
	 * </UL>
	 * May return null
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
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Pair<K>> iterator() {
		return pairsSet.iterator();
	} 
	
	//////////////////// PROTECTED & PRIVATE PART ///////////////////////
	
	protected Map<K,Set<Pair<K>>> mapKeyToPairContainingIt = new HashMap<K,Set<Pair<K>>>();
	protected Set<Pair<K>> pairsSet = new LinkedHashSet<Pair<K>>();

}
