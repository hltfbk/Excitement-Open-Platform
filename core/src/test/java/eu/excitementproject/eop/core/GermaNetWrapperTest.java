
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;



import org.junit.Assume;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.TERuleRelation;
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
 * Instructions how to initialize GermaNetWrapper in these three ways can be found in GermaNetWrapperMiniTest.
 *  
 * All callable methods are tested:
 * getRulesForLeft(lemma, pos), getRulesForLeft(lemma, pos, relation)
 * getRulesForRight(lemma, pos), getRulesForRight(lemma, pos, relation)
 * getRules(lemma, pos, lemma, pos), getRules(lemma, pos, lemma, pos, relation)
 * 
 * @author Jan Pawellek, Britta Zeller, Julia Kreutzer 
 *
 */
public class GermaNetWrapperTest {

	@Test
	public void test() throws UnsupportedPosTagStringException {
		
		// Test for initialization with GN path
		GermaNetWrapper gnw=null;
		try {
			gnw = new GermaNetWrapper("/path/to/GermaNet/version8.0/germanet-8.0/GN_V80_XML/");	
		}
		catch (GermaNetNotInstalledException e) {
			System.out.println("WARNING: GermaNet files are not found in the given path. Please correctly install and pass the path to GermaNetWrapper");
		}
		
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}
		Assume.assumeNotNull(gnw); // if gnw is null, the following tests will not be run. 	
		
		//System.out.println("### run 1: path");
		
		//System.out.println("** 1: getRulesForLeft(lemma, pos)"); 
		// test getRulesForLeft(lemma, pos) for nouns, verbs and adjectives
		// -> no hyponyms, antonyms allowed
		// -> left lemma, left pos equal with input
		// -> left lemma not on right hand side
		// -> GNROOT not on right hand side
		// -> N/NN as left POS should return same number of rules
		try {
			//System.out.println("   - nouns (N)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules1 = gnw.getRulesForLeft("Haus", new GermanPartOfSpeech("N")); // test for noun
			int l1 = rules1.size();
			for (LexicalRule<? extends GermaNetInfo> rule : rules1){
				String rel = rule.getRelation();
				assertFalse(rel.equals("has_hyponym")); // no hyponyms allowed
				assertFalse(rel.equals("has_antonym")); // no antonyms allowed
				assertTrue( rel.equals("has_hypernym") || rel.equals("has_synonym") || rel.equals("entails") || rel.equals("causes") );
				assertTrue(rule.getLLemma().equals("Haus"));
				assertTrue(rule.getLPos().toString().equals("N"));
				assertFalse(rule.getRLemma().equals("Haus"));
				assertFalse(rule.getRLemma().equals("GNROOT"));
			}
			
			//System.out.println("   - nouns (NN)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules2 = gnw.getRulesForLeft("Haus", new GermanPartOfSpeech("NN")); // use POS "NN" instead
			int l2 = rules2.size();
			for (LexicalRule<? extends GermaNetInfo> rule : rules2){
				String rel = rule.getRelation();
				assertFalse(rel.equals("has_hyponym")); // no hyponyms allowed
				assertFalse(rel.equals("has_antonym")); // no antonyms allowed
				assertTrue( rel.equals("has_hypernym") || rel.equals("has_synonym") || rel.equals("entails") || rel.equals("causes") );
				assertTrue(rule.getLLemma().equals("Haus"));
				assertTrue(rule.getLPos().toString().equals("NN"));
				assertFalse(rule.getRLemma().equals("Haus"));
				assertFalse(rule.getRLemma().equals("GNROOT"));
			}
			assertTrue( l1 == l2); // using "NN" instead of "N" should not make a difference for the number of resulting rules
			
			//System.out.println("   - verbs (V)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules3 = gnw.getRulesForLeft("beschreiben", new GermanPartOfSpeech("V")); // test for verb
			int l3 = rules3.size(); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules3){
				String rel = rule.getRelation();
				assertFalse(rel.equals("has_hyponym")); // no hyponyms allowed
				assertFalse(rel.equals("has_antonym")); // no antonyms allowed
				assertTrue( rel.equals("has_hypernym") || rel.equals("has_synonym") || rel.equals("entails") || rel.equals("causes") );
				assertTrue(rule.getLLemma().equals("beschreiben"));
				assertTrue(rule.getLPos().toString().equals("V"));
				assertFalse(rule.getRLemma().equals("beschreiben"));
				assertFalse(rule.getRLemma().equals("GNROOT"));
				assertTrue(l3 == 3); // for version 8.0
			}
			
