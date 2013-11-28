package eu.excitementproject.eop.core;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Assume;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.representation.partofspeech.GermanPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewacTransDm.GermanTransDmInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewacTransDm.GermanTransDmNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewacTransDm.GermanTransDmResource;


public class GermanTransDmResourceTest {

	
	@Test
	public void test() throws UnsupportedPosTagStringException {

		
		/** TEST 1: USE CONFIGURATION FILE */

		
		System.out.println("***************************************************************");
		System.out.println("TEST 1");
		System.out.println("***************************************************************");
		GermanTransDmResource transDm = null;

		try {
			transDm = new GermanTransDmResource(new 
					ImplCommonConfig(new File("./src/test/resources/german_resource_test_configuration.xml")));
		} catch (GermanTransDmNotInstalledException e) {
			System.err.println("WARNING: GermanTransDm file was not found in the given path.");
		
		} catch (ConfigurationException | ComponentException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(transDm);
		
		
		// getRulesForLeft for a N noun
		System.out.println("lTest for Einkommen");
		List<LexicalRule<? extends GermanTransDmInfo>> list1 = null; 
		try {
			list1 = transDm.getRulesForLeft("Einkommen", new GermanPartOfSpeech("N")); 
			assertTrue(list1.size() > 0);
			int i = 0;
			for (LexicalRule<? extends GermanTransDmInfo> rule : list1) {
				assertTrue(rule.getLLemma().equals("Einkommen"));
				if (i < 50) {
					System.out.print("one rightLemma for 'Einkommen': " + rule.getRLemma());
					System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
				}
				i++;
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			

		// NULL RESULT getRulesForLeft for a N noun
		System.out.println("lTest for Abcdefghijk");
		List<LexicalRule<? extends GermanTransDmInfo>> list1a = null; 
		try {
			list1a = transDm.getRulesForLeft("Abcdefghijk", new GermanPartOfSpeech("N")); 
			assertTrue(list1a.size() == 0);
			System.out.println("Rule list size is " + list1a.size());
			for (LexicalRule<? extends GermanTransDmInfo> rule : list1a) {
				assertTrue(rule.getLLemma().equals("Abcdefghijk"));
				System.out.print("SHOULD NOT BE SEEN: one rightLemma for 'Abcdefghijk': " + rule.getRLemma());
				System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
				

		
		// getRulesForRight for a NN noun
		System.out.println("rTest for Truppe");
		List<LexicalRule<? extends GermanTransDmInfo>> list2 = null; 
		try {
			list2 = transDm.getRulesForRight("Truppe", new GermanPartOfSpeech("NN")); 
			assertTrue(list2.size() > 0);
			for (LexicalRule<? extends GermanTransDmInfo> rule : list2) {
				assertTrue(rule.getRLemma().equals("Truppe"));
				System.out.print("one leftLemma for 'Truppe': " + rule.getLLemma());
				System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
		
			

		// NULL RESULT getRules for a J adjective + N noun
		System.out.println("rTest for witzig");
		List<LexicalRule<? extends GermanTransDmInfo>> list3 = null; 
		try {
			list3 = transDm.getRules("witzig", new GermanPartOfSpeech("ADJ"), "Katze", new GermanPartOfSpeech("N")); 
			assertTrue(list3.size() == 0);
			System.out.println("Rule list size is " + list3.size());
			for (LexicalRule<? extends GermanTransDmInfo> rule : list3) {
				System.out.print("SHOULD NOT BE SEEN: one leftLemma for 'witzig': " + rule.getLLemma());
				System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		
		// RESULT getRules for a J adjective + N noun
		System.out.println("rTest for witzig");
		List<LexicalRule<? extends GermanTransDmInfo>> list3a = null; 
		try {
			list3a = transDm.getRules("witzig", new GermanPartOfSpeech("ADJ"), "Einkommen", new GermanPartOfSpeech("N")); 
			assertTrue(list3a.size() > 0);
			System.out.println("Rule list size is " + list3a.size());
			for (LexicalRule<? extends GermanTransDmInfo> rule : list3a) {
				assertTrue(rule.getLLemma().equals("witzig") ^ rule.getRLemma().equals("witzig"));
				System.out.print("one righttLemma for 'witzig': " + rule.getRLemma());
				System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
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
		
		//////////////////////////////////////////////////////////////////////////////////////////
		
		/** TEST 2: USE ALL MEASURES DIRECTLY */

		System.out.println("***************************************************************");
		System.out.println("TEST 2");
		System.out.println("***************************************************************");
		GermanTransDmResource transDmDirect1 = null;

		try {
			transDmDirect1 = new GermanTransDmResource("all");
		} catch (GermanTransDmNotInstalledException e) {
			System.err.println("WARNING: GermanTransDm file was not found in the given path.");
		
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(transDmDirect1);
		
		
		
		System.out.println("THIS SHOULD OUTPUT THE SAME AS THE TEST BEFORE: ");
		// RESULT getRules for a J adjective + N noun
		System.out.println("rTest for witzig");
		List<LexicalRule<? extends GermanTransDmInfo>> list5 = null; 
		try {
			list5 = transDmDirect1.getRules("witzig", new GermanPartOfSpeech("ADJ"), "Einkommen", new GermanPartOfSpeech("N")); 
			assertTrue(list5.size() > 0);
			System.out.println("Rule list size is " + list5.size());
			for (LexicalRule<? extends GermanTransDmInfo> rule : list5) {
				assertTrue(rule.getLLemma().equals("witzig") ^ rule.getRLemma().equals("witzig"));
				System.out.print("one rightLemma for 'witzig': " + rule.getRLemma());
				System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}				
	
	
		//////////////////////////////////////////////////////////////////////////////////////////
		

		/** TEST 3: USE ONE MEASURE DIRECTLY */

		System.out.println("***************************************************************");
		System.out.println("TEST 3");
		System.out.println("***************************************************************");
		GermanTransDmResource transDmDirect2 = null;

		try {
			transDmDirect2 = new GermanTransDmResource("balapinc");
		} catch (GermanTransDmNotInstalledException e) {
			System.err.println("WARNING: GermanTransDm file was not found in the given path.");
		
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		Assume.assumeNotNull(transDmDirect2);
		
		
		
		System.out.println("THIS SHOULD OUTPUT LESS RESULTS THAN THE TEST BEFORE: ");
		// RESULT getRules for a J adjective + N noun
		System.out.println("rTest for witzig");
		List<LexicalRule<? extends GermanTransDmInfo>> list6 = null; 
		try {
			list6 = transDmDirect2.getRules("witzig", new GermanPartOfSpeech("ADJ"), "Einkommen", new GermanPartOfSpeech("N")); 
			assertTrue(list6.size() > 0);
			System.out.println("Rule list size is " + list6.size());
			for (LexicalRule<? extends GermanTransDmInfo> rule : list6) {
				assertTrue(rule.getLLemma().equals("witzig") ^ rule.getRLemma().equals("witzig"));
				System.out.print("one leftLemma for 'witzig': " + rule.getLLemma());
				System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		//////////////////////////////////////////////////////////////////////////////////////////
		
		
		/** TEST 4: USE INVALID MEASURE DIRECTLY */
		
		System.out.println("***************************************************************");
		System.out.println("TEST 4");
		System.out.println("***************************************************************");
		GermanTransDmResource transDmDirect3 = null;

		try {
			transDmDirect3 = new GermanTransDmResource("abcdefg");
		} catch (GermanTransDmNotInstalledException e) {
			System.err.println("WARNING: GermanTransDm file was not found in the given path.");
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		// The test halts here if transDmDirect3 is null (this should be the case)
		Assume.assumeNotNull(transDmDirect3);
		
		System.out.println("THIS LINE AND FOLLOWIGN SHOULD NOT BE SEEN!");
		
		// RESULT getRules for a J adjective + N noun
		System.out.println("rTest for witzig");
		List<LexicalRule<? extends GermanTransDmInfo>> listN = null; 
		try {
			listN = transDmDirect3.getRules("witzig", new GermanPartOfSpeech("ADJ"), "Einkommen", new GermanPartOfSpeech("N")); 
			assertTrue(listN.size() > 0);
			System.out.println("Rule list size is " + listN.size());
			for (LexicalRule<? extends GermanTransDmInfo> rule : listN) {
				assertTrue(rule.getRLemma().equals("witzig"));
				System.out.print("one leftLemma for 'witzig': " + rule.getLLemma());
				System.out.println(" , corresponding score (" + rule.getRelation() + "): " + rule.getConfidence());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		
	}
}
