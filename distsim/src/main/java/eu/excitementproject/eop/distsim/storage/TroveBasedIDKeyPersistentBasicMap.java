/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.io.Serializable;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
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
public class TroveBasedIDKeyPersistentBasicMap<V extends Serializable> extends DefaultIDKeyPersistentBasicMap<V> implements IDKeyPersistentBasicMap<V> {

	
	private static final long serialVersionUID = 1L;
	
	public TroveBasedIDKeyPersistentBasicMap(ConfigurationParams params) {
		this();
	}
	
	public TroveBasedIDKeyPersistentBasicMap() {
		map = new TIntObjectHashMap<V>();
	}

	public TroveBasedIDKeyPersistentBasicMap(ConfigurationParams params, PersistenceDevice persistenceDevice) throws LoadingStateException {
		this(persistenceDevice);
	}

	public TroveBasedIDKeyPersistentBasicMap(PersistenceDevice persistenceDevice) throws LoadingStateException {
		map = new TIntObjectHashMap<V>();
		loadState(persistenceDevice);
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
	public synchronized ImmutableIterator<Pair<Integer,V>> iterator() {
		return new TroveBasedIntObjImmutableIterator<V>(map.iterator());
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.BasicMap#size()
	 */
	@Override
	public synchronized int size() {
		return map.size();
	}

	TIntObjectMap<V> map;

}
