package eu.excitementproject.eop.distsim.storage.iterators;

import java.util.Iterator;

import eu.excitementproject.eop.distsim.items.Countable;
import eu.excitementproject.eop.distsim.items.InvalidCountException;

/**
 * Filters the items of a given iterator according to a their counts, where minimal count is required
 * 
 * <P>not a  thread-safe
 * 
 * @author Meni Adler
 * @since 13/08/2012
 *
 * @param <E> the type of the retrieved items
 */
public class MinCountFilterIterator<E extends Countable> extends FilterIterator<E> {

	public MinCountFilterIterator(Iterator<E> iterator, long minCount) {
		super(iterator);
		this.minCount = minCount;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.iterators.FilterIterator#filtered(java.lang.Object)
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
