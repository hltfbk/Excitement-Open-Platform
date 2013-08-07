/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.io.Serializable;
import java.util.Iterator;

import eu.excitementproject.eop.distsim.util.Pair;

/**
 * Implements the saving and loading operations of the countable-identifiable storage
 *
 * @author Meni Adler
 * @since 22/07/2012
 * 
 *
 */
public abstract class DefaultIDKeyPersistentBasicMap<V extends Serializable> implements IDKeyPersistentBasicMap<V> {
	

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.Persistence#saveState(org.excitement.distsim.storage.PersistenceDevice)
	 */
	@Override
	public void saveState(PersistenceDevice... devices) throws SavingStateException {
		if (devices.length != 1)
			throw new SavingStateException(devices.length + " persistence devices was providied for saving, but only one is expected");
		Iterator<Pair<Integer,V>> it = iterator();
		while (it.hasNext()) {
			Pair<Integer,V> item = it.next();
			try {
				devices[0].write(item.getFirst(),item.getSecond());
			} catch (Exception e) {
				throw new SavingStateException(e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.Persistence#loadState()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void loadState(PersistenceDevice... devices) throws LoadingStateException {
		if (devices.length != 1)
			throw new LoadingStateException(devices.length + " persistence devices was providied for loading, but only one is expected");
		try {			
			Pair<Integer, Serializable> pair = null;
			while ((pair = devices[0].read()) != null)
				try {
					put(pair.getFirst(), (V)pair.getSecond());
				} catch (ClassCastException e) {
					throw new LoadingStateException(e);
				}
		} catch (Exception e) {
			throw new LoadingStateException(e);
		}
	}	
	
}
