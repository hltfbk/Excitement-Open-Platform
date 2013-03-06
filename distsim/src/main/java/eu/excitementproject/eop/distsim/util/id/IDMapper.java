package eu.excitementproject.eop.distsim.util.id;


import eu.excitementproject.eop.distsim.storage.ItemNotFoundException;
import eu.excitementproject.eop.distsim.util.Resetable;
import gnu.trove.iterator.TObjectIntIterator;

/**
  * Basic functionality of mapping items to ids
  * 
* @author Meni Adler
 * @since 18/04/2012
 *
 * @param <T> type of items
 * 
 */

public interface IDMapper<T> extends Resetable {
	/**
	 * @param item item to be assigned to a unique id
	 * @return the id for the given item (new id in case the item was not assigned to id yet
	 */
	int getID(T item);
	
	/**
	 * @return the number of generated id
	 */
	int size();
	
	/**
	 * @return an iterator for the id-value mapping
	 */
	TObjectIntIterator<T> iterator();
	
	
	/**
	 * @param item 
	 * @param id
	 * 
	 * add the given item and id as entry to the map
	 */
	void put(T item, int id);

	/**
	 * @param item
	 * @return the id assigned to the given item
	 */
	int getId(T item) throws ItemNotFoundException;
	
	T[] getItems();

}
