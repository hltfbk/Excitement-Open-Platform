package eu.excitementproject.eop.distsim.storage.iterators;

import java.util.Iterator;

import eu.excitementproject.eop.distsim.items.Countable;
import eu.excitementproject.eop.distsim.items.Externalizable;
import eu.excitementproject.eop.distsim.items.Identifiable;
import eu.excitementproject.eop.distsim.storage.CountableIdentifiableStorage;

/**
 * A specific extension of the {@link  IDBasedIterator} class, which is based on a given iterator id set iterator
 * 
 * @author Meni Adler
 * @since 13/08/2012
 *
 * @param <E> the type of the item retrieved by the iterator
 * 
 */
public class SingleIDBasedIterator<E extends Externalizable & Countable & Identifiable> extends IDBasedIterator<E>  {

	public SingleIDBasedIterator(Iterator<Integer> iterator, CountableIdentifiableStorage<E> items) {
		super(items);
		this.iterator = iterator;
		moveNext();
	}

	/**
	 * Move to the next item, according to the filter condition
	 */
	@Override
	protected void moveNext() {
		try {
			next = items.getData(iterator.next());
			while (filtered(next)) {
				next = items.getData(iterator.next());
			}
		} catch (Exception e) {
			next = null;
		}
	}
	
	protected Iterator<Integer> iterator;
}
