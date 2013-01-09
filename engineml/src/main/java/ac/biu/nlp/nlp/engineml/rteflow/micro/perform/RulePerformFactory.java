package ac.biu.nlp.nlp.engineml.rteflow.micro.perform;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.finders.Finder;
import ac.biu.nlp.nlp.engineml.operations.finders.RulesByBagOfRulesRuleBaseFinder;
import ac.biu.nlp.nlp.engineml.operations.finders.RulesFromDirtDBFinder;
import ac.biu.nlp.nlp.engineml.operations.operations.ExtendedSubstitutionRuleApplicationOperation;
import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.operations.IntroductionRuleApplicationOperation;
import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaPosLexicalRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseEnvelope;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.operations.specifications.RuleSpecification;
import ac.biu.nlp.nlp.engineml.operations.updater.FeatureVectorUpdater;
import ac.biu.nlp.nlp.engineml.operations.updater.UpdaterForRule;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;


/**
 * 
 * @author Asher Stern
 * @since Jan 25, 2012
 *
 */
public class RulePerformFactory extends PerformFactory<RuleSpecification>
{
	public RulePerformFactory(ImmutableSet<String> hypothesisTemplates,
			ImmutableSet<String> hypothesisLemmasOnly)
	{
		super();
		this.hypothesisTemplates = hypothesisTemplates;
		this.hypothesisLemmasOnly = hypothesisLemmasOnly;
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
			return new IntroductionRuleApplicationOperation(text, hypothesis, specification.getRule().getRule(), specification.getMapLhsToTree());
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
	private static UpdaterForRule updaterForRule = new UpdaterForRule();

}
