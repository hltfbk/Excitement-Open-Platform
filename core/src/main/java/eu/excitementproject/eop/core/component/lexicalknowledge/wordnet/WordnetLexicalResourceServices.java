package eu.excitementproject.eop.core.component.lexicalknowledge.wordnet;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.EmptySynset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.SensedWord;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetInitializationException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordnetDictionaryImplementationType;

/**
 * This class inherits its interface and implementation-outline from {@link WordnetLexicalResource}, and completes the implementation.  
 * <p>
 * A Wordnet lexical resource is a {@link LexicalResource} that extracts rules from Wordnet, matching not only lemma+POS tokens, but also wordnet 
 * Synset numbers (optional)and wordnet relations types (optional).
 * The rules extracted here use {@link WordnetRuleInfo} in their {@link LexicalRule}s, which has a pair of wordnet {@link Synset}s and the 
 * {@link WordNetRelation} connecting them. 
 * <p>
 * <b>The definition of retrieved {@link LexicalRule}s: </b>The right side (lemmma+POS+synset) is a possible result of applying the 
 * {@link WordNetRelation} on the left (lemma+POS+synset). For example, the query {@link #getRulesForLeft(cat, noun, hypernym)} will
 * return rules like <code>(cat, noun, hypernym, feline, noun)</code>. Also, the query {@link #getRulesForRight(cat, noun, hypernym)}
 * will return rules like <code>(wildcat, noun, hypernym, cat, noun)</code>.
 * <p>
 * {@link #defaultRelations} - will be used as the  filter of WN-relations whenever a getRules*() method is called without specifying it 
 * explicitly. It's set in the constructor or in {@link #setRelationSet(Set)}. <br>
 * {@link #defaultLSynset} and {@link #defaultRSynset} will be used as the left Synset and right Synset whenever a getRules*() method is called
 * without specifying them explicitly. Note they're 0-based. They're set in the constructor or in {@link #setDefaultLSynset(int)} and 
 * {@link #setDefaultRSynset(int)}. 
 * <p>
 * Note that here <i>sense</i> means a wordnet<i> Synset number</i>, not the interface <i>Sense</i> 
 * 
 * @author Amnon Lotan
 * @since 19/06/2011
 * 
 */
public class WordnetLexicalResourceServices {

	private static final String RESOURCE_NAME = "WORDNET";
	private static final WordNetRelation SYNONYM = WordNetRelation.SYNONYM;
	private static final WordNetPartOfSpeech[] ARRAY_OF_ONE_POS = new WordNetPartOfSpeech[1];
	private static final WordNetPartOfSpeech[] EMPTY_ARRAY_OF_POS = new WordNetPartOfSpeech[0];
	private Dictionary dictionary;

	/**
	 * Ctor
	 * @param wnDictionaryDir e.g. "d:/data/RESOURCES/WordNet/2.1/dict.snow.400K"
	 * @param wordnetDictionaryImplementation The client's choice of underlying {@link Dictionary} implementation. May be null.
	 * @throws LexicalResourceException
	 */
	public WordnetLexicalResourceServices(File wnDictionaryDir, WordnetDictionaryImplementationType wordnetDictionaryImplementation)	throws LexicalResourceException {
		if (!wnDictionaryDir.exists())
			throw new LexicalResourceException(wnDictionaryDir + " doesn't exist");
		if (!wnDictionaryDir.isDirectory())
			throw new LexicalResourceException(wnDictionaryDir + " isn't a directory");
		
		try 										{	dictionary =  WordNetDictionaryFactory.newDictionary(wnDictionaryDir, wordnetDictionaryImplementation);	}
		catch (WordNetInitializationException e) 	{	throw new LexicalResourceException("faild to initialize this wordnet resource at " + wnDictionaryDir, e);}
	}
	
	///////////////////////////////////////////////////////////////// PACKAGE VISIBlE ///////////////////////////////////////////////////////////

	void close()
	{
		this.dictionary.close();
	}
	
