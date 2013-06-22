package eu.excitementproject.eop.distsim.util.id;

import eu.excitementproject.eop.distsim.storage.ItemNotFoundException;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;


/**
 * Implements IDGenerator with item-id map
 * 
 * Thread-safe
 * 
 * @author Meni Adler
 * @since 18/04/2012
 *
 * @param <T>  type of items
 * 
 */
public class DefaultIDMapper<T> implements IDMapper<T> {


	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.util.id.IDMapper#getID(java.lang.Object)
	 */
	@Override
	public synchronized int getID(T item) {
		if (!item2id.contains(item)) {
			item2id.put(item,item2id.size());
		}
		return item2id.get(item);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.corpus.extractor.IDGenerator#size()
	 */
	@Override
	public int size() {
		return item2id.size();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.corpus.extractor.IDGenerator#iterator()
	 */
	@Override
	public TObjectIntIterator<T> iterator() {
		return item2id.iterator();
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.corpus.extractor.IDMapper#put(java.lang.Object, int)
	 */
	@Override
	public void put(T item, int id) {
		item2id.put(item,id);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.corpus.extractor.IDGenerator#reset()
	 */
	@Override
	public void reset() {
		item2id.clear();		
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.corpus.extractor.IDMapper#getId(java.lang.Object)
	 */
	@Override
	public int getId(T item) throws ItemNotFoundException {
		if (!item2id.contains(item))
			throw new ItemNotFoundException(item.toString());
		return item2id.get(item);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] getItems() {
		return (T[])item2id.keys();
	}
	
	protected TObjectIntHashMap<T> item2id = new TObjectIntHashMap<T>();


}
