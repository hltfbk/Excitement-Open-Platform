package eu.excitementproject.eop.transformations.operations.rules.manual;
import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.datastructures.DummySet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.operations.rules.BagOfRulesRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;

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
