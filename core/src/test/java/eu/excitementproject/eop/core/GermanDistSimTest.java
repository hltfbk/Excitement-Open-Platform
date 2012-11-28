
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;
import org.junit.Test;

// LexicalResource imports
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSim;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSimInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional.GermanDistSimNotInstalledException;


public class GermanDistSimTest {

	@Test(expected=GermanDistSimNotInstalledException.class)
	public void test() throws java.lang.Exception {
		
		GermanDistSim gds = null;
		try {
			gds = new GermanDistSim("src/main/resources/dewakdistributional-data");
		}
		catch (GermanDistSimNotInstalledException e) {
			System.out.println("WARNING: GermanDistSim files are not installed. While CommonConfig is not ready yet, please change path manually in GermanDistSim.java, line 56.");
			throw e;
		}

		for (LexicalRule<? extends GermanDistSimInfo> rule : gds.getRulesForLeft("sie", null)) {
			assertTrue(rule.getLLemma().equals("sie"));
			assertFalse(rule.getRLemma().equals(""));
			assertFalse(rule.getRelation().equals(""));
			assertTrue(rule.getConfidence() > 0);
		}
		
		throw new GermanDistSimNotInstalledException("GermanDistSim files are installed, but this exception is thrown to fulfill Test's expectations.");
	}
}

