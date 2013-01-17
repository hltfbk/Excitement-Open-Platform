package eu.excitementproject.eop.transformations.operations.rules;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

/**
 * A {@link LexicalRuleBase} which returns rule that match a given lemma-and-part-of-speech
 * as left-hand-side.
 * 
 * @author Asher Stern
 * @since February 2011
 *
 */
public abstract class ByLemmaPosLexicalRuleBase<T extends LexicalRule> extends LexicalRuleBase<T>
{
	public abstract ImmutableSet<T> getRules(String lhsLemma, PartOfSpeech lhsPos) throws RuleBaseException;
}
