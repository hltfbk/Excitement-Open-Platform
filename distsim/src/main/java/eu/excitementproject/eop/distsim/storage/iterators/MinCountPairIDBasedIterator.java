/**
 * 
 */
package eu.excitementproject.eop.distsim.storage.iterators;

import java.util.Iterator;

import eu.excitementproject.eop.distsim.items.Countable;
import eu.excitementproject.eop.distsim.items.Externalizable;
import eu.excitementproject.eop.distsim.items.Identifiable;
import eu.excitementproject.eop.distsim.items.InvalidCountException;
import eu.excitementproject.eop.distsim.storage.CountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * An extension of {@link PairIDBasedIterator} which filters items with low frequencies
 *
 * @author Meni Adler
 * @since 15/08/2012
 *
 * @param <E> the type of the item retrieved by the iterator
 */
public class MinCountPairIDBasedIterator<E extends Externalizable & Countable & Identifiable> extends PairIDBasedIterator<E> {

	public MinCountPairIDBasedIterator(Iterator<? extends Pair<Integer, ?>> iterator, CountableIdentifiableStorage<E> items, long minCount) {
		super(iterator, items);
		this.minCount = minCount;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.iterators.IDBasedIterator#filtered(org.excitement.distsim.items.Externalizable)
	 */
	@Override
	protected boolean filtered(E e) {
		try {
			return e.getCount() < minCount;
		} catch (InvalidCountException e1) {
			return true;
		}
	}
	
	protected long minCount;

}
