package eu.excitementproject.eop.core.utilities.dictionary.wordnet.ext_jwnl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.PointerUtils;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.data.list.PointerTargetNode;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.data.list.PointerTargetTreeNode;
import net.sf.extjwnl.data.list.PointerTargetTreeNodeList;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.EnglishWordnetLexicographerFileRetriever;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.LexicographerFileInformation;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.SensedWord;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordAndUsage;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetMethodNotSupportedException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;


/**
 * A wrapper for the {@link Synset}
 * <p>
 * <b>NOTE</b> ExtJwnl <i>lowercases</i> the words it is queried about.
 * 
 * @author Amnon Lotan
 *
 */
public class ExtJwnlSynset implements Synset
{
	ExtJwnlSynset(ExtJwnlDictionary jwnlDictionary, net.sf.extjwnl.data.Synset jwnlRealSynset)
	{
		this.extJwnlDictionary = jwnlDictionary;
		this.realSynset = jwnlRealSynset;
	}
	
	public WordNetPartOfSpeech getPartOfSpeech() 
	{
		return ExtJwnlUtils.getWordNetPartOfSpeech(this.realSynset.getPOS());
	}
	
	public Set<String> getWords() throws WordNetException
	{
		if (words == null)
		{
			words = new LinkedHashSet<String>();
			for (Word word : this.realSynset.getWords())
			{
				String lemma = word.getLemma();
				lemma = lemma.replaceAll("_", " ");	// clean
				words.add(lemma);
			}
		}
		return new LinkedHashSet<String>(words);	// return a copy
	}
	
	public String getGloss() throws WordNetException
	{
		return this.realSynset.getGloss();
	}

