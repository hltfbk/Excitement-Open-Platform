/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wiktionary;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An interface to the dictionary services of Wiktionary. Mainly, extracting word+pos entries and senses (each word+POS has up to one entry, which 
 * in turn can have several senses), and lexical relations between them.
 * <p>
 * 
 * @author Amnon Lotan
 * @since Jun 22, 2011
 */
public interface WiktionaryDictionary  {
	
	/**
	 * Pull the synsets matching this <lemma, partOfSpeech>. If none match, return an empty list.
	 * @param lemma
	 * @param partOfSpeech
	 * @return
	 * @throws WiktionaryException
	 */
	public Set<WiktionarySense> getSensesOf(String lemma, WiktionaryPartOfSpeech partOfSpeech) throws WiktionaryException;
	/**
	 * For each {@link WiktionaryPartOfSpeech}, pull the Senses matching the <lemma, partOfSpeech>. If none match, return an empty list.
	 * @param lemma
	 * @return
	 * @throws WiktionaryException
	 */
	public Map<WiktionaryPartOfSpeech,Set<WiktionarySense>> getSensesOf(String lemma) throws WiktionaryException;
	/**
	 * Pull the Senses matching this <lemma, partOfSpeech>, in order of their Wiktionary Sense indices. If none match, return an empty list.
	 * @param lemma
	 * @param partOfSpeech
	 * @return
	 * @throws WiktionaryException
	 */
	public List<WiktionarySense> getSortedSensesOf(String lemma, WiktionaryPartOfSpeech partOfSpeech) throws WiktionaryException;
	/**
	 * Return all the {@link WiktionarySense}s that match this lemma, mapped to parts of speech
	 * in order of their Wiktionary  indices. 
	 * If none match, return an empty map.
	 * @param lemma
	 * @return
	 * @throws WiktionaryException
	 */
	public Map<WiktionaryPartOfSpeech,List<WiktionarySense>> getSortedSensesOf(String lemma) throws WiktionaryException;
	
	/**
	 * If exists, return the entry matching this <lemma, pos>. Else, return null.<br>
	 * For every {@link WiktionaryEntry} there are 0/1 {@link WiktionaryEntry}s. 
	 * @param lemma
	 * @param partOfSpeech
	 * @return
	 * @throws WiktionaryException
	 */
	public WiktionaryEntry getEntry(String lemma, WiktionaryPartOfSpeech partOfSpeech) throws WiktionaryException;
	/**
	 * Return all the {@link WiktionaryEntry}s that match this lemma, mapped to parts of speech.<br>
	 * For every {@link WiktionaryEntry} there are 0/1 {@link WiktionaryEntry}s. If none match, return an empty map.
	 * @param lemma
	 * @return
	 * @throws WiktionaryException
	 */
	public Map<WiktionaryPartOfSpeech, WiktionaryEntry> getEntriesOf(String lemma) throws WiktionaryException;
}
