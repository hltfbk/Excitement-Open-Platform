package eu.excitementproject.eop.distsim.storage;

import java.util.Iterator;
import java.util.NoSuchElementException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.DefaultCooccurrence;
import eu.excitementproject.eop.distsim.items.DefaultRelation;
import eu.excitementproject.eop.distsim.items.IDBasedCooccurrence;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.util.SerializationException;

/**
 * General implementation of the CooccurrenceStorageView interface
 * 
 * @author Meni Adler
 * @since 19/07/2012
 *
 * <p>non thread-safe
 * 
 */
public class DefaultCooccurrenceStorage<R> implements CooccurrenceStorage<R> {
	
	public DefaultCooccurrenceStorage() {
		
	}
	
	public DefaultCooccurrenceStorage(CountableIdentifiableStorage<TextUnit> textUnitStorage, 
									  CountableIdentifiableStorage<IDBasedCooccurrence<R>> cooccurrenceStorage) {
		this.textUnitStorage = textUnitStorage;
		this.cooccurrenceStorage = cooccurrenceStorage;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.view.CooccurrenceStorageView#getCooccurrenceInstance(int)
	 */
	@Override
	public Cooccurrence<R> getCooccurrenceInstance(int cooccurrenceId) throws ItemNotFoundException {
		IDBasedCooccurrence<R> idBasedCooccurrence;
		try {
			idBasedCooccurrence = cooccurrenceStorage.getData(cooccurrenceId);
		} catch (SerializationException e) {
			throw new ItemNotFoundException(e);
		}
		return getCoOccurrence(idBasedCooccurrence);

	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.view.CooccurrenceStorageView#getCooccurrenceInstances()
	 */
	@Override
	public ImmutableIterator<Cooccurrence<R>> getCooccurrenceInstances() {
		return new CoOccurenceIterator();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.view.CooccurrenceStorageView#getCooccurrenceInstances(int)
	 */
	@Override
	public ImmutableIterator<Cooccurrence<R>> getCooccurrenceInstances(int minCount) {
		return new CoOccurenceIterator(minCount);
	}
	
	protected Cooccurrence<R> getCoOccurrence(IDBasedCooccurrence<R> idBasedCooccurrence)  throws ItemNotFoundException {
		try {
			return new DefaultCooccurrence<R>(
										   textUnitStorage.getData(idBasedCooccurrence.getTextUnitID1()),
					                       textUnitStorage.getData(idBasedCooccurrence.getTextUnitID2()),
					                       new DefaultRelation<R>(idBasedCooccurrence.getRelation()),
					                       idBasedCooccurrence.getID(),idBasedCooccurrence.getCount());
		} catch (Exception e) {
			throw new ItemNotFoundException(e);
		}		
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.Persistence#saveState(org.excitement.distsim.storage.PersistenceDevice)
	 * 
	 * Assumption: two persistence devices are provided: the first for text units, and the second for co-occurrences
	 */
	@Override
	public void saveState(PersistenceDevice... devices) throws SavingStateException {
		if (devices.length != 2)
			throw new SavingStateException(devices.length + " persistence devices was providied for saving, where two are expected");
		textUnitStorage.saveState(devices[0]);
		cooccurrenceStorage.saveState(devices[1]);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.Persistence#loadState(org.excitement.distsim.storage.PersistenceDevice)
	 * 
	 * Assumption: two persistence devices are provided: the first of text units, and the second of co-occurrences
	 */
	@Override
	public void loadState(PersistenceDevice... devices) throws LoadingStateException {
		if (devices.length != 2)
			throw new LoadingStateException(devices.length + " persistence devices was providied for loading, where two are expected");
		textUnitStorage.loadState(devices[0]);
		cooccurrenceStorage.loadState(devices[1]);
	}

	protected CountableIdentifiableStorage<TextUnit> textUnitStorage;
	protected CountableIdentifiableStorage<IDBasedCooccurrence<R>> cooccurrenceStorage;

	/**
	 * Implements an iterator for Cooccurrences, based on IDBasedCoocuurence iterator 
	 * and a CountableIdentifiableStorage (for the resolution of the text unit ids)
	 *
	 */
	protected class CoOccurenceIterator extends ImmutableIterator<Cooccurrence<R>> {

		CoOccurenceIterator() {
			this(0);
		}

		CoOccurenceIterator(long minCount) {
			this.iterator = cooccurrenceStorage.iterator();
			this.minCount = minCount;
			moveNext();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return (next != null);
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Cooccurrence<R> next() {
			if (next == null)
				throw new NoSuchElementException();
			
			IDBasedCooccurrence<R> idbasedCooccurrence = next;
			moveNext();
			try {
				return getCoOccurrence(idbasedCooccurrence);
			} catch (Exception e) {
				throw new NoSuchElementException(e.toString());
			}
		}

		/**
		 * Move to the next item, according to the minCount condition
		 */
		protected void moveNext() {
			try {
				next = iterator.next();
				while (next.getCount() < minCount)
					next = iterator.next();
			} catch (Exception e) {
				next = null;
			}
		}
		
		Iterator<IDBasedCooccurrence<R>> iterator;
		IDBasedCooccurrence<R> next;
		double minCount;
	}

}
