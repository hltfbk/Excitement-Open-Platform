package eu.excitementproject.eop.transformations.operations.rules.lexicalchain;
import java.util.List;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * A version of {@link ChainOfLexicalRules} with mutable List.
 * @author Eyal Shnarch
 * @since 15/09/2011
 *
 */
public class ChainOfLexicalRulesMutable extends LexicalRule
{

	private static final long serialVersionUID = 1319410992591575715L;

	public ChainOfLexicalRulesMutable(String lhsLemma, PartOfSpeech lhsPos, String rhsLemma, PartOfSpeech rhsPos, double confidence, List<LexicalRuleWithName> chain) throws TeEngineMlException
	{
		super(lhsLemma, lhsPos, rhsLemma, rhsPos, confidence);
		this.chain = chain;
		if (null==chain) throw new TeEngineMlException("Null chain");
		if (chain.size()==0)throw new TeEngineMlException("Empty chain");
		if (!chain.get(0).getRule().getLhsLemma().equals(lhsLemma)) throw new TeEngineMlException("Bad chain");
		if (!chain.get(0).getRule().getLhsPos().equals(lhsPos)) throw new TeEngineMlException("Bad chain");
		if (!chain.get(chain.size()-1).getRule().getRhsLemma().equals(rhsLemma)) throw new TeEngineMlException("Bad chain");
		if (!chain.get(chain.size()-1).getRule().getRhsPos().equals(rhsPos)) throw new TeEngineMlException("Bad chain");
	}

	public List<LexicalRuleWithName> getChain()
	{
		return chain;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((chain == null) ? 0 : chain.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChainOfLexicalRulesMutable other = (ChainOfLexicalRulesMutable) obj;
		if (chain == null)
		{
			if (other.chain != null)
				return false;
		} else if (!chain.equals(other.chain))
			return false;
		return true;
	}
	
	

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(LexicalRuleWithName rule : getChain()){
			sb.append(rule +", ");
		}
		return sb.substring(0, sb.length()-2);
	}



	protected final List<LexicalRuleWithName> chain;
}