	/**
	 * Return all the rules matching the given lemma+pos of both sides of the rule, filtered to the given relations and info. The rules are between 1 and 
	 * #chainingLength WN edges long.
	 * 
	 * @param lLemma
	 * @param lPos
	 * @param rLemma
	 * @param rPos
	 * @param relations
	 * @param info
	 * @param chainingLength
	 * @return
	 * @throws LexicalResourceException
	 */
	List<LexicalRule<? extends WordnetRuleInfo>> getRules(String lLemma, PartOfSpeech lPos, String rLemma, 
			PartOfSpeech rPos, Set<WordNetRelation> relations, WordnetRuleInfo info, int chainingLength)	throws LexicalResourceException
	{
		// sanity
		checkRelationsBeforeUse(relations);
		checkInfo(info);
		if (lLemma == null)
			throw new LexicalResourceException("got null lLemma");
		if (rLemma == null)
			throw new LexicalResourceException("got null rLemma");
		
		List<LexicalRule<? extends WordnetRuleInfo>> rules = new Vector<LexicalRule<? extends WordnetRuleInfo>>();
		// carefully pull the two synsets
		List<Synset> lSelectSynsets = pullSynsets(lLemma, lPos, info.getLeftSenseNo());
		List<Synset> rSelectSynsets = pullSynsets(rLemma, rPos, info.getRightSenseNo());

		try {	
			for (int lSynsetNo = 1; lSynsetNo <= lSelectSynsets.size()  ; lSynsetNo++ )
			{
				Synset lSynset = lSelectSynsets.get(lSynsetNo-1);

				for (int rSynsetNo = 1; rSynsetNo <= rSelectSynsets.size()  ; rSynsetNo++ )
				{
					Synset rSynset = rSelectSynsets.get(rSynsetNo-1);

					for (WordNetRelation relation : relations)
					{
						Set<Synset> neighbors = getSemanticOrLexicalNeighbors(lLemma, lSynset, relation, chainingLength);
						if (neighbors.contains(rSynset)	|| 								// usually, the relation connects between neighboring synsets
								doubleCheckContains(neighbors, rSynset) ||
								(relation.equals(SYNONYM) && lSynset.equals(rSynset) ))	// special condition for SYNONYMs, which are just words within a Synset
						{
							// just in case the given rPos or lPos are null, replace them with the POSs from the synsets
							PartOfSpeech concreteLPos = lSynset.getPartOfSpeech().toPartOfSpeech();
							PartOfSpeech concreteRPos = rSynset.getPartOfSpeech().toPartOfSpeech();
							rules.add(new LexicalRule<WordnetRuleInfo>(lLemma, concreteLPos, rLemma, concreteRPos, relation.toString(), RESOURCE_NAME, 
									new WordnetRuleInfo(lSynset, lSynsetNo, rSynset, rSynsetNo, relation)));
						}
					}
				}
			}
		} catch (WordNetException e) {
			throw new LexicalResourceException("An error occured while loading the neighbors of a synset of <" + lLemma + ", " + lPos + "> :", e);
		}
		
		return rules;
	}
	
	/**
	 * Return all the rules matching the given lemma+pos on one side of the rule, filtered to the given relations and info.
	 * 
	 * @param lemma
	 * @param pos
	 * @param relations
	 * @param info
	 * @param chainingLength
	 * @param isFromRight
	 * @return
	 * @throws LexicalResourceException
	 */
	List<LexicalRule<? extends WordnetRuleInfo>> getRulesForSide(String lemma, PartOfSpeech pos,
			Set<WordNetRelation> relations, WordnetRuleInfo info, int chainingLength, boolean isFromRight) throws LexicalResourceException {
		// sanity
		checkRelationsBeforeUse(relations);
		checkInfo(info);
		
		List<LexicalRule<? extends WordnetRuleInfo>> rulesList = new ArrayList<LexicalRule<? extends WordnetRuleInfo>>();	// use Set to eliminate duplicate rules
		
		
		int sourceSenseNum, targetSenseNum;
		if(isFromRight){
			sourceSenseNum = info.getRightSenseNo();
			targetSenseNum = info.getLeftSenseNo();
		}else{
			sourceSenseNum = info.getLeftSenseNo();
			targetSenseNum = info.getRightSenseNo();
		}
		// carefully pull the synset
		List<Synset> synsets = pullSynsets(lemma, pos, sourceSenseNum);

		try {
			for (int synsetNo = 1; synsetNo <= synsets.size()  ; synsetNo++ )
			{
				Synset synset = synsets.get(synsetNo-1);
				for (WordNetRelation relation : relations)
				{
					if (relation.equals(SYNONYM))
						rulesList.addAll(getSynonymRules(synset, synsetNo, lemma, isFromRight, targetSenseNum));
					else
					{
						// when querying words on the right, look to the left, using the symmetric relation
						WordNetRelation relationToQuery = isFromRight ? relation.getSymmetricRelation() : relation;

						try {
							if (relationToQuery != null)	//	getSymmetricRelation() may return null
								for (SensedWord neighborSensedWord :  getSemanticOrLexicalNeighborSensedWords(lemma, synset, relationToQuery, chainingLength))
								{
									// construct a rule from all this data we now have 
									Synset neighborSynset = neighborSensedWord.getSynset();
									PartOfSpeech originPos;
									PartOfSpeech neighborPos;
									try {	
										originPos 	= synset.getPartOfSpeech().toPartOfSpeech();
										neighborPos =  neighborSynset.getPartOfSpeech().toPartOfSpeech();	
									}	catch ( WordNetException e) {	throw new LexicalResourceException("Bug! this POS from wordnet '" + neighborSynset.getPartOfSpeech() + "' isn't valid");	}
									String neighborLemma = neighborSensedWord.getWord();
									
									boolean addThisRule = true;
									if (targetSenseNum != -1)
									{
										addThisRule = isCorrectSenseOfGivenWord(neighborSynset, neighborLemma, neighborSynset.getPartOfSpeech(), targetSenseNum);
									}
									
									if (addThisRule)
									{
										LexicalRule<WordnetRuleInfo> rule = newDirectedRule(neighborLemma, neighborPos,lemma, originPos, relation, neighborSynset,
												synset, synsetNo, isFromRight);
										if (!rulesList.contains(rule))
											rulesList.add( rule );
									}
								}
						} catch (WordNetException e) {
							throw new LexicalResourceException("wordnet error occured, see nested", e);
						}
					}
				}
			}
		} catch (LexicalResourceException e) {
			throw new LexicalResourceException("An error occured while extracting the neighbors for <" + lemma + ", " + pos + ">", e);
		}
		
		return rulesList;
	}
	
