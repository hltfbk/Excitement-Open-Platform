package eu.excitementproject.eop.distsim.storage;

import java.io.Serializable;
import java.util.Set;

import eu.excitementproject.eop.distsim.items.Countable;
import eu.excitementproject.eop.distsim.items.Identifiable;

/**
 * Extends the CountableIdentifiableStorage interface with the functionality of getting items according to a given regular expression
 * 
 * @author Meni Adler
 * @since 10/09/2012
 *
 * @param <T>  The type of the data of the storage
 */
public interface PatternBasedCountableIdentifiableStorage<T extends Serializable & Countable & Identifiable> extends
		CountableIdentifiableStorage<T> {

	/**
	 * Gets the ids of the items which their key string fits the given regular expression
	 * 
	 * @param regularExpr a regular expression that defines the required items
	 * @return the items which their key string fits the given regular expression
	 */
	Set<Integer> getItemIds(String regularExpr);
}
