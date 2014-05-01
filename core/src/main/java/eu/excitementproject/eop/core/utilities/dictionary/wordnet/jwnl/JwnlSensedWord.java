/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwnl;
import java.util.HashSet;
import java.util.Set;

import eu.excitementproject.eop.core.utilities.dictionary.wordnet.SensedWord;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.Pointer;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.Word;

/**
 * this is the JWNL implementation of {@link SensedWord}
 * @author Amnon Lotan
 *
 * @since 30 Nov 2011
 */
public class JwnlSensedWord implements SensedWord {

	private final JwnlSynset synset;
	private final String word;
	private final Word wordObj;
	private final JwnlDictionary dictionary;
	private final WordNetPartOfSpeech pos;

	/**
	 * Ctor
	 * @param synset
	 * @param strWord
	 * @throws WordNetException 
	 */
	public JwnlSensedWord(JwnlSynset synset, String strWord) throws WordNetException {
		this.synset = synset;
		this.word = strWord;
		
		String wordToLookup = strWord.replace(' ', '_');	// mimic jwnl, which replaces underscores with spaces when looking up
	 	Word[] words = synset.realSynset.getWords();
	 	Word wordObj = lookupWordInWords(words, wordToLookup);
	 	if (wordObj == null)
	 		throw new WordNetException("\""+ strWord + "\" is not a memeber of the given synset " + synset);
	 	this.wordObj = wordObj;
	 	dictionary = synset.jwnlDictionary;
	 	this.pos = JwnlUtils.getWordNetPartOfSpeech( wordObj.getPOS());
	}
	
	/**
	 * Ctor with an {@link Word} and a dictionary
	 * This Ctor is quicker than the other.
	 * @param jwiDictionary 
	 * @throws WordNetException 
	 */
	JwnlSensedWord(Word objWord, JwnlDictionary jwnlDictionary) throws WordNetException {
		this.wordObj = objWord;
		this.synset = new JwnlSynset(jwnlDictionary, objWord.getSynset());
		this.word = objWord.getLemma();
		this.dictionary = jwnlDictionary;
		this.pos = JwnlUtils.getWordNetPartOfSpeech( wordObj.getPOS());
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

		Set<SensedWord> sensedWords = new HashSet<SensedWord>();
		if (relation.isLexical())
		{
			PointerType pointerType = JwnlUtils.wordNetRelationToPointerType(relation);
			if (pointerType != null)
			{
				Pointer[] pointers = wordObj.getPointers(pointerType);
				for (Pointer pointer : pointers)
					try {	sensedWords.add( new JwnlSensedWord(((Word) pointer.getTarget()), dictionary));	}
					catch (JWNLException e) { throw new WordNetException("See nested",e);	}
			}
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
		JwnlSensedWord other = (JwnlSensedWord) obj;
		if (wordObj == null) {
			if (other.wordObj != null)
				return false;
		} else if (!wordObj.equals(other.wordObj))
			return false;
		return true;
	}

	///////////////////////////////////////////////////// PRIVATE	////////////////////////////////////////////////////
	
	/**
	 * @param words
	 * @param wordToLookup 
	 * @return
	 */
	private Word lookupWordInWords(Word[] words, String wordToLookup) {
	 	boolean found = false;
	 	Word someWord = null;
		for (int i = 0; i < words.length && !found; i++)
	 	{
			someWord = words[i];
			found = someWord.getLemma().equalsIgnoreCase(wordToLookup);
	 	}

		return someWord;
	}
}
