
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;

import java.util.List;


import org.junit.Assume;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.exception.BaseException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetRelation;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetWrapper;
import eu.excitementproject.eop.common.representation.partofspeech.GermanPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;;



/**
 * Tests from this class can also be found in GermaNetWrapperTest class
 * @author Britta Zeller 
 *
 */
public class GermaNetAntonymyTest {

	@Test
	public void test() throws UnsupportedPosTagStringException {
		
		//System.out.println("################### for direct path-only call");
		// Confidence values are automatically set in the GermaNetWrapper constructor: 
		// all DEFAULT_CONF, except antonymy (0.0)  
		GermaNetWrapper gnw=null;
		try {
			gnw = new GermaNetWrapper("/home/julia/Dokumente/HiWi/germanet/germanet-8.0/GN_V80_XML");		//originally: path/to/GermaNetFiles/GN_V70/GN_V70_XML		
		}
		catch (GermaNetNotInstalledException e) {
			//System.out.println("WARNING: GermaNet files are not found in the given path. Please correctly install and pass the path to GermaNetWrapper");
			//throw e;
		}
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}
		Assume.assumeNotNull(gnw); // if gnw is null, the following tests will not be run. 

		
		//System.out.println("################################################################################");
		
		//System.out.println("****************************************************");
		//System.out.println("WITHOUT FINE-GRAINED RELATIONS");
		//System.out.println("****************************************************");
		
		// ******************************************************************************
		// NO FINE-GRAINED RELATIONS
		// ******************************************************************************
		
