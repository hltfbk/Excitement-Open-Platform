package eu.excitementproject.eop.core.utilities.dictionary.wordnet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.core.utilities.dictionary.wordnet.ext_jwnl.ExtJwnlUtils;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwi.JwiUtils;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwnl.JwnlUtils;


/**
 * Word Net dictionary.
 * <P>
 * The reason to create a wrapper over the WordNet's Java implementation(s), is
 * to preserve flexibility when replacing the underlying implementation.
 * <P>
 * Note that some relations do not work due to third party limitations (currently,
 * CATEGORY_MEMBER, TROPONYM and DERIVED might not work. See {@link ExtJwnlUtils},
 * {@link JwnlUtils}, {@link JwiUtils}.
 * 
 * @author Asher Stern
 * @see	http://wordnet.princeton.edu/wordnet/documentation/ 
 *
 */
public interface Dictionary
{
	/**
	 * Clean up
	 */
	public void close();
	
	/**
	 * Pull the synsets matching this <lemma, partOfSpeech>. If none match, return an empty list.
	 * @param lemma
	 * @param partOfSpeech
	 * @return
	 * @throws WordNetException
	 */
	public Set<Synset> getSynsetsOf(String lemma, WordNetPartOfSpeech partOfSpeech) throws WordNetException;
	/**
	 * For each {@link WordNetPartOfSpeech}, pull the synsets matching the <lemma, partOfSpeech>. If none match, return an empty list.
	 * @param lemma
	 * @return
	 * @throws WordNetException
	 */
	public Map<WordNetPartOfSpeech,Set<Synset>> getSynsetOf(String lemma) throws WordNetException;
	/**
	 * Pull the synsets matching this <lemma, partOfSpeech>, in order of their WN synset indices. If none match, return an empty list.
	 * @param lemma
	 * @param partOfSpeech
	 * @return
	 * @throws WordNetException
	 */
	public List<Synset> getSortedSynsetsOf(String lemma, WordNetPartOfSpeech partOfSpeech) throws WordNetException;
	/**
	 * For each {@link WordNetPartOfSpeech}, pull the synsets matching the <lemma, partOfSpeech>, in order of their WN synset indices. 
	 * If none match, return an empty list.
	 * @param lemma
	 * @return
	 * @throws WordNetException
	 */
	public Map<WordNetPartOfSpeech,List<Synset>> getSortedSynsetOf(String lemma) throws WordNetException;
	

	/**
	 * construct a {@link SensedWord} out of this sense and word.
	 * <p>
	 * <b>CAUTION</b> if the given {@link Synset} synset, and this {@link Dictionary} are not of the same WordNet implementation package, an exception will be thrown. 
	 * @param lemma
	 * @param synset implemented in the same package as this Dictionary
	 * @return
	 * @throws WordNetException 
	 */
	public SensedWord getSensedWord(String lemma, Synset synset) throws WordNetException;
}
