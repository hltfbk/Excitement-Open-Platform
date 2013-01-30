
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;

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
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;;



/**
 * @author Jan Pawellek 
 *
 */
public class GermaNetWrapperTest {

	@Test
	public void test() throws UnsupportedPosTagStringException {
		
		GermaNetWrapper gnw=null;
		try {
			// TODO: in the future, this test code also should read from the common config. 
			gnw = new GermaNetWrapper("/mnt/resources/ontologies/germanet-7.0/GN_V70/GN_V70_XML/");
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

		
		// Test for verbs
		try{
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForLeft("wachsen", new GermanPartOfSpeech("VINF"), GermaNetRelation.has_antonym)) {
				assertTrue(rule.getLLemma().equals("wachsen"));
				assertTrue(rule.getInfo().getLeftSynsetID() == 59751 || rule.getInfo().getLeftSynsetID() == 54357); // might only be true in GermaNet 7.0
				assertTrue(rule.getRLemma().equals("schrumpfen"));
				assertTrue(rule.getInfo().getRightSynsetID() == 59780 || rule.getInfo().getRightSynsetID() == 54511); // might only be true in GermaNet 7.0
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
		
		
		// Negative test for verbs to show the given POS is used
		try{
			List<LexicalRule<? extends GermaNetInfo>> rule = gnw.getRulesForLeft("wachsen", new GermanPartOfSpeech("NN"), GermaNetRelation.has_antonym);
			assertTrue(rule.isEmpty());
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
		
		
		// Test for common nouns
		try{
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForLeft("Hitze", new GermanPartOfSpeech("NN"), GermaNetRelation.has_antonym)) {
				assertTrue(rule.getLLemma().equals("Hitze"));
				assertTrue(rule.getRLemma().equals("Kälte"));
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		// Test of not-supported POS type (should return an empty list) 
		try {
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
		
		
	}
}

