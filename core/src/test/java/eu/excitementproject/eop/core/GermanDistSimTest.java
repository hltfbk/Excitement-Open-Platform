
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;

//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Set;

import org.junit.Assume;
//import org.junit.Ignore;
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
	//This intends to illustrate the superiority of the 10k distributional data:
	//assume that a word can only be found in 10k data, not in 1k data 
	//choose word for testing, e.g.
	//"sie", "werden", "und" - for failed test runs (words found in both or only 1k data)
	//"Fauna", "Forschungsreaktor", "sonderpädagogisch" - for a successful test run
	
	private String testword = "Abfallbehandlung"; //word to be tested - found in distributional data?
	
	
	// Gil: Note that we no longer support 1k data loading. (See constructor) 
//	@Ignore 
//	@Test
//	public void test1() /* throws java.lang.Exception */ {
//		
//		//for 1k dewak-distributional data
//		GermanDistSim gds1 = null;
//		try {
//			gds1 = new GermanDistSim("src/main/resources/dewakdistributional-data");
//		}
//		catch (GermanDistSimNotInstalledException e) {
//			System.out.println("WARNING: GermanDistSim files are not found. Please install them properly, and pass its location correctly to the component.");
//			//throw e;
//		}
//		catch (BaseException e)
//		{
//			e.printStackTrace(); 
//		}
//
//		Assume.assumeNotNull(gds1); 
//		
//		try {
//			try {
//				if (! gds1.getRulesForLeft(testword, null).isEmpty()){
//					System.out.println('"'+testword+'"'+" found in 1k data");
//				}
//				else {System.out.println('"'+testword+'"'+" not found in 1k data");}
//				assertTrue(gds1.getRulesForRight(testword, null).isEmpty());
//			} catch (LexicalResourceException e1) {
//				e1.printStackTrace();
//			}
//			for (LexicalRule<? extends GermanDistSimInfo> rule : gds1.getRulesForLeft(testword, null)) {
//				assertTrue(rule.getLLemma().equals(testword));
//				assertFalse(rule.getRLemma().equals(""));
//				assertFalse(rule.getRelation().equals(""));
//				assertTrue(rule.getConfidence() > 0);
//			}
//		}
//		catch (LexicalResourceException e)
//		{
//			e.printStackTrace(); 
//		}
//	}
	
	@Test
	public void test10() /* throws java.lang.Exception */ {
		
		//for 10k dewak-distributional data
		GermanDistSim gds10 = null;
		try {
			//gds10 = new GermanDistSim("src/main/resources/dewakdistributional-data-10k");
			gds10 = new GermanDistSim(); 
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
			//System.out.println("here should come nothing for 'Abfallbehandlung';");
			for (LexicalRule<? extends GermanDistSimInfo> rule : gds10.getRulesForLeft(testword, null)) {
				//System.out.println("lLemma " + rule.getLLemma() + ", rLemma " + rule.getRLemma());
				assertTrue(rule.getLLemma().equals(testword));
				assertFalse(rule.getRLemma().equals(""));
				assertFalse(rule.getRelation().equals(""));
				assertTrue(rule.getConfidence() > 0);
			}

			//System.out.println("here should come something for 'Abfallbehandlung';");
			for (LexicalRule<? extends GermanDistSimInfo> rule : gds10.getRulesForRight(testword, null)) {
				//System.out.println("lLemma " + rule.getLLemma() + ", rLemma " + rule.getRLemma());
				assertTrue(rule.getRLemma().equals(testword));
				assertFalse(rule.getRLemma().equals(""));
				assertFalse(rule.getRelation().equals(""));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		/*for DistSim-Evaluation
		 
		//String[] wordlistVerbs = {"hineinhorchen","mästen","zugrundeliegen","ragen","antizipieren","vorausberechnen","überspringen","verpassen","einschießen","zurückzahlen","anstecken","bloßstellen","zermahlen","zieren","glühen"}; 
		String[] wordlistVerbs = {"abstürzen","terminieren","abkürzen","umringen"};
		for (String wordV : wordlistVerbs) {
			try {
				Set<String> wordSetV = new HashSet<String>();
				for ( LexicalRule<? extends GermanDistSimInfo> rightWordV : gds10.getRulesForLeft(wordV, null) ){
					wordSetV.add(rightWordV.getRLemma());
				}
				for (String wordRV: wordSetV){ System.out.println(wordV+"\t"+wordRV);}
			} catch (LexicalResourceException e) {
				e.printStackTrace();
			}
		}
		
		String[] wordlistNouns = {"Forstwirtschaft","Uran","Fauna","Malta","Linse","Kurve","Bargeld","Banane","Spargel","Wäscherei","Tennis","Kegelbahn","Hirn","Reifen","Dozent"};
		for (String wordN : wordlistNouns) {
			try {
				Set<String> wordSetN = new HashSet<String>();
				for ( LexicalRule<? extends GermanDistSimInfo> rightWordN : gds10.getRulesForLeft(wordN, null) ){
					wordSetN.add(rightWordN.getRLemma());
				}
				for (String wordRN: wordSetN){ System.out.println(wordN+"\t"+wordRN);}
			} catch (LexicalResourceException e) {
				e.printStackTrace();
			}
		} 
		*/

	}
}
	