	/**
	 * Get the ordinal sense number of a lamma+pos with a given synset offset
	 * @param lemma
	 * @param pos
	 * @param synsetOffset
	 * @return ordinal sense number of the input term
	 * @throws LexicalResourceException if the given offset did not match any 
	 * of the offsets found for the given lemma and part of speech.<br> 
	 * The exception is also thrown when a synset does not have an offset value (as in {@link EmptySynset}). 
	 */
	public int getSenseNo(String lemma, PartOfSpeech pos, long synsetOffset) throws LexicalResourceException{
		final int ALL_SENSES = -1;
		List<Synset> synsets = pullSynsets(lemma, pos, ALL_SENSES);
		for (int synsetNo = 1; synsetNo <= synsets.size()  ; synsetNo++ ){
			Synset synset = synsets.get(synsetNo-1);
			try {
				if(synset.getOffset() == synsetOffset){
					return synsetNo;
				}
			} catch (WordNetException e) {
				throw new LexicalResourceException("Nested exception while getting " +
						"the ordinal sense number of "+lemma+" "+pos+" "+synsetOffset,e);
			}
		}
		throw new LexicalResourceException("The given offset "+synsetOffset+" " +
				"did not match any of those of the given lemma and part of speech "+lemma+" "+pos);
	}

	////////////////////////////////////////////////////////////////	PRIVATE	///////////////////////////////////////////////////////////
	

	private boolean isCorrectSenseOfGivenWord(Synset givenSynset, String word, WordNetPartOfSpeech pos, int senseOrdinal) throws WordNetException
	{
		boolean ret = false;
		List<Synset> synsetsOfGivenWord = dictionary.getSortedSynsetsOf(word, pos);
		if (synsetsOfGivenWord.size()<1)
		{
			ret = false;
		}
		else
		{
			Synset correctSenseofGivenWord = synsetsOfGivenWord.get(senseOrdinal-1);
			ret = correctSenseofGivenWord.equals(givenSynset);
		}
		return ret;
	}
	
	/**
	 * If relation is lexical, return the {@link SensedWord}s that are directly related to the given <lemma, synset>. If relation is semantic,
	 * return a {@link SensedWord} for each word in the synsets related to the given synset, within #chainingLength steps away.
	 *  
	 * @param lemma
	 * @param synset
	 * @param relation
	 * @param chainingLength 	is the size of transitive relation chaining to be performed on the retrieved rules. E.g. if chainingLength = 3, then every
	 * hypernym/hyponym, merornym and holonym query will return rules with words related up to the 3rd degree (that's 1st, 2nd or 3rd) from the original term. Queries
	 * on non transitive relations are unaffected by this parameter. Must be positive.
	 * @return
	 * @throws WordNetException 
	 */
	private Set<SensedWord> getSemanticOrLexicalNeighborSensedWords(String lemma, Synset synset, WordNetRelation relation, int chainingLength) throws WordNetException {
		Set<SensedWord> relatedSensedWords;
		if (relation.isLexical())
		{
			SensedWord sensedWord = dictionary.getSensedWord(lemma, synset);
			relatedSensedWords = sensedWord.getNeighborSensedWords(relation);
		}
		else
		{
			relatedSensedWords = new LinkedHashSet<SensedWord>();
			Set<Synset> relatedSynsets = synset.getRelatedSynsets(relation, chainingLength);	//getNeighbors(relation);
			for (Synset relatedSynset : relatedSynsets)
				relatedSensedWords.addAll(relatedSynset.getAllSensedWords());
		}
		return relatedSensedWords;
	}

