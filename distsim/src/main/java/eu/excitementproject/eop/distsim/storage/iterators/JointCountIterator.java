package eu.excitementproject.eop.distsim.storage.iterators;

import java.util.NoSuchElementException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.storage.BasicMap;
import eu.excitementproject.eop.distsim.storage.DefaultElementFeatureJointCounts;
import eu.excitementproject.eop.distsim.storage.ElementFeatureJointCounts;
import eu.excitementproject.eop.distsim.util.Pair;

/**
* Implements an iterator for {@link ElementFeatureJointCounts} items, based on a given iterator of pairs of id and count map,
*  
 * @author Meni Adler
 * @since 28/12/2012
 *
 */
public class JointCountIterator extends ImmutableIterator<ElementFeatureJointCounts> {

	public JointCountIterator(ImmutableIterator<Pair<Integer, BasicMap<Integer, Double>>> iterator) {
		this.iterator = iterator;
		moveNext();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return next != null;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public ElementFeatureJointCounts next() {
		if (next == null)
			throw new NoSuchElementException();
		ElementFeatureJointCounts ret = next;
		moveNext();
		return ret;
		
	}
	
	/**
	 * Determine whether a given item should be filtered
	 * 
	 * @param count a count of some item
	 * @return true if the item should be filtered, i.e., the item does not stand the minimal required criteria 
	 */
	protected boolean filtered(Double count) {
		return false;
	}

	/**
	 * Move to the next element-feature-count point
	 */
	protected void moveNext() {
		if (iterator.hasNext()) {
			Pair<Integer, BasicMap<Integer, Double>> pair = iterator.next();
			next = new DefaultElementFeatureJointCounts(pair.getFirst(), pair.getSecond().iterator());
		} else 
			next = null;
	}

	
	protected ImmutableIterator<Pair<Integer, BasicMap<Integer, Double>>> iterator;
	protected ElementFeatureJointCounts next;
}
