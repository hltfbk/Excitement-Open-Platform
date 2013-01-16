package eu.excitementproject.eop.transformations.operations.rules.lexicalchain;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * A {@link LexicalRule} which is actually a chain of lexical rules.
 * 
 * @author Asher Stern
 * @since Jul 15, 2011
 *
 */
public class ChainOfLexicalRules extends LexicalRule
{
	private static final long serialVersionUID = 2007408894759152754L;


	public ChainOfLexicalRules(String lhsLemma, PartOfSpeech lhsPos, String rhsLemma, PartOfSpeech rhsPos, double confidence, ImmutableList<LexicalRuleWithName> chain) throws TeEngineMlException
	{
		super(lhsLemma, lhsPos, rhsLemma, rhsPos, confidence);
		this.chain = chain;
		if (null==chain) throw new TeEngineMlException("Null chain");
		if (chain.size()==0)throw new TeEngineMlException("Empty chain");
		if (!chain.get(0).getRule().getLhsLemma().equals(lhsLemma)) throw new TeEngineMlException("Bad chain: expected: "+lhsLemma+" but found: "+chain.get(0).getRule().getLhsLemma()+parametersDescription(lhsLemma,  lhsPos, rhsLemma, rhsPos, confidence, chain));
		if (!simplerPos(chain.get(0).getRule().getLhsPos().getCanonicalPosTag()).equals(simplerPos(lhsPos.getCanonicalPosTag()))) throw new TeEngineMlException("Bad chain: expected pos: "+simplerPos(lhsPos.getCanonicalPosTag())+", but found: "+simplerPos(chain.get(0).getRule().getLhsPos().getCanonicalPosTag())+parametersDescription(lhsLemma,  lhsPos, rhsLemma, rhsPos, confidence, chain));
		if (!chain.get(chain.size()-1).getRule().getRhsLemma().equals(rhsLemma)) throw new TeEngineMlException("Bad chain: expected rhsLemma: "+rhsLemma+" but found: "+chain.get(chain.size()-1).getRule().getRhsLemma()+parametersDescription(lhsLemma,  lhsPos, rhsLemma, rhsPos, confidence, chain));
		if (!simplerPos(chain.get(chain.size()-1).getRule().getRhsPos().getCanonicalPosTag()).equals(simplerPos(rhsPos.getCanonicalPosTag()))) throw new TeEngineMlException("Bad chain: expected rhs pos: "+simplerPos(rhsPos.getCanonicalPosTag())+", but found: "+simplerPos(chain.get(chain.size()-1).getRule().getRhsPos().getCanonicalPosTag())+parametersDescription(lhsLemma,  lhsPos, rhsLemma, rhsPos, confidence, chain));
	}

	public ImmutableList<LexicalRuleWithName> getChain()
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
		ChainOfLexicalRules other = (ChainOfLexicalRules) obj;
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


	private static final String parametersDescription(String lhsLemma, PartOfSpeech lhsPos, String rhsLemma, PartOfSpeech rhsPos, double confidence, ImmutableList<LexicalRuleWithName> chain)
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			sb.append("rule: ").append(lhsLemma).append("/").append(lhsPos);
			sb.append(" ==> ");
			sb.append(rhsLemma).append("/").append(rhsPos);
			sb.append("Chain: ");
			for (LexicalRuleWithName lrwn : chain)
			{
				sb.append("{").append(lrwn.getRuleBaseName()).append(":");
				sb.append(lrwn.getRule().getLhsLemma()).append("/").append(lrwn.getRule().getLhsPos());
				sb.append(" ==> ");
				sb.append(lrwn.getRule().getRhsLemma()).append("/").append(lrwn.getRule().getRhsPos());
				sb.append("} ");
			}
			return sb.toString();
		}
		catch(RuntimeException e)
		{
			return "cannot create description of parameters";
		}

	}

	protected final ImmutableList<LexicalRuleWithName> chain;
}
