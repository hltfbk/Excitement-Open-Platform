package eu.excitementproject.eop.core.component.lexicalknowledge.wiktionary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryDictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryEntry;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryException;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryRelation;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionarySense;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.jwktl.JwktlDictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.jwktl.JwktlException;

/**
 * A class of package-visible service methods that use {@link WktDictionary} for {@link WiktionaryLexicalResource}
 * @author Amnon Lotan
 * @since 24/06/2011
 * 
 */
public class WiktionaryLexicalResourceServices {
	
	private final String RESOURCE_NAME;
	private WiktionaryDictionary dictionary;
	
	/**
	 * Ctor
	 * @param wiktionaryDir e.g. "\\\\nlp-srv/data/RESOURCES/Wiktionary/parse"
	 * @param posTaggerModelFile e.g. "b:/jars/stanford-postagger-2008-09-28/bidirectional-wsj-0-18.tagger"
	 * @throws LexicalResourceException 
	 */
	WiktionaryLexicalResourceServices(String wiktionaryDir, String posTaggerModelFile, String resourceName) throws LexicalResourceException {
		if (wiktionaryDir == null)
			throw new LexicalResourceException("wiktionaryDir is null");
		if (resourceName == null || (resourceName.length() == 0) )
			throw new LexicalResourceException("resourceName is null/empty");
		try {
			this.dictionary = new JwktlDictionary(wiktionaryDir, posTaggerModelFile);
		} catch (JwktlException e) {
			throw new LexicalResourceException("Error constructing the JwktlDictionary", e);
		}
		
		this.RESOURCE_NAME = resourceName;
	}

	/**
	 * Returns the rules matching this specific pair of lemmas, and the given sense number, using any of the given relations. Since Wiktionary
	 * Rules only have a Sense on one side, the given senseNum is interpreted to be on the side specified by senseIsOnTheRight.
	 * 
	 * @param lLemma
	 * @param pos
	 * @param rLemma
	 * @param senseNum 0-based index into the <lemma, pos> term's synsets
	 * @param useEntryData
	 * @param relations
	 * @param senseIsOnTheRight
	 * @return
	 * @throws LexicalResourceException
	 */
	List<LexicalRule<? extends WiktionaryRuleInfo>> getRulesWithOneSidedSense(String lLemma, PartOfSpeech pos,  String rLemma, 
			int senseNum, boolean useEntryData, Set<WiktionaryRelation> relations, boolean senseIsOnTheRight) throws LexicalResourceException
	{
		// carefully pull the left/right sense
		String originLemma = senseIsOnTheRight ? rLemma : lLemma;
		String targetLemma = senseIsOnTheRight ? lLemma : rLemma;
		
		List<WiktionarySense> senses = pullSenses(originLemma, pos, senseNum, useEntryData);

		List<LexicalRule<? extends WiktionaryRuleInfo>> rules = new Vector<LexicalRule<? extends WiktionaryRuleInfo>>();

		for (WiktionarySense sense : senses)
		{
			// make a rule for each meaningful relation
			for (WiktionaryRelation relation : relations)
			{
				if (senseIsOnTheRight)	// right-to-left relations are symmetric to left-to-right relations
					relation = relation.getSymmetricRelation();	
				if (relation != null)	// but getSymmetricRelation() isn't comprehensive and may return null!
				{
					ImmutableList<String> relatedWords;
					try {	relatedWords = sense.getRelatedWords(relation);		} 
					catch (WiktionaryException e) { 	throw new LexicalResourceException("Error in getRelatedWords for " + sense + " and for " + relation , e); }
					if (relation != null && relatedWords.contains(targetLemma))
					{
						if (senseIsOnTheRight)
							relation = relation.getSymmetricRelation();		// this is safe cos it cannot return null the second time around
						WiktionaryRuleInfo ruleInfo = senseIsOnTheRight ? 
								// if senseIsOnTheRight then flip the relation again, cos the rule always points from left to right
								WiktionaryRuleInfo.newRightSenseWktRuleInfo(sense, relation) 
							: 
								WiktionaryRuleInfo.newLeftSenseWktRuleInfo(sense, relation);
								
						PartOfSpeech posOfRule;
						try {	posOfRule = sense.getWiktionaryPartOfSpeech().toPartOfSpeech();	}
						catch (WiktionaryException e) {	throw new LexicalResourceException("error pulling the part of speech of this sense: " + sense, e);	}
						rules.add(new LexicalRule<WiktionaryRuleInfo>(lLemma, posOfRule, rLemma, posOfRule, relation.name(), RESOURCE_NAME, ruleInfo));
					}
				}
			}
		}
		return rules;
	}
	
