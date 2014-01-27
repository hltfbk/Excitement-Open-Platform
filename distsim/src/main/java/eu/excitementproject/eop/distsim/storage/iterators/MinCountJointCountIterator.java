package eu.excitementproject.eop.distsim.storage.iterators;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.storage.BasicMap;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * Filters the items of a given iterator of pairs of id and joint count map, according to a their counts, where minimal count is required
 * 
 * @author Meni Adler
 * @since 28/12/2012
 *
 */
public class MinCountJointCountIterator extends JointCountIterator {

	public MinCountJointCountIterator(ImmutableIterator<Pair<Integer, BasicMap<Integer, Double>>> iterator, long minCount) {
		super(iterator);
		this.minCount = minCount;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.JointCountIterator#filtered(java.lang.Double)
	 */
	@Override
	protected boolean filtered(Double count) {
		return count < minCount;
	}

	protected long minCount;	
}
