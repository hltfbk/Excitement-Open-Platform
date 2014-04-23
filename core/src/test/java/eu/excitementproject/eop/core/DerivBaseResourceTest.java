
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Assume;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.exception.BaseException;
import eu.excitementproject.eop.core.component.lexicalknowledge.derivbase.DerivBaseInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.derivbase.DerivBaseNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.derivbase.DerivBaseResource;
import eu.excitementproject.eop.common.representation.partofspeech.GermanPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;



/**
 * @author Britta Zeller
 *
 */
public class DerivBaseResourceTest {

	@Test
	public void test() throws UnsupportedPosTagStringException {

		/**
		 * ****************************
		 * TEST 1: DErivBase format without scores, called directly
		 * ****************************
		 */
		//System.out.println("******* TEST 1: DErivBase format without scores, called directly *******");
		
		DerivBaseResource dbWithoutScores = null;
		try {
			dbWithoutScores = new DerivBaseResource(false, null);
		}
		catch (DerivBaseNotInstalledException e) {
			System.out.println("WARNING: DErivBase file was not found in the given path.");
		}
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}
		Assume.assumeNotNull(dbWithoutScores); // if db is null, the following tests will not be run. 
	
		// getRulesForLeft for a N noun
		//System.out.println("lTest for Ziehung");
		List<LexicalRule<? extends DerivBaseInfo>> list1 = null; 
		try {
			list1 = dbWithoutScores.getRulesForLeft("Ziehung", new GermanPartOfSpeech("N")); 
			assertTrue(list1.size() > 0);
			for (LexicalRule<? extends DerivBaseInfo> rule : list1) {
				assertTrue(rule.getLLemma().equals("Ziehung"));
				//System.out.println("one rightLemma for 'Ziehung': " + rule.getRLemma());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
				
		// getRulesForRight for a verb 
		//System.out.println("rTest for wachsen");
		List<LexicalRule<? extends DerivBaseInfo>> list2 = null; 
		try {
			list2 = dbWithoutScores.getRulesForRight("wachsen", new GermanPartOfSpeech("VINF")); 
			assertTrue(list2.size() > 0);
			for (LexicalRule<? extends DerivBaseInfo> rule : list2) {
				assertTrue(rule.getRLemma().equals("wachsen"));
				//System.out.println("one leftLemma for 'wachsen': " + rule.getLLemma());
			}			
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
		
		// getRules for a NN+N
		//System.out.println("rulesTest for Beziehung + Erzieherin");
		List<LexicalRule<? extends DerivBaseInfo>> list3 = null; 
		try {
			list3 = dbWithoutScores.getRules("Beziehung", new GermanPartOfSpeech("N"), "Erzieherin", new GermanPartOfSpeech("NN"));
			assertTrue(list3.size() > 0);
			assertTrue(list3.size() == 1);
			for (LexicalRule<? extends DerivBaseInfo> rule : list3) {
				assertTrue(rule.getLLemma().equals("Beziehung"));
				//System.out.println("one getRules rule (should be only one) for nn+n: " + rule.toString());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
					
		// getRules for a NN+ADJ
		//System.out.println("rulesTest for Beziehung + erziehbar");
		List<LexicalRule<? extends DerivBaseInfo>> list4 = null; 
		try {
			list4 = dbWithoutScores.getRules("Beziehung", new GermanPartOfSpeech("N"), "erziehbar", new GermanPartOfSpeech("ADJ"));
			assertTrue(list4.size() > 0);
			assertTrue(list4.size() == 1);
			for (LexicalRule<? extends DerivBaseInfo> rule : list4) {
				assertTrue(rule.getLLemma().equals("Beziehung"));
				//System.out.println("one getRules rule (should be only one) for nn+adj: " + rule.toString());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
	
		// Negative test for verbs to show the given POS is used
		try{
			//System.out.println("Test for wachsen_NN");
			List<LexicalRule<? extends DerivBaseInfo>> rule = dbWithoutScores.getRulesForLeft("wachsen", new GermanPartOfSpeech("NN"));
			assertTrue(rule.isEmpty());
			//System.out.println("no rule should come up for wrong POS: " + rule.toString());
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
				
		// Test of not-supported POS type (should return an empty list) 
		try {
			//System.out.println("Test for Hitze_PTKA");
			List<LexicalRule<? extends DerivBaseInfo>> l = dbWithoutScores.getRulesForLeft("Hitze", new GermanPartOfSpeech("PTKA")); 
			assertTrue(l.size() == 0); 
			// Still, null POS should mean, don't care
			//System.out.println("no rule should come up for unsupported POS: " + l.toString());
			//System.out.println("Test for Hitze_null");
			l = dbWithoutScores.getRulesForLeft("Hitze",  null); 
			assertTrue(l.size() > 0);
			//System.out.println("at least one rule should come up for null POS: " + l.toString());
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		dbWithoutScores = null; //set null to help GC

		
		/**
		 * ****************************
		 * TEST 2: DErivBase format without scores, called via CommonConfig
		 * ****************************
		 */
		//System.out.println("******* TEST 2: DErivBase format without scores, called via CommonConfig *******");

		
		DerivBaseResource dbWithoutScoresCommConf = null;
		try {
			dbWithoutScoresCommConf = new DerivBaseResource(new ImplCommonConfig(new File("./src/test/resources/german_resource_test_configuration.xml")));
		}
		catch (DerivBaseNotInstalledException e) {
			System.out.println("WARNING: DErivBase file was not found in the given path.");
		}
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}
		Assume.assumeNotNull(dbWithoutScoresCommConf); // if db is null, the following tests will not be run. 
	
		// getRulesForLeft for a N noun
		//System.out.println("lTest for Abwehrspiel");
		List<LexicalRule<? extends DerivBaseInfo>> list5 = null; 
		try {
			list5 = dbWithoutScoresCommConf.getRulesForLeft("Abwehrspiel", new GermanPartOfSpeech("N")); 
			assertTrue(list5.size() > 0);
			for (LexicalRule<? extends DerivBaseInfo> rule : list5) {
				assertTrue(rule.getLLemma().equals("Abwehrspiel"));
				//System.out.println("one rightLemma for 'Abwehrspiel': " + rule.getRLemma());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
				
		// getRulesForRight for a verb 
		//System.out.println("rTest for abzeichnen");
		List<LexicalRule<? extends DerivBaseInfo>> list6 = null; 
		try {
			list6 = dbWithoutScoresCommConf.getRulesForRight("abzeichnen", new GermanPartOfSpeech("VINF")); 
			assertTrue(list6.size() > 0);
			for (LexicalRule<? extends DerivBaseInfo> rule : list6) {
				assertTrue(rule.getRLemma().equals("abzeichnen"));
				//System.out.println("one leftLemma for 'abzeichnen': " + rule.getLLemma());
			}			
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
		
		// getRules for a NN+N
		//System.out.println("rulesTest for Afrikameisterschaft + Afrikameister");
		List<LexicalRule<? extends DerivBaseInfo>> list7 = null; 
		try {
			list7 = dbWithoutScoresCommConf.getRules("Afrikameisterschaft", new GermanPartOfSpeech("N"), "Afrikameister", new GermanPartOfSpeech("NN"));
			assertTrue(list7.size() > 0);
			assertTrue(list7.size() == 1);
			for (LexicalRule<? extends DerivBaseInfo> rule : list7) {
				assertTrue(rule.getLLemma().equals("Afrikameisterschaft"));
				//System.out.println("one getRules rule (should be only one) for nn+n: " + rule.toString());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}	
		
		// Negative test for verbs to show the given POS is used
		try{
			//System.out.println("Test for affig_NN");
			List<LexicalRule<? extends DerivBaseInfo>> rule = dbWithoutScoresCommConf.getRulesForLeft("affig", new GermanPartOfSpeech("NN"));
			assertTrue(rule.isEmpty());
			//System.out.println("no rule should come up for wrong POS: " + rule.toString());
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
				
		// Test of not-supported POS type (should return an empty list) 
		try {
			//System.out.println("Test for affig_PTKA");
			List<LexicalRule<? extends DerivBaseInfo>> l = dbWithoutScoresCommConf.getRulesForLeft("affig", new GermanPartOfSpeech("PTKA")); 
			assertTrue(l.size() == 0); 
			// Still, null POS should mean, don't care
			//System.out.println("no rule should come up for unsupported POS: " + l.toString());
			//System.out.println("Test for Hitze_null");
			l = dbWithoutScoresCommConf.getRulesForLeft("Hitze",  null); 
			assertTrue(l.size() > 0);
			//System.out.println("at least one rule should come up for null POS: " + l.toString());
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		dbWithoutScoresCommConf = null; //set null to help GC
		
		/**
		 * ****************************
		 * TEST 3: DErivBase format with scores, called directly
		 * ****************************
		 */
		//System.out.println("******* TEST 3: DErivBase format with scores, called directly *******");

		
		DerivBaseResource dbWithScores = null;
		try {
			dbWithScores = new DerivBaseResource(true, 10);
		}
		catch (DerivBaseNotInstalledException e) {
			System.out.println("WARNING: DErivBase file was not found in the given path.");
		}
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}
		Assume.assumeNotNull(dbWithScores); // if db is null, the following tests will not be run. 
	
		// getRulesForLeft for a N noun
		//System.out.println("lTest for Abwehrspiel");
		List<LexicalRule<? extends DerivBaseInfo>> list8 = null; 
		try {
			list8 = dbWithScores.getRulesForLeft("Abwehrspiel", new GermanPartOfSpeech("N")); 
			assertTrue(list8.size() > 0);
			for (LexicalRule<? extends DerivBaseInfo> rule : list8) {
				assertTrue(rule.getLLemma().equals("Abwehrspiel"));
				//System.out.println("one rule for 'Abwehrspiel': " + rule.toString());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
				
		// getRulesForRight for a verb 
		//System.out.println("rTest for aalen");
		List<LexicalRule<? extends DerivBaseInfo>> list9 = null; 
		try {
			list9 = dbWithScores.getRulesForRight("aalen", new GermanPartOfSpeech("VINF")); 
			assertTrue(list9.size() > 0);
			for (LexicalRule<? extends DerivBaseInfo> rule : list9) {
				assertTrue(rule.getRLemma().equals("aalen"));
				//System.out.println("one rule for 'aalen': " + rule.toString());
			}			
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
		
		// getRules for a NN+N
		//System.out.println("rulesTest for Afrikameisterschaft + Afrikameister");
		List<LexicalRule<? extends DerivBaseInfo>> list10 = null; 
		try {
			list10 = dbWithScores.getRules("Afrikameisterschaft", new GermanPartOfSpeech("N"), "Afrikameister", new GermanPartOfSpeech("NN"));
			assertTrue(list10.size() > 0);
			assertTrue(list10.size() == 1);
			for (LexicalRule<? extends DerivBaseInfo> rule : list10) {
				assertTrue(rule.getLLemma().equals("Afrikameisterschaft"));
				//System.out.println("one getRules rule (should be only one) for nn+n: " + rule.toString());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
		
		// getRules for a NN+N
		//System.out.println("rulesTest for Bettlerin + Erbitterte");
		List<LexicalRule<? extends DerivBaseInfo>> list11 = null; 
		try {
			list11 = dbWithScores.getRules("Bettlerin", new GermanPartOfSpeech("N"), "Erbitterte", new GermanPartOfSpeech("NN"));
			assertTrue(list11.size() > 0);
			assertTrue(list11.size() == 1);
			for (LexicalRule<? extends DerivBaseInfo> rule : list11) {
				assertTrue(rule.getLLemma().equals("Bettlerin"));
				//System.out.println("one getRules rule (should be only one) for nn+n: " + rule.toString());
			}						
		}		
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
					
		// Negative test for verbs to show the given POS is used
		try{
			//System.out.println("Test for affig_NN");
			List<LexicalRule<? extends DerivBaseInfo>> rule = dbWithScores.getRulesForLeft("affig", new GermanPartOfSpeech("NN"));
			assertTrue(rule.isEmpty());
			//System.out.println("no rule should come up for wrong POS: " + rule.toString());
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
				
		// Test of not-supported POS type (should return an empty list) 
		try {
			//System.out.println("Test for affig_PTKA");
			List<LexicalRule<? extends DerivBaseInfo>> l = dbWithScores.getRulesForLeft("affig", new GermanPartOfSpeech("PTKA")); 
			assertTrue(l.size() == 0); 
			// Still, null POS should mean, don't care
			//System.out.println("no rule should come up for unsupported POS: " + l.toString());
			//System.out.println("Test for Hitze_null");
			l = dbWithScores.getRulesForLeft("Hitze",  null); 
			assertTrue(l.size() > 0);
			//System.out.println("at least one rule should come up for null POS: " + l.toString());
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		dbWithScores = null; //set null to help GC
	}
}

