

package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jmwn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.itc.mwn.PointerUtils;
import org.itc.mwn.Word;

import eu.excitementproject.eop.core.utilities.dictionary.wordnet.LexicographerFileInformation;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.SensedWord;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordAndUsage;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetMethodNotSupportedException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;


public class JmwnSynset implements Synset {

	/**
	*
	*/
	private static final long serialVersionUID = 7237110583140606492L;
	
	protected JmwnDictionary jmwnDictionary;
	org.itc.mwn.Synset realSynset;
	protected Set<String> words;
	
	JmwnSynset(JmwnDictionary jmwnDictionary, org.itc.mwn.Synset jmwnRealSynset) {
		this.jmwnDictionary = jmwnDictionary;
		this.realSynset = jmwnRealSynset;
	}
	
	public List<SensedWord> getAllSensedWords() throws WordNetException {
		Word[] words = realSynset.getWords();
		if (words != null) {
			List<SensedWord> sensedWords = new ArrayList<SensedWord>(words.length);
			for (int i = 0; i < words.length; i++)
			sensedWords.add(new JmwnSensedWord(words[i], this.jmwnDictionary));
			return sensedWords;
		}
		return new ArrayList<SensedWord>(0);
	}
	
	public Set<Synset> getAlsoSees() throws WordNetException {
		return getNeighbors(WordNetRelation.SEE_ALSO);
	}
	
	public Set<Synset> getAntonyms() throws WordNetException {
		return getNeighbors(WordNetRelation.ANTONYM);
	}
	
	public Set<Synset> getAttributes() throws WordNetException {
		return getNeighbors(WordNetRelation.ATTRIBUTE);
	}
	
	public Set<Synset> getCauses() throws WordNetException {
		return getNeighbors(WordNetRelation.CAUSE);
	}
	
