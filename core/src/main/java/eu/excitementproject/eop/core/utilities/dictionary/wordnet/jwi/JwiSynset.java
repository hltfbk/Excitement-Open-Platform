package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwi;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ILexFile;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.Pointer;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.EnglishWordnetLexicographerFileRetriever;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.LexicographerFileInformation;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.SensedWord;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordAndUsage;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;


/**
 * This is the JWI implementation of {@link Synset}. It can only be constructed through the getters of {@link JwiDictionary}.
 * 
 * @author Amnon Lotan
 *
 */
public class JwiSynset implements Synset
{
	private static final long serialVersionUID = 8631815454348507242L;
	/**
	 * package visible Ctor -  to be accessed by the dictionary only.
	 * 
	 * @param jwiDictionary
	 * @param jwiRealSynset
	 */
	JwiSynset(JwiDictionary jwiDictionary, ISynset jwiRealSynset)
	{
		this.jwiDictionary = jwiDictionary;
		this.realSynset = jwiRealSynset;
	}
	
	public WordNetPartOfSpeech getPartOfSpeech() 
	{
		return JwiUtils.getWordNetPartOfSpeech(this.realSynset.getPOS());
	}
	
	public Set<String> getWords() throws WordNetException
	{
		Set<String> ret = new HashSet<String>();
		for (IWord word : this.realSynset.getWords())
		{
			ret.add(word.getLemma());
		}
		return ret;
	}
	
	public Set<Synset> getNeighbors(WordNetRelation relationType) throws WordNetException {
		return getRelatedSynsets(relationType, 1);
	}
	
	public Set<Synset> getRelatedSynsets(WordNetRelation relation, int chainingLength) throws WordNetException {
		if (relation == null) throw new WordNetException("null wordnet relation");

		if (relation.equals(WordNetRelation.SYNONYM))		// some relations (SYNONYM) have no neighbors, cos they have no matching JWNL relation 
			return getSynonyms();
		else if (relation.equals(WordNetRelation.STRICT_2ND_DEGREE_COUSIN))
			return this.jwiDictionary.cousinFinder.getStrictCousinSynsets(this.realSynset, 2);
		else
		{
			Pointer jwiPointer = JwiUtils.wordNetRelationToPointer(relation);
			if (jwiPointer == null)
				return new HashSet<Synset>();	// relationType has no matching JWI Pointer. shouldn't happen, but for a couple of marginal relations
			else
				return relation.isLexical() ? getLexicalNeighbors(jwiPointer) : getSemanticNeighbors(jwiPointer, chainingLength, relation.isTransitive());
		}
	}
	
	public String getGloss() throws WordNetException
	{
		return this.realSynset.getGloss();
	}

	public Set<Synset> getAlsoSees() throws WordNetException
	{
		return getNeighbors(WordNetRelation.SEE_ALSO);
	}

	public Set<Synset> getAntonyms() throws WordNetException
	{
		return getNeighbors(WordNetRelation.ANTONYM);
	}

	public Set<Synset> getAttributes() throws WordNetException
	{
		return getNeighbors(WordNetRelation.ATTRIBUTE);
	}

	public Set<Synset> getCauses() throws WordNetException
	{
		return getNeighbors(WordNetRelation.CAUSE);
	}

	public Set<Synset> getDerived() throws WordNetException
	{
		return getNeighbors(WordNetRelation.DERIVED);
	}

	public Set<Synset> getEntailments() throws WordNetException
	{
		return getNeighbors(WordNetRelation.ENTAILMENT);
	}


	public Set<Synset> getHolonyms() throws WordNetException
	{
		Set<Synset> ret = getNeighbors(WordNetRelation.MEMBER_HOLONYM);
		ret.addAll(getNeighbors(WordNetRelation.PART_HOLONYM));
		ret.addAll(getNeighbors(WordNetRelation.SUBSTANCE_HOLONYM));
		return ret;
	}

	public Set<Synset> getHypernyms() throws WordNetException
	{
		return getNeighbors(WordNetRelation.HYPERNYM);
	}

	public Set<Synset> getHyponyms() throws WordNetException
	{
		return getNeighbors(WordNetRelation.HYPONYM);
	}

	public Set<Synset> getMemberHolonyms() throws WordNetException
	{
		return getNeighbors(WordNetRelation.MEMBER_HOLONYM);
	}

	public Set<Synset> getMemberMeronyms() throws WordNetException
	{
		return getNeighbors(WordNetRelation.MEMBER_MERONYM);
	}

	public Set<Synset> getMeronyms() throws WordNetException
	{
		Set<Synset> ret = getNeighbors(WordNetRelation.MEMBER_MERONYM);
		ret.addAll(getNeighbors(WordNetRelation.PART_MERONYM));
		ret.addAll(getNeighbors(WordNetRelation.SUBSTANCE_MERONYM));
		return ret;
	}

	public Set<Synset> getPartHolonyms() throws WordNetException
	{
		return getNeighbors(WordNetRelation.PART_HOLONYM);
	}

	public Set<Synset> getPartMeronyms() throws WordNetException
	{
		return getNeighbors(WordNetRelation.PART_MERONYM);
	}

