
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;

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
 * @author Jan Pawellek & Julia Kreutzer
 *
 */
public class GermanDistSimTest {
	//This intends to illustrate the superiority of the 10k distributional data:
	//assume that a word can only be found in 10k data, not in 1k data 
	//choose word for testing, e.g.
	//"sie", "werden", "und" - for failed test runs (words found in both or only 1k data)
	//"Fauna", "Forschungsreaktor", "sonderpädagogisch" - for a successful test run
	
	private String testword = "sonderpädagogisch"; //word to be tested - found in distributional data?
	
	@Test
	public void test1() /* throws java.lang.Exception */ {
		
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
			try {
				if (! gds1.getRulesForLeft(testword, null).isEmpty()){
					System.out.println('"'+testword+'"'+" found in 1k data");
				}
				else {System.out.println('"'+testword+'"'+" not found in 1k data");}
				assertTrue(gds1.getRulesForRight(testword, null).isEmpty());
			} catch (LexicalResourceException e1) {
				e1.printStackTrace();
			}
			for (LexicalRule<? extends GermanDistSimInfo> rule : gds1.getRulesForLeft(testword, null)) {
				assertTrue(rule.getLLemma().equals(testword));
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
	
	@Test
	public void test10() /* throws java.lang.Exception */ {
		
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
			try {
				if (gds10.getRulesForLeft(testword, null).isEmpty()){
					System.out.println('"'+testword+'"'+" not found in 10k data");
				}
				else {System.out.println('"'+testword+'"'+" found in 10k data");}
				assertFalse(gds10.getRulesForRight(testword, null).isEmpty());
			} catch (LexicalResourceException e1) {
				e1.printStackTrace();
			}
			for (LexicalRule<? extends GermanDistSimInfo> rule : gds10.getRulesForRight(testword, null)) {
				assertTrue(rule.getLLemma().equals(testword));
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

