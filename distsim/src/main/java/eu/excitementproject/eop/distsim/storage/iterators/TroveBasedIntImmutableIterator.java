package eu.excitementproject.eop.distsim.storage.iterators;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import gnu.trove.iterator.TIntIterator;


/**
 * Implements an immutable iterator for integer set, based on Trove's TIntIterator
 * 
 * <p>non thread-safe
 * 
 * @author Meni Adler
 * @since 15/08/2012
 *
 *  
 */
public class TroveBasedIntImmutableIterator extends ImmutableIterator<Integer> {

	public TroveBasedIntImmutableIterator(TIntIterator iterator) {
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
	public Integer next() {
		return iterator.next();
	}
	
	
	TIntIterator iterator;
}
