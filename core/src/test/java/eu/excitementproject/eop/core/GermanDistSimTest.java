
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;
import org.junit.Test;

// LexicalResource imports
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSim;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSimInfo;


public class GermanDistSimTest {

	@Test
	public void test() throws java.lang.Exception {
		
		GermanDistSim gds = new GermanDistSim();
		gds.initialize(null);

		for (LexicalRule<? extends GermanDistSimInfo> rule : gds.getRulesForLeft("sie", null)) {
			assertTrue(rule.getLLemma().equals("sie"));
			assertFalse(rule.getRLemma().equals(""));
			assertFalse(rule.getRelation().equals(""));
			assertTrue(rule.getConfidence() > 0);
		}
	}
}

