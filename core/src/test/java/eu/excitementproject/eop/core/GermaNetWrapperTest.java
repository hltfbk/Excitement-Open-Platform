
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;
import org.junit.Test;

// LexicalResource imports
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetRelation;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetWrapper;


public class GermaNetWrapperTest {

	@Test
	public void test() throws java.lang.Exception {
		
		GermaNetWrapper gnw = new GermaNetWrapper();
		gnw.initialize(null);

		for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForLeft("wachsen", null, GermaNetRelation.has_antonym)) {
			assertTrue(rule.getLLemma().equals("wachsen"));
			assertTrue(rule.getInfo().getLeftSynsetID() == 59751 || rule.getInfo().getLeftSynsetID() == 54357); // might only be true in GermaNet 7.0
			assertTrue(rule.getRLemma().equals("schrumpfen"));
			assertTrue(rule.getInfo().getRightSynsetID() == 59780 || rule.getInfo().getRightSynsetID() == 54511); // might only be true in GermaNet 7.0
			assertTrue(rule.getRelation().equals("has_antonym"));
			assertTrue(rule.getConfidence() > 0);
		}
	}
}

