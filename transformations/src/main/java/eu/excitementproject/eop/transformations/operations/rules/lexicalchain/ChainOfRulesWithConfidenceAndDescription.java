package eu.excitementproject.eop.transformations.operations.rules.lexicalchain;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.util.ArrayList;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * 
 * @author Asher Stern
 * @since Jul 17, 2011
 *
 * @param <I>
 * @param <S>
 */
public class ChainOfRulesWithConfidenceAndDescription<I, S extends AbstractNode<I,S>> extends RuleWithConfidenceAndDescription<I,S>
{
	private static final long serialVersionUID = 6761109387880460519L;
	
	public static <I, S extends AbstractNode<I,S>> ChainOfRulesWithConfidenceAndDescription<I,S>
	fromChainOfLexicalRules(ChainOfLexicalRules chain, RuleWithConfidenceAndDescription<I, S> rule)
	{
		ArrayList<ConfidenceChainItem> list = new ArrayList<ConfidenceChainItem>(chain.getChain().size());
		for (LexicalRuleWithName lexicalRule : chain.getChain())
		{
			list.add(new ConfidenceChainItem(lexicalRule.getRuleBaseName(), lexicalRule.getRule().getConfidence()));
		}
		return new ChainOfRulesWithConfidenceAndDescription<I, S>(
				rule.getRule(), rule.getConfidence(), rule.getDescription()+": "+createDescription(chain),
				new ImmutableListWrapper<ConfidenceChainItem>(list)); 
	}

	public ChainOfRulesWithConfidenceAndDescription(SyntacticRule<I, S> rule,
			double confidence, String description,
			ImmutableList<ConfidenceChainItem> confidences)
	{
		super(rule, confidence, description);
		this.confidences = confidences;
	}
	
	public ImmutableList<ConfidenceChainItem> getConfidences()
	{
		return confidences;
	}
	
	protected static String createDescription(ChainOfLexicalRules chain)
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			for (LexicalRuleWithName realLexicalRule : chain.getChain())
			{
				sb.append("[").append(
						realLexicalRule.getRule().getLhsLemma()).append("/").append(
								simplerPos(realLexicalRule.getRule().getLhsPos().getCanonicalPosTag()).name()
								).append("==>").append(
										realLexicalRule.getRule().getRhsLemma()
										).append("/").append(simplerPos(realLexicalRule.getRule().getRhsPos().getCanonicalPosTag()).name()).append("] ");
			}
			return sb.toString();
		}
		catch(Exception e) // I stop the exception, since it is only a log-message, but failure in retrieving the description does not imply any bug in the rule itself.
		{
			return "[Could not create chain description due to exception"+e.getClass().getSimpleName()+" - "+e.getMessage()+"]";
		}
	}


	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((confidences == null) ? 0 : confidences.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChainOfRulesWithConfidenceAndDescription<?,?> other = (ChainOfRulesWithConfidenceAndDescription<?,?>) obj;
		if (confidences == null)
		{
			if (other.confidences != null)
				return false;
		} else if (!confidences.equals(other.confidences))
			return false;
		return true;
	}




	protected final ImmutableList<ConfidenceChainItem> confidences;
}
