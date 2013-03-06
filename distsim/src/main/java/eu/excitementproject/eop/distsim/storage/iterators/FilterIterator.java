package eu.excitementproject.eop.distsim.storage.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;

/**
 * The FilterIterator filters the items of a given iterator, according to some policy, defined by the filtered(item) method 
 * 
 * <P>non thread-safe
 * 
 * @author Meni Adler
 * @since 12/08/2012
 *
 * @param <E> the type of the retrieved elements
 * 
 * 
 */
public abstract class FilterIterator<E> extends ImmutableIterator<E> {

	FilterIterator(Iterator<E> iterator) {		
		this.iterator = iterator;
		moveNext();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return (next != null);
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public E next() {
		if (next == null)
			throw new NoSuchElementException();

		E ret = next;
		moveNext();
		return ret;
	}

	/**
	 * Move to the next item, according to the filter condition
	 */
	protected void moveNext() {
		try {
			next = iterator.next();
			while (filtered(next))
				next = iterator.next();
		} catch (Exception e) {
			next = null;
		}
	}
	
	/**
	 * Determine whether a given element should be filtered
	 * 
	 * @param e an element 
	 * @return true if the element should be filtered, i.e., the element does not stand the minimal required criteria 
	 */
	protected abstract boolean filtered(E e);
		
	Iterator<E> iterator;
	E next;
}
