package eu.excitementproject.eop.distsim.storage;

import java.util.NoSuchElementException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.DefaultCooccurrence;
import eu.excitementproject.eop.distsim.items.DefaultRelation;
import eu.excitementproject.eop.distsim.items.IDBasedCooccurrence;
import eu.excitementproject.eop.distsim.items.TextUnit;
//import org.apache.log4j.Logger;

/**
 * Implements the BasicCooccurrenceStorage interface, based on a given PersistenceDevice of the co-occurrences and TextUnit storage
 * In this way, the co-occurences are not loaded to the memory, but read one by one from the device.
 * 
 * @author Meni Adler
 * @since 19/07/2012
 *
 * <p>non thread-safe
 * 
 */
public class DeviceBasedCooccurrenceStorage<R> implements BasicCooccurrenceStorage<R> {
	
//	private final static Logger logger = Logger.getLogger(DeviceBasedCooccurrenceStorage.class);
	
	public DeviceBasedCooccurrenceStorage(CountableIdentifiableStorage<TextUnit> textUnitStorage, PersistenceDevice cooccurrenceDevice) {
		this.textUnitStorage = textUnitStorage;
		this.cooccurrenceDevice = cooccurrenceDevice;
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

	protected CountableIdentifiableStorage<TextUnit> textUnitStorage;
	protected PersistenceDevice cooccurrenceDevice;

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
		@SuppressWarnings("unchecked")
		protected void moveNext() {
			try {
				next = (IDBasedCooccurrence<R>) cooccurrenceDevice.read().getSecond();
				while (next.getCount() < minCount)
					next = (IDBasedCooccurrence<R>) cooccurrenceDevice.read().getSecond();
			} catch (Exception e) {
				//logger.info(ExceptionUtil.getStackTrace(e));
				next = null;
			}
		}
		
		IDBasedCooccurrence<R> next;
		double minCount;
	}

}
