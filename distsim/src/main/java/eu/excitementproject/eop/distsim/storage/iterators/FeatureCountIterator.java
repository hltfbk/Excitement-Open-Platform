/**
 * 
 */
package eu.excitementproject.eop.distsim.storage.iterators;

import java.util.NoSuchElementException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.storage.DefaultFeatureCount;
import eu.excitementproject.eop.distsim.storage.FeatureCount;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * Implements an iterator for {@link FeatureCount} items, based on a given iterator of pairs of id and count 
 *  
 * @author Meni Adler
 * @since 04/09/2012
 *
 */
public class FeatureCountIterator extends ImmutableIterator<FeatureCount> {

	public FeatureCountIterator(ImmutableIterator<Pair<Integer, Double>> iterator) {
		this.iterator = iterator;
	}
			
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public FeatureCount next() {
		Pair<Integer, Double> pair = iterator.next();
		if (pair == null)
			throw new NoSuchElementException();
		return new DefaultFeatureCount(pair.getFirst(),pair.getSecond());
	}

	protected ImmutableIterator<Pair<Integer, Double>> iterator;
}
