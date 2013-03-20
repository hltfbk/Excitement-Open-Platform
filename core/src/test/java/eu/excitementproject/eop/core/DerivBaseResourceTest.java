
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;

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



/**
 * @author Britta Zeller
 *
 */
public class DerivBaseResourceTest {

	@Test
	public void test() throws UnsupportedPosTagStringException {
		
		DerivBaseResource db = null;
		try {
			db = new DerivBaseResource("src/main/resources/derivbase/DErivBase-v1.3-pairsWithoutScore.txt");
		}
		catch (DerivBaseNotInstalledException e) {
			System.out.println("WARNING: DErivBase file was not found in the given path.");
		}
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}
		Assume.assumeNotNull(db); // if db is null, the following tests will not be run. 

		
		
		// getRulesForRight for a verb 
		//System.out.println("rTest for wachsen");
		List<LexicalRule<? extends DerivBaseInfo>> list1 = null; 
		try {
			list1 = db.getRulesForRight("wachsen", new GermanPartOfSpeech("VINF")); 
			assertTrue(list1.size() > 0);
			for (LexicalRule<? extends DerivBaseInfo> rule : list1) {
				assertTrue(rule.getRLemma().equals("wachsen"));
				//System.out.println("one leftLemma for 'wachsen': " + rule.getLLemma());
			}			
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
		
		
		
		
		// getRulesForLeft for a N noun
		//System.out.println("lTest for Ziehung");
		List<LexicalRule<? extends DerivBaseInfo>> list2 = null; 
		try {
			list2 = db.getRulesForLeft("Ziehung", new GermanPartOfSpeech("N")); 
			assertTrue(list2.size() > 0);
			for (LexicalRule<? extends DerivBaseInfo> rule : list2) {
				assertTrue(rule.getLLemma().equals("Ziehung"));
				//System.out.println("one rightLemma for 'Ziehung': " + rule.getRLemma());
				//System.out.println("one rightPos for '" + rule.getRLemma() + "': " + rule.getRPos());
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
			list3 = db.getRules("Beziehung", new GermanPartOfSpeech("N"), "Erzieherin", new GermanPartOfSpeech("NN"));
			assertTrue(list3.size() > 0);
			assertTrue(list3.size() == 1);
			for (LexicalRule<? extends DerivBaseInfo> rule : list3) {
				assertTrue(rule.getLLemma().equals("Beziehung"));
				//System.out.println("single rightLemma for 'Beziehung(N)': " + rule.getRLemma());
				//System.out.println("single rightPos for '" + rule.getRLemma() + "': " + rule.getRPos());
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
			list4 = db.getRules("Beziehung", new GermanPartOfSpeech("N"), "erziehbar", new GermanPartOfSpeech("ADJ"));
			assertTrue(list4.size() > 0);
			assertTrue(list4.size() == 1);
			for (LexicalRule<? extends DerivBaseInfo> rule : list4) {
				assertTrue(rule.getLLemma().equals("Beziehung"));
				//System.out.println("single rightLemma for 'Beziehung(N)': " + rule.getRLemma());
				//System.out.println("single rightPos for '" + rule.getRLemma() + "': " + rule.getRPos());
			}						
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
	
		
		
		
		// Negative test for verbs to show the given POS is used
		try{
			//System.out.println("Test for wachsen_NN");
			List<LexicalRule<? extends DerivBaseInfo>> rule = db.getRulesForLeft("wachsen", new GermanPartOfSpeech("NN"));
			assertTrue(rule.isEmpty());
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
		
		
		
				
		// Test of not-supported POS type (should return an empty list) 
		try {
			//System.out.println("Test for Hitze_PTKA");
			List<LexicalRule<? extends DerivBaseInfo>> l = db.getRulesForLeft("Hitze", new GermanPartOfSpeech("PTKA")); 
			assertTrue(l.size() == 0); 
			// Still, null POS should mean, don't care
			//System.out.println("Test for Hitze_null");
			l = db.getRulesForLeft("Hitze",  null); 
			assertTrue(l.size() == 0); //TODO: if null is later accepted, change to "> 0"
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		
		
//TODO: test for CommonConfig setting!
		
		
	}
}

