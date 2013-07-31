/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.wordnet;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.SensedWord;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordnetDictionaryImplementationType;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.ext_jwnl.ExtJwnlUtils;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwi.JwiUtils;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwnl.JwnlUtils;

/**
 * This class defines the interface for a wordnet lexical resource, and holds some of the implementation. The implementation here uses a
 * {@link Dictionary} as the wrapper for wordnet (to pull {@link Synset}s and {@link SensedWord}s). See {@link WordNetDictionaryFactory}
 * to find out or alter which dictionary implementation is used.
 * <p>
 * A Wordnet lexical resource is a {@link LexicalResource} that extracts rules from Wordnet, matching not only lemma+POS tokens, but also wordnet 
 * sense (Synset) numbers (optional), and wordnet relations types (optional).
 * The rules extracted here use {@link WordnetRuleInfo} in their {@link LexicalRule}s, which has a pair of wordnet {@link Synset}s that 
 * match the pair of lemmas+POSs in the {@link LexicalRule},  and also has the {@link WordNetRelation} connecting them.<br>
 * <p>
 * <b>The definition of retrieved {@link LexicalRule}s: </b>The right side (lemmma+POS+synset) is a possible result of applying the 
 * {@link WordNetRelation} on the left (lemma+POS+synset). For example, the query {@link #getRulesForLeft(cat, noun, hypernym)} will
 * return rules like <code>(cat, noun, hypernym, feline, noun)</code>. Also, the query {@link #getRulesForRight(cat, noun, hypernym)}
 * will return rules like <code>(wildcat, noun, hypernym, cat, noun)</code>.
 * <p>
 * The parameter {@link #defaultRelations} - will be used as the  filter of WN-relations whenever a getRules*() method is called without specifying it 
 * explicitly. It's set in the constructor or in {@link #setDefaultRelationSet(Set)}. <br>
 * The retrieved rules can be further filtered to correspond to either the first sense of each lemma+pos term, or all senses. This flag for 
 * senses is set in the constructor or in {@link #setUseFirstSenseOnlyLeft(boolean)} and {@link #setUseFirstSenseOnlyRight(boolean)}.  
 * <p>
 * <b>NOTE</b> In case the user gives a POS that is not supported by wordnet, then the class return a an empty list (not null).
 * <P>
 * Note that some relations do not work due to third party limitations (currently,
 * CATEGORY_MEMBER, TROPONYM and DERIVED might not work. See {@link ExtJwnlUtils},
 * {@link JwnlUtils}, {@link JwiUtils}.
 * 
 * @author Amnon Lotan
 * @since 28/05/2011
 * 
 */
public class WordnetLexicalResource implements LexicalResource<WordnetRuleInfo> 
{
	public static final String PARAM_WN_DIR = "wordnet-dir";
	public static final String PARAM_ONLY_FIRST_LEFT = "useFirstSenseOnlyLeft";
	public static final String PARAM_ONLY_FIRST_RIGHT = "useFirstSenseOnlyRight";
	public static final String PARAM_DEFAULT_RELATIONS = "entailing-relations";
	public static final String PARAM_CHAINING_LENGTH = "wordnet-depth";
	/**
	 * optional parameter
	 */
	public static final String PARAM_WORDNET_DICTIONARY_IMPLEMENTATION_TYPE = "wordnet dictionary implementation type";

	
	/////////////////////////////////////////////// CONSTRUCTORS ////////////////////////////////////////////////////////////////////

	/**
	 * Ctor
	 * @param wnDictionaryDir e.g. "//qa-srv\\Data\\RESOURCES\\WordNet\\3.0\\dict.wn.orig"
	 * @throws LexicalResourceException
	 */
	public WordnetLexicalResource(File wnDictionaryDir) throws LexicalResourceException 
	{
		this(wnDictionaryDir, false, false, null);
	}
	
