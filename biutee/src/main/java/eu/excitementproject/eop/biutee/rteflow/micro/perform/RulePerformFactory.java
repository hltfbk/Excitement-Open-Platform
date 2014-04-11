package eu.excitementproject.eop.biutee.rteflow.micro.perform;
import eu.excitementproject.eop.biutee.operations.updater.FeatureVectorUpdater;
import eu.excitementproject.eop.biutee.operations.updater.UpdaterForRule;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.finders.ExcitementSyntacticRuleFinder;
import eu.excitementproject.eop.transformations.operations.finders.Finder;
import eu.excitementproject.eop.transformations.operations.finders.RulesByBagOfRulesRuleBaseFinder;
import eu.excitementproject.eop.transformations.operations.finders.RulesFromDirtDBFinder;
import eu.excitementproject.eop.transformations.operations.operations.ExtendedSubstitutionRuleApplicationOperation;
import eu.excitementproject.eop.transformations.operations.operations.GenerationOperation;
import eu.excitementproject.eop.transformations.operations.operations.IntroductionRuleApplicationOperation;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseEnvelope;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * @author Asher Stern
 * @since Jan 25, 2012
 *
 */
public class RulePerformFactory extends PerformFactory<RuleSpecification>
{
	public RulePerformFactory(ImmutableSet<String> hypothesisTemplates,
			ImmutableSet<String> hypothesisLemmasOnly, BasicNode hypothesisTreeAsBasicNode, boolean collapseMode)
	{
		super();
		this.hypothesisTemplates = hypothesisTemplates;
		this.hypothesisLemmasOnly = hypothesisLemmasOnly;
		this.hypothesisTreeAsBasicNode = hypothesisTreeAsBasicNode;
		this.collapseMode = collapseMode;
	}

	@Override
	public Finder<RuleSpecification> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
			throws TeEngineMlException, OperationException
	{
		return null;
	}

	@Override
	public Finder<RuleSpecification> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			ByLemmaPosLexicalRuleBase<LexicalRule> lexicalRuleBase,
			String ruleBaseName) throws TeEngineMlException, OperationException
	{
		return null;
	}

	@Override
	public Finder<RuleSpecification> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			String ruleBaseName) throws TeEngineMlException, OperationException
	{
		return null;
	}

	@Override
	public Finder<RuleSpecification> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			RuleBaseEnvelope<Info, BasicNode> ruleBase, String ruleBaseName)
			throws TeEngineMlException, OperationException
	{
		try
		{
			if (ruleBase.getBagOfRulesRuleBase()!=null)
			{
				return new RulesByBagOfRulesRuleBaseFinder(text, nonNull(ruleBase.getBagOfRulesRuleBase(),"bag-of-rules-rulebase"), ruleBaseName);
			}
			else if (ruleBase.getDirtDBRuleBase()!=null)
			{
				return new RulesFromDirtDBFinder(nonNull(ruleBase.getDirtDBRuleBase(),"Dirt-DB-rulebase"),ruleBaseName,text.getTree(),this.hypothesisTemplates,this.hypothesisLemmasOnly);
			}
			else if (ruleBase.getRuleBaseSyntacticResource()!=null)
			{
				return new ExcitementSyntacticRuleFinder(ruleBase.getRuleBaseSyntacticResource(), ruleBaseName, text.getTree(), hypothesisTreeAsBasicNode);
			}
			else
			{
				throw new TeEngineMlException("Unrecognized type of rule base");
			}
		}
		catch(RuleBaseException e)
		{
			throw new TeEngineMlException("rule base failure",e);
		}
	}

	@Override
	public GenerationOperation<ExtendedInfo, ExtendedNode> getOperation(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			RuleSpecification specification) throws TeEngineMlException,
			OperationException
	{
		if (specification.isExtraction())
		{
			return new IntroductionRuleApplicationOperation(text, hypothesis, specification.getRule().getRule(), specification.getMapLhsToTree(),collapseMode);
		}
		else
		{
			return new ExtendedSubstitutionRuleApplicationOperation(text, hypothesis, specification.getRule().getRule(), specification.getMapLhsToTree());
		}
	}

	@Override
	public FeatureVectorUpdater<RuleSpecification> getUpdater(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
			throws TeEngineMlException, OperationException
	{
		return updaterForRule;
	}
	
	protected static final <T> T nonNull(T t, String name) throws TeEngineMlException
	{
		if (null==t)throw new TeEngineMlException("null "+name);
		return t;
	}
	
	private ImmutableSet<String> hypothesisTemplates;
	private ImmutableSet<String> hypothesisLemmasOnly;
	private BasicNode hypothesisTreeAsBasicNode;
	private final boolean collapseMode;
	
	private static UpdaterForRule updaterForRule = new UpdaterForRule();

}
