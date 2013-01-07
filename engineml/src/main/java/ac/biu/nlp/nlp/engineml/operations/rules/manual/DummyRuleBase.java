package ac.biu.nlp.nlp.engineml.operations.rules.manual;

import ac.biu.nlp.nlp.engineml.operations.rules.BagOfRulesRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleWithConfidenceAndDescription;
import ac.biu.nlp.nlp.engineml.rteflow.macro.DefaultOperationScript;
import ac.biu.nlp.nlp.general.DummySet;
import ac.biu.nlp.nlp.general.immutable.ImmutableSet;
import ac.biu.nlp.nlp.general.immutable.ImmutableSetWrapper;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

/**
 * An empty rule base.
 * 
 * @see DefaultOperationScript
 * 
 * @author Asher Stern
 * @since Jul 3, 2011
 *
 */
public class DummyRuleBase implements BagOfRulesRuleBase<Info,BasicNode>
{

	public ImmutableSet<RuleWithConfidenceAndDescription<Info, BasicNode>> getRules()
			throws RuleBaseException
	{
		return empty;
	}
	
	private static final ImmutableSet<RuleWithConfidenceAndDescription<Info, BasicNode>> empty =
		new ImmutableSetWrapper<RuleWithConfidenceAndDescription<Info,BasicNode>>(new DummySet<RuleWithConfidenceAndDescription<Info,BasicNode>>());
}
