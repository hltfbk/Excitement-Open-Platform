/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wiktionary;

import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;



/**
 * An interface for a wiktionary entry, which represents all the information available for a <lemma, part of speech> couple.
 * An entry has several senses, aka {@link WiktionarySense}s. 
 * <p>It extends {@link WiktionarySense} cos often a lot of data lays at the entry level, not specific to any
 * of the entry's senses. In this respect, an entry behaves just like a sense with no sense-number. 
 * @author Amnon Lotan
 * @since Jun 22, 2011
 * 
 */
public interface WiktionaryEntry extends WiktionarySense {


	/**
	 * Get the number of sense's this entry has
	 * @return
	 */
	public int getNumberOfSenses();
	
	/**
	 * Get a specific sense of this Entry.
	 * @param senseNum
	 * @return
	 * @throws WiktionaryException if the senseNum is not positive and < {@link #getNumberOfSenses()}
	 */
	public WiktionarySense getSense(int senseNum) throws WiktionaryException;
	
	/**
	 * Get all the senses of this entry
	 * @return
	 * @throws WiktionaryException
	 */
	public Set<WiktionarySense> getAllSenses() throws WiktionaryException;
	
	/**
	 * Get all the senses of this entry, guaranteed in the order they appear in WIktionary.
	 * @return
	 * @throws WiktionaryException
	 */
	public List<WiktionarySense> getAllSortedSenses() throws WiktionaryException;

	/**
	 * Get a string of detailed info available for this wiktionary entry.
	 * @return
	 */
	public String getDetailedInformation();
	
	/**
	 * Returns "categories", to which this entry belongs, and which are not automatically derived from the language or part-of-speech information in Wiktionary.
	 * @return
	 */
	public ImmutableList<String> getCategories();
}