	/**
	 * Ctor
	 * @param wnDictionaryDir e.g. "d:/data/RESOURCES/WordNet/2.1/dict.snow.400K"
	 * @param defaultRelations may be null (to leave undefined) but not empty
	 * @throws LexicalResourceException
	 */
	public WordnetLexicalResource(File wnDictionaryDir, Set<WordNetRelation> defaultRelations) throws LexicalResourceException 
	{
		this(wnDictionaryDir, false, false, defaultRelations);
	}
	
	/**
	 * Ctor
	 * @param wnDictionaryDir e.g. "d:/data/RESOURCES/WordNet/2.1/dict.snow.400K"
	 * @param useFirstSenseOnlyLeft	if true, only the first sense of each term will be considered. otherwise, all senses will be considered
	 * @param useFirstSenseOnlyRight		if true, only the first sense of each term will be considered. otherwise, all senses will be considered
	 * @throws LexicalResourceException
	 */
	public WordnetLexicalResource(File wnDictionaryDir, boolean useFirstSenseOnlyLeft, boolean useFirstSenseOnlyRight) throws LexicalResourceException 
	{
		this(wnDictionaryDir, useFirstSenseOnlyLeft, useFirstSenseOnlyRight, null);
	}
	
	/**
	 * Ctor
	 * @param wnDictionaryDir e.g. "d:/data/RESOURCES/WordNet/2.1/dict.snow.400K"
	 * @param useFirstSenseOnlyLeft if true, only the first sense of each term will be considered. otherwise, all senses will be considered
	 * @param useFirstSenseOnlyRight if true, only the first sense of each term will be considered. otherwise, all senses will be considered
	 * @param defaultRelations may be null (to leave undefined) but not empty
	 * @throws LexicalResourceException
	 */
	public WordnetLexicalResource(
			File wnDictionaryDir,boolean useFirstSenseOnlyLeft, boolean useFirstSenseOnlyRight, Set<WordNetRelation> defaultRelations) throws LexicalResourceException 
	{
		this(wnDictionaryDir, useFirstSenseOnlyLeft, useFirstSenseOnlyRight, defaultRelations, 1);
	}
	
	/**
	 * Ctor
	 * @param wnDictionaryDir e.g. "d:/data/RESOURCES/WordNet/2.1/dict.snow.400K"
	 * @param useFirstSenseOnlyLeft if true, only the first sense of each term will be considered. otherwise, all senses will be considered
	 * @param useFirstSenseOnlyRight if true, only the first sense of each term will be considered. otherwise, all senses will be considered
	 * @param defaultRelations may be null (to leave undefined) but not empty
	 * @param chainingLength	is the size of transitive relation chaining to be performed on the retrieved rules. E.g. if leftChainingLength = 3, then every
	 * hypernym/hyponym, merornym and holonym query will return rules with words related up to the 3rd degree (that's 1st, 2nd or 3rd) from the original term. Queries
	 * on non transitive relations are unaffected by this parameter. Must be positive.   
	 * @param wordnetDictionaryImplementation The client's choice of underlying {@link Dictionary} implementation. May be null.
	 * @throws LexicalResourceException
	 */
	public WordnetLexicalResource(
			File wnDictionaryDir,boolean useFirstSenseOnlyLeft, boolean useFirstSenseOnlyRight, Set<WordNetRelation> defaultRelations, 
			int chainingLength) throws LexicalResourceException
	{
		this(wnDictionaryDir, useFirstSenseOnlyLeft, useFirstSenseOnlyRight, defaultRelations, chainingLength, null);
	}
	
