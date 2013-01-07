package ac.biu.nlp.nlp.engineml.rteflow.micro.perform;

import ac.biu.nlp.nlp.engineml.datastructures.CanonicalLemmaAndPos;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.finders.Finder;
import ac.biu.nlp.nlp.engineml.operations.finders.Substitution2DChainOfLexicalRulesByLemmaPosFinder;
import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.operations.SubstituteNodeOperation;
import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaPosLexicalRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseEnvelope;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.ChainOfLexicalRules;
import ac.biu.nlp.nlp.engineml.operations.specifications.RuleSubstituteNodeSpecification;
import ac.biu.nlp.nlp.engineml.operations.updater.FeatureVectorUpdater;
import ac.biu.nlp.nlp.engineml.operations.updater.UpdaterForChainOfLexicalRules;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.general.immutable.ImmutableSet;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;


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
