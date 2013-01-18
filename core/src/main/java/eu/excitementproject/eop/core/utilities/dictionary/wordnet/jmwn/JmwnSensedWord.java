package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jmwn;

import java.util.HashSet;
import java.util.Set;

import org.itc.mwn.Pointer;
import org.itc.mwn.PointerType;
import org.itc.mwn.Word;

import eu.excitementproject.eop.core.utilities.dictionary.wordnet.SensedWord;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;



public class JmwnSensedWord implements SensedWord {

	private final JmwnSynset synset;
	private final String word;
	private final Word wordObj;
	private final JmwnDictionary dictionary;
	private final WordNetPartOfSpeech pos;
	
	public JmwnSensedWord(JmwnSynset synset, String strWord) throws WordNetException {
		this.synset = synset;
		this.word = strWord;
		
		String wordToLookup = strWord.replace(' ', '_');
		Word[] words = synset.realSynset.getWords();
		Word wordObj = lookupWordInWords(words, wordToLookup);
		if (wordObj == null) 
	 		throw new WordNetException("\""+ strWord + "\" is not a memeber of the given synset " + synset);
	 	this.wordObj = wordObj;
	 	dictionary = synset.jmwnDictionary;
	 	this.pos = JmwnUtils.getWordNetPartOfSpeech( wordObj.getPOS());
	}
	
	public JmwnSensedWord(Word objWord, JmwnDictionary jmwnDictionary) throws WordNetException {
		this.wordObj = objWord;
		this.synset = new JmwnSynset(jmwnDictionary, objWord.getSynset());
		this.word = objWord.getLemma();
		this.dictionary = jmwnDictionary;
		this.pos = JmwnUtils.getWordNetPartOfSpeech(wordObj.getPOS());
	}
	
	public Set<SensedWord> getAlsoSee() throws WordNetException {
		return getNeighborSensedWords(WordNetRelation.SEE_ALSO);
	}

	public Set<SensedWord> getAntonyms() throws WordNetException {
		return getNeighborSensedWords(WordNetRelation.ANTONYM) ;
	}

	public Set<SensedWord> getDerivationallyRelated() throws WordNetException {
		return getNeighborSensedWords(WordNetRelation.DERIVATIONALLY_RELATED) ;
	}

	public Set<SensedWord> getDerived() throws WordNetException {
		return getNeighborSensedWords(WordNetRelation.DERIVED);
	}

	public Set<SensedWord> getNeighborSensedWords(WordNetRelation relation) throws WordNetException {
		Set<SensedWord> sensedWords = new HashSet<SensedWord>();
		if (relation.isLexical()) {
			PointerType pointerType = JmwnUtils.wordNetRelationToPointerType(relation);
			if (pointerType != null) {
				Pointer[] pointers = wordObj.getPointers(pointerType);
				for (Pointer pointer: pointers) { 
					try {
						sensedWords.add(new JmwnSensedWord(((Word) pointer.getTarget()), dictionary));
					} catch (Exception e) {
						throw new WordNetException("See nested", e);
					}
				}
			}
		}
		return sensedWords;
	}

	public Set<SensedWord> getParticipleOf() throws WordNetException {
		return getNeighborSensedWords(WordNetRelation.PARTICIPLE_OF) ;
	}

	public Set<SensedWord> getPertanym() throws WordNetException {
		return getNeighborSensedWords(WordNetRelation.PERTAINYM) ;
	}

	public WordNetPartOfSpeech getPartOfSpeech() throws WordNetException {
		return getSynset().getPartOfSpeech();
	}

	public WordNetPartOfSpeech getPos() {
		return pos;
	}
	
	public Synset getSynset() {
		return synset;
	}

	public String getWord() {
		return word;
	}
	
	private Word lookupWordInWords(Word[] words, String wordToLookup) {
		boolean found = false;
		Word someWord = null;
		for (int i = 0; i < words.length && !found; i++) {
			someWord = words[i];
			found = someWord.getLemma().equalsIgnoreCase(wordToLookup);
		}
		return someWord;
	}

	
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
		JmwnSensedWord other = (JmwnSensedWord) obj;
		if (wordObj == null) {
			if (other.wordObj != null)
				return false;
		} else if (!wordObj.equals(other.wordObj))
			return false;
		return true;
	}

}
