package ac.biu.nlp.nlp.engineml.operations.rules.lexical;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaPosLexicalRuleBaseWithCache;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRuleBaseCloseException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.lexical_resource.LexicalResource;
import ac.biu.nlp.nlp.lexical_resource.LexicalResourceCloseException;
import ac.biu.nlp.nlp.lexical_resource.LexicalResourceException;
import ac.biu.nlp.nlp.lexical_resource.RuleInfo;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;


/**
 * Wraps {@link LexicalResource}
 * 
 * @author Asher Stern
 * @since Mar 1, 2012
 *
 */
public class LexicalResourceWrapper extends ByLemmaPosLexicalRuleBaseWithCache<LexicalRule>
{
	public LexicalResourceWrapper(
			LexicalResource<? extends RuleInfo> realLexicalResource)
	{
		super();
		this.realLexicalResource = realLexicalResource;
	}
	
	public void close() throws LexicalRuleBaseCloseException
	{
		try{
		this.realLexicalResource.close();}
		catch(LexicalResourceCloseException e){throw new LexicalRuleBaseCloseException("Cannot close the rule base.",e);}
	}

	@Override
	protected ImmutableSet<LexicalRule> getRulesNotInCache(String lhsLemma, PartOfSpeech lhsPos) throws RuleBaseException
	{
		try
		{
			List<? extends ac.biu.nlp.nlp.lexical_resource.LexicalRule<? extends RuleInfo>> rulesFromResource =
					realLexicalResource.getRulesForLeft(lhsLemma, lhsPos);
			Set<LexicalRule> ret = new LinkedHashSet<LexicalRule>();
			if (rulesFromResource!=null)
			{
				for (ac.biu.nlp.nlp.lexical_resource.LexicalRule<? extends RuleInfo> ruleFromResource : rulesFromResource)
				{
					double confidence = ruleFromResource.getConfidence();
					if ( (confidence<=0) || (confidence >= 1) ) throw new RuleBaseException("Bad confidence for rule from "+this.realLexicalResource.getClass().getSimpleName()+". The confidene is: "+String.format("%-4.4f", confidence));
					
					ret.add(new LexicalRule(ruleFromResource.getLLemma(), ruleFromResource.getLPos(), ruleFromResource.getRLemma(), ruleFromResource.getRPos(), confidence));
				}
			}
			
			return new ImmutableSetWrapper<LexicalRule>(ret);
		}
		catch (LexicalResourceException e)
		{
			throw new RuleBaseException("Lexical resource failure. See nested exception.",e);
		}
	}
	
	protected LexicalResource<? extends RuleInfo> realLexicalResource;
}
