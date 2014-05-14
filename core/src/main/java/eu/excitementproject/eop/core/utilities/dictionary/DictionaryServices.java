package eu.excitementproject.eop.core.utilities.dictionary;

import eu.excitementproject.eop.common.representation.parse.representation.basic.BurstPartOfSpeechType;

/**
 * This interface is not yet implemented, and is a candidate for
 * being dropped out.
 * @author Asher Stern
 *
 */
@Deprecated
public interface DictionaryServices
{
	public void setWord(String word);
	public void setPartOfSpeech(BurstPartOfSpeechType partOfSpeech);
	
	/**
	 * Lemma is the base form of a word. for example the lemma for "going"
	 * is "go".
	 * If a word has more than one possible lemmas - they are all returned
	 * in the array.
	 * In any case - the array must contain at least one word, unless the
	 * word is not in the dictionary (or is an empty word, etc.)
	 * @return
	 */
	public String[] getLemmas() throws DictionaryServicesException;

}
