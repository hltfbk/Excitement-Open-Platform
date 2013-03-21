
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Assume;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.exception.BaseException;
// LexicalResource imports
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSim;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSimInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSimNotInstalledException;


/**
 * @author Jan Pawellek 
 *
 */
public class GermanDistSimTest {
	
	@Test 
	public void test() /* throws java.lang.Exception */ {
		
		//for 1k dewak-distributional data
		GermanDistSim gds1 = null;
		try {
			gds1 = new GermanDistSim("src/main/resources/dewakdistributional-data");
		}
		catch (GermanDistSimNotInstalledException e) {
			System.out.println("WARNING: GermanDistSim files are not found. Please install them properly, and pass its location correctly to the component.");
			//throw e;
		}
		catch (BaseException e)
		{
			e.printStackTrace(); 
		}

		Assume.assumeNotNull(gds1); 
		
		try {
			for (LexicalRule<? extends GermanDistSimInfo> rule : gds1.getRulesForLeft("sie", null)) {
				assertTrue(rule.getLLemma().equals("sie"));
				assertFalse(rule.getRLemma().equals(""));
				assertFalse(rule.getRelation().equals(""));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}

		/* Testing init via CommonConfig */ 
		gds1=null; 
		try {
			// An example configuration file that only holds two German lexical component
			// configuration. 
			File f = new File("./src/test/resources/german_resource_test_configuration.xml");
			gds1 = new GermanDistSim(new ImplCommonConfig(f)); 
		}
		catch (GermanDistSimNotInstalledException e) {
			System.out.println("WARNING: GermanDistSim files are not found. Please install them properly, and pass its location correctly in the CommonConfig, that is passed to the component.");
			//throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace(); 
		}
		assertNotNull(gds1); 
		//Assume.assumeNotNull(gds); // Let's assume the file is correct. no need. 
		
		try {
			for (LexicalRule<? extends GermanDistSimInfo> rule : gds1.getRulesForLeft("sie", null)) {
				assertTrue(rule.getLLemma().equals("sie"));
				assertFalse(rule.getRLemma().equals(""));
				assertFalse(rule.getRelation().equals(""));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		
		//for 10k dewak-distributional data
		GermanDistSim gds10 = null;
		try {
			gds10 = new GermanDistSim("src/main/resources/dewakdistributional-data-10k");
		}
		catch (GermanDistSimNotInstalledException e) {
			System.out.println("WARNING: GermanDistSim files are not found. Please install them properly, and pass its location correctly to the component.");
			//throw e;
		}
		catch (BaseException e)
		{
			e.printStackTrace(); 
		}

		Assume.assumeNotNull(gds10); 
		
		try {
			for (LexicalRule<? extends GermanDistSimInfo> rule : gds10.getRulesForLeft("sie", null)) {
				assertTrue(rule.getLLemma().equals("sie"));
				assertFalse(rule.getRLemma().equals(""));
				assertFalse(rule.getRelation().equals(""));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}

		/* Testing init via CommonConfig */ 
		gds10=null; 
		try {
			// An example configuration file that only holds two German lexical component
			// configuration. 
			File f = new File("./src/test/resources/german_resource_test_configuration.xml");
			gds10 = new GermanDistSim(new ImplCommonConfig(f)); 
		}
		catch (GermanDistSimNotInstalledException e) {
			System.out.println("WARNING: GermanDistSim files are not found. Please install them properly, and pass its location correctly in the CommonConfig, that is passed to the component.");
			//throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace(); 
		}
		assertNotNull(gds10); 
		//Assume.assumeNotNull(gds10); // Let's assume the file is correct. no need. 
		
		try {
			for (LexicalRule<? extends GermanDistSimInfo> rule : gds10.getRulesForLeft("sie", null)) {
				assertTrue(rule.getLLemma().equals("sie"));
				assertFalse(rule.getRLemma().equals(""));
				assertFalse(rule.getRelation().equals(""));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}

	}
}

