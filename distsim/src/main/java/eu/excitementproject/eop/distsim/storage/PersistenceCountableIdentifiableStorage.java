/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.io.Serializable;

import java.util.Iterator;

import eu.excitementproject.eop.distsim.items.Countable;
import eu.excitementproject.eop.distsim.items.Externalizable;
import eu.excitementproject.eop.distsim.items.Identifiable;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * Implements the saving and loading operations of the countable-identifiable storage
 *
 * @author Meni Adler
 * @since 22/07/2012
 * 
 *
 */
public abstract class PersistenceCountableIdentifiableStorage<T extends Externalizable & Countable & Identifiable> implements CountableIdentifiableStorage<T> {
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.Persistence#saveState(org.excitement.distsim.storage.PersistenceDevice)
	 */
	@Override
	public void saveState(PersistenceDevice... devices) throws SavingStateException {
		if (devices.length != 1)
			throw new SavingStateException(devices.length + " persistence devices was providied for saving, but only one is expected");
		Iterator<T> it = iterator();
		while (it.hasNext()) {
			T item = it.next();
			try {
				devices[0].write(item.getID(),item);
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
					add(pair.getFirst(), (T)pair.getSecond());
				} catch (ClassCastException e) {
					throw new LoadingStateException(e);
				}
				//tmp
				/*catch (UndefinedKeyException e2) {
					System.out.println(e2.toString());
				}*/
				
		} catch (Exception e) {
			throw new LoadingStateException(e);
		}
	}	

}
