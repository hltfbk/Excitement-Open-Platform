package eu.excitementproject.eop.distsim.storage.iterators;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import gnu.trove.iterator.TIntObjectIterator;

/**
 * Implements an immutable iterator for values of a given integer-object Trove iterator
 * 
 * <p>non thread-safe
 *  
 * @author Meni Adler
 * @since 16/08/2012
 *
 * @param <T> the type of the items to be retrieved 
 */
public class TroveBasedValuesIterator<T> extends ImmutableIterator<T>  {

	public TroveBasedValuesIterator(TIntObjectIterator<T> troveIterator) {
		this.troveIterator = troveIterator;
	}
	

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next() {
		troveIterator.advance();
		return troveIterator.value();
	}

	protected TIntObjectIterator<T> troveIterator;

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return troveIterator.hasNext();
	}
}
