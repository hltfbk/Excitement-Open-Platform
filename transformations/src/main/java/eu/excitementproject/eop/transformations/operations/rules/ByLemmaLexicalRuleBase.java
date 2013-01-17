package eu.excitementproject.eop.transformations.operations.rules;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;

/**
 * A {@link LexicalRuleBase} which returns rule that match a given lemma as
 * left-hand-side.
 * 
 * @author Asher Stern
 * @since February 2011
 *
 */
public abstract class ByLemmaLexicalRuleBase extends LexicalRuleBase<LexicalRule>
{
	public abstract ImmutableSet<LexicalRule> getRules(String lhsLemma) throws RuleBaseException;
}
