package eu.excitementproject.eop.transformations.operations.rules.lexical;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBaseWithCache;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRuleBaseCloseException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.utilities.Constants;


/**
 * Wraps {@link LexicalResource} as a {@link ByLemmaPosLexicalRuleBase}.
 * <P>
 * While the "common" project defines {@link LexicalResource}, BIUTEE uses another
 * interface, {@link ByLemmaPosLexicalRuleBase}.
 * This class wraps a {@link LexicalResource} as a {@link ByLemmaPosLexicalRuleBase}.
 * 
 * @author Asher Stern
 * @since Mar 1, 2012
 *
 */
public class LexicalResourceWrapper extends ByLemmaPosLexicalRuleBaseWithCache<LexicalRule>
{
	public LexicalResourceWrapper(LexicalResource<? extends RuleInfo> realLexicalResource)
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
			List<? extends eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule<? extends RuleInfo>> rulesFromResource =
					realLexicalResource.getRulesForLeft(lhsLemma, lhsPos);
			Set<LexicalRule> ret = new LinkedHashSet<LexicalRule>();
			if (rulesFromResource!=null)
			{
				for (eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule<? extends RuleInfo> ruleFromResource : rulesFromResource)
				{
					double confidence = 0.0;
					if (Constants.LEXICAL_RESOURCES_USE_CONSTANT_SCORE_FOR_ALL_RULES)
					{
						confidence = Constants.LEXICAL_RESOURCE_CONSTANT_SCORE_WHEN_USING_CONSTANT_SCORE;
					}
					else
					{
						confidence = ruleFromResource.getConfidence();
					}
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