	public Set<Synset> getParticipleOf() throws WordNetException
	{
		return getNeighbors(WordNetRelation.PARTICIPLE_OF);
	}

	public Set<Synset> getSubstanceHolonyms() throws WordNetException
	{
		return getNeighbors(WordNetRelation.SUBSTANCE_HOLONYM);
	}
	public Set<Synset> getSubstanceMeronyms() throws WordNetException
	{
		return getNeighbors(WordNetRelation.SUBSTANCE_MERONYM);
	}
	public Set<Synset> getSynonyms() throws WordNetException
	{
		Set<Synset> ret = new HashSet<Synset>();
		ret.add(this);
		return ret;
	}
	public Set<Synset> getVerbGroup() throws WordNetException
	{
		return getNeighbors(WordNetRelation.VERB_GROUP);
	}
	public Set<Synset> getDerivationallyRelated() throws WordNetException {
		return getNeighbors(WordNetRelation.DERIVATIONALLY_RELATED);
	}
	public Set<Synset> getPertanym() throws WordNetException {
		return getNeighbors(WordNetRelation.PERTAINYM);
	}
	
	public long getUsageOf(String word) throws WordNetException
	{
		IIndexWord idxWord = jwiDictionary.getJwiRealDictionary().getIndexWord(word, this.realSynset.getPOS());
		return idxWord.getTagSenseCount();
	}
	
	public List<WordAndUsage> getWordsAndUsages() throws WordNetException
	{
		List<IWord> words = this.realSynset.getWords();
		List<WordAndUsage> wordsAndUsages = new ArrayList<WordAndUsage>(words.size());
		for (IWord word : words)
			wordsAndUsages.add(new WordAndUsage(word.getLemma(), getUsageOf(word.getLemma())));
		return wordsAndUsages;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.dictionary.wordnet.Synset#getAllSensedWords()
	 */
	@Override
	public List<SensedWord> getAllSensedWords() {
		List<IWord> objWords = realSynset.getWords();
		List<SensedWord> sensedWords = new ArrayList<SensedWord>(objWords.size());
		for ( IWord word : objWords)
			sensedWords.add(new JwiSensedWord(word, this.jwiDictionary));
		return sensedWords;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((realSynset == null) ? 0 : realSynset.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JwiSynset other = (JwiSynset) obj;
		if (realSynset == null) {
			if (other.realSynset != null)
				return false;
		} else if (!realSynset.equals(other.realSynset))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JwiSynset [realSynset=" + realSynset + "]";
	}
	
	public long getOffset() {
		return realSynset.getOffset();
	}
	
	@Override
	public LexicographerFileInformation getLexicographerFileInformation() throws WordNetException
	{
		ILexFile lexFile = realSynset.getLexicalFile();
		return EnglishWordnetLexicographerFileRetriever.get(lexFile.getNumber());
	}

	///////////////////////////////////////////////////////////////////////// protected //////////////////////////////////////////////////////////////////

	/**
	 * Get neighbors for a lexical relation type
	 * @param antonym
	 * @return
	 */
	protected Set<Synset> getLexicalNeighbors(Pointer pointer) {
		Set<Synset> synsets = new HashSet<Synset>();
		for (IWord word : realSynset.getWords())
		{
			synsets.addAll(jwiDictionary.getSetOfSynsetsOfWords( word.getRelatedWords(pointer) ));
		}
		return synsets;
	}

	/**
	 * Get neighbors for a semantic relation type
	 * @param jwiPointer
	 * @param chainingLength 
	 * @param isTransitive 
	 * @return
	 */
	protected Set<Synset> getSemanticNeighbors(Pointer jwiPointer, int chainingLength, boolean isTransitive) {
		List<ISynsetID> targetSynsetIDs = getTransitivelyRelatedSynsets(new ArrayList<ISynsetID>(), realSynset, jwiPointer, isTransitive, chainingLength);
		return jwiDictionary.getSetOfSynsets(targetSynsetIDs);
	}
	
	/**
	 * @param ret 
	 * @param realSynset
	 * @param jwiPointer
	 * @param isTransitive 
	 * @param chainingLength
	 * @return
	 */
	protected List<ISynsetID> getTransitivelyRelatedSynsets(List<ISynsetID> ret, ISynset realSynset, Pointer jwiPointer, boolean isTransitive, int chainingLength) {
		List<ISynsetID> neighborSynsetIDs = realSynset.getRelatedSynsets(jwiPointer);
		ret.addAll(neighborSynsetIDs);
		IDictionary realDictionary = jwiDictionary.jwiRealDictionary;
		if (chainingLength == 1 || !isTransitive)
			;	//return neighborSynsetIDs;
		else
			for (ISynsetID synsetID : neighborSynsetIDs)
				getTransitivelyRelatedSynsets(ret, realDictionary.getSynset(synsetID), jwiPointer, isTransitive, chainingLength - 1);	// the return value is an arg
		return ret;
	}

	protected ISynset realSynset;
	protected JwiDictionary jwiDictionary;
}
