/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wordnet.ext_jwnl;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.sf.extjwnl.data.Pointer;
import net.sf.extjwnl.data.Word;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.SensedWord;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;

/**
 * this is the ExtJwnl implementation of {@link SensedWord}
 * <p>
 * <b>NOTE</b> ExtJwnl <i>lowercases</i> the words it is queried about. But the words it retrieves in methods like <code>realSynset.getWords()</code> 
 * are case sensitive!
 * 
 * @author Amnon Lotan
 *
 * @since 30 Nov 2011
 */
public class ExtJwnlSensedWord implements SensedWord {

	private final ExtJwnlSynset synset;
	private final String word;
	private final Word wordObj;
	private final ExtJwnlDictionary dictionary;
	private final WordNetPartOfSpeech pos;
	/**
	 * Ctor that takes a synset and a word. It verifies that the word indeed belongs to the synset.
	 * <p>
	 * <b>NOTE</b> ExtJwnl <i>lowercases</i> the words it is queried about. But the words it retrieves in methods like <code>realSynset.getWords()</code> 
	 * are case sensitive!
	 * 
	 * @param synset
	 * @param strWord
	 * @throws WordNetException 
	 */
	public ExtJwnlSensedWord(ExtJwnlSynset synset, String strWord) throws WordNetException {
		this.synset = synset;
		this.word = strWord;
		
		String wordToLookup = strWord.replace(' ', '_');	// mimic jwnl, which replaces underscores with spaces when looking up
	 	List<Word> words = synset.realSynset.getWords();
	 	Word wordObj = lookupWordInWords(words, wordToLookup);
	 	if (wordObj == null)
	 		throw new WordNetException("\""+ strWord + "\" is not a memeber of the given synset " + synset);
	 	this.wordObj = wordObj;
	 	dictionary = synset.extJwnlDictionary;
	 	this.pos = ExtJwnlUtils.getWordNetPartOfSpeech( wordObj.getPOS());
	}
	
	/**
	 * Ctor with an {@link Word} and a dictionary
	 * This Ctor is quicker than the other.
	 * @param jwiDictionary 
	 * @throws WordNetException 
	 */
	ExtJwnlSensedWord(Word wordObj, ExtJwnlDictionary extJwnlDictionary) throws WordNetException {
		if (wordObj==null)
			throw new WordNetException("wordObj is null!");
		this.wordObj = wordObj;
		this.synset = new ExtJwnlSynset(extJwnlDictionary, wordObj.getSynset());
		this.word = wordObj.getLemma();
		this.dictionary = extJwnlDictionary;
		this.pos = ExtJwnlUtils.getWordNetPartOfSpeech( wordObj.getPOS());
	}
	
	public String getWord() {
		return word;
	}
	public Synset getSynset() {
		return synset;
	}
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.SensedWord#getPartOfSpeech()
	 */
	@Override
	public WordNetPartOfSpeech getPartOfSpeech() throws WordNetException {
		return getSynset().getPartOfSpeech();
	}
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.SensedWord#getAntonyms()
	 */
	@Override
	public Set<SensedWord> getAntonyms() throws WordNetException {
		return getNeighborSensedWords(WordNetRelation.ANTONYM);
	}
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.SensedWord#getAlsoSee()
	 */
	@Override
	public Set<SensedWord> getAlsoSee() throws WordNetException {
		return getNeighborSensedWords(WordNetRelation.SEE_ALSO);	
	}
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.SensedWord#getDerived()
	 */
	@Override
	public Set<SensedWord> getDerived() throws WordNetException {
		return getNeighborSensedWords(WordNetRelation.DERIVED);
	}
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.SensedWord#getDerivationallyRelated()
	 */
	@Override
	public Set<SensedWord> getDerivationallyRelated() throws WordNetException {
		return getNeighborSensedWords(WordNetRelation.DERIVATIONALLY_RELATED);	
	}
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.SensedWord#getParticipleOf()
	 */
	@Override
	public Set<SensedWord> getParticipleOf() throws WordNetException {
		return getNeighborSensedWords(WordNetRelation.PARTICIPLE_OF);
	}
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.SensedWord#getPertanym()
	 */
	@Override
	public Set<SensedWord> getPertanym() throws WordNetException {
		return getNeighborSensedWords(WordNetRelation.PERTAINYM);
	}
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.SensedWord#getRelatedSensedWords(ac.biu.nlp.nlp.instruments.dictionary.wordnet.WordNetRelation)
	 */
	public Set<SensedWord> getNeighborSensedWords(WordNetRelation relation) throws WordNetException {

		Set<SensedWord> sensedWords = new LinkedHashSet<SensedWord>();
		if (relation.isLexical())
		{
			List<Pointer> pointers = wordObj.getPointers(ExtJwnlUtils.wordNetRelationToPointerType(relation));
			for (Pointer pointer : pointers)
				// add a new SensedWord made of the Word-cast of this pointer 
				sensedWords.add( new ExtJwnlSensedWord(((Word) pointer.getTarget()), dictionary));
		}
		return sensedWords;
	}
	
	/**
	 * @return the pos
	 */
	public WordNetPartOfSpeech getPos() {
		return pos;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExtJwnlSensedWord [objWord=" + wordObj + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((wordObj == null) ? 0 : wordObj.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtJwnlSensedWord other = (ExtJwnlSensedWord) obj;
		if (wordObj == null) {
			if (other.wordObj != null)
				return false;
		} else if (!wordObj.equals(other.wordObj))
			return false;
		return true;
	}

	/////////////////////////////////////////// PRIVATE	//////////////////////////////////////////////////////////
	
	/**
	 * @param words
	 * @param wordToLookup 
	 * @return
	 */
	private Word lookupWordInWords(List<Word> words, String wordToLookup) {
	 	boolean found = false;
	 	Word someWord = null;
		for (int i = 0; i < words.size() && !found; i++)
	 	{
			someWord = words.get(i);
			found = someWord.getLemma().equalsIgnoreCase(wordToLookup);
	 	}

		return someWord;
	}	
}
