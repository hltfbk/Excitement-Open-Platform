package eu.excitementproject.eop.biutee.rteflow.micro.perform;
import eu.excitementproject.eop.biutee.operations.updater.FeatureVectorUpdater;
import eu.excitementproject.eop.biutee.operations.updater.UpdaterForChainOfLexicalRules;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.datastructures.CanonicalLemmaAndPos;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.finders.Finder;
import eu.excitementproject.eop.transformations.operations.finders.Substitution2DChainOfLexicalRulesByLemmaPosFinder;
import eu.excitementproject.eop.transformations.operations.operations.GenerationOperation;
import eu.excitementproject.eop.transformations.operations.operations.SubstituteNodeOperation;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseEnvelope;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ChainOfLexicalRules;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSubstituteNodeSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * @author Asher Stern
 * @since Jan 25, 2012
 *
 */
public class ChainOfLexicalRulesPerformFactory extends PerformFactory<RuleSubstituteNodeSpecification<ChainOfLexicalRules>>
{
	public ChainOfLexicalRulesPerformFactory(
			ImmutableSet<CanonicalLemmaAndPos> hypothesisLemmasAndCanonicalPos,
			ImmutableSet<String> hypothesisLemmasOnly,
			ImmutableSet<String> stopWords)
	{
		super();
		this.hypothesisLemmasAndCanonicalPos = hypothesisLemmasAndCanonicalPos;
		this.hypothesisLemmasOnly = hypothesisLemmasOnly;
		this.stopWords = stopWords;
	}

	@Override
	public Finder<RuleSubstituteNodeSpecification<ChainOfLexicalRules>> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
			throws TeEngineMlException, OperationException
	{
		return null;
	}

	@Override
	public Finder<RuleSubstituteNodeSpecification<ChainOfLexicalRules>> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			ByLemmaPosLexicalRuleBase<LexicalRule> lexicalRuleBase,
			String ruleBaseName) throws TeEngineMlException, OperationException
	{
		return null;
	}

	@Override
	public Finder<RuleSubstituteNodeSpecification<ChainOfLexicalRules>> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			String ruleBaseName) throws TeEngineMlException, OperationException
	{
		return null;
	}

	@Override
	public Finder<RuleSubstituteNodeSpecification<ChainOfLexicalRules>> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			RuleBaseEnvelope<Info, BasicNode> ruleBase, String ruleBaseName)
			throws TeEngineMlException, OperationException
	{
		if (ruleBase.getChainOfLexicalRulesRuleBase()!=null)
		{
			return new Substitution2DChainOfLexicalRulesByLemmaPosFinder(text, ruleBase.getChainOfLexicalRulesRuleBase(), ruleBaseName,
					Constants.FILTER_STOP_WORDS_IN_LEFT_IN_LEXICAL_RESOURCES,Constants.FILTER_STOP_WORDS_IN_LEFT_IN_LEXICAL_RESOURCES,stopWords,
					hypothesisLemmasAndCanonicalPos, hypothesisLemmasOnly);
		}
		else
		{
			throw new TeEngineMlException("unrecognized type of meta rule");
		}
	}

	@Override
	public GenerationOperation<ExtendedInfo, ExtendedNode> getOperation(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			RuleSubstituteNodeSpecification<ChainOfLexicalRules> specification)
			throws TeEngineMlException, OperationException
	{
		return new SubstituteNodeOperation(text, hypothesis, specification.getTextNodeToBeSubstituted(), specification.getNewNodeInfo(), specification.getNewAdditionalNodeInformation());
	}

	@Override
	public FeatureVectorUpdater<RuleSubstituteNodeSpecification<ChainOfLexicalRules>> getUpdater(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
			throws TeEngineMlException, OperationException
	{
		return updaterForChainOfLexicalRules;
	}

	private ImmutableSet<CanonicalLemmaAndPos> hypothesisLemmasAndCanonicalPos;
	private ImmutableSet<String> hypothesisLemmasOnly;
	private ImmutableSet<String> stopWords;
	private static UpdaterForChainOfLexicalRules updaterForChainOfLexicalRules = new UpdaterForChainOfLexicalRules();
}
