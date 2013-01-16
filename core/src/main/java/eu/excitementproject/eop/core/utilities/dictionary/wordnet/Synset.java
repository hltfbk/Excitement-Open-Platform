package eu.excitementproject.eop.core.utilities.dictionary.wordnet;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * A WordNet's synset (a set of synonyms). Synsets are the source and target of semantic relation types (as in {@link WordNetRelation#isLexical()}). 
 * {@link SensedWord}s are the source and targets of other, lexical, relations.
 * <p>
 * Implementations must be immutable and implement equals() and hashCode() 
 * @author Asher Stern
 * @see	http://wordnet.princeton.edu/wordnet/man/wninput.5WN.html
 */
public interface Synset extends Serializable
{
	public Set<String> getWords() throws WordNetException;
	public List<WordAndUsage> getWordsAndUsages() throws WordNetException;
	public String getGloss() throws WordNetException;
	public WordNetPartOfSpeech getPartOfSpeech() throws WordNetException;
	
	/**
	 * Get the direct neighbor Synsets connected by the given relation type. If none exist, an empty Set is returned.<br>
	 * Recall that only semantic relation types link to synonyms. Giving other, lexical, relation types will return an empty set. 
	 * @param relationType
	 * @return
	 * @throws SensedException
	 */
	public Set<Synset> getNeighbors(WordNetRelation relationType) throws WordNetException;
	
	/**
	 * Get the direct and indirect related Synsets connected by the given relation type, via a chain of up to #chainingLength edges. 
	 * If none exist, an empty Set is returned.<br>
	 * Recall that only semantic relation types link to synsets. Giving other, lexical, relation types will return an empty set.<br>
	 * Also, most relations are not transitive and make no sense when chained. Therefore the chainingLength will be ignored for them. 
	 * See {@link WordNetRelation#isTransitive()}
	 * 
	 * @param relation
	 * @param chainingLength	is the size of transitive relation chaining to be performed on the retrieved rules. E.g. if leftChainingLength = 3, then every
	 * hypernym/hyponym, merornym and holonym query will return rules with words related up to the 3rd degree (that's 1st, 2nd or 3rd) from the original term. Queries
	 * on non transitive relations are unaffected by this parameter. Must be positive.
	 * @return
	 * @throws WordNetException 
	 */
	public Set<Synset> getRelatedSynsets(WordNetRelation relation, int chainingLength) throws WordNetException;
	
	//
	// there is one getter for each semantic WordNetRelation enum type
	//
	
	public Set<Synset> getAttributes() throws WordNetException;
	public Set<Synset> getCauses() throws WordNetException;
	public Set<Synset> getHypernyms() throws WordNetException;
	public Set<Synset> getHyponyms() throws WordNetException;
	public Set<Synset> getEntailments() throws WordNetException;
	public Set<Synset> getHolonyms() throws WordNetException;
	public Set<Synset> getMemberHolonyms() throws WordNetException;
	public Set<Synset> getMemberMeronyms() throws WordNetException;
	public Set<Synset> getMeronyms() throws WordNetException;
	public Set<Synset> getPartHolonyms() throws WordNetException;
	public Set<Synset> getPartMeronyms() throws WordNetException;
	public Set<Synset> getSubstanceHolonyms() throws WordNetException;
	public Set<Synset> getSubstanceMeronyms() throws WordNetException;
	/**
	 * a synset has no synonymous synsets, but itself 
	 * @return this synset
	 * @throws WordNetException
	 */
	public Set<Synset> getSynonyms() throws WordNetException;			
	public Set<Synset> getVerbGroup() throws WordNetException;
	
	public long getUsageOf(String word) throws WordNetException;
	
	/**
	 * Retrieve a list of all the {@link SensedWord}s in this synset. one for each word. 
	 * @return
	 * @throws WordNetException 
	 */
	public List<SensedWord> getAllSensedWords() throws WordNetException;
	
	/**
	 * 
	 * @return the offset (database location on the Web search) of the synset
	 */
	public long getOffset() throws WordNetException;
	
	/**
	 * Returns information about the lexicographer file of the synset.<BR>
	 * See http://wordnet.princeton.edu/wordnet/man/lexnames.5WN.html<BR>
	 * See also the file "lexnames" in the wordnet=dictionary directory.
	 * 
	 * @return Information about the wordnet lexicographer file of this synset.
	 * @throws WordNetException
	 */
	public LexicographerFileInformation getLexicographerFileInformation() throws WordNetException;
}