			//System.out.println("   - adjectives (ADJ)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules4 = gnw.getRulesForLeft("gut", new GermanPartOfSpeech("ADJ")); // test for adjectives
			int l4 = rules4.size(); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules4){
				String rel = rule.getRelation();
				assertFalse(rel.equals("has_hyponym")); // no hyponyms allowed
				assertFalse(rel.equals("has_antonym")); // no antonyms allowed
				assertTrue( rel.equals("has_hypernym") || rel.equals("has_synonym") || rel.equals("entails") || rel.equals("causes") );
				assertTrue(rule.getLLemma().equals("gut"));
				assertTrue(rule.getLPos().toString().equals("ADJ"));
				assertFalse(rule.getRLemma().equals("gut"));
				assertFalse(rule.getRLemma().equals("GNROOT"));
				assertTrue(l4 == 2); // for version 8.0
			}
			
			//System.out.println("   - null (OTHER)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules5 = gnw.getRulesForLeft("gut", null); // test for null pos
			int l5 = rules5.size(); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules5){
				String rel = rule.getRelation();
				assertFalse(rel.equals("has_hyponym")); // no hyponyms allowed
				assertFalse(rel.equals("has_antonym")); // no antonyms allowed
				assertTrue( rel.equals("has_hypernym") || rel.equals("has_synonym") || rel.equals("entails") || rel.equals("causes") );
				assertTrue(rule.getLLemma().equals("gut"));
				assertTrue(rule.getLPos().toString().equals("OTHER"));
				assertFalse(rule.getRLemma().equals("gut"));
				assertFalse(rule.getRLemma().equals("GNROOT"));
			}
			assertTrue(l5 >= l4); // there should at least as many rules for flexible pos as for only adjectives
			
		} catch (LexicalResourceException e1) {
			e1.printStackTrace();
		}
		
		//System.out.println("** 2: getRulesForRight(lemma, pos)"); 
		// test getRulesForRight(lemma, pos) for nouns, verbs and adjectives
		// -> only hyponyms, synonyms allowed
		// -> right lemma, left pos equal with input
		// -> right lemma not on left hand side
		// -> GNROOT not on left hand side
		// -> N/NN as left POS should return same number of rules
		try {
			//System.out.println("   - nouns (N)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules1 = gnw.getRulesForRight("Haus", new GermanPartOfSpeech("N")); // test for noun
			int l1 = rules1.size();
			for (LexicalRule<? extends GermaNetInfo> rule : rules1){
				String rel = rule.getRelation();
				assertFalse(rel.equals("has_hypernym")); // no hyponyms allowed
				assertFalse(rel.equals("has_antonym")); // no antonyms allowed
				assertFalse(rel.equals("causes")); // causes is not allowed
				assertFalse(rel.equals("entails")); // entails is not allowed
				assertTrue( rel.equals("has_hyponym") || rel.equals("has_synonym") );
				assertTrue(rule.getRLemma().equals("Haus"));
				assertTrue(rule.getRPos().toString().equals("N"));
				assertFalse(rule.getLLemma().equals("Haus"));
				assertFalse(rule.getLLemma().equals("GNROOT"));
			}
			
			//System.out.println("   - nouns (NN)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules2 = gnw.getRulesForRight("Haus", new GermanPartOfSpeech("NN")); // use POS "NN" instead
			int l2 = rules2.size();
			for (LexicalRule<? extends GermaNetInfo> rule : rules2){
				String rel = rule.getRelation();
				assertFalse(rel.equals("has_hypernym")); // no hyponyms allowed
				assertFalse(rel.equals("has_antonym")); // no antonyms allowed
				assertFalse(rel.equals("causes")); // causes is not allowed
				assertFalse(rel.equals("entails")); // entails is not allowed
				assertTrue( rel.equals("has_hyponym") || rel.equals("has_synonym") );
				assertTrue(rule.getRLemma().equals("Haus"));
				assertTrue(rule.getRPos().toString().equals("NN"));
				assertFalse(rule.getLLemma().equals("Haus"));
				assertFalse(rule.getLLemma().equals("GNROOT"));
			}
			assertTrue( l1 == l2); // using "NN" instead of "N" should not make a difference for the number of resulting rules
			
			//System.out.println("   - verbs (V)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules3 = gnw.getRulesForRight("beschreiben", new GermanPartOfSpeech("V")); // test for verb
			int l3 = rules3.size(); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules3){
				String rel = rule.getRelation();
				assertFalse(rel.equals("has_hypernym")); // no hyponyms allowed
				assertFalse(rel.equals("has_antonym")); // no antonyms allowed
				assertFalse(rel.equals("causes")); // causes is not allowed
				assertFalse(rel.equals("entails")); // entails is not allowed
				assertTrue( rel.equals("has_hyponym") || rel.equals("has_synonym") );
				assertTrue(rule.getRLemma().equals("beschreiben"));
				assertTrue(rule.getRPos().toString().equals("V"));
				assertFalse(rule.getLLemma().equals("beschreiben"));
				assertFalse(rule.getLLemma().equals("GNROOT"));
				assertTrue(l3 == 7); // for version 8.0
			}
			
			//System.out.println("   - adjectives (ADJ)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules4 = gnw.getRulesForRight("gut", new GermanPartOfSpeech("ADJ")); // test for adjectives
			int l4 = rules4.size(); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules4){
				String rel = rule.getRelation();
				assertFalse(rel.equals("has_hypernym")); // no hyponyms allowed
				assertFalse(rel.equals("has_antonym")); // no antonyms allowed
				assertFalse(rel.equals("causes")); // causes is not allowed
				assertFalse(rel.equals("entails")); // entails is not allowed
				assertTrue( rel.equals("has_hyponym") || rel.equals("has_synonym") );
				assertTrue(rule.getRLemma().equals("gut"));
				assertTrue(rule.getRPos().toString().equals("ADJ"));
				assertFalse(rule.getLLemma().equals("gut"));
				assertFalse(rule.getLLemma().equals("GNROOT"));
				assertTrue(l4 == 112); // for version 8.0
			}
			
			//System.out.println("   - null (OTHER)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules5 = gnw.getRulesForRight("gut", null); // test for null pos
			int l5 = rules5.size(); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules5){
				String rel = rule.getRelation();
				assertFalse(rel.equals("has_hypernym")); // no hyponyms allowed
				assertFalse(rel.equals("has_antonym")); // no antonyms allowed
				assertFalse(rel.equals("causes")); // causes is not allowed
				assertFalse(rel.equals("entails")); // entails is not allowed
				assertTrue( rel.equals("has_hyponym") || rel.equals("has_synonym") );
				assertTrue(rule.getRLemma().equals("gut"));
				assertTrue(rule.getRPos().toString().equals("OTHER"));
				assertFalse(rule.getLLemma().equals("gut"));
				assertFalse(rule.getLLemma().equals("GNROOT"));
			}
			assertTrue(l5 >= l4); // there should be more rules for flexible pos than for only adjectives
			
		} catch (LexicalResourceException e1) {
			e1.printStackTrace();
		}
		
		//System.out.println("** 3: check antonymy confidence"); 
		// test getRulesForLeft(lemma, pos, antonym) for correct antonymy confidence scores (default)
		try {
			//System.out.println("   - default confidence"); 
			List<LexicalRule<? extends GermaNetInfo>> rules1 = gnw.getRulesForLeft("Haus", new GermanPartOfSpeech("N"), GermaNetRelation.has_antonym); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules1){
				String rel = rule.getRelation();
				assertTrue(rel.equals("has_antonym")); // only antonyms allowed
				assertTrue(rule.getLLemma().equals("Haus"));
				assertTrue(rule.getLPos().toString().equals("N"));
				assertFalse(rule.getRLemma().equals("Haus"));
				assertFalse(rule.getRLemma().equals("GNROOT"));
				assertTrue(rule.getConfidence()==0.5); // default confidence for antonymy
			}
		} catch (LexicalResourceException e1) {
			e1.printStackTrace();
		}
		
		//System.out.println("** 4: Named Entities");
		// test getRules(ForLeft/Right) for handling Named Entities
		// -> Named Entities should have NP POS tag, on both sides of the rules
		try {
			//System.out.println("   - NE on LHS");
			List<LexicalRule<? extends GermaNetInfo>> rules1 = gnw.getRulesForLeft("Berlin", new GermanPartOfSpeech("NP")); // for NE with "NP" pos
			int l1 = rules1.size();
			for (LexicalRule<? extends GermaNetInfo> rule : rules1){
				assertTrue(rule.getLPos().toString().equals("NP"));
			}
			List<LexicalRule<? extends GermaNetInfo>> rules2 = gnw.getRulesForLeft("Berlin", new GermanPartOfSpeech("NN")); // with "NN" pos
			int l2 = rules2.size();
			for (LexicalRule<? extends GermaNetInfo> rule : rules2){
				assertTrue(rule.getLPos().toString().equals("NN"));
			}
			List<LexicalRule<? extends GermaNetInfo>> rules3 = gnw.getRulesForLeft("Berlin", new GermanPartOfSpeech("N")); // with "N" pos
			int l3 = rules3.size();
			for (LexicalRule<? extends GermaNetInfo> rule : rules3){
				assertTrue(rule.getLPos().toString().equals("N"));
			}
			assertTrue((l1 == l2) && (l2 == l3)); // should all return same results
			
			//System.out.println("   - NE on RHS");
			List<LexicalRule<? extends GermaNetInfo>> rules4 = gnw.getRulesForRight("Berlin", new GermanPartOfSpeech("NP")); // for NE with "NP" pos
			for (LexicalRule<? extends GermaNetInfo> rule : rules4){
				assertTrue(rule.getRPos().toString().equals("NP"));
			}
			
			//System.out.println("   - NE on LHS and RHS");
			List<LexicalRule<? extends GermaNetInfo>> rules5 = gnw.getRules("Berlin", new GermanPartOfSpeech("NP"), "Kreuzberg", new GermanPartOfSpeech("NP")); // for NP on both rule sides
			for (LexicalRule<? extends GermaNetInfo> rule : rules5){
				assertTrue(rule.getLLemma().equals("Berlin"));
				assertTrue(rule.getRLemma().equals("Kreuzberg"));
				assertTrue(rule.getRPos().toString().equals("NP"));
				assertTrue(rule.getLPos().toString().equals("NP"));
			}
		} catch (LexicalResourceException e1) {
			e1.printStackTrace();
		}
		
		
		
		// Test for CommonConfig passing
		gnw = null;
		try {
			File f = new File("./src/test/resources/german_resource_test_configuration.xml");
			gnw = new GermaNetWrapper(new ImplCommonConfig(f)); 
		}
		catch (GermaNetNotInstalledException e) {
			System.out.println("WARNING: GermaNet files are not found in the given path. Please correctly install and update the path in the configuration file");
		}
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}
		Assume.assumeNotNull(gnw); // if gnw is null, the following tests will not be run. 
		
		//System.out.println("### run 2: common config");

		//System.out.println("** 1: getRulesForLeft(lemma, pos, GermaNetRelation)"); 
		// test getRulesForLeft(lemma, pos, GermaNetRelation)
		// -> only rules with given relation are retrieved
		
		try {
			//System.out.println("   - hypernym"); 
			List<LexicalRule<? extends GermaNetInfo>> rules1 = gnw.getRulesForLeft("Haus", new GermanPartOfSpeech("N"), GermaNetRelation.has_hypernym); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules1){
				assertTrue(rule.getRelation().equals("has_hypernym"));
				assertTrue(rule.getLLemma().equals("Haus"));
			}
			//System.out.println("   - synonym"); 
			List<LexicalRule<? extends GermaNetInfo>> rules2 = gnw.getRulesForLeft("Sofa", new GermanPartOfSpeech("N"), GermaNetRelation.has_synonym); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules2){
				assertTrue(rule.getRelation().equals("has_synonym"));
				assertTrue(rule.getLLemma().equals("Sofa"));
			}
			
			//System.out.println("   - entails"); 
			List<LexicalRule<? extends GermaNetInfo>> rules3 = gnw.getRulesForLeft("Einkäufe", null, GermaNetRelation.entails); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules3){
				assertTrue(rule.getRelation().equals("entails"));
				assertTrue(rule.getLLemma().equals("Einkäufe"));
			}
			
			//System.out.println("   - causes"); 
			List<LexicalRule<? extends GermaNetInfo>> rules4 = gnw.getRulesForLeft("ansaufen", null, GermaNetRelation.causes); 
			if (rules4.size() == 0){ System.out.println(":-(");}
			for (LexicalRule<? extends GermaNetInfo> rule : rules4){
				assertTrue(rule.getRelation().equals("causes"));
				assertTrue(rule.getLLemma().equals("ansaufen"));
			}
			
			//System.out.println("   - antonym"); 
			List<LexicalRule<? extends GermaNetInfo>> rules5 = gnw.getRulesForLeft("Kauf", new GermanPartOfSpeech("N"), GermaNetRelation.has_antonym); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules5){
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getLLemma().equals("Kauf"));
			}
			
		} catch (LexicalResourceException e1) {
			e1.printStackTrace();
		}
		
		//System.out.println("** 2: getRulesForRight(lemma, pos, GermaNetRelation)"); 
		// test getRulesForRight(lemma, pos, GermaNetRelation)
		// -> only rules with given relation are retrieved
		
		try {
			//System.out.println("   - hyponym"); 
			List<LexicalRule<? extends GermaNetInfo>> rules1 = gnw.getRulesForRight("Haus", new GermanPartOfSpeech("N"), GermaNetRelation.has_hyponym); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules1){
				assertTrue(rule.getRelation().equals("has_hyponym"));
				assertTrue(rule.getRLemma().equals("Haus"));
			}
			//System.out.println("   - synonym"); 
			List<LexicalRule<? extends GermaNetInfo>> rules2 = gnw.getRulesForRight("Sofa", new GermanPartOfSpeech("N"), GermaNetRelation.has_synonym); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules2){
				assertTrue(rule.getRelation().equals("has_synonym"));
				assertTrue(rule.getRLemma().equals("Sofa"));
			}
					
			//System.out.println("   - antonym"); 
			List<LexicalRule<? extends GermaNetInfo>> rules5 = gnw.getRulesForRight("Kauf", new GermanPartOfSpeech("N"), GermaNetRelation.has_antonym); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules5){
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getRLemma().equals("Kauf"));
			}
			
		} catch (LexicalResourceException e1) {
			e1.printStackTrace();
		}
		
		//System.out.println("** 3: getRulesForLeft(lemma, pos, TERuleRelation)"); 
		// test getRulesForLeft(lemma, pos, TERuleRelation)
		// -> "nonentailment"/"entailment" define the relations allowed
		try {
			//System.out.println("   - ENTAILMENT"); 
			List<LexicalRule<? extends GermaNetInfo>> rules1 = gnw.getRulesForLeft("Haus", new GermanPartOfSpeech("N"), TERuleRelation.Entailment); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules1){
				assertFalse(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getLLemma().equals("Haus"));
				assertTrue(rule.getConfidence() == 0.5); // all entailment relations have confidence 0.5 as defined on config file
			}
			//System.out.println("   - NONENTAILMENT"); 
			List<LexicalRule<? extends GermaNetInfo>> rules2 = gnw.getRulesForLeft("Kauf", new GermanPartOfSpeech("N"), TERuleRelation.NonEntailment); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules2){
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getLLemma().equals("Kauf"));
				assertTrue(rule.getConfidence() == 0.0); // antonym confidence score is set to 0.0 in config file
			}
					
		} catch (LexicalResourceException e1) {
			e1.printStackTrace();
		}
		
		//System.out.println("** 4: getRulesForRight(lemma, pos, TERuleRelation)"); 
		// test getRulesForRight(lemma, pos, TERuleRelation)
		// -> "nonentailment"/"entailment" define the relations allowed
		try {
			//System.out.println("   - ENTAILMENT"); 
			List<LexicalRule<? extends GermaNetInfo>> rules1 = gnw.getRulesForRight("Haus", new GermanPartOfSpeech("N"), TERuleRelation.Entailment); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules1){
				assertFalse(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getRLemma().equals("Haus"));
				assertTrue(rule.getConfidence() == 0.5); // all entailment relations have confidence 0.5 as defined on config file
			}
			//System.out.println("   - NONENTAILMENT"); 
			List<LexicalRule<? extends GermaNetInfo>> rules2 = gnw.getRulesForRight("Kauf", new GermanPartOfSpeech("N"), TERuleRelation.NonEntailment); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules2){
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getRLemma().equals("Kauf"));
				assertTrue(rule.getConfidence() == 0.0); // antonym confidence score is set to 0.0 in config file
			}
					
		} catch (LexicalResourceException e1) {
			e1.printStackTrace();
		}		
		
		// Test for confidence passing
		gnw = null; 
		try {// Initiating with 0 confidence on entails, causes, hypernym -> relations with 0 confidence are not included from resulting rules
			gnw = new GermaNetWrapper("/home/julia/Dokumente/HiWi/germanet/germanet-8.0/GN_V80_XML", 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
		}
		catch (GermaNetNotInstalledException e) {
			System.out.println("WARNING: GermaNet files are not found in the given path. Please correctly install and pass the path to GermaNetWrapper");
		}
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}
		Assume.assumeNotNull(gnw); // if gnw is null, the following tests will not be run. 
		
		//System.out.println("### run 3: check manually set confidence values");

		//System.out.println("** 1: getRules(leftlemma, leftpos, rightlemma, rightpos)"); 
		// test getRules(leftlemma, leftpos, rightlemma, rightpos)
		// -> only given lemmas and pos are found in results
		// -> given confidence in rules
		try {
			//System.out.println("   - nouns (N)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules1 = gnw.getRules("Haus", new GermanPartOfSpeech("N"), "Gebäude", new GermanPartOfSpeech("N")); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules1){
				assertTrue(rule.getRelation().equals("has_hypernym"));
				assertTrue(rule.getLLemma().equals("Haus"));
				assertTrue(rule.getRLemma().equals("Gebäude"));
				assertTrue(rule.getConfidence() == 0.0); // hypernymy has confidence 0.0
			}
			//System.out.println("   - nouns (NN)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules2 = gnw.getRules("Kauf", new GermanPartOfSpeech("NN"), "Verkauf", new GermanPartOfSpeech("NN")); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules2){
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getLLemma().equals("Kauf"));
				assertTrue(rule.getRLemma().equals("Verkauf"));
				assertTrue(rule.getConfidence() == 1.0); // antonymy confidence score is set to 1.0 
			}
			//System.out.println("   - verbs (V)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules3 = gnw.getRules("gehen", new GermanPartOfSpeech("V"), "erstrecken", new GermanPartOfSpeech("V")); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules3){
				assertTrue(rule.getRelation().equals("has_hypernym"));
				assertTrue(rule.getLLemma().equals("gehen"));
				assertTrue(rule.getRLemma().equals("erstrecken"));
				assertTrue(rule.getConfidence() == 0.0); // hypernymy confidence score is set to 0.0 
			}
			//System.out.println("   - adjectives (ADJ)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules4 = gnw.getRules("gut", new GermanPartOfSpeech("ADJ"), "gutartig", new GermanPartOfSpeech("ADJ"));
			for (LexicalRule<? extends GermaNetInfo> rule : rules4){
				assertTrue(rule.getRelation().equals("has_hyponym"));
				assertTrue(rule.getLLemma().equals("gut"));
				assertTrue(rule.getRLemma().equals("gutartig"));
				assertTrue(rule.getConfidence() == 1.0); // hyponymy confidence score is set to 1.0 
			}
			
		} catch (LexicalResourceException e1) {
			e1.printStackTrace();
		}		
		
		//System.out.println("** 2: getRules(leftlemma, leftpos, rightlemma, rightpos, GermaNetRelation)"); 
		// test getRules(leftlemma, leftpos, rightlemma, rightpos, GermaNetRelation)
		// -> only given lemmas, pos, relation are found in results
		// -> given confidence in rules
		try {
			//System.out.println("   - nouns (N)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules1 = gnw.getRules("Haus", new GermanPartOfSpeech("N"), "Gebäude", new GermanPartOfSpeech("N"), GermaNetRelation.has_hypernym); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules1){
				assertTrue(rule.getRelation().equals("has_hypernym"));
				assertTrue(rule.getLLemma().equals("Haus"));
				assertTrue(rule.getRLemma().equals("Gebäude"));
				assertTrue(rule.getConfidence() == 0.0); // hypernymy has confidence 0.0
			}
			//System.out.println("   - nouns (NN)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules2 = gnw.getRules("Kauf", new GermanPartOfSpeech("NN"), "Verkauf", new GermanPartOfSpeech("NN"), GermaNetRelation.has_antonym); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules2){
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getLLemma().equals("Kauf"));
				assertTrue(rule.getRLemma().equals("Verkauf"));
				assertTrue(rule.getConfidence() == 1.0); // antonymy confidence score is set to 1.0 
			}
			//System.out.println("   - verbs (V)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules3 = gnw.getRules("gehen", new GermanPartOfSpeech("V"), "erstrecken", new GermanPartOfSpeech("V"), GermaNetRelation.has_hypernym); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules3){
				assertTrue(rule.getRelation().equals("has_hypernym"));
				assertTrue(rule.getLLemma().equals("gehen"));
				assertTrue(rule.getRLemma().equals("erstrecken"));
				assertTrue(rule.getConfidence() == 0.0); // hypernymy confidence score is set to 0.0 
			}
			//System.out.println("   - adjectives (ADJ)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules4 = gnw.getRules("gut", new GermanPartOfSpeech("ADJ"), "gutartig", new GermanPartOfSpeech("ADJ"), GermaNetRelation.has_hyponym);
			for (LexicalRule<? extends GermaNetInfo> rule : rules4){
				assertTrue(rule.getRelation().equals("has_hyponym"));
				assertTrue(rule.getLLemma().equals("gut"));
				assertTrue(rule.getRLemma().equals("gutartig"));
				assertTrue(rule.getConfidence() == 1.0); // hyponymy confidence score is set to 1.0 
			}
			
		} catch (LexicalResourceException e1) {
			e1.printStackTrace();
		}		
		
		//System.out.println("** 3: getRules(leftlemma, leftpos, rightlemma, rightpos, TERuleRelation)"); 
		// test getRules(leftlemma, leftpos, rightlemma, rightpos, TERuleRelation)
		// -> only given lemmas and pos are found in results for entailment/nonentailment
		// -> given confidence in rules
		try {
			//System.out.println("   - nouns (N)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules1 = gnw.getRules("Haus", new GermanPartOfSpeech("N"), "Gebäude", new GermanPartOfSpeech("N"), TERuleRelation.Entailment); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules1){
				assertTrue(rule.getRelation().equals("has_hypernym"));
				assertTrue(rule.getLLemma().equals("Haus"));
				assertTrue(rule.getRLemma().equals("Gebäude"));
				assertTrue(rule.getConfidence() == 0.0); // hypernymy has confidence 0.0
			}
			//System.out.println("   - nouns (NN)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules2 = gnw.getRules("Kauf", new GermanPartOfSpeech("NN"), "Verkauf", new GermanPartOfSpeech("NN"), TERuleRelation.NonEntailment); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules2){
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getLLemma().equals("Kauf"));
				assertTrue(rule.getRLemma().equals("Verkauf"));
				assertTrue(rule.getConfidence() == 1.0); // antonymy confidence score is set to 1.0 
			}
			//System.out.println("   - verbs (V)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules3 = gnw.getRules("gehen", new GermanPartOfSpeech("V"), "erstrecken", new GermanPartOfSpeech("V"), TERuleRelation.Entailment); 
			for (LexicalRule<? extends GermaNetInfo> rule : rules3){
				assertTrue(rule.getRelation().equals("has_hypernym"));
				assertTrue(rule.getLLemma().equals("gehen"));
				assertTrue(rule.getRLemma().equals("erstrecken"));
				assertTrue(rule.getConfidence() == 0.0); // hypernymy confidence score is set to 0.0 
			}
			//System.out.println("   - adjectives (ADJ)"); 
			List<LexicalRule<? extends GermaNetInfo>> rules4 = gnw.getRules("gut", new GermanPartOfSpeech("ADJ"), "gutartig", new GermanPartOfSpeech("ADJ"), TERuleRelation.Entailment);
			for (LexicalRule<? extends GermaNetInfo> rule : rules4){
				assertTrue(rule.getRelation().equals("has_hyponym"));
				assertTrue(rule.getLLemma().equals("gut"));
				assertTrue(rule.getRLemma().equals("gutartig"));
				assertTrue(rule.getConfidence() == 1.0); // hyponymy confidence score is set to 1.0 
			}
			
		} catch (LexicalResourceException e1) {
			e1.printStackTrace();
		}		
	}
}
