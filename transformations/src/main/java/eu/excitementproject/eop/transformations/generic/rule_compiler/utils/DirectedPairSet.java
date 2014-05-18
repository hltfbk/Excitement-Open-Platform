/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.utils;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.Pair;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;


/**
 * Much like {@link PairSet}, except that here the pairs are {@link DirectedPair}s.
 * @author amnon
 *
 */
@SuppressWarnings("serial")
public class DirectedPairSet<K> extends PairSet<K> implements Serializable {

	public void put(DirectedPair<K> pair)
	{
		super.put(pair);
	}
	
	public boolean containsPair(DirectedPair<K> pair)
	{
		return super.containsPair(pair);
	}
	
	public ImmutableSet<DirectedPair<K>> getDirectedPairsContaining(K key) 
	{	
		Set<DirectedPair<K>> ret = new LinkedHashSet<DirectedPair<K>>();
		for (Pair<K> pair : super.mapKeyToPairContainingIt.get(key))
			ret.add((DirectedPair<K>) pair);
		
		
		return new ImmutableSetWrapper<DirectedPair<K>>(ret);
	}
	
	public ImmutableSet<DirectedPair<K>> getAll()
	{
		Set<DirectedPair<K>> ret = new LinkedHashSet<DirectedPair<K>>();
		for (Pair<K> pair : super.pairsSet)
			ret.add((DirectedPair<K>) pair);
		
		return new ImmutableSetWrapper<DirectedPair<K>>(ret);				
	}
}
