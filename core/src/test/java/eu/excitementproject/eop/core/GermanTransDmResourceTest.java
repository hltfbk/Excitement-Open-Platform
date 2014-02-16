package eu.excitementproject.eop.core;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Assume;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.representation.partofspeech.GermanPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.core.component.lexicalknowledge.transDm.GermanTransDmException;
import eu.excitementproject.eop.core.component.lexicalknowledge.transDm.GermanTransDmInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.transDm.GermanTransDmResource;

/**
 * Tests the GermanTransDmResource in four different settings:
 * 1: using all similarity measures according to a given configuration file
 * 2: using all similarity measures by calling the resource directly
 * 3: using only one measure by calling the resource directly
 * 4: using an invalid measure (test exception handling)
 * 
 * Test scenario 1 is the vastest one, by testing calls via getRulesForLeft, 
 * getRulesForRight, and getRules, and by testing lemmas/lemma pairs which 
 * both exist and do not exist in the resource.  
 * 
 * Scenarios 2-4 are commented out to make testing quicker. 
 * 
 * @author Britta Zeller
 *
 */
public class GermanTransDmResourceTest {

	
	@Test
	public void test() throws UnsupportedPosTagStringException, LexicalResourceCloseException {
		
		/** TEST 1: USE CONFIGURATION FILE */

		
		//System.out.println("***************************************************************");
		//System.out.println("TEST 1");
		//System.out.println("***************************************************************");
		GermanTransDmResource transDm = null;

		try {
			transDm = new GermanTransDmResource(new 
					ImplCommonConfig(new File("./src/test/resources/german_resource_test_configuration.xml")));
		} catch (GermanTransDmException e) {
			System.err.println("WARNING: GermanTransDm file was not found in the given path.");
		
		} catch (ConfigurationException | ComponentException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(transDm);
		
		
		// getRulesForLeft for a N noun
		//System.out.println("lTest for Einkommen");
		List<LexicalRule<? extends GermanTransDmInfo>> list1 = null; 
		try {
			list1 = transDm.getRulesForLeft("Einkommen", new GermanPartOfSpeech("N")); 
			assertTrue(list1.size() > 0);
			int i = 0;
			for (LexicalRule<? extends GermanTransDmInfo> rule : list1) {
				assertTrue(rule.getLLemma().equals("Einkommen"));
				if (i < 50) {
					//System.out.print("one rightLemma for 'Einkommen': " + rule.getRLemma());
					//System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
				}
				i++;
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			

		// NULL RESULT getRulesForLeft for a N noun
		//System.out.println("lTest for Abcdefghijk");
		List<LexicalRule<? extends GermanTransDmInfo>> list1a = null; 
		try {
			list1a = transDm.getRulesForLeft("Abcdefghijk", new GermanPartOfSpeech("N")); 
			assertTrue(list1a.size() == 0);
			//System.out.println("Rule list size is " + list1a.size());
			for (LexicalRule<? extends GermanTransDmInfo> rule : list1a) {
				assertTrue(rule.getLLemma().equals("Abcdefghijk"));
				//System.out.print("SHOULD NOT BE SEEN: one rightLemma for 'Abcdefghijk': " + rule.getRLemma());
				//System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
				

		
		// getRulesForRight for a NN noun
		//System.out.println("rTest for Stelle");
		List<LexicalRule<? extends GermanTransDmInfo>> list2 = null; 
		try {
			list2 = transDm.getRulesForRight("Stelle", new GermanPartOfSpeech("NN")); 
			assertTrue(list2.size() > 0);
			for (LexicalRule<? extends GermanTransDmInfo> rule : list2) {
				assertTrue(rule.getRLemma().equals("Stelle"));
				//System.out.print("one leftLemma for 'Truppe': " + rule.getLLemma());
				//System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
		
			

		// NULL RESULT getRules for a V + N noun
		//System.out.println("rTest for tätigen");
		List<LexicalRule<? extends GermanTransDmInfo>> list3 = null; 
		try {
			list3 = transDm.getRules("tätigen", new GermanPartOfSpeech("V"), "Katze", new GermanPartOfSpeech("N")); 
			assertTrue(list3.size() == 0);
			//System.out.println("Rule list size is " + list3.size());
			//for (LexicalRule<? extends GermanTransDmInfo> rule : list3) {
				//System.out.print("SHOULD NOT BE SEEN: one leftLemma for 'tätigen': " + rule.getLLemma());
				//System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
			//}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		

		// RESULT getRules for 2x verb
		//System.out.println("rTest for tätigen");
		List<LexicalRule<? extends GermanTransDmInfo>> list3a = null; 
		try {
			list3a = transDm.getRules("tätigen", new GermanPartOfSpeech("V"), "sein", new GermanPartOfSpeech("V")); 
			assertTrue(list3a.size() > 0);
			//System.out.println("Rule list size is " + list3a.size());
			for (LexicalRule<? extends GermanTransDmInfo> rule : list3a) {
				assertTrue(rule.getLLemma().equals("tätigen") ^ rule.getRLemma().equals("tätigen"));
				//System.out.print("one righttLemma for 'tätigen': " + rule.getRLemma());
				//System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}	
		
		//getRulesForLeft for null pos
//				System.out.println("Test for null pos");
				List<LexicalRule<? extends GermanTransDmInfo>> list7 = null; 
				try {
					list7 = transDm.getRules("Telekommunikation", null, "wirtschaftlich", null); 
					assertTrue(list7.size() > 0);
//					System.out.println("Rule list size is " + list7.size());
					for (LexicalRule<? extends GermanTransDmInfo> rule : list7) {
						assertTrue(rule.getLLemma().equals("Telekommunikation"));
						assertTrue(rule.getRLemma().equals("wirtschaftlich"));
//						System.out.println(rule.toString());
					}						
				}
				catch (LexicalResourceException e)
				{
					e.printStackTrace(); 
				}	
				
		
		/*
		// getRulesForLeft for a N noun
		System.out.println("lTest for XXX");
		List<LexicalRule<? extends GermanTransDmInfo>> list4 = null; 
		try {
			list4 = transDm.getRulesForLeft("XXX", new GermanPartOfSpeech("N")); 
			assertTrue(list4.size() > 0);
			for (LexicalRule<? extends GermanTransDmInfo> rule : list4) {
				assertTrue(rule.getLLemma().equals("XXX"));
				System.out.print("one rightLemma for 'XXX': " + rule.getRLemma());
				System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		*/
		
		transDm.close();
		
		//////////////////////////////////////////////////////////////////////////////////////////
		
		/** TEST 2: USE ALL MEASURES DIRECTLY */
/*
		//System.out.println("***************************************************************");
		//System.out.println("TEST 2");
		//System.out.println("***************************************************************");
		GermanTransDmResource transDmDirect1 = null;

		try {
			transDmDirect1 = new GermanTransDmResource("all");
		} catch (GermanTransDmException e) {
			System.err.println("WARNING: GermanTransDm file was not found in the given path.");
		
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(transDmDirect1);
		
		
		
		//System.out.println("THIS SHOULD OUTPUT THE SAME AS THE TEST BEFORE: ");
		// RESULT getRules for a V + V verb
		//System.out.println("rTest for tätigen");
		List<LexicalRule<? extends GermanTransDmInfo>> list5 = null; 
		try {
			list5 = transDmDirect1.getRules("tätigen", new GermanPartOfSpeech("V"), "sein", new GermanPartOfSpeech("V")); 
			assertTrue(list5.size() > 0);
			//System.out.println("Rule list size is " + list5.size());
			for (LexicalRule<? extends GermanTransDmInfo> rule : list5) {
				assertTrue(rule.getLLemma().equals("tätigen") ^ rule.getRLemma().equals("tätigen"));
				//System.out.print("one rightLemma for 'tätigen': " + rule.getRLemma());
				//System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}				
	
		transDmDirect1.close();
		
*/
		//////////////////////////////////////////////////////////////////////////////////////////
		

		/** TEST 3: USE ONE MEASURE DIRECTLY */
/*
		//System.out.println("***************************************************************");
		//System.out.println("TEST 3");
		//System.out.println("***************************************************************");
		GermanTransDmResource transDmDirect2 = null;

		try {
			transDmDirect2 = new GermanTransDmResource("cosine");
		} catch (GermanTransDmException e) {
			System.err.println("WARNING: GermanTransDm file was not found in the given path.");
		
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(transDmDirect2);
		
		
		
		//System.out.println("THIS SHOULD OUTPUT LESS RESULTS THAN THE TEST BEFORE: ");
		// RESULT getRules for a V+V
		//System.out.println("rTest for tätigen");
		List<LexicalRule<? extends GermanTransDmInfo>> list6 = null; 
		try {
			list6 = transDmDirect2.getRules("tätigen", new GermanPartOfSpeech("V"), "sein", new GermanPartOfSpeech("V")); 
			assertTrue(list6.size() > 0);
			//System.out.println("Rule list size is " + list6.size());
			for (LexicalRule<? extends GermanTransDmInfo> rule : list6) {
				assertTrue(rule.getLLemma().equals("tätigen") ^ rule.getRLemma().equals("tätigen"));
				//System.out.print("one leftLemma for 'tätigen': " + rule.getLLemma());
				//System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		transDmDirect2.close();
*/	
		//////////////////////////////////////////////////////////////////////////////////////////
		
		
		/** TEST 4: USE INVALID MEASURE DIRECTLY */
/*		
		//System.out.println("***************************************************************");
		//System.out.println("TEST 4");
		//System.out.println("***************************************************************");
		GermanTransDmResource transDmDirect3 = null;

		try {
			transDmDirect3 = new GermanTransDmResource("abcdefg");
		} catch (GermanTransDmException e) {
			System.err.println("WARNING: GermanTransDm file was not found in the given path.");
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		// The test halts here if transDmDirect3 is null (this should be the case)
		Assume.assumeNotNull(transDmDirect3);
		
		//System.out.println("THIS LINE AND FOLLOWING SHOULD NOT BE SEEN!");
		
		// RESULT getRules for a J adjective + N noun
		//System.out.println("rTest for tätigen");
		List<LexicalRule<? extends GermanTransDmInfo>> listN = null; 
		try {
			listN = transDmDirect3.getRules("tätigen", new GermanPartOfSpeech("V"), "Einkommen", new GermanPartOfSpeech("N")); 
			assertTrue(listN.size() > 0);
			//System.out.println("Rule list size is " + listN.size());
			for (LexicalRule<? extends GermanTransDmInfo> rule : listN) {
				assertTrue(rule.getRLemma().equals("tätigen"));
				//System.out.print("one leftLemma for 'tätigen': " + rule.getLLemma());
				//System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		transDmDirect3.close();
*/		
	}
}
