/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwi;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.SensedWord;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;

/**
 * this is the JWI implementation of {@link SensedWord}
 * @author Amnon Lotan
 *
 * @since 30 Nov 2011
 */
public class JwiSensedWord implements SensedWord {

	private final JwiSynset synset;
	private final String word;
	private final IWord iWord;
	private final JwiDictionary dictionary;
	private final WordNetPartOfSpeech pos;
	/**
	 * Ctor
	 * @param synset
	 * @param word
	 * @throws WordNetException 
	 */
	public JwiSensedWord(JwiSynset synset, String word) throws WordNetException {
		this.synset = synset;
		this.word = word;
		
		String wordToLookup = word.replace(' ', '_');	// mimic jwnl, which replaces underscores with spaces when looking up
	 	IWord iWord = null;
	 	boolean found = false;
	 	for (int i = 1; i <= synset.realSynset.getWords().size() && !found; i++)
	 	{
			iWord = synset.realSynset.getWord(i);
			found = iWord.getLemma().equalsIgnoreCase(wordToLookup);
	 	}
	 	if (!found)
	 		throw new WordNetException("\""+ word + "\" is not a memeber of the given synset " + synset);
	 	this.iWord = iWord;
	 	dictionary = synset.jwiDictionary;
	 	this.pos = JwiUtils.getWordNetPartOfSpeech(iWord.getPOS());
	}
	
	/**
	 * Ctor with an {@link IWord} and a {@link JwiDictionary}
	 * This Ctor is quicker than the other.
	 * @param jwiDictionary 
	 */
	JwiSensedWord(IWord iWord, JwiDictionary jwiDictionary) {
		this.iWord = iWord;
		this.synset = new JwiSynset(jwiDictionary, iWord.getSynset());
		this.word = iWord.getLemma();
		this.dictionary = jwiDictionary;
		this.pos = JwiUtils.getWordNetPartOfSpeech(iWord.getPOS());
	}
	
	public String getWord() {
		return word;
	}
	@Override
	public WordNetPartOfSpeech getPos() {
		return pos;
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
		{
			List<IWordID> relatedWordIDs = iWord.getRelatedWords(JwiUtils.wordNetRelationToPointer(relation));
			for (IWordID relatedWordID : relatedWordIDs)
				sensedWords.add( new JwiSensedWord(dictionary.jwiRealDictionary.getWord( relatedWordID), dictionary));          
		}
		return sensedWords;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JwiSensedWord [iWord=" + iWord + "]";
	}
}
