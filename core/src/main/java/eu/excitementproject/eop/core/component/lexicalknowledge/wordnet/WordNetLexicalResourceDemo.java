package eu.excitementproject.eop.core.component.lexicalknowledge.wordnet;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;

/**
 * Demo for LexResource
 * 
 * @author Amnon Lotan
 * @since 06/05/2011
 * 
 */
public class WordNetLexicalResourceDemo {


	/**
	 * @param args
	 * @throws LexicalResourceException 
	 * @throws UnsupportedPosTagStringException 
	 * @throws ConfigurationException 
	 */
	public static void main(String[] args) throws LexicalResourceException, UnsupportedPosTagStringException, ConfigurationException 
	{
		System.out.println("Start \n*****************************\n");
		if (args.length<1)
		{
			System.err.println("Configuration file should be provided as argument");
			return;
		}
		String configurationFileName = args[0];
		String moduleName = "WNV2";
		if (args.length>=2)
		{
			moduleName = args[1];
		}
		System.out.println("Using module name: "+moduleName);
	

		String lLemma = "peach";
		PartOfSpeech pos1 = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN);
		PartOfSpeech pos2 = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN);
		String rLemma = "fruit";
		System.out.println("Looking for all rules from \"" + lLemma + "\" to \"" + rLemma + "\"");
		
		// test WN
		System.out.println("\nFrom the new WN:");
		
		ConfigurationFile  confFile = new ConfigurationFile(new File(configurationFileName));
		confFile.setExpandingEnvironmentVariables(true);
		WordnetLexicalResource wnLexR = new WordnetLexicalResource(confFile.getModuleConfiguration(moduleName));

		
//		WordnetLexResource wnLexR = new WordnetLexResource(new File("//qa-srv\\Data\\RESOURCES\\WordNet\\3.0\\dict.wn.orig"));
//		WordnetLexicalResource wnLexR = new WordnetLexicalResource(new File("//qa-srv\\Data\\RESOURCES\\WordNet\\3.0\\dict.wn.orig"));
		
		// check for HYPERNYM, and also SYNONYM, DERIVATION
		
		Set<WordNetRelation> relations = new LinkedHashSet<WordNetRelation>();
//		relations2.addAll( Utils.arrayToCollection(WordNetRelation.values(), new HashSet<WordNetRelation>()));
//		relations.add(WordNetRelation.DERIVATIONALLY_RELATED);
//		wnLexR.setDefaultRelationSet(relations);
		
	//				relations.add(WordnetRelation.PART_HOLONYM);
//		wnLexR.setUseFirstSenseOnlyLeft(false);
//		wnLexR.setUseFirstSenseOnlyRight(false);
		
		List<LexicalRule<? extends WordnetRuleInfo>> rules = wnLexR.getRulesForLeft(lLemma, pos1 );
		
		System.out.println("Got "+rules.size() + " left rules for: " + lLemma + ", " + pos1 + ", " + relations);
		for (LexicalRule<? extends WordnetRuleInfo> rule : rules)
			System.out.println(rule);
		
		System.out.println(lLemma +" has " + rules.size() + " "+relations.toString()+" relations");
		System.out.println("\n*****************************\n");
		
		rules = wnLexR.getRulesForRight(rLemma, pos2 );
		
		System.out.println("Got "+rules.size() + " right rules for: " + rLemma + ", " + pos2 + ", " + relations);
		for (LexicalRule<? extends WordnetRuleInfo> rule : rules)
			System.out.println(rule);
		System.out.println(rLemma +" has " + rules.size() + " "+relations.toString()+" relations");
		System.out.println("\n*****************************\n");

		List<LexicalRule<? extends WordnetRuleInfo>> otherRules = wnLexR.getRules(lLemma, null, rLemma, null, relations, null);
		System.out.println("Got "+otherRules.size() + " for: " + lLemma + ", " + pos2 + ", "  + rLemma + ", "  + null + ", "  + relations);
		for (LexicalRule<? extends WordnetRuleInfo> rule : otherRules)
			System.out.println(rule);
		
		System.out.println(lLemma +" has " + otherRules.size() + " "+relations.toString()+" relations");
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

