package ac.biu.nlp.nlp.engineml.operations.rules;

import ac.biu.nlp.nlp.engineml.datastructures.ImmutableSetSubTypeWrapper;
import ac.biu.nlp.nlp.general.immutable.ImmutableSet;
import ac.biu.nlp.nlp.representation.PartOfSpeech;

/**
 * 
 * @author Asher Stern
 * @since Dec 24, 2012
 *
 * @param <T>
 * @param <U>
 */
public class ByLemmaPosLexicalRuleBaseWrapper<T extends LexicalRule, U extends T> extends ByLemmaPosLexicalRuleBase<T>
{
	public ByLemmaPosLexicalRuleBaseWrapper(
			ByLemmaPosLexicalRuleBase<U> realRuleBase)
	{
		super();
		this.realRuleBase = realRuleBase;
	}


	@Override
	public ImmutableSet<T> getRules(String lhsLemma, PartOfSpeech lhsPos) throws RuleBaseException
	{
		return new ImmutableSetSubTypeWrapper<T,U>(realRuleBase.getRules(lhsLemma, lhsPos));
	}
	
	private final ByLemmaPosLexicalRuleBase<U> realRuleBase;
}