	public Set<Synset> getCoordinateTerms() throws WordNetException
	{
		try {
			return getSetOfSynsets(PointerUtils.getInstance().getCoordinateTerms(this.realSynset));
		} catch (Exception e) {
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
	
	public Set<Synset> getEntailments() throws WordNetException {
		return getNeighbors(WordNetRelation.ENTAILMENT);
	}
	
	public String getGloss() throws WordNetException {
		return this.realSynset.getGloss();
	}
	
	public Set<Synset> getHolonyms() throws WordNetException {
		try {
			return getSetOfSynsets(org.itc.mwn.PointerUtils.getInstance().getHolonyms(this.realSynset));
		} catch (Exception e) {
		throw new WordNetException("See nested", e);
		}
	}
	
	public Set<Synset> getHypernyms() throws WordNetException {
		return getNeighbors(WordNetRelation.HYPERNYM);
	}
	
	public Set<Synset> getHyponyms() throws WordNetException {
		return getNeighbors(WordNetRelation.HYPONYM);
	}
	
	public Set<Synset> getMemberHolonyms() throws WordNetException {
		return getNeighbors(WordNetRelation.MEMBER_HOLONYM);
	}
	
	public Set<Synset> getMemberMeronyms() throws WordNetException {
		return getNeighbors(WordNetRelation.MEMBER_MERONYM);
	}
	
	public Set<Synset> getMeronyms() throws WordNetException {
	try {
			return getSetOfSynsets(PointerUtils.getInstance().getMeronyms(this.realSynset));
		} catch (Exception e) {
		throw new WordNetException("See nested", e);
	}
	}
	
	public Set<Synset> getNeighbors(WordNetRelation relationType)
	throws WordNetException {
		return getRelatedSynsets(relationType, 1);
	}
	
	
	// The offset in MultiWordNet also contains the part of speech
	public long getOffset() throws WordNetException {
		String mwnOffset = realSynset.getOffset();
		/* if (mwnOffset.matches("\\w\\#\\d+")) {
		return Long.parseLong(realSynset.getOffset().substring(mwnOffset.indexOf("#")+1));
		}
		*/
		Pattern p = Pattern.compile(".*?(\\d+)");
		Matcher pm = p.matcher(mwnOffset);
		if (pm.matches()) {
			// System.out.println("Offset found: " + pm.group(1));
			return Long.parseLong(pm.group(1));
		}
	
		System.err.println("malformed offset for synset " + this.getWords());
		throw new WordNetException("Malformed offset: " + mwnOffset + " for synset " + this.getWords());
	}
	
	public Set<Synset> getPartHolonyms() throws WordNetException {
		return getNeighbors(WordNetRelation.PART_HOLONYM);
	}
	
	public Set<Synset> getPartMeronyms() throws WordNetException {
		return getNeighbors(WordNetRelation.PART_MERONYM);
	}
	
	public WordNetPartOfSpeech getPartOfSpeech() throws WordNetException {
		return JmwnUtils.getWordNetPartOfSpeech(this.realSynset.getPOS());
	}
	
	public Set<Synset> getRelatedSynsets(WordNetRelation relation,
		int chainingLength) throws WordNetException {
		
		// System.out.println("Getting related synsets for " + this.getGloss() + "\n\t relation: " + relation.name());
		
		if (chainingLength < 1)
			throw new WordNetException("chaining length must be positive. I got " + chainingLength);
		if (WordNetRelation.STRICT_2ND_DEGREE_COUSIN.equals(relation))
			throw new WordNetMethodNotSupportedException("Extracting cousin relations is currently not supported by JwnlDictionary. Use JwiDictionary instead");
		org.itc.mwn.PointerType pointerType = JmwnUtils.wordNetRelationToPointerType(relation);
		
		if (pointerType == null) {
			// some relations (inc. SYNONYM) have no neighbors, cos they have no matching JMWN relation
			// other relations just don't exist in ext JMWN
			return new HashSet<Synset>();
		} else {
			// System.out.println("Relation type found: " + pointerType.getLabel());
			if (!relation.isTransitive())
				chainingLength = 1; // most relations make no sense when chained
			try { return getSetOfSynsets(PointerUtils.getInstance().gatherPointerTargets(realSynset, pointerType, chainingLength)); }
			catch (Exception e) { throw new WordNetException("see nested" , e); }
		}
	}
	
	public Set<Synset> getSubstanceHolonyms() throws WordNetException {
		return getNeighbors(WordNetRelation.SUBSTANCE_HOLONYM);
	}
	
	public Set<Synset> getSubstanceMeronyms() throws WordNetException {
		return getNeighbors(WordNetRelation.SUBSTANCE_MERONYM);
	}
	
	public Set<Synset> getSynonyms() throws WordNetException {
		return getNeighbors(WordNetRelation.SYNONYM);
	}
	
	public long getUsageOf(String word) throws WordNetException {
		// not sure how to get this from MultiWordNet
		return 0;
	}
	
	public Set<Synset> getVerbGroup() throws WordNetException {
		return getNeighbors(WordNetRelation.VERB_GROUP);
	}
	
	public Set<String> getWords() throws WordNetException {
		if (words == null) {
			words = new HashSet<String>();
			Word[] mwnWords = this.realSynset.getWords();
			if (mwnWords != null) {
				for(int i = 0; i < mwnWords.length; i++) {
					// for (org.itc.mwn.Word word : this.realSynset.getWords()) {
					// String lemma = word.getLemma();
					String lemma = mwnWords[i].getLemma();
					lemma = lemma.replaceAll("_", " ");
					words.add(lemma);
				}
			}
		}
		return new HashSet<String>(words);
	}
	
	public List<WordAndUsage> getWordsAndUsages() throws WordNetException {
		// Not sure how to get this from MultiWordNet
		return null;
	}
	
	
	protected Set<Synset> getSetOfSynsets(org.itc.mwn.PointerTarget[] list) {
		if (null==list)
			return null;
		Set<Synset> ret = new HashSet<Synset>();
		
		for (int i=0; i < list.length; i++) {
			if (list[i] != null) {
				// System.out.println(this.getClass().toString() + " word: " + list[i].getDescription());
				ret.add(new JmwnSynset(this.jmwnDictionary,(org.itc.mwn.Synset)list[i]));
			}
		}
	
	return ret;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JmwnSynset other = (JmwnSynset) obj;
		if (realSynset == null) {
			if (other.realSynset != null)
				return false;
			return true;
		} else {
		try {
			if (this.getOffset() == other.getOffset())
				return true;
		} catch (Exception e) {
		System.err.println("Problems checking JMWN synset equality: ");
		e.printStackTrace();
		}
		return false;
	}
	
	// if (!realSynset.equals(other.realSynset))
	// return false;
	// return true;
	}
	
	public LexicographerFileInformation getLexicographerFileInformation() throws WordNetException {
		// TODO Auto-generated method stub
		throw new WordNetException("MultiWordNet does not have lexicographer file information");
	}

}