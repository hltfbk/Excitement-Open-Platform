package eu.excitementproject.eop.transformations.operations.rules;
import java.util.LinkedHashSet;

import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResource;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.transformations.operations.rules.distsimnew.DirtDBRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ChainOfLexicalRules;
//import eu.excitementproject.eop.transformations.rteflow.micro.TreesGeneratorByOperations;



/**
 * 
 * An RTTI-like mechanism for RuleBase.
 * <P>
 * This class is eventually used in {@link TreesGeneratorByOperations#getFinder}
 * <BR>
 * RTTI is an unsafe programming practice, including, in my
 * opinion, the "contract programming" paradigm.
 * <BR>
 * The mechanism given by this class is safe. Think about it (for example: first base class cannot
 * be extended and used, no duplicate rule base types can be used, and more).
 * 
 * @author Asher Stern
 * @since Feb 5, 2011
 * @see TreesGeneratorByOperations#getFinder
 *
 * @param <I>
 * @param <S>
 */
public class RuleBaseEnvelope<I, S extends AbstractNode<I, S>>
{
	// for each RuleBase type add the following lines:
//	public RuleBaseEnvelope(RuleBaseX<I,S> ruleBase) throws RuleBaseException
//	{
//		if (null==ruleBase)
//			throw new RuleBaseException("The given RuleBaseX was null");
//		this.ruleBaseX = ruleBase;
//	}
//	public RuleBaseX<I,S> getRuleBaseX()
//	{
//		return this.ruleBaseX;
//	}
//	private RuleBaseX<I,S> ruleBaseX = null;

	
	/////////////////////////////// DynamicRuleBase ///////////////////////////////
	
	@Deprecated
	public RuleBaseEnvelope(DynamicRuleBase<I, S> ruleBase) throws RuleBaseException
	{
		if (null==ruleBase)
			throw new RuleBaseException("null in constructor for DynamicRuleBase<I, S> ruleBase");
		this.dynamicRuleBase = ruleBase;
	}
	
	@Deprecated
	public DynamicRuleBase<I, S> getDynamicRuleBase() throws RuleBaseException
	{
		return this.dynamicRuleBase;
	}

	@Deprecated
	private DynamicRuleBase<I, S> dynamicRuleBase = null;
	
	
	
	/////////////////////////////// BagOfRulesRuleBase ///////////////////////////////

	public RuleBaseEnvelope(BagOfRulesRuleBase<I, S> ruleBase) throws RuleBaseException
	{
		if (null==ruleBase)
			throw new RuleBaseException("null in constructor for BagOfRulesRuleBase<I, S> ruleBase");
		this.bagOfRulesRuleBase = ruleBase;
	}
	
	public BagOfRulesRuleBase<I, S> getBagOfRulesRuleBase() throws RuleBaseException
	{
		return this.bagOfRulesRuleBase;
	}

	private BagOfRulesRuleBase<I, S> bagOfRulesRuleBase = null;

	
	/////////////////////////////// ChainOfLexicalRules ///////////////////////////////

	public RuleBaseEnvelope(ByLemmaPosLexicalRuleBase<ChainOfLexicalRules> chainOfLexicalRulesRuleBase) throws RuleBaseException
	{
		if (null==chainOfLexicalRulesRuleBase)
			throw new RuleBaseException("null in constructor for LexicalRuleBase<ChainOfLexicalRules> ruleBase");
		this.chainOfLexicalRulesRuleBase = chainOfLexicalRulesRuleBase;
	}

	public ByLemmaPosLexicalRuleBase<ChainOfLexicalRules> getChainOfLexicalRulesRuleBase()
	{
		return chainOfLexicalRulesRuleBase;
	}

	private ByLemmaPosLexicalRuleBase<ChainOfLexicalRules> chainOfLexicalRulesRuleBase = null;
	
	//////////////////////////////// DirtDBRuleBase ////////////////////////////////
	
	public RuleBaseEnvelope(DirtDBRuleBase ruleBase) throws RuleBaseException
	{
		if (null==ruleBase)
			throw new RuleBaseException("The given ruleBase was null");
		this.dirtDBRuleBase = ruleBase;
	}
	public DirtDBRuleBase getDirtDBRuleBase()
	{
		return this.dirtDBRuleBase;
	}
	private DirtDBRuleBase dirtDBRuleBase = null;

	
	/////////////////// eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResource ///////////////////
	
	
	public RuleBaseEnvelope(SyntacticResource<I,S> ruleBaseSyntacticResource) throws RuleBaseException
	{
		if (null==ruleBaseSyntacticResource)
			throw new RuleBaseException("The given ruleBaseSyntacticResource was null");
		this.ruleBaseSyntacticResource = ruleBaseSyntacticResource;
	}
	public SyntacticResource<I,S> getRuleBaseSyntacticResource()
	{
		return this.ruleBaseSyntacticResource;
	}
	private SyntacticResource<I,S> ruleBaseSyntacticResource = null;
	
	
	//////////////////////////// General Methods & Fields ///////////////////////////////
	

	public void setRuleBasesNames(LinkedHashSet<String> ruleBasesNames)
	{
		this.ruleBasesNames = ruleBasesNames;
	}
	
	public LinkedHashSet<String> getRuleBasesNames()
	{
		return ruleBasesNames;
	}

	/**
	 * If the encapsulated rule base actually contains many "real" rule bases
	 * Then this filed should be set.
	 */
	private LinkedHashSet<String> ruleBasesNames;



}