	/**
	 * Ctor
	 * @param wnDictionaryDir e.g. "d:/data/RESOURCES/WordNet/2.1/dict.snow.400K"
	 * @param useFirstSenseOnlyLeft if true, only the first sense of each term will be considered. otherwise, all senses will be considered
	 * @param useFirstSenseOnlyRight if true, only the first sense of each term will be considered. otherwise, all senses will be considered
	 * @param defaultRelations may be null (to leave undefined) but not empty
	 * @param chainingLength	is the size of transitive relation chaining to be performed on the retrieved rules. E.g. if leftChainingLength = 3, then every
	 * hypernym/hyponym, merornym and holonym query will return rules with words related up to the 3rd degree (that's 1st, 2nd or 3rd) from the original term. Queries
	 * on non transitive relations are unaffected by this parameter. Must be positive.   
	 * @param wordnetDictionaryImplementation The client's choice of underlying {@link Dictionary} implementation. May be null.
	 * @throws LexicalResourceException
	 */
	public WordnetLexicalResource(
			File wnDictionaryDir,boolean useFirstSenseOnlyLeft, boolean useFirstSenseOnlyRight, Set<WordNetRelation> defaultRelations, 
			int chainingLength, WordnetDictionaryImplementationType wordnetDictionaryImplementation) throws LexicalResourceException
	{
		wordnetLexResourceServices = new WordnetLexicalResourceServices(wnDictionaryDir, wordnetDictionaryImplementation); 
		this.useFirstSenseOnlyLeft = useFirstSenseOnlyLeft;
		this.useFirstSenseOnlyRight = useFirstSenseOnlyRight;
		if (defaultRelations != null)	setDefaultRelationSet(defaultRelations); 
		if (chainingLength < 1)
			throw new LexicalResourceException("the left chaining length must be positive. I got " + chainingLength);
		this.chainingLength = chainingLength;
	}
	

	public WordnetLexicalResource(ConfigurationParams params) throws LexicalResourceException, ConfigurationException
	{
		this(
				params.getDirectory(PARAM_WN_DIR),
				params.getBoolean(PARAM_ONLY_FIRST_LEFT),
				params.getBoolean(PARAM_ONLY_FIRST_RIGHT),
				params.getEnumSet(WordNetRelation.class, PARAM_DEFAULT_RELATIONS),
				params.getInt(PARAM_CHAINING_LENGTH),
				params.containsKey(PARAM_WORDNET_DICTIONARY_IMPLEMENTATION_TYPE) ? 
					params.getEnum(WordnetDictionaryImplementationType.class, PARAM_WORDNET_DICTIONARY_IMPLEMENTATION_TYPE) : null);
	}

	///////////////////////////////////////////// PUBLIC ////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.lexical_resource.LexicalResource#close()
	 */
	public void close()
	{
		this.wordnetLexResourceServices.close();
	}

	
	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.LexResource#getRulesForRight(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	public List<LexicalRule<? extends WordnetRuleInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException
	{
		if (defaultRelations == null) throw new LexicalResourceException("No relations set set. You must call setDefaultRelationSet() first");
		return getRulesForRight(lemma, pos, defaultRelations, null);	
	}
	
	/**
	 * Same as {@link #getRulesForRight(String, PartOfSpeech)} with an additional parameter which enables
	 *  passing specifications for the desired rule (e.g. the sense ordinal number of rule sides
	 *  using {@link WordnetRuleInfoWithSenseNumsOnly}).
	 * @param lemma
	 * @param pos
	 * @param info
	 * @return
	 * @throws LexicalResourceException
	 */
	public List<LexicalRule<? extends WordnetRuleInfo>> getRulesForRight(String lemma, PartOfSpeech pos, 
			WordnetRuleInfo info) throws LexicalResourceException
	{
		if (defaultRelations == null) throw new LexicalResourceException("No relations set set. You must call setDefaultRelationSet() first");
		return getRulesForRight(lemma, pos, defaultRelations, info);	
	}
	
	
	/**
	 * Returns the rules matching this specific lemma+pos, using the given relations and {@link WordnetRuleInfo}.<br>
	 * In case the user gives null POS, retrieve rules for all possible POSs.<br>
	 * In case the user gives a POS that is not supported by wordnet, then the class return a an empty list (not null).<br>
	 * In case the user gives null WordnetRuleInfo, uses the boolean parameters given in the constructor.
	 * <p>
	 *  <b>The definition of retrieved {@link LexicalRule}s: </b>The right side (lemmma+POS+synset) is a possible result of applying the 
	 * {@link WordNetRelation} on the left (lemma+POS+synset). 
	 * @param lemma
	 * @param pos
	 * @param relations
	 * @param info
	 * @return
	 * @throws LexicalResourceException 
	 */
	public List<LexicalRule<? extends WordnetRuleInfo>> getRulesForRight(String lemma, PartOfSpeech pos, Set<WordNetRelation> relations,
									WordnetRuleInfo info) throws LexicalResourceException {
		if(info == null){
			int leftSenseNum = useFirstSenseOnlyLeft ? 1 : -1;
			int rightSenseNum = useFirstSenseOnlyRight ? 1 : -1;
			info = new WordnetRuleInfoWithSenseNumsOnly(leftSenseNum, rightSenseNum);
		}
		return wordnetLexResourceServices.getRulesForSide(lemma, pos, relations, info, chainingLength, true);
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.LexicalResource#getRulesForLeft(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	public List<LexicalRule<? extends WordnetRuleInfo>> getRulesForLeft(String lemma,	PartOfSpeech pos) throws LexicalResourceException {
		if (defaultRelations == null) throw new LexicalResourceException("No relations set set. You must call setDefaultRelationSet() first");
		return getRulesForLeft(lemma, pos, defaultRelations, null);	
	}
	