	/**
	 * return the synsets that are either the semantically related synsets, or the synsets containing the lexically related words. If the relation is semantic, 
	 * these synsets can be from 1 to #chainingLength WN steps away from the given synset.
	 * 
	 * @param lemma
	 * @param synset
	 * @param relation
	 * @param chainingLength 	is the size of transitive relation chaining to be performed on the retrieved rules. E.g. if chainingLength = 3, then every
	 * hypernym/hyponym, merornym and holonym query will return rules with words related up to the 3rd degree (that's 1st, 2nd or 3rd) from the original term. Queries
	 * on non transitive relations are unaffected by this parameter. Must be positive.
	 * @return
	 * @throws WordNetException 
	 */
	private Set<Synset> getSemanticOrLexicalNeighbors(String lemma, Synset synset, WordNetRelation relation, int chainingLength) throws WordNetException {
		Set<Synset> synsets;
		
		if (relation.isLexical())
		{
			SensedWord sensedWord = dictionary.getSensedWord(lemma, synset);
			synsets = new LinkedHashSet<Synset>();
			for (SensedWord aSensedWord : sensedWord.getNeighborSensedWords(relation)) {
				synsets.add(aSensedWord.getSynset());
			}
		} else { 
			synsets = synset.getRelatedSynsets(relation, chainingLength);
		}
		return synsets;
	}
	
	/**
	 * Find the ordinal of targetSynset, among the lemma's synsets 
	 * @param lemma
	 * @param pos 
	 * @param targetSynset
	 * @return
	 * @throws LexicalResourceException 
	 * @throws WordNetException 
	 */
	private int getSynsetNo(String lemma, PartOfSpeech pos, Synset targetSynset) throws LexicalResourceException {
		int synsetNo = 1;
		WordNetPartOfSpeech wnPos = null;
		try {
			wnPos = WordNetPartOfSpeech.toWordNetPartOfspeech(pos);
			if (wnPos != null)
				for (Synset synset : dictionary.getSortedSynsetsOf(lemma, wnPos))
					if (synset.equals(targetSynset))
						return synsetNo;
					else
						synsetNo++;
		} catch (WordNetException e) {
			throw new LexicalResourceException("Error pulling the synsets of <"+lemma+", "+pos+">", e);
		}

		throw new LexicalResourceException("Internal error. could not find the following synset in the synset-set of <"+lemma+", "+wnPos+">: " + targetSynset);
	}


	/**
	 * @param synset
	 * @param synsetNo
	 * @param baseLemma
	 * @param isNotCrossed
	 * @param targetSenseNum
	 * @return
	 * @throws LexicalResourceException
	 */
	private Set<LexicalRule<WordnetRuleInfo>> getSynonymRules(Synset synset, int synsetNo, String baseLemma, boolean isNotCrossed, int targetSenseNum)
			throws LexicalResourceException 
	{
		Set<LexicalRule<WordnetRuleInfo>> rules = new LinkedHashSet<LexicalRule<WordnetRuleInfo>>();
		try {
			for (String lemma : synset.getWords())
				if (!lemma.equalsIgnoreCase(baseLemma))		// filter out the one rule from the baseLemma to itself
				{
					boolean addThisRule = true;
					if (targetSenseNum != -1)
					{
						addThisRule = isCorrectSenseOfGivenWord(synset, lemma, synset.getPartOfSpeech(), targetSenseNum);
					}
					if (addThisRule)
					{
						BySimplerCanonicalPartOfSpeech pos = synset.getPartOfSpeech().toPartOfSpeech();
						rules.add(newDirectedRule(lemma, pos, baseLemma, pos, SYNONYM, synset, synset, synsetNo, isNotCrossed));
					}
				}
		} catch (WordNetException e) {
			throw new LexicalResourceException("wordnet error, see nested", e);
		}
		return rules;
	}
	
