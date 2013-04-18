package eu.excitementproject.eop.distsim.builders.cooccurrence;

import java.io.File;
import java.io.IOException;

import eu.excitementproject.eop.distsim.builders.Builder;
import eu.excitementproject.eop.distsim.items.Relation;
import eu.excitementproject.eop.distsim.storage.CooccurrenceStorage;

/**
 * The CooccurrencesExtractor interface defines the basic functionality of extracting co-occurrence instances ({@link eu.excitementproject.eop.distsim.items.Cooccurrence}) 
 * of various types from a given corpus.
 * The overall outcome is represented by a {@link CooccurrenceStorage} object.
 *     
 * @author Meni Adler
 * @since 23/05/2012
 *
 * @param <R> the enum type of the relation domain, as defined by {@link Relation} interface
 * 
 */
public interface CooccurrencesExtractor<R> extends Builder {
	/**
	 * Construct a storage view of co-occurrences, extracted from a given corpus
	 * 
	 * @param corpus a root directory of some corpus representation
	 * @return a co-occurrence db, which stores all extracted co-occurrence instances 
	 * @throws IOException for problems in reading the given corpus
	 */
	CooccurrenceStorage<R> constructCooccurrenceDB(File corpus) throws CooccurrenceDBConstructionException;
}