	/**
	 * Same as {@link #getRulesForLeft(String, PartOfSpeech)} with an additional parameter which enables
	 *  passing specifications for the desired rule (e.g. the sense ordinal number of rule sides
	 *  using {@link WordnetRuleInfoWithSenseNumsOnly}).
	 * @param lemma
	 * @param pos
	 * @param info
	 * @return
	 * @throws LexicalResourceException
	 */
	public List<LexicalRule<? extends WordnetRuleInfo>> getRulesForLeft(String lemma, PartOfSpeech pos, 
									WordnetRuleInfo info) throws LexicalResourceException
	{
		if (defaultRelations == null) throw new LexicalResourceException("No relations set set. You must call setDefaultRelationSet() first");
		return getRulesForLeft(lemma, pos, defaultRelations, info);	
	}

	/**
	 * Returns the rules matching this specific lemma+pos, using the given relations and {@link WordnetRuleInfo}.<br>
	 * In case the user gives null POS, retrieve rules for all possible POSs.<br>
	 * In case the user gives a POS that is not supported by wordnet, then the class return a an empty list (not null).<br>
	 * In case the user gives null WordnetRuleInfo, uses the boolean parameters given in the constructor.
	 * <p>
	 *  <b>The definition of retrieved {@link LexicalRule}s: </b>The right side (lemmma+POS+synset) is a possible result of applying the 
	 * {@link WordNetRelation} on the left (lemma+POS+synset). 
	 * @param lemma
	 * @param pos
	 * @param relations
	 * @param info
	 * @return
	 * @throws LexicalResourceException 
	 */
	public List<LexicalRule<? extends WordnetRuleInfo>> getRulesForLeft(String lemma, PartOfSpeech pos, Set<WordNetRelation> relations,
									WordnetRuleInfo info) throws LexicalResourceException {
		if(info == null){
			int leftSenseNum = useFirstSenseOnlyLeft ? 1 : -1;
			int rightSenseNum = useFirstSenseOnlyRight ? 1 : -1;
			info = new WordnetRuleInfoWithSenseNumsOnly(leftSenseNum, rightSenseNum);
		}
		return wordnetLexResourceServices.getRulesForSide(lemma, pos, relations, info, chainingLength, false);
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.LexicalResource#getRules(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech, java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	public List<LexicalRule<? extends WordnetRuleInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos)
			throws LexicalResourceException {
		if (defaultRelations == null) throw new LexicalResourceException("No relations set set. You must call setDefaultRelationSet() first");
		return getRules(leftLemma, leftPos, rightLemma, rightPos, defaultRelations, null);
	}
	
	/**
	 * Same as {@link #getRules(String, PartOfSpeech, String, PartOfSpeech) with an additional parameter which enables
	 *  passing specifications for the desired rule (e.g. the sense ordinal number of rule sides
	 *  using {@link WordnetRuleInfoWithSenseNumsOnly}).
	 * @param lemma
	 * @param pos
	 * @param info
	 * @return
	 * @throws LexicalResourceException
	 */
	public List<LexicalRule<? extends WordnetRuleInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos,
									WordnetRuleInfo info) throws LexicalResourceException {
		if (defaultRelations == null) throw new LexicalResourceException("No relations set set. You must call setDefaultRelationSet() first");
		return getRules(leftLemma, leftPos, rightLemma, rightPos, defaultRelations, info);
	}

