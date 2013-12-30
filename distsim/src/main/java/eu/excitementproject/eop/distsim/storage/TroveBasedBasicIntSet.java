/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.storage.iterators.TroveBasedIntImmutableIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/**
 * An implementation of the BasicMap interface for integer keys, based on the Trove map library
 * 
 * 'add' operations can be applied in parallel, but add/iterator not  
 * 
 * @author Meni Adler
 * @since 12/08/2012
 *
 */
public class TroveBasedBasicIntSet implements BasicSet<Integer> {

	
	private static final long serialVersionUID = 1L;
	
	public TroveBasedBasicIntSet() {
		set = new TIntHashSet();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ThinMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public synchronized void add(Integer item) {
		set.add(item);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.BasicMap#keys()
	 */
	@Override
	public ImmutableIterator<Integer> iterator() {
		return new TroveBasedIntImmutableIterator(set.iterator());
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.BasicSet#size()
	 */
	@Override
	public int size() {
		return set.size();
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.storage.BasicSet#contains(java.io.Serializable)
	 */
	@Override
	public boolean contains(Integer i) {
		return set.contains(i);
	}
	
	TIntSet set;
}
