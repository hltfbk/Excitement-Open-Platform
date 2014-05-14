/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wordnet;
import java.util.Set;


/**
 * A sensed word is a lemma coupled with a {@link Synset} that contains it. In wordnet these sensed words are the source and
 * targets of <i>lexical</i> {@link WordNetRelation}s (Antonym, Derived, Derivations, See_Also, Participle and Pertanym). In contrast, Synsets are 
 * the source and target of all the other <i>semantic</i> relations. 
 * @author Amnon Lotan
 *
 * @since 30 Nov 2011
 * @see	http://wordnet.princeton.edu/wordnet/man/wninput.5WN.html
 */
public interface SensedWord {
	
	/**
	 * @return the word
	 */
	public String getWord();
	/**
	 * @return the part of speech
	 */
	public WordNetPartOfSpeech getPos(); 
	/**
	 * @return the synset
	 */
	public Synset getSynset();
	/**
	 * @return the part of speech
	 * @throws WordNetException 
	 */
	public WordNetPartOfSpeech getPartOfSpeech() throws WordNetException;
	
	public  Set<SensedWord> getAntonyms() throws WordNetException;
	public  Set<SensedWord> getAlsoSee() throws WordNetException;
	public  Set<SensedWord> getDerived()throws WordNetException;
	public  Set<SensedWord> getDerivationallyRelated()throws WordNetException;
	public  Set<SensedWord> getParticipleOf()throws WordNetException;
	public  Set<SensedWord> getPertanym() throws WordNetException;
	
	/**
	 * Get the direct neighbors connected by the given relation type. If none exist, an empty Set is returned.<br>
	 * The relation must be lexical (see {@link WordNetRelation#isLexical()}) in order to link to other {@link SensedWord}s.
	 * If another, semantic, relation type is given, an empty set is returned. 
	 * @param relationType
	 * @return
	 * @throws SensedException
	 */
	public Set<SensedWord> getNeighborSensedWords(WordNetRelation relationType) throws WordNetException;
}
