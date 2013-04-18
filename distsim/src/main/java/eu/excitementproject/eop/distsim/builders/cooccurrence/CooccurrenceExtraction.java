package eu.excitementproject.eop.distsim.builders.cooccurrence;

import java.util.List;

import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.Relation;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * The CooccurrenceExtraction interface defines the construction of co-occurrences, based on a given source of a general type T
 * 
 * @author Meni Adler
 * @since 04/09/2012
 *
 * @param <T> the type of the source for extraction co-occurrences
 * @param <R> the type of the extracted relations, as defined by {@link Relation} interface 
 */
public interface CooccurrenceExtraction<T,R> {
	/**
	 * Extracts co-occurences from a given data
	 * 
	 * @param data a source for extracting co-coccurences
	 * @return a pair of extracted text unit list and co-occurence list
	 * @throws CooccurrenceExtractionException
	 */
	Pair<? extends List<? extends TextUnit>, ? extends List<? extends Cooccurrence<R>>> 
		extractCooccurrences(T data) throws CooccurrenceExtractionException;
	
}
