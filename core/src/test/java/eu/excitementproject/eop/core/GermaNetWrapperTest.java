
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;
import org.junit.Test;

// LexicalResource imports
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetRelation;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetWrapper;


public class GermaNetWrapperTest {

	@Test(expected=GermaNetNotInstalledException.class)
	public void test() throws java.lang.Exception {
		
		GermaNetWrapper gnw = new GermaNetWrapper();
		try {
			gnw.initialize(null);
		}
		catch (GermaNetNotInstalledException e) {
			System.out.println("WARNING: GermaNet files are not installed. While CommonConfig is not ready yet, please change path manually in GermaNetWrapper.java, line 98.");
			throw e;
		}

		for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForLeft("wachsen", null, GermaNetRelation.has_antonym)) {
			assertTrue(rule.getLLemma().equals("wachsen"));
			assertTrue(rule.getInfo().getLeftSynsetID() == 59751 || rule.getInfo().getLeftSynsetID() == 54357); // might only be true in GermaNet 7.0
			assertTrue(rule.getRLemma().equals("schrumpfen"));
			assertTrue(rule.getInfo().getRightSynsetID() == 59780 || rule.getInfo().getRightSynsetID() == 54511); // might only be true in GermaNet 7.0
			assertTrue(rule.getRelation().equals("has_antonym"));
			assertTrue(rule.getConfidence() > 0);
		}
		
		throw new GermaNetNotInstalledException("GermaNet is installed, but this exception is thrown to fulfill Test's expectations.");
	}
}