	/**
	 * Retrieve rules that match any of the given relations, along with the given <lemma, pos, senses> tuple on either the left/tail side 
	 * or on the right/head side. 'isRight' flag determines the side of the tuple. If useEntryData is lit, return also rules matching the 
	 * <lemma, pos> entry and relations as well (not sense specific).
	 *  
	 * @param lemma
	 * @param pos
	 * @param senseNum
	 * @param relations
	 * @param useEntryData
	 * @param isRight
	 * @return
	 * @throws LexicalResourceException
	 */
	List<LexicalRule<? extends WiktionaryRuleInfo>> getRulesForSide(String lemma, PartOfSpeech pos, int senseNum, 
			Set<WiktionaryRelation> relations, boolean useEntryData, boolean isRight) throws LexicalResourceException {
		// carefully pull the senses
		List<WiktionarySense> senses = pullSenses(lemma, pos, senseNum, useEntryData);
		
		return  getRulesForSide(lemma, pos, relations, senses, isRight);		
	}
	

	

//	/**
//	 * @param relations
//	 * @throws LexResourceException 
//	 */
//	private void checkRelationsBeforeUse(Set<WiktionaryRelation> relations) throws LexResourceException {
//		if (relations == null)
//			throw new LexResourceException("The relations set is null. You must first call setDefaultRelationSet(), " +
//					"or use a getRules*() method that explicitly sets the relations");
//	}
	
	/**
	 * Consolidate rules that differ only in one having no right-sense and the other no left-sense. Return the given rules where said pairs are
	 * consolidated into one, with two {@link WiktionarySense}s
	 * @param rules
	 * @return
	 * @throws LexicalResourceException 
	 */
	static List<LexicalRule<? extends WiktionaryRuleInfo>> consolidateWktRules( List<LexicalRule<? extends WiktionaryRuleInfo>> rules) 
		throws LexicalResourceException {
		
		// build a map from rule keys to rule-infos
		Map<String, LexicalRule<? extends WiktionaryRuleInfo>> mapKeyToRuleInfo = 
			new HashMap<String, LexicalRule<? extends WiktionaryRuleInfo>>(rules.size());
		for (LexicalRule<? extends WiktionaryRuleInfo> rule : rules)
		{
			String key = rule.getLLemma()+","+rule.getLPos()+","+rule.getRLemma()+","+rule.getRPos()+","+rule.getRelation();
			if (!mapKeyToRuleInfo.containsKey(key))
				mapKeyToRuleInfo.put(key, rule);
			else
			{
				// consolidate the two rules
				WiktionaryRuleInfo ruleInfo1 = mapKeyToRuleInfo.get(key).getInfo();
				WiktionaryRuleInfo ruleInfo2 = rule.getInfo();
				// check if the two rules together contain right+left senses (or otherwise both have a sense on the same side)
				if ((ruleInfo1.getLeftSense() != null) && (ruleInfo2.getLeftSense() != null) ||
					(ruleInfo1.getRightSense() != null) && (ruleInfo2.getRightSense() != null))
					// 27.11.11 commented this exception, cos it's common to pull two rules that are identical except for one having the WktEntry as the Sense, and the other
					// having one of the WktEntry's WiktionarySenses as the Sense
//					throw new LexicalResourceException("Anomaly: there are two rules between the same two terms and relation, but apparently with " +
//							"different sets of senses: " + rule + ", and " + mapKeyToRuleInfo.get(key));
					;
				else
				{
					WiktionarySense lSense;
					WiktionarySense rSense;
					if (ruleInfo1.getLeftSense() != null)
					{
						lSense = ruleInfo1.getLeftSense();
						rSense = ruleInfo2.getRightSense();
					}
					else
					{
						lSense = ruleInfo2.getLeftSense();
						rSense = ruleInfo1.getRightSense();
					}

					WiktionaryRuleInfo consolidatedRuleInfo = WiktionaryRuleInfo.newWktRuleInfo(lSense, rSense, rule.getInfo().getTypedRelation());
					// keep the consolidatedRuleInfo to detect anomaly exceptions (above) with other rules
					mapKeyToRuleInfo.put(key, new LexicalRule<WiktionaryRuleInfo>(rule.getLLemma(), rule.getLPos(), 
							rule.getRLemma(), rule.getRPos(), rule.getRelation(), rule.getResourceName(), consolidatedRuleInfo));
				}
			}
		}
		return new Vector<LexicalRule<? extends WiktionaryRuleInfo>>(mapKeyToRuleInfo.values());
	}
	