	/**
	 * @param lemma1
	 * @param pos1
	 * @param lemma2
	 * @param pos2
	 * @param relation
	 * @param synset1
	 * @param synset2 
	 * @param isNotCrossed
	 * @param synset2No 
	 * @return
	 * @throws LexicalResourceException 
	 */
	private LexicalRule<WordnetRuleInfo> newDirectedRule(	String lemma1, PartOfSpeech pos1, String lemma2, 
			PartOfSpeech pos2, WordNetRelation relation, Synset synset1, Synset synset2, int synset2No, boolean isNotCrossed  ) throws LexicalResourceException {
		int synset1No = getSynsetNo(lemma1, pos1, synset1);
		return isNotCrossed ?
				new LexicalRule<WordnetRuleInfo>(lemma1, pos1, lemma2, pos2, relation.toString(), RESOURCE_NAME, new WordnetRuleInfo(synset1, synset1No, synset2, synset2No, relation))
					: 
				new LexicalRule<WordnetRuleInfo>(lemma2, pos2, lemma1, pos1, relation.toString(), RESOURCE_NAME, new WordnetRuleInfo(synset2, synset2No, synset1, synset1No, relation));
	}
	

	/**
	 * @param relations
	 * @throws LexicalResourceException 
	 */
	private void checkRelationsBeforeUse(Set<WordNetRelation> relations) throws LexicalResourceException {
		if (relations == null)
			throw new LexicalResourceException("The relations set is null. You must first call setDefaultRelationSet(), " +
					"or use a getRules*() method that explicitly sets the relations");
	}
	
	private void checkInfo(WordnetRuleInfo info) throws LexicalResourceException {
		if (info == null)
			throw new LexicalResourceException("got null info");
		if ((info.getLeftSenseNo() != -1 && info.getLeftSenseNo() < 1) || 
				(info.getRightSenseNo() != -1 && info.getRightSenseNo() < 1))
			throw new LexicalResourceException("one of the sense ordinal in the input WordnetRuleInfo is not valid, got "+
					info.getLeftSenseNo() +" for left and for right "+ info.getRightSenseNo());
	}
	
	/**
	 * carefully pull the synsets
	 * @param lemma
	 * @param genericPos
	 * @param senseOrdinalNum
	 * @return
	 * @throws LexicalResourceException 
	 */
	private List<Synset> pullSynsets(String lemma,	PartOfSpeech genericPos, int senseOrdinalNum) throws LexicalResourceException {

		WordNetPartOfSpeech[] poss = toWordNetPartOfspeech(genericPos);
		
		List<Synset> selectSynsets = new Vector<Synset>();
		try {
			for (WordNetPartOfSpeech pos : poss)
			{
				List<Synset> allSynsets = dictionary.getSortedSynsetsOf(lemma, pos);
				if (senseOrdinalNum != -1)
				{
					if (senseOrdinalNum <= allSynsets.size())
					{
						selectSynsets.add(allSynsets.get(senseOrdinalNum - 1)); //the 1st sense is in index 0 of the list
					}
				}
				else 
					selectSynsets.addAll(allSynsets);
			}
		}
		catch (WordNetException e) {	throw new LexicalResourceException("An error occured while extracting the synsets for <" + lemma + ", " + genericPos + ">, or while loading its neighbors", e);	}
		
		return selectSynsets;
	}
	
	/**
	 * return the list of WN POSs corresponding to  the given generic POS. may return an empty list
	 * @param genericPos
	 * @return
	 * @throws LexicalResourceException 
	 */
	private WordNetPartOfSpeech[] toWordNetPartOfspeech(PartOfSpeech genericPos) throws LexicalResourceException 
	{
		WordNetPartOfSpeech[] poss;
		if (genericPos == null)
			poss = WordNetPartOfSpeech.values();	// null is a wildcard POS
		else
		{
			WordNetPartOfSpeech wnPos = WordNetPartOfSpeech.toWordNetPartOfspeech(genericPos);
			if (wnPos != null)
			{
				poss = ARRAY_OF_ONE_POS;
				poss[0] = wnPos; 
			}
			else
				poss = EMPTY_ARRAY_OF_POS;
		}
		return poss;
	}
	
	private boolean doubleCheckContains(Set<Synset> set, Synset s) {
		if (s != null) {
			if (set.contains(s)) 
				return true;
		
			if (set != null && !set.isEmpty()) {
				for(Synset x: set) {
					if (s.equals(x))
						return true;
				}
			}
		}
		return false;
	}
	
}
