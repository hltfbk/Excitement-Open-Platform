/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;


import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.storage.iterators.TroveBasedIntObjImmutableIterator;
import eu.excitementproject.eop.distsim.util.Pair;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * An implementation of the BasicMap interface for integer keys, based on the Trove map library
 *
 * The thread is safe for parallel put/get operation, but not safe for the parallel put/iterator operation
 * 
 * @author Meni Adler
 * @since 12/08/2012
 *
 */
public class TroveBasedIDKeyBasicMap<V>  implements BasicMap<Integer,V> {

	
	private static final long serialVersionUID = 1L;
	
	public TroveBasedIDKeyBasicMap() {
		map = new TIntObjectHashMap<V>();
	}

	public TroveBasedIDKeyBasicMap(TIntObjectMap<V> map) {
		this.map = map;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ThinMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public synchronized void put(Integer key, V value) {
		map.put(key,value);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ThinMap#get(java.lang.Object)
	 */
	@Override
	public synchronized  V get(Integer key) {
		return map.get(key);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.BasicMap#keys()
	 */
	@Override
	public ImmutableIterator<Pair<Integer,V>> iterator() {
		return new TroveBasedIntObjImmutableIterator<V>(map.iterator());
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.BasicMap#size()
	 */
	@Override
	public int size() {
		return map.size();
	}

	TIntObjectMap<V> map;

}