	/**
	 * Returns the rule matching this specific pair, using the given relations and {@link WordnetRuleInfo}.<br>
	 * In case the user gives null POS, retrieve rules for all possible POSs.<br>
	 * In case the user gives a POS that is not supported by wordnet, then the class return a an empty list (not null).<br>
	 * In case the user gives null WordnetRuleInfo, uses the boolean parameters given in the constructor.
	 * <p>
	 *  <b>The definition of retrieved {@link LexicalRule}s: </b>The right side (lemmma+POS+synset) is a possible result of applying the 
	 * {@link WordNetRelation} on the left (lemma+POS+synset). 
	 * @param leftLemma
	 * @param leftPos
	 * @param rightLemma
	 * @param rightPos
	 * @param relations
	 * @param info
	 * @return
	 * @throws LexicalResourceException
	 */
	public List<LexicalRule<? extends WordnetRuleInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos, 
			Set<WordNetRelation> relations, WordnetRuleInfo info) throws LexicalResourceException 
	{
		if(info == null){
			int leftSenseNum = useFirstSenseOnlyLeft ? 1 : -1;
			int rightSenseNum = useFirstSenseOnlyRight ? 1 : -1;
			info = new WordnetRuleInfoWithSenseNumsOnly(leftSenseNum, rightSenseNum);
		}
		return wordnetLexResourceServices.getRules(leftLemma, leftPos, rightLemma, rightPos, relations, info, chainingLength);
	}
	
	/**
	 * if true, only the first sense of each term will be considered. otherwise, all senses will be considered
	 * @param useFirstSenseOnlyLeft the useFirstSenseOnlyLeft to set
	 */
	public void setUseFirstSenseOnlyLeft(boolean useFirstSenseOnlyLeft) {
		this.useFirstSenseOnlyLeft = useFirstSenseOnlyLeft;
	}
	
	/**
	 * if true, only the first sense of each term will be considered. otherwise, all senses will be considered
	 * @param useFirstSenseOnlyRight the useFirstSenseOnlyRight to set
	 */
	public void setUseFirstSenseOnlyRight(boolean useFirstSenseOnlyRight) {
		this.useFirstSenseOnlyRight = useFirstSenseOnlyRight;
	}
	
	/**
	 * set the new default set of relations, to be used in all queries that don't specify it explicitly
	 * @param defaultRelations the new default set of relations, to be used in all queries that don't specify it explicitly
	 * @throws LexicalResourceException
	 */
	public void setDefaultRelationSet(Set<WordNetRelation> defaultRelations) throws LexicalResourceException {
		if (defaultRelations == null)
			throw new LexicalResourceException("Got null relation set");
		if (defaultRelations.isEmpty())
			throw new LexicalResourceException("The relations set cannot be empty");
		this.defaultRelations = new LinkedHashSet<WordNetRelation>(defaultRelations);
	}
	
	///////////////////////////////////////////////// PROTECTED + PRIVATE ////////////////////////////////////////////////////////////////////

	protected final WordnetLexicalResourceServices wordnetLexResourceServices;
	protected boolean useFirstSenseOnlyRight;
	protected boolean useFirstSenseOnlyLeft;
	protected Set<WordNetRelation> defaultRelations;
	protected int chainingLength;


}

