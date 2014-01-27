package eu.excitementproject.eop.distsim.storage.iterators;

import java.util.Iterator;

import eu.excitementproject.eop.distsim.items.Countable;
import eu.excitementproject.eop.distsim.items.Externalizable;
import eu.excitementproject.eop.distsim.items.Identifiable;
import eu.excitementproject.eop.distsim.storage.CountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * A specific extension of the {@link  IDBasedIterator} class, which is based on a given iterator of pairs, where the first item in each pair is the retrieved id
 * 
 * @author Meni Adler
 * @since 13/08/2012
 *
 * @param <E> the type of the item retrieved by the iterator
 * 
 */
public class PairIDBasedIterator<E extends Externalizable & Countable & Identifiable> extends IDBasedIterator<E>  {

	public PairIDBasedIterator(Iterator<? extends Pair<Integer,?>> iterator, CountableIdentifiableStorage<E> items) {
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
			int id = iterator.next().getFirst();
			next = items.getData(id);
			while (filtered(next)) {
				id = iterator.next().getFirst();
				next = items.getData(id);
			}
		} catch (Exception e) {
			next = null;
		}
	}
	
	protected Iterator<? extends Pair<Integer,?>> iterator;
}
