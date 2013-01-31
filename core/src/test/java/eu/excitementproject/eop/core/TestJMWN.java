package eu.excitementproject.eop.core;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assume;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnspecifiedPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordnetDictionaryImplementationType;

public class TestJMWN {

	@Test
	public void test() {

		WordnetLexicalResource wnLexR = null;
		Set<WordNetRelation> relations = new HashSet<WordNetRelation>();
		
		String configurationFileName = "src/test/resources/";

		String lLemma = "gigante";
		String rLemma = "piccolo";
		
		PartOfSpeech pos1 = null, pos2 = null;
		try {
			pos1 = new UnspecifiedPartOfSpeech(SimplerCanonicalPosTag.NOUN);
			pos2 = new UnspecifiedPartOfSpeech(SimplerCanonicalPosTag.ADJECTIVE);

			System.out.println("Looking for all rules from \"" + lLemma + "\" to \"" + rLemma + "\"");
		
			relations.add(WordNetRelation.INSTANCE_HYPERNYM);
			relations.add(WordNetRelation.INSTANCE_HYPONYM);
			relations.add(WordNetRelation.HYPERNYM);
			relations.add(WordNetRelation.HYPONYM);
			relations.add(WordNetRelation.PART_HOLONYM);
			relations.add(WordNetRelation.CATEGORY_MEMBER);
			relations.add(WordNetRelation.SYNONYM);
		
			wnLexR = new WordnetLexicalResource(new File(configurationFileName), false, false, relations, 1, WordnetDictionaryImplementationType.JMWN);
			
		} catch (UnsupportedPosTagStringException e1) {
			System.out.println("Error assigning POS tag");
			e1.printStackTrace();
		} catch (LexicalResourceException e) {
			System.out.println("Error initialising Italian MultiWordnet");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assume.assumeNotNull(wnLexR);
		
		List<LexicalRule<? extends WordnetRuleInfo>> rules;
		try {
			
			rules = wnLexR.getRulesForLeft(lLemma, pos1 );

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

			
			//for (int i = 0; i < 1000000; i++) {
			List<LexicalRule<? extends WordnetRuleInfo>> otherRules = wnLexR.getRules(lLemma, pos1, rLemma, pos2, relations, null);
			//List<LexicalRule<? extends WordnetRuleInfo>> otherRules = wnLexR.getRules(lLemma, null, rLemma, null);
				
			System.out.println("Got "+otherRules.size() + " for: " + lLemma + ", " + pos2 + ", "  + rLemma + ", "  + null + ", "  + relations);
			for (LexicalRule<? extends WordnetRuleInfo> rule : otherRules)
				System.out.println(rule);
			
		
			//System.out.println(lLemma +" has " + otherRules.size() + " "+relations.toString()+" relations");
			//}
			} catch (LexicalResourceException e) {
			System.out.println("Error extracting entailment rules from Italian MultiWordNet");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
