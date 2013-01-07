package ac.biu.nlp.nlp.engineml.operations.rules;

import ac.biu.nlp.nlp.general.immutable.ImmutableSet;
import ac.biu.nlp.nlp.representation.PartOfSpeech;

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
