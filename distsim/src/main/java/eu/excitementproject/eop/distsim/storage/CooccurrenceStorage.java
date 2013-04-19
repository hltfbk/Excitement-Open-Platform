package eu.excitementproject.eop.distsim.storage;

import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.Relation;

/**
 * An interface for accessing a storage of co-occurrences
 * 
 * @author Meni Adler
 * @since 22/05/2012
 *
 * 
 * @param <R> the enum type of the relation domain, as defined by {@link Relation} interface
 */

public interface CooccurrenceStorage<R> extends BasicCooccurrenceStorage<R>, Persistence {
	/**
	 * Get a co-occurrence by a given unique id
	 * 
	 * @param cooccurrenceId a unique id of some co-occurrences
	 * @return the matched co-occurrence
	 * @throws ItemNotFoundException in case the given id is not assigned to any co-occurrence
	 */
	Cooccurrence<R> getCooccurrenceInstance(int cooccurrenceId) throws ItemNotFoundException;
	
}