	public Set<Synset> getAlsoSees() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getAlsoSees(this.realSynset));
	}

	public Set<Synset> getAntonyms() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getAntonyms(this.realSynset));
	}

	public Set<Synset> getAttributes() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getAttributes(this.realSynset));
	}

	public Set<Synset> getCauses() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getCauses(this.realSynset));
	}

	public Set<Synset> getCoordinateTerms() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getCoordinateTerms(this.realSynset));
	}

	/** not supported */
	public Set<Synset> getDerived() throws WordNetException
	{
		throw new WordNetException("getDerived() is not supported by ExtJwnl");
		// return new HashSet<Synset>();
	}

	/** not supported */
	public Set<Synset> getEntailedBy() throws WordNetException
	{
		throw new WordNetException("getEntailedBy() is not supported by ExtJwnl");
		// return new HashSet<Synset>();
	}

	public Set<Synset> getEntailments() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getEntailments(this.realSynset));
	}


	public Set<Synset> getHolonyms() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getHolonyms(this.realSynset));
	}

	public Set<Synset> getHypernyms() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getDirectHypernyms(this.realSynset));
	}

	public Set<Synset> getHyponyms() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getDirectHyponyms(this.realSynset));
	}

	public Set<Synset> getMemberHolonyms() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getMemberHolonyms(this.realSynset));
	}

	public Set<Synset> getMemberMeronyms() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getMemberMeronyms(this.realSynset));
	}

	public Set<Synset> getMeronyms() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getMeronyms(this.realSynset));
	}

	public Set<Synset> getPartHolonyms() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getPartHolonyms(this.realSynset));
	}

	public Set<Synset> getPartMeronyms() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getPartMeronyms(this.realSynset));
	}


	public Set<Synset> getParticipleOf() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getParticipleOf(this.realSynset));
	}

	public Set<Synset> getSubstanceHolonyms() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getSubstanceHolonyms(this.realSynset));
	}

	public Set<Synset> getSubstanceMeronyms() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getSubstanceMeronyms(this.realSynset));
	}

	public Set<Synset> getSynonyms() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getSynonyms(this.realSynset));
	}

	public Set<Synset> getVerbGroup() throws WordNetException
	{
		return getSetOfSynsets(PointerUtils.getVerbGroup(this.realSynset));
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
			throw new WordNetMethodNotSupportedException("Extracting cousin relations is currently not supported by ExtJwnlDictionary. Use JwiDictionary instead");
		
		PointerType pointerType = ExtJwnlUtils.wordNetRelationToPointerType(relation);
		
		if (pointerType == null)		
			// some relations (inc. SYNONYM) have no neighbors, cos they have no matching JWNL relation
			// other relations just don't exist in ext JWNL
			return new LinkedHashSet<Synset>();
		else {
			if (realSynset==null)
				throw new WordNetException("Internal bug: realSynset is null!");
			return getSetOfSynsets(PointerUtils.makePointerTargetTreeList(realSynset, pointerType,chainingLength));
		}
	}

	public long getUsageOf(String word) throws WordNetException
	{
		throw new WordNetException("Method not supported. ExtJwnl does not support retrieving usage counts");
		
		// This is the code used in JwnlSynset. It should work here just the same, but always returns 0. Don't know why.
		
//		long ret = 0;
//		List<Word> wordsOfSynset = this.realSynset.getWords();
//		for (Word wordOfSynset : wordsOfSynset)
//		{
//			if (wordOfSynset.getLemma().equals(word))
//			{
//				ret = wordOfSynset.getUseCount();
//				break;
//			}
//		}
//		return ret;

	}
	
	public List<WordAndUsage> getWordsAndUsages() throws WordNetException
	{
		List<WordAndUsage> ret = null;
		List<Word> words = this.realSynset.getWords();
		ret = new ArrayList<WordAndUsage>(words.size());
		for (Word word : words)
		{
			// TODO handle this better
			try
			{
				ret.add(new WordAndUsage(word.getLemma(), word.getUseCount()));
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
		List<Word> objWords = realSynset.getWords();
		List<SensedWord> sensedWords = new ArrayList<SensedWord>(objWords.size());
		for ( Word word : objWords)
			sensedWords.add( new ExtJwnlSensedWord(word, this.extJwnlDictionary));
		return sensedWords;
	}
	
	@Override
	public String toString() {
		return "ExtJwnlSynset [realSynset=" + realSynset + "]";
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
		ExtJwnlSynset other = (ExtJwnlSynset) obj;
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
		return EnglishWordnetLexicographerFileRetriever.get((int)this.realSynset.getLexFileNum());
	}
	
	///////////////////////////////////////////////////////////////// protected	//////////////////////////////////////////////////
	
	/**
	 * Harvest all the synsets from this {@link PointerTargetTreeNodeList}, iteratively and recursively. Each treeNode holds a synset, and a pointer to a child list 
	 * of treeNodes.
	 * 
	 * @param pointerTargetTreeList must not be null!
	 * @return
	 */
	protected Set<Synset> getSetOfSynsets(PointerTargetTreeNodeList pointerTargetTreeList) {
		Set<Synset> ret = new LinkedHashSet<Synset>();
		for (PointerTargetTreeNode treeNode : pointerTargetTreeList)
		{
			ret.add(new ExtJwnlSynset(extJwnlDictionary, treeNode.getSynset()));
			if (treeNode.hasValidChildTreeList())
				ret.addAll(getSetOfSynsets(treeNode.getChildTreeList()));
		}
		return ret;
	}
	
	protected Set<Synset> getSetOfSynsets(PointerTargetNodeList list)
	{
		if (null==list)
			return null;
		Set<Synset> ret = new LinkedHashSet<Synset>();
		for (Object nodeAsObject : list)
		{
			PointerTargetNode node = (PointerTargetNode) nodeAsObject;
			ret.add(new ExtJwnlSynset(this.extJwnlDictionary,node.getSynset()));
		}
		return ret;
	}

	private static final long serialVersionUID = -6327404099038480598L;
	
	net.sf.extjwnl.data.Synset realSynset;	// package visible
	protected Set<String> words;
	protected ExtJwnlDictionary extJwnlDictionary;
}
