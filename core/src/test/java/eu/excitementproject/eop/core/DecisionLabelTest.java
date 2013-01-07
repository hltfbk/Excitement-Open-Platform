package eu.excitementproject.eop.core;

import static org.junit.Assert.*;
import eu.excitementproject.eop.common.DecisionLabel;

import org.junit.Test;

public class DecisionLabelTest {

	@Test
	public void test() {
		DecisionLabel a = DecisionLabel.Entailment;
		DecisionLabel b = DecisionLabel.Paraphrase;
		DecisionLabel c = DecisionLabel.Contradiction; 
		
		// is() (is-a) relation. 
		assertTrue(b.is(a));
		assertTrue(DecisionLabel.Paraphrase.is(DecisionLabel.Entailment));
		assertFalse(a.is(b)); 
		assertFalse(DecisionLabel.Entailment.is(DecisionLabel.Paraphrase));
		
		// is() can be used for "general" check, instead of ==, or equals() 
		assertTrue(c.is(DecisionLabel.Contradiction));
		assertTrue(c.is(DecisionLabel.NonEntailment)); 
		assertFalse(c.is(DecisionLabel.Entailment));
		
	}

}