	///////////////////////////////////////////////////////// PRIVATE	/////////////////////////////////////////////////////
	
	/**
	 * Retrieve rules that match any of the given relations, along with the given <lemma, pos, senses> tuple on either the left/tail side 
	 * or on the right/head side. 'isRight' flag determines the side of the tuple. 
	 *  
	 * @param lemma
	 * @param pos
	 * @param relations
	 * @param senses
	 * @param useEntryData
	 * @param isRight
	 * @return
	 * @throws LexicalResourceException
	 */
	private List<LexicalRule<? extends WiktionaryRuleInfo>> getRulesForSide(String lemma, PartOfSpeech pos, 
			Set<WiktionaryRelation> relations, List<WiktionarySense> senses, boolean isRight) throws LexicalResourceException {
		Set<LexicalRule<? extends WiktionaryRuleInfo>> rulesSet = new HashSet<LexicalRule<? extends WiktionaryRuleInfo>>();	// use Set to eliminate duplicates
		for (WiktionarySense sense : senses)	
			for (WiktionaryRelation relation : relations)
			{
				if (isRight)
					relation = relation.getSymmetricRelation();	// right-to-left relations are symmetric to left-to-right relations
				if (relation != null)
					try {
						for (String neighbor : sense.getRelatedWords(relation))
							if (!lemma.equals(neighbor))	// don't retrieve reflexive rules
								rulesSet.add( isRight ?
										// if isRight then flip the relation again, cos the rule always points from left to right 
										new LexicalRule<WiktionaryRuleInfo>( neighbor, pos, lemma, pos, relation.getSymmetricRelation().name(),
												RESOURCE_NAME, WiktionaryRuleInfo.newRightSenseWktRuleInfo(sense, relation))
												:
													new LexicalRule<WiktionaryRuleInfo>( lemma, pos, neighbor, pos, relation.name(),  
															RESOURCE_NAME, WiktionaryRuleInfo.newLeftSenseWktRuleInfo(sense, relation)));
					} catch (WiktionaryException e) {
						throw new LexicalResourceException("Error in getRelatedWords for " + sense + " and for " + relation , e);
					}
			}
		List<LexicalRule<? extends WiktionaryRuleInfo>> rules = new Vector<LexicalRule<? extends WiktionaryRuleInfo>>(rulesSet);
		return rules;
	}
	
	/**
	 * carefully pull the {@link Sense}(s). If pos is null, return Senses for all possible POSs 
	 * @param lemma
	 * @param partOfSpeech
	 * @param senseNum
	 * @param useEntryData 
	 * @return
	 * @throws LexicalResourceException 
	 */
	private List<WiktionarySense> pullSenses(String lemma, PartOfSpeech partOfSpeech, int senseNum, boolean useEntryData) 
			throws LexicalResourceException {
		List<WiktionarySense> selectSenses = new Vector<WiktionarySense>();
		Set<WiktionaryEntry> entries = new HashSet<WiktionaryEntry>();
		try {							
			if (partOfSpeech == null )
			{
				entries.addAll(dictionary.getEntriesOf(lemma).values());
			}
			else
			{
				WiktionaryPartOfSpeech wiktionaryPartOfSpeech;
				try {	wiktionaryPartOfSpeech = WiktionaryPartOfSpeech.toWiktionaryPartOfspeech(partOfSpeech);	}
				catch (WiktionaryException e) {	throw new LexicalResourceException("see nested", e);	}
				entries.add( dictionary.getEntry(lemma, wiktionaryPartOfSpeech));
			}
		}
		catch (WiktionaryException e) {	throw new LexicalResourceException("An error occured while extracting the senses for <" + lemma + ", " + partOfSpeech + ">", e);	}
		for (WiktionaryEntry entry : entries)
		{
			if (useEntryData)
				selectSenses.add(entry);
			if (senseNum != WiktionaryLexicalResource.NO_SENSES)
			{
				try {
					if (senseNum == WiktionaryLexicalResource.ALL_SENSES)
						selectSenses.addAll(entry.getAllSortedSenses());
					else 
					{
						if (senseNum >= entry.getNumberOfSenses())
							throw new LexicalResourceException("You asked for sense #"+senseNum+" but there are only "+entry.getNumberOfSenses()+" in the entry for <"+lemma +", "+partOfSpeech +">");
						selectSenses.add(entry.getSense(senseNum));
					}
				} catch (WiktionaryException e) {
					throw new LexicalResourceException("Error pulling the senses of the entry for <"+lemma +", "+partOfSpeech +">");
				}
			}
		}
		return selectSenses;
	}
}