		// Test for relations without fine grained relation -- thus without antonymy.
		// test via getRulesForLeft
		try{
			//System.out.println("*** without fine grained relation, getRulesForLeft");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRulesForLeft("Kauf", new GermanPartOfSpeech("NN"));
			//System.out.println("resulting rules (size: " +rules.size() + "): ");
			for (LexicalRule<? extends GermaNetInfo> rule : rules) {
				System.out.println(rule.toString());
				assertTrue(rule.getLLemma().equals("Kauf"));
				assertTrue(!rule.getRLemma().equals("Verkauf"));
				assertTrue(!rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}	
		
		
		// Test for relations without fine grained relation -- thus without antonymy.
		// test via getRulesForRight
		try{
			//System.out.println("*** without fine grained relation, getRulesForRight");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRulesForRight("Kauf", new GermanPartOfSpeech("NN"));
			//System.out.println("resulting rules (size: " +rules.size() + "): ");
			for (LexicalRule<? extends GermaNetInfo> rule : rules) {
				System.out.println(rule.toString());
				assertTrue(rule.getRLemma().equals("Kauf"));
				assertTrue(!rule.getLLemma().equals("Verkauf"));
				assertTrue(!rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
		

		// Test for relations without fine grained relation -- thus without antonymy.
		// test via getRules
		try{
			//System.out.println("*** without fine grained relation, getRules for antonym pair");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRules("Kauf", new GermanPartOfSpeech("NN"), "Verkauf", new GermanPartOfSpeech("NN"));
			//System.out.println("resulting rules (size: " +rules.size() + "): ");
			for (LexicalRule<? extends GermaNetInfo> rule : rules) {
				System.out.println(rule.toString());
				assertTrue(rule.getLLemma().equals("Kauf"));
				assertTrue(!rule.getRLemma().equals("Verkauf"));
				assertTrue(!rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}

		
		// Test for relations without fine grained relation -- thus without antonymy.
		// test via getRules, this time for a valid (positively related) pair
		try{
			//System.out.println("*** without fine grained relation, getRules for synonym pair");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRules("Kauf", new GermanPartOfSpeech("N"), "Ankauf", new GermanPartOfSpeech("NN"));
			//System.out.println("resulting rules (size: " +rules.size() + "): ");
			for (LexicalRule<? extends GermaNetInfo> rule : rules) {
				System.out.println(rule.toString());
				assertTrue(rule.getLLemma().equals("Kauf"));
				assertTrue(!rule.getRLemma().equals("Verkauf"));
				assertTrue(!rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		

		
		// ******************************************************************************
		// FINE-GRAINED RELATIONS
		//System.out.println("****************************************************");
		//System.out.println("WITH FINE-GRAINED RELATIONS");
		//System.out.println("****************************************************");
		// ******************************************************************************
		
		// Test for relations with fine grained relation "antonymy".
		// test via getRulesForLeft
		try{
			//System.out.println("*** with fine grained relation 'antonymy', getRulesForLeft");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRulesForLeft("Kauf", new GermanPartOfSpeech("NN"), GermaNetRelation.has_antonym);
			//System.out.println("resulting rules (size: " +rules.size() + "): ");
			for (LexicalRule<? extends GermaNetInfo> rule : rules) {
				System.out.println(rule.toString());
				assertTrue(rule.getLLemma().equals("Kauf"));
				assertTrue(rule.getRLemma().equals("Verkauf"));
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() == 0.5); //default confidence for antonymy
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}	

		// Test for relations with fine grained relation "synonymy".
		// test via getRulesForLeft
		try{
			//System.out.println("*** with fine grained relation 'synonymy', getRulesForLeft");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRulesForLeft("Kauf", new GermanPartOfSpeech("NN"), GermaNetRelation.has_synonym);
			//System.out.println("resulting rules (size: " +rules.size() + "): ");
			for (LexicalRule<? extends GermaNetInfo> rule : rules) {
				//System.out.println(rule.toString());
				assertTrue(rule.getLLemma().equals("Kauf"));
				assertTrue(!rule.getRLemma().equals("Verkauf"));
				assertTrue(!rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
		
		// Test for relations with fine grained relation "antonymy".
		// test via getRulesForRight
		try{
			//System.out.println("*** with fine grained relation 'antonymy', getRulesForRight");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRulesForRight("Kauf", new GermanPartOfSpeech("NN"), GermaNetRelation.has_antonym);
			//System.out.println("resulting rules (size: " +rules.size() + "): ");
			for (LexicalRule<? extends GermaNetInfo> rule : rules) {
				//System.out.println(rule.toString());
				assertTrue(rule.getRLemma().equals("Kauf"));
				assertTrue(rule.getLLemma().equals("Verkauf"));
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() == 0.5); //default confidence for antonymy
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			


		// Test for relations with fine grained relation "synonymy".
		// test via getRulesForRight
		try{
			//System.out.println("*** with fine grained relation 'synonymy', getRulesForRight");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRulesForRight("Kauf", new GermanPartOfSpeech("NN"), GermaNetRelation.has_synonym);
			//System.out.println("resulting rules (size: " +rules.size() + "): ");
			for (LexicalRule<? extends GermaNetInfo> rule : rules) {
				//System.out.println(rule.toString());
				assertTrue(rule.getRLemma().equals("Kauf"));
				assertTrue(!rule.getLLemma().equals("Verkauf"));
				assertTrue(!rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getRelation().equals("has_synonym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
		

		// Test for relations with fine grained relation "hypernymy".
		// test via getRulesForRight -- should result in empty result list
		try{
			//System.out.println("*** with fine grained relation 'hypernymy', getRulesForRight");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRulesForRight("Kauf", new GermanPartOfSpeech("NN"), GermaNetRelation.has_hypernym);
			//System.out.println("resulting rules (size: " +rules.size() + "): ");
			for (LexicalRule<? extends GermaNetInfo> rule : rules) {
				//System.out.println(rule.toString());
				assertTrue(rule.getRLemma().equals("Kauf"));
				assertTrue(!rule.getLLemma().equals("Verkauf"));
				assertTrue(!rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}	
		

		// Test for relations with fine grained relation "hyponymy".
		// test via getRulesForRight -- should result in empty result list
		try{
			//System.out.println("*** with fine grained relation 'hyponymy', getRulesForRight");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRulesForRight("Kauf", new GermanPartOfSpeech("NN"), GermaNetRelation.has_hyponym);
			//System.out.println("resulting rules (size: " +rules.size() + "): ");
			for (LexicalRule<? extends GermaNetInfo> rule : rules) {
				//System.out.println(rule.toString());
				assertTrue(rule.getRLemma().equals("Kauf"));
				assertTrue(!rule.getLLemma().equals("Verkauf"));
				assertTrue(!rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getRelation().equals("has_hyponym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
		
		// Test for relations with fine grained relation "antonymy".
		// test via getRules, with an antonymic pair, thus resulting in an 1-entry list
		try{
			//System.out.println("*** with fine grained relation 'antonymy', getRules for antonym pair");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRules("Kauf", new GermanPartOfSpeech("NN"), "Verkauf", new GermanPartOfSpeech("NN"), GermaNetRelation.has_antonym);
			//System.out.println("resulting rules (size: " +rules.size() + "): ");
			for (LexicalRule<? extends GermaNetInfo> rule : rules) {
				//System.out.println(rule.toString());
				assertTrue(rule.getLLemma().equals("Kauf"));
				assertTrue(rule.getRLemma().equals("Verkauf"));
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() == 0.5); //default value
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}

		
		// Test for relations with fine grained relation "antonymy".
		// test via getRules, this time for a valid (positively related) pair, thus resulting in an empty list
		try{
			//System.out.println("*** with fine grained relation 'antonymy', getRules for synonym pair");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRules("Kauf", new GermanPartOfSpeech("N"), "Ankauf", new GermanPartOfSpeech("NN"), GermaNetRelation.has_antonym);
			//System.out.println("resulting rules (size: " +rules.size() + "): ");
			for (LexicalRule<? extends GermaNetInfo> rule : rules) {
				//System.out.println(rule.toString());
				assertTrue(rule.getLLemma().equals("Kauf"));
				assertTrue(!rule.getRLemma().equals("Verkauf"));
				assertTrue(!rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		

				
		// Test for relations with fine grained relation "synonymy".
		// test via getRules, this time for a valid (positively related) pair
		try{
			//System.out.println("*** with fine grained relation 'synonymy', getRules for synonym pair");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRules("Kauf", new GermanPartOfSpeech("N"), "Ankauf", new GermanPartOfSpeech("NN"), GermaNetRelation.has_synonym);
			//System.out.println("resulting rules (size: " +rules.size() + "): ");
			for (LexicalRule<? extends GermaNetInfo> rule : rules) {
				//System.out.println(rule.toString());
				assertTrue(rule.getLLemma().equals("Kauf"));
				assertTrue(!rule.getRLemma().equals("Verkauf"));
				assertTrue(!rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
	
	}
}

