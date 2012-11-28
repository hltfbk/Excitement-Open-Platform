
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;

import org.junit.Assume;
import org.junit.Test;

import eu.excitementproject.eop.common.exception.BaseException;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetRelation;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetWrapper;


/**
 * @author Jan Pawellek 
 *
 */
public class GermaNetWrapperTest {

//	@Test(expected=GermaNetNotInstalledException.class) 
//  [Gil: used Assume.assumeNotNull instead of expected exception.] 
	@Test
	public void test() /* throws java.lang.Exception */ {
		
		GermaNetWrapper gnw=null;
		try {
			// TODO: in the future, this test code also should read from the common config. 
			gnw = new GermaNetWrapper("/home/tailblues/resources/ontologies/germanet-7.0/GN_V70/GN_V70_XML/");
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

		try{
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForLeft("wachsen", null, GermaNetRelation.has_antonym)) {
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
		//throw new GermaNetNotInstalledException("GermaNet is installed, but this exception is thrown to fulfill Test's expectations.");
	}
}

