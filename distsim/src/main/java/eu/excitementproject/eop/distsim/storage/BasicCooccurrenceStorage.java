package eu.excitementproject.eop.distsim.storage;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.Relation;

/**
 * An interface for basic accessing a storage of co-occurrences
 * 
 * @author Meni Adler
 * @since 22/05/2012
 *
 * 
 * @param <R> the type of the relation domain, as defined by {@link Relation} interface
 */

public interface BasicCooccurrenceStorage<R>  {
	/**
	 * Get all co-occurrences
	 * 
	 * @return an iterator for co-occurrences 
	 */
	ImmutableIterator<Cooccurrence<R>> getCooccurrenceInstances();
	
	/**
	 * Get the co-occurrences that occurred at least minCount times
	 * 
	 * @param minCount the required minimal count of co-occurrence instance 
	 * 
	 * @return an iterator for co-occurrences which occurred at least minCount times
	 */
	ImmutableIterator<Cooccurrence<R>> getCooccurrenceInstances(int minCount);
}
