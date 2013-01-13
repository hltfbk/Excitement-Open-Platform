package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwnl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import eu.excitementproject.eop.core.utilities.dictionary.wordnet.EnglishWordnetLexicographerFileRetriever;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.LexicographerFileInformation;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.SensedWord;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordAndUsage;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetMethodNotSupportedException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.data.list.PointerTargetNode;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.data.list.PointerTargetTreeNode;
import net.didion.jwnl.data.list.PointerTargetTreeNodeList;


/**
 * A wrapper for the {@link Synset} over JWNL
 * @author Amnon Lotan
 *
 */
public class JwnlSynset implements Synset
{
	private static final long serialVersionUID = -6327404099038480598L;

	JwnlSynset(JwnlDictionary jwnlDictionary, net.didion.jwnl.data.Synset jwnlRealSynset)
	{
		this.jwnlDictionary = jwnlDictionary;
		this.realSynset = jwnlRealSynset;
	}
		
	public WordNetPartOfSpeech getPartOfSpeech() 
	{
		return JwnlUtils.getWordNetPartOfSpeech(this.realSynset.getPOS());
	}
	
	public Set<String> getWords() throws WordNetException
	{
		if (words == null)
		{
			words = new HashSet<String>();
			for (Word word : this.realSynset.getWords())
			{
				String lemma = word.getLemma();
				lemma = lemma.replaceAll("_", " ");	// clean
				words.add(lemma);
			}
		}
		return new HashSet<String>(words);	// return a copy
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

	public Set<Synset> getCoordinateTerms() throws WordNetException
	{
		try {
			return getSetOfSynsets(PointerUtils.getInstance().getCoordinateTerms(this.realSynset));
		} catch (JWNLException e) {
			throw new WordNetException("See nested", e);
		}
	}

	/** not supported */
	public Set<Synset> getDerived() throws WordNetException
	{
		return getNeighbors(WordNetRelation.DERIVED);
	}

	/** not supported */
	public Set<Synset> getEntailedBy() throws WordNetException
	{
		return new HashSet<Synset>();
	}

	public Set<Synset> getEntailments() throws WordNetException
	{
		return getNeighbors(WordNetRelation.ENTAILMENT);
	}


	public Set<Synset> getHolonyms() throws WordNetException
	{
		try {
			return getSetOfSynsets(PointerUtils.getInstance().getHolonyms(this.realSynset));
		} catch (JWNLException e) {
			throw new WordNetException("See nested", e);
		}
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
		try {
			return getSetOfSynsets(PointerUtils.getInstance().getMeronyms(this.realSynset));
		} catch (JWNLException e) {
			throw new WordNetException("See nested", e);
		}
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
		return getNeighbors(WordNetRelation.SYNONYM);
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
	
	public Set<Synset> getNeighbors(WordNetRelation relationType) throws WordNetException {
		return getRelatedSynsets(relationType, 1);
	}
	
	public Set<Synset> getRelatedSynsets(WordNetRelation relation, int chainingLength) throws WordNetException {
		if (chainingLength < 1)
			throw new WordNetException("chaining length must be positive. I got " + chainingLength);
		if (WordNetRelation.STRICT_2ND_DEGREE_COUSIN.equals(relation))
			throw new WordNetMethodNotSupportedException("Extracting cousin relations is currently not supported by JwnlDictionary. Use JwiDictionary instead");
		PointerType pointerType = JwnlUtils.wordNetRelationToPointerType(relation);
		
		if (pointerType == null)		
			// some relations (inc. SYNONYM) have no neighbors, cos they have no matching JWNL relation
			// other relations just don't exist in ext JWNL
			return new HashSet<Synset>();
		else
		{
			if (!relation.isTransitive())
				chainingLength = 1;			// most relations make no sense when chained
			try {	return getSetOfSynsets(PointerUtils.getInstance().makePointerTargetTreeList(realSynset, pointerType, chainingLength));	}
			catch (JWNLException e) { throw new WordNetException("see nested" , e);	}
		}
	}

	
	public long getUsageOf(String word) throws WordNetException
	{
		long ret = 0;
		Word[] wordsOfSynset = this.realSynset.getWords();
		for (Word wordOfSynset : wordsOfSynset)
		{
			if (wordOfSynset.getLemma().equals(word))
			{
				ret = wordOfSynset.getUsageCount();
			}
		}
		return ret;

	}
	
	public List<WordAndUsage> getWordsAndUsages() throws WordNetException
	{
		List<WordAndUsage> ret = null;
		Word[] words = this.realSynset.getWords();
		ret = new ArrayList<WordAndUsage>(words.length);
		for (Word word : words)
		{
			// TODO handle this better
			try
			{
				ret.add(new WordAndUsage(word.getLemma(), word.getUsageCount()));
			}
			catch(NoSuchElementException e){} // a JWNL bug
		}
		Collections.sort(ret,
				Collections.reverseOrder( // I'm preferring correctness to performance (in this case)
						new Comparator<WordAndUsage>()
						{
							public int compare(WordAndUsage o1, WordAndUsage o2)
							{
								if (o1.getUsage()<o2.getUsage())return -1;
								else if (o1.getUsage()==o2.getUsage()) return 0;
								else return 1;
							}

						} // end of anonymous class Comparator
				) // end of reverseOrder function (which returns the reverse of the defined comparator)
		);
		
		return ret;
	}
	
	@Override
	public List<SensedWord> getAllSensedWords() throws WordNetException {
		Word[] objWords = realSynset.getWords();
		List<SensedWord> sensedWords = new ArrayList<SensedWord>(objWords.length);
		for ( Word word : objWords)
			new JwnlSensedWord(word, this.jwnlDictionary);
		return sensedWords;
	}
	
	@Override
	public String toString() {
		return "JwnlSynset [realSynset=" + realSynset + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((realSynset == null) ? 0 : realSynset.hashCode());
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
		JwnlSynset other = (JwnlSynset) obj;
		if (realSynset == null) {
			if (other.realSynset != null)
				return false;
		} else if (!realSynset.equals(other.realSynset))
			return false;
		return true;
	}

	public long getOffset() {
		return realSynset.getOffset();
	}
	
	@Override
	public LexicographerFileInformation getLexicographerFileInformation() throws WordNetException
	{
		return EnglishWordnetLexicographerFileRetriever.get((int)this.realSynset.getLexFileId());
	}

	
	///////////////////////////////////////////////////////////////// protected	//////////////////////////////////////////////////
	
	/**
	 * Harvest all the synsets from this {@link PointerTargetTreeNodeList}, iteratively and recursively. Each treeNode holds a synset, and a pointer to a child list 
	 * of treeNodes.
	 * 
	 * @param pointerTargetTreeList must not be null!
	 * @return
	 * @throws WordNetException 
	 */
	protected Set<Synset> getSetOfSynsets(PointerTargetTreeNodeList pointerTargetTreeList) throws WordNetException {
		Set<Synset> ret = new HashSet<Synset>();
		for (Object treeNodeAsObject : pointerTargetTreeList)
		{
			if (!(treeNodeAsObject instanceof PointerTargetTreeNode))
				throw new WordNetException("Internal error. this was supposed to be a PointerTargetTreeNodeList: " + pointerTargetTreeList);
			PointerTargetTreeNode treeNode = (PointerTargetTreeNode) treeNodeAsObject;
			ret.add(new JwnlSynset(jwnlDictionary, treeNode.getSynset()));
			if (treeNode.hasValidChildTreeList())
				ret.addAll(getSetOfSynsets(treeNode.getChildTreeList()));
		}
		return ret;
	}
	
	protected Set<Synset> getSetOfSynsets(PointerTargetNodeList list)
	{
		if (null==list)
			return null;
		Set<Synset> ret = new HashSet<Synset>();
		for (Object nodeAsObject : list)
		{
			PointerTargetNode node = (PointerTargetNode) nodeAsObject;
			ret.add(new JwnlSynset(this.jwnlDictionary,node.getSynset()));
			
		}
		return ret;
	}

	net.didion.jwnl.data.Synset realSynset;	// package visible
	protected Set<String> words;
	protected JwnlDictionary jwnlDictionary;
}
