package eu.excitementproject.eop.transformations.operations.rules;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.transformations.datastructures.ImmutableSetSubTypeWrapper;

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
