package eu.excitementproject.eop.transformations.operations.rules.lexicalchain.builder;
import java.util.Map;

import eu.excitementproject.eop.common.datastructures.DummySet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.transformations.datastructures.CanonicalLemmaAndPos;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ChainOfLexicalRules;
//import eu.excitementproject.eop.transformations.rteflow.macro.DefaultOperationScript;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * A lexical rule base, which is not a single lexical rule base, but
 * contains rules that each of them is a chain of lexical rules.
 * 
 * @see BuilderSetOfWords
 * @see DefaultOperationScript
 * 
 * @author Asher Stern
 * @since Jan 19, 2012
 *
 */
public class SimpleLexicalChainRuleBase extends ByLemmaPosLexicalRuleBase<ChainOfLexicalRules>
{
	/**
	 * Constructor. The rule base is <B>not</B> ready to use
	 * after being constructed. The rules must be set, using
	 * the method {@link #setRules(Map, Object)}.
	 * 
	 * @param creator The caller of the constructor. Only this
	 * caller will be allowed later to call {@link #setRules(Map, Object)}.
	 */
	public SimpleLexicalChainRuleBase(Object creator)
	{
		super();
		this.rules = null;
		this.creator = creator;
	}

	/**
	 * Set the rules in the rule base.
	 * The typical usage is that for each text-hypothesis pair, the
	 * rules are retrieved from the rule bases
	 * (see {@link BuilderSetOfWords} {@link DefaultOperationScript}),
	 * and set into this rule-base by this method.
	 * 
	 * @param rules the rules.
	 * @param creator The object who called the constructor, and was given
	 * as parameter to the constructor.
	 * @throws RuleBaseException
	 */
	public void setRules(Map<CanonicalLemmaAndPos, ImmutableSet<ChainOfLexicalRules>> rules, Object creator) throws RuleBaseException
	{
		if (this.creator!=creator) throw new RuleBaseException("Rules can be set only by the rule base creator.");
		if (null==rules) throw new RuleBaseException("Null rules given.");
		this.rules = rules;
	}

	@Override
	public ImmutableSet<ChainOfLexicalRules> getRules(String lhsLemma, PartOfSpeech lhsPos) throws RuleBaseException
	{
		if (null==this.rules) throw new RuleBaseException("Rules were not set!");
		try
		{
			CanonicalLemmaAndPos lemmaAndPos = new CanonicalLemmaAndPos(lhsLemma, lhsPos);
			ImmutableSet<ChainOfLexicalRules> ret = null;
			if (rules.containsKey(lemmaAndPos))
			{
				ret = rules.get(lemmaAndPos);
			}
			if (null==ret)
			{
				ret = new ImmutableSetWrapper<ChainOfLexicalRules>(new DummySet<ChainOfLexicalRules>());
			}
			return ret;
		}
		catch(TeEngineMlException e)
		{
			throw new RuleBaseException("Failed to fetch rules",e);
		}
	}
	
	private Map<CanonicalLemmaAndPos, ImmutableSet<ChainOfLexicalRules>> rules = null;
	private final Object creator;
}
