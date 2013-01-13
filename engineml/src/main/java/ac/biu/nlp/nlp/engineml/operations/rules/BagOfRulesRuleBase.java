package ac.biu.nlp.nlp.engineml.operations.rules;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;

/**
 * A rule base which contains all the rules with no particular structure.
 * To get a rule - the user has to examine all the rules. No method of getting
 * rule according to some criteria is available in this rule base.
 * 
 * @author Asher Stern
 * @since Feb 24, 2011
 *
 * @param <I>
 * @param <S>
 */
public interface BagOfRulesRuleBase<I, S extends AbstractNode<I, S>> extends RuleBase<I, S>
{
	public ImmutableSet<RuleWithConfidenceAndDescription<I, S>> getRules() throws RuleBaseException;
}
