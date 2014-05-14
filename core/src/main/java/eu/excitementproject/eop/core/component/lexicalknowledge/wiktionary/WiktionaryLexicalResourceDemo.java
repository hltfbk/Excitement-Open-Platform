package eu.excitementproject.eop.core.component.lexicalknowledge.wiktionary;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryRelation;


/**
 * Demo for LexResource
 * 
 * @author Amnon Lotan
 * @since 06/05/2011
 * 
 */
public class WiktionaryLexicalResourceDemo {


	/**
	 * @param args
	 * @throws LexicalResourceException 
	 * @throws UnsupportedPosTagStringException 
	 */
	public static void main(String[] args) throws LexicalResourceException, UnsupportedPosTagStringException 
	{
		System.out.println("Start \n*****************************\n");
	

		String lLemma = "work";
		PartOfSpeech pos2 = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN);
		String rLemma = "worker";
		System.out.println("Looking for all rules from \"" + lLemma + "\" to \"" + rLemma + "\"");
		
		// test WN
		System.out.println("\nFrom the new WN:");
		
//		WordnetLexResource wnLexR = new WordnetLexResource(new File("//qa-srv\\Data\\RESOURCES\\WordNet\\3.0\\dict.wn.orig"));
		WiktionaryLexicalResource wtkLexR = new WiktionaryLexicalResource("//qa-srv/data/RESOURCES/Wiktionary/parse", 
				"//qa-srv/jars/stanford-postagger-2008-09-28/bidirectional-wsj-0-18.tagger"		);
		
		// check for HYPERNYM, and also SYNONYM, DERIVATION
		
		Set<WiktionaryRelation> relations2 = new HashSet<WiktionaryRelation>();
		relations2.addAll( Utils.arrayToCollection(WiktionaryRelation.values(), relations2));
//		relations2.add(WiktionaryRelation.HYPERNYM);
//		relations2.add(WiktionaryRelation.DERIVED_TERM);
//		relations2.add(WiktionaryRelation.SYNONYM);
//		relations2.add(WiktionaryRelation.HYPONYM);
		wtkLexR.setRelationSet(relations2);
		
	//				relations.add(WiktionaryRelation.PART_HOLONYM);
		wtkLexR.setLeftSense(WiktionaryLexicalResource.ALL_SENSES);
		wtkLexR.setRightSense(WiktionaryLexicalResource.ALL_SENSES);

		
		List<LexicalRule<? extends WiktionaryRuleInfo>> rules2 = wtkLexR.getRulesForLeft(lLemma, pos2 );
		
		System.out.println("Got "+rules2.size() + " for: " + lLemma + ", " + pos2 + ", " + relations2);
		for (LexicalRule<? extends WiktionaryRuleInfo> rule : rules2)
			System.out.println(rule);
		
		System.out.println(lLemma +" has " + rules2.size() + " "+relations2.toString()+" relations");
		System.out.println("\n*****************************\n");
				
		
		
		List<LexicalRule<? extends WiktionaryRuleInfo>> otherRules = wtkLexR.getRules(lLemma, null, rLemma, null);
		System.out.println("Got "+otherRules.size() + " for: " + lLemma + ", " + pos2 + ", "  + rLemma + ", "  + pos2 + ", "  + relations2);
		for (LexicalRule<? extends WiktionaryRuleInfo> rule : otherRules)
			System.out.println(rule);
		
		System.out.println(lLemma +" has " + otherRules.size() + " "+relations2.toString()+" relations");
	}
	
	
	
//	Set<WiktionaryRelation> relations = new HashSet<WiktionaryRelation>();
//	rel = new WiktionaryRelation();
//	relations.add(WiktionaryRelation.HYPERNYM);
//	relations.addAll(Utils.arrayToCollection(WiktionaryRelation.values(), new HashSet<WiktionaryRelation>()));
//	WiktionaryLexResource wktLexR = new WiktionaryLexResource("//qa-srv/data/RESOURCES/Wiktionary/parse", "//qa-srv/jars/stanford-postagger-2008-09-28/bidirectional-wsj-0-18.tagger");
//	
//	String lemma = "dog";
//	PartOfSpeech pos = new UnspecifiedPartOfSpeech(SimplerCanonicalPosTag.NOUN);
//	String lemma2 = "cat";
//	wktLexR.setRightSense(WiktionaryLexResource.ALL_SENSES);
//	wktLexR.setLeftSense(WiktionaryLexResource.ALL_SENSES);
//	wktLexR.setRelationSet(relations);
//	List<LexRule<? extends WiktionaryRuleInfo>> rules = wktLexR.getRulesForLeft(lemma, pos);
//	for (LexRule<? extends WiktionaryRuleInfo> rule : rules)
//		System.out.println(rule);
//	
//	System.out.println("\n*****************************\n");
//
//	rules = wktLexR.getRules(lemma, pos, lemma2);
//	for (LexRule<? extends WiktionaryRuleInfo> rule : rules)
//		System.out.println(rule);
	

}

