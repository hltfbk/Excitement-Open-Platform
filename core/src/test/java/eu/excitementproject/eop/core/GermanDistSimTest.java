
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
		
		GermanDistSim gds = null;
		try {
			gds = new GermanDistSim("src/main/resources/dewakdistributional-data");
		}
		catch (GermanDistSimNotInstalledException e) {
			System.out.println("WARNING: GermanDistSim files are not found. Please install them properly, and pass its location correctly to the component.");
			//throw e;
		}
		catch (BaseException e)
		{
			e.printStackTrace(); 
		}

		Assume.assumeNotNull(gds); 
		
		try {
			for (LexicalRule<? extends GermanDistSimInfo> rule : gds.getRulesForLeft("sie", null)) {
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
		gds=null; 
		try {
			File f = new File("./src/main/resources/german_resource_test_configuration.xml");
			gds = new GermanDistSim(new ImplCommonConfig(f)); 
		}
		catch (GermanDistSimNotInstalledException e) {
			System.out.println("WARNING: GermanDistSim files are not found. Please install them properly, and pass its location correctly in the CommonConfig, that is passed to the component.");
			//throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace(); 
		}
		
		//Assume.assumeNotNull(gds); // Let's assume the file is correct. no need. 
		
		try {
			for (LexicalRule<? extends GermanDistSimInfo> rule : gds.getRulesForLeft("sie", null)) {
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

