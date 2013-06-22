package eu.excitementproject.eop.distsim.storage.iterators;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.util.Pair;
import gnu.trove.iterator.TIntObjectIterator;


/**
 * Implements an immutable iterator for integer-object pairs, based on Trove's TIntObjectIterator
 * 
 * <p>non thread-safe
 * 
 * @author Meni Adler
 * @since 15/08/2012
 *
 * @param <T> The type of the 'value' (second pair object)
 *  
 */
public class TroveBasedIntObjImmutableIterator<T> extends ImmutableIterator<Pair<Integer,T>> {

	public TroveBasedIntObjImmutableIterator(TIntObjectIterator<T> iterator) {
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
	public Pair<Integer,T> next() {
		iterator.advance();
		return new Pair<Integer,T>(iterator.key(),iterator.value());
	}
	
	
	TIntObjectIterator<T> iterator;
}
