/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wiktionary;

import java.io.Serializable;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;


/**
 * A wiktionary sense holds all the wiktionary info about a specific sense  of a <lemma, part of speech> entry. (each entry can have several senses).
 * <p>
 * Implementations must be immutable and implement equals() and hashCode() 
 * @author Amnon Lotan
 * @since Jun 22, 2011
 * 
 */
public interface WiktionarySense extends Serializable {

	/**
	 * Get this sense's  word 
	 * @return
	 */
	public String getWord();
	
	/**
	 * Get the part of speech of this sense
	 * @return
	 * @throws WiktionaryException
	 */
	public WiktionaryPartOfSpeech getWiktionaryPartOfSpeech() throws WiktionaryException;
	
	/**
	 * Return the plain text gloss of the sense
	 * @return
	 * @throws WiktionaryException
	 */
	public String getGloss() throws WiktionaryException;
	
	/**
	 * Return true iff this sense represents an {@link WiktionaryEntry}, i.e. not sense specific.
	 * @return
	 */
	public boolean isEntry();
	
	/**
	 * @return If <code>{@link #isEntry()} == true</code>, return the ordinal of this sense, unique among other senses of the same entry.<br>
	 * Else, (it's a {@link WiktionaryEntry}, return 0. 
	 */
	public int getSenseNo();

	/**
	 * Get the words related to this sense by the given relation type 
	 * @param relation
	 * @return
	 */
	public ImmutableList<String> getRelatedWords(WiktionaryRelation relation) throws WiktionaryException;
	
	/**
	 * get the sense's usage examples
	 * @return
	 */
	public ImmutableList<String> getExamples();
	
	/**
	 * get known quotations featuring this sense
	 * @return
	 */
	public ImmutableList<String> getQuotations();
}