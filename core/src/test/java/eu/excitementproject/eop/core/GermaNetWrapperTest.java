
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;


import java.io.File;
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
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;



/**
 * Test class to check if GermaNetWrapper works correctly with all three 
 * different initialization possibilities and with all different callable 
 * methods.
 * 
 * The first part of the tests loads GermaNetWrapper by only handing the 
 * GermaNet installation path to its constructor.
 * The second part loads GermaNetWrapper via a CommonConfig configuration file.
 * The third part loads GermaNetWrapper by handing the GermaNet installation
 * path and confidence values to GermaNetWrapper's constructor.
 *  
 * All callable methods are tested:
 * getRulesForLeft(lemma, pos), getRulesForLeft(lemma, pos, ownRelation)
 * getRulesForRight(lemma, pos), getRulesForRight(lemma, pos, ownRelation)
 * getRules(lemma, pos, lemma, pos), getRules(lemma, pos, lemma, pos, ownRelation)
 * 
 * 
 * @author Jan Pawellek, Britta Zeller, Julia Kreutzer 
 *
 */
public class GermaNetWrapperTest {

	@Test
	public void test() throws UnsupportedPosTagStringException {
		
		GermaNetWrapper gnw=null;

	
		try {

			System.out.println("### run 1: path");
			gnw = new GermaNetWrapper("/mnt/resources/ontologies/germanet-7.0/GN_V70/GN_V70_XML/");
			
			/* for GermaNet Evaluation -> test GermaNet for outputs for specific words, saved in testwords
			try {
				String[] testwords = {"Forstwirtschaft","Uran","Fauna","Malta","Linse","Kurve","Bargeld","Banane","Spargel","Wäscherei","Tennis","Kegelbahn","Hirn","Reifen","Dozent","hineinhorchen","mästen","zugrundeliegen","ragen","antizipieren","vorausberechnen","überspringen","verpassen","einschießen","zurückzahlen","anstecken","bloßstellen","zermahlen","zieren","glühen"};
				//String[] testwords = {"abstürzen","verlegen","abkürzen","umringen"};
				//String[] testwords = {"schön","abstrakt","klein", "laufen", "lernen", "hoffen"};
				for (String word : testwords){
					List<LexicalRule<? extends GermaNetInfo>> l = gnw.getRulesForLeft(word, new GermanPartOfSpeech("NN"));
					for (LexicalRule<? extends GermaNetInfo> rule : l){
						System.out.println(rule.getLLemma()+"\t"+rule.getRLemma());
					}
				}
				
				List<LexicalRule<? extends GermaNetInfo>> k = gnw.getRulesForRight("Hund", null);
				System.out.print("RightRules for Hund:\n ");
				for (LexicalRule<? extends GermaNetInfo> rule : k){
					System.out.println(rule.getRLemma()+" "+rule.getRelation());
				}
				
				
			} catch (LexicalResourceException e) {
				e.printStackTrace();
			}	
				*/	
			
		}
		catch (GermaNetNotInstalledException e) {
			System.out.println("WARNING: GermaNet files are not found in the given path. Please correctly install and pass the path to GermaNetWrapper");
			//throw e;
		}
		
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}
		Assume.assumeNotNull(gnw); // if gnw is null, the following tests will not be run. 



		// Test for "simplest" generic method 
		List<LexicalRule<? extends GermaNetInfo>> list1 = null; 
		try {
			//System.out.println("** 1");
			list1 = gnw.getRulesForLeft("wachsen", null);
			assertTrue(list1.size() > 0); 
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
		
		// Test for verbs
		try{

			//System.out.println("** 2");
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForLeft("wachsen", new GermanPartOfSpeech("VINF"), GermaNetRelation.has_antonym)) {
				assertTrue(rule.getLLemma().equals("wachsen"));
				//System.out.println("for wachsen: " + rule.getRLemma());
				assertTrue(rule.getInfo().getLeftSynsetID() == 59751 || rule.getInfo().getLeftSynsetID() == 54357); // might only be true in GermaNet 7.0
				assertTrue(rule.getRLemma().equals("schrumpfen"));
				assertTrue(rule.getInfo().getRightSynsetID() == 59780 || rule.getInfo().getRightSynsetID() == 54511); // might only be true in GermaNet 7.0
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() == 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}	
		
		
		// Negative test for verbs to show the given POS is used
		try{
			//System.out.println("** 3");
			List<LexicalRule<? extends GermaNetInfo>> rule = gnw.getRulesForLeft("wachsen", new GermanPartOfSpeech("NN"), GermaNetRelation.has_synonym);
			assertTrue(rule.isEmpty());
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
		
		// Test for level-maximum for verbs, granted that "lernen" has 4 rules on the first level (might only be true in GermaNet 7.0)
		try{
			//System.out.println("** 4");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRulesForLeft("lernen", new GermanPartOfSpeech("VINF"));
			assertTrue(rules.size()==4);
			for (LexicalRule<? extends GermaNetInfo> rule : rules){
				assertFalse(rule.getRLemma()=="lernen");
				assertFalse(rule.getRLemma()=="aufnehmen");
				assertFalse(rule.getRLemma()=="GN_ROOT");
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace();
		}
		
		// Test for common nouns

		try{
			//System.out.println("** 5");
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForLeft("Hitze", new GermanPartOfSpeech("NN"), GermaNetRelation.has_antonym)) {
				//System.out.println("lLemma: " + rule.getLLemma() + ", rLemma: " + rule.getRLemma());
				assertTrue(rule.getLLemma().equals("Hitze"));
				assertTrue(rule.getRLemma().equals("Kälte"));
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() == 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		// Test of not-supported POS type (should return an empty list) 
		try {
			//System.out.println("** 6");
			List<LexicalRule<? extends GermaNetInfo>> l = gnw.getRulesForLeft("Hitze", new GermanPartOfSpeech("PTKA")); 
			assertTrue(l.size() == 0); 
			// Still, null POS should mean, don't care
			l = gnw.getRulesForLeft("Hitze",  null); 
			assertTrue(l.size() > 0);
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		
		}
		
		// Test for level-maximum for nouns, granted that "Katze" has 7 rules on the first two levels (might only be true in GermaNet 7.0)
		try{
			//System.out.println("** 7");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRulesForLeft("Katze", new GermanPartOfSpeech("NN"));
			assertTrue(rules.size()==9);
			for (LexicalRule<? extends GermaNetInfo> rule : rules){
				//System.out.println("lLemma: " + rule.getLLemma() + ", rLemma: " + rule.getRLemma());
				assertFalse(rule.getRLemma()=="Katze");
				assertFalse(rule.getRLemma()=="Bestie");
				assertFalse(rule.getRLemma()=="GN_ROOT");
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace();
		}
		
		// Test for level-maximum for adjectives, granted that "klein" has 1 rule on the first level (might only be true in GermaNet 7.0)
		try{
			//System.out.println("** 8");
			List<LexicalRule<? extends GermaNetInfo>> rules = gnw.getRulesForLeft("klein", new GermanPartOfSpeech("ADJ"));
			assertTrue(rules.size()==1);
			for (LexicalRule<? extends GermaNetInfo> rule : rules){
				//System.out.println("lLemma: " + rule.getLLemma() + ", rLemma: " + rule.getRLemma());
				assertFalse(rule.getRLemma()=="klein");
				assertFalse(rule.getRLemma()=="klassenübergreifend");
				assertFalse(rule.getRLemma()=="GN_ROOT");
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace();
		}

		
		
		// Test for CommonConfig passing
		//System.out.println("### run 2: common config");
		
		gnw=null;
		try {
			File f = new File("./src/test/resources/german_resource_test_configuration.xml");
			gnw = new GermaNetWrapper(new ImplCommonConfig(f)); 
		}
		catch (GermaNetNotInstalledException e) {
			System.out.println("WARNING: GermaNet files are not found in the given path. Please correctly install and update the path in the configuration file");
			//throw e;
		}
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}
		Assume.assumeNotNull(gnw); // if gnw is null, the following tests will not be run. 

		// repeat the test for common nouns, with CommonConfig inited gnw
		try{
			//System.out.println("** 9");
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForLeft("Hitze", new GermanPartOfSpeech("NN"), GermaNetRelation.has_antonym)) {
				//System.out.println("lLemma: " + rule.getLLemma() + ", rLemma: " + rule.getRLemma());
				assertTrue(rule.getLLemma().equals("Hitze"));
				assertTrue(rule.getRLemma().equals("Kälte"));
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() == 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		// check for hypernyms only
		try{
			//System.out.println("** 9a");
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForLeft("Hitze", new GermanPartOfSpeech("NN"), GermaNetRelation.has_hypernym)) {
				//System.out.println("lLemma: " + rule.getLLemma() + ", rLemma: " + rule.getRLemma());
				assertTrue(rule.getLLemma().equals("Hitze"));
				assertTrue(rule.getRLemma().equals("Wärmegrad") || rule.getRLemma().equals("Wert") || rule.getRLemma().equals("Temperatur") );
				assertTrue(rule.getRelation().equals("has_hypernym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
		
		// repeat test for "simplest" generic method, with CommonConfig initiated gnw. 
		// and compares the result to previous one.  
		List<LexicalRule<? extends GermaNetInfo>> list2 = null; 
		try {
			//System.out.println("** 10");
			list2 = gnw.getRulesForLeft("wachsen", null); 
			assertTrue(list2.size() > 0);
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			

		
		// should be identical... (well, unless someone edited the test configuration. none should have) 
		assertTrue(list1.size() == list2.size());
		for(int i=0; i < list1.size(); i++)
		{
			assertTrue(list1.get(i).getLLemma().equals(list2.get(i).getLLemma())); 
			assertTrue(list1.get(i).getRLemma().equals(list2.get(i).getRLemma())); 			
		}
		
		// checking that no antonym in getRulesForLeft() 
		try{
			//System.out.println("** 11");
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForLeft("Hitze", null)) {
				//System.out.println("lLemma: " + rule.getLLemma() + ", rLemma: " + rule.getRLemma());
				assertTrue(rule.getLLemma().equals("Hitze"));
				assertFalse(rule.getRLemma().equals("Kälte")); // no "Kaelte" should be here.  
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
			

		
		// check that no 0 confidence value returns 
		//System.out.println("### run 3: check man. set confidence values");
		try {// Initiating with "no synonym and antonym" (0 confidence on synonym and per internal definition antonym)
			gnw = new GermaNetWrapper("/mnt/resources/ontologies/germanet-7.0/GN_V70/GN_V70_XML/", 1.0, 1.0, 1.0, 0.0, 1.0); // , 0.0
		}
		catch (GermaNetNotInstalledException e) {
			System.out.println("WARNING: GermaNet files are not found in the given path. Please correctly install and pass the path to GermaNetWrapper");
			//throw e;
		}
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}

		// there should be no synonym RHS, neither anyone with 0 confidence. 
		try{
			//System.out.println("** 12");
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForLeft("Hund", null)) {
				//System.out.println("lLemma: " + rule.getLLemma() + ", rLemma: " + rule.getRLemma());
				assertTrue(rule.getConfidence() > 0);
				assertFalse(rule.getRelation().equals("has_synonym")); 
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		//test for getRulesForRight, only hyponyms and synonyms allowed per definition
		try{
			//System.out.println("** 13");
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForRight("Hund", new GermanPartOfSpeech("NN"))) {
				//System.out.println(rule.getLLemma() + ", " + rule.getRLemma());
				assertTrue(rule.getRLemma().equals("Hund"));
				assertTrue(!rule.getLLemma().equals("Hund"));
				assertTrue(rule.getInfo().getLeftSynsetID() == 50708); // might only be true in GermaNet 7.0
				assertTrue((rule.getRelation().equals("has_hyponym")) || (rule.getRelation().equals("has_synonym")));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}


		//test for getRulesForRight, only hyponyms wanted
		try{
			//System.out.println("** 14");
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForRight("Hund", new GermanPartOfSpeech("NN"), GermaNetRelation.has_hyponym)) {
				//System.out.println(rule.getLLemma() + ", " + rule.getRLemma());
				assertTrue(rule.getRLemma().equals("Hund"));
				assertTrue(!rule.getLLemma().equals("Hund"));
				assertTrue(rule.getInfo().getLeftSynsetID() == 50708); // might only be true in GermaNet 7.0
				assertTrue(rule.getRelation().equals("has_hyponym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		

		//test for getRules with hyponyms (but no relation restriction in the call); 
		// should return 1 rule
		try{
			//System.out.println("** 15");
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRules("Pudel", new GermanPartOfSpeech("NN"), "Hund", new GermanPartOfSpeech("NN"))) {
				//System.out.println(rule.getLLemma() + ", " + rule.getRLemma());
				assertTrue(rule.getRLemma().equals("Hund"));
				assertTrue(rule.getLLemma().equals("Pudel"));
				assertTrue(!rule.getLLemma().equals("Hund"));
				assertTrue(rule.getInfo().getRightSynsetID() == 50708); // might only be true in GermaNet 7.0
				assertTrue((rule.getRelation().equals("has_hypernym")));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
		
		//"false" test for getRules with hyponyms (but no relation restriction in the call); 
		// should return empty list
		try{
			//System.out.println("** 15a");
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRules("Hund", new GermanPartOfSpeech("NN"), "Pudel", new GermanPartOfSpeech("NN"))) {
				System.err.println("this text for rule " + rule.toString() 
						+ " in GermaNet lookup should not occur; it means an error in the logic");
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
				
		
		//test for getRules, only hypernyms allowed
		try{
			//System.out.println("** 16");
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRules("Pudel", new GermanPartOfSpeech("NN"), "Hund", new GermanPartOfSpeech("NN"), GermaNetRelation.has_hypernym)) {
				//System.out.println(rule.getLLemma() + ", " + rule.getRLemma());
				assertTrue(rule.getLLemma().equals("Pudel"));
				assertTrue(rule.getRLemma().equals("Hund"));
				assertTrue(rule.getRelation().equals("has_hypernym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
			
		
	}
}

