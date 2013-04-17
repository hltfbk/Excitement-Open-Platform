package eu.excitementproject.eop.distsim.storage.iterators;

import java.util.NoSuchElementException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.items.Countable;
import eu.excitementproject.eop.distsim.items.Externalizable;
import eu.excitementproject.eop.distsim.items.Identifiable;
import eu.excitementproject.eop.distsim.storage.CountableIdentifiableStorage;


/**
 * A general iterator which iterates a given set of Ids and retrieves the elements which they identify
 * 
 * @author Meni Adler
 * @since 13/08/2012
 *
 * @param <E> the type of the item retrieved by the iterator
 * 
 */
public abstract class IDBasedIterator<E extends Externalizable & Countable & Identifiable> extends ImmutableIterator<E>  {

	protected IDBasedIterator(CountableIdentifiableStorage<E> items) {
		this.items = items;
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
	protected abstract void moveNext();
	
	/**
	 * Determine whether a given element should be filtered
	 * 
	 * @param e an element 
	 * @return true if the element should be filtered, i.e., the element does not stand the minimal required criteria 
	 */
	protected boolean filtered(E e) {
		return false;
	}
	
	
	protected CountableIdentifiableStorage<E> items;
	protected E next;
}
