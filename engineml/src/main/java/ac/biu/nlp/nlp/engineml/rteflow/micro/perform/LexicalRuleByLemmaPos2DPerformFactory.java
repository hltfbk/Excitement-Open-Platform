package ac.biu.nlp.nlp.engineml.rteflow.micro.perform;

import ac.biu.nlp.nlp.engineml.datastructures.CanonicalLemmaAndPos;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.finders.Finder;
import ac.biu.nlp.nlp.engineml.operations.finders.Substitution2DLexicalRuleByLemmaPosFinder;
import ac.biu.nlp.nlp.engineml.operations.finders.Substitution2DLexicalRuleByLemmaPosNerFinder;
import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.operations.SubstituteNodeOperation;
import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaPosLexicalRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseEnvelope;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseWithNamedEntities;
import ac.biu.nlp.nlp.engineml.operations.specifications.RuleSubstituteNodeSpecification;
import ac.biu.nlp.nlp.engineml.operations.updater.FeatureVectorUpdater;
import ac.biu.nlp.nlp.engineml.operations.updater.UpdaterForLexicalRule;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.general.immutable.ImmutableSet;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

public class LexicalRuleByLemmaPos2DPerformFactory extends PerformFactory<RuleSubstituteNodeSpecification<LexicalRule>>
{
	public LexicalRuleByLemmaPos2DPerformFactory(
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
	public Finder<RuleSubstituteNodeSpecification<LexicalRule>> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
			throws TeEngineMlException, OperationException
	{
		return null;
	}

	@Override
	public Finder<RuleSubstituteNodeSpecification<LexicalRule>> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			ByLemmaPosLexicalRuleBase<LexicalRule> lexicalRuleBase,
			String ruleBaseName) throws TeEngineMlException, OperationException
	{
		Finder<RuleSubstituteNodeSpecification<LexicalRule>> ret = null;
		// TODO get rid of this RTTI
		if (lexicalRuleBase instanceof RuleBaseWithNamedEntities)
		{
			try
			{
				ret = new Substitution2DLexicalRuleByLemmaPosNerFinder(text, lexicalRuleBase, ruleBaseName,
						Constants.FILTER_STOP_WORDS_IN_LEFT_IN_LEXICAL_RESOURCES,Constants.FILTER_STOP_WORDS_IN_LEFT_IN_LEXICAL_RESOURCES,stopWords,
						hypothesisLemmasAndCanonicalPos, hypothesisLemmasOnly,
						((RuleBaseWithNamedEntities)lexicalRuleBase).getNamedEntitiesOfRuleBase()
						);
			}
			catch(RuleBaseException e)
			{
				throw new TeEngineMlException("Could not retrieve named entities for: \""+ruleBaseName+"\"",e);
			}
			
		}
		else
		{
			ret = new Substitution2DLexicalRuleByLemmaPosFinder<LexicalRule>(text, lexicalRuleBase, ruleBaseName,
					Constants.FILTER_STOP_WORDS_IN_LEFT_IN_LEXICAL_RESOURCES,Constants.FILTER_STOP_WORDS_IN_LEFT_IN_LEXICAL_RESOURCES,stopWords,
					hypothesisLemmasAndCanonicalPos, hypothesisLemmasOnly);
		}
		return ret;
	}

	@Override
	public Finder<RuleSubstituteNodeSpecification<LexicalRule>> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			String ruleBaseName) throws TeEngineMlException, OperationException
	{
		return null;
	}

	@Override
	public Finder<RuleSubstituteNodeSpecification<LexicalRule>> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			RuleBaseEnvelope<Info, BasicNode> ruleBase, String ruleBaseName)
			throws TeEngineMlException, OperationException
	{
		return null;
	}

	@Override
	public GenerationOperation<ExtendedInfo, ExtendedNode> getOperation(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			RuleSubstituteNodeSpecification<LexicalRule> specification)
			throws TeEngineMlException, OperationException
	{
		return new SubstituteNodeOperation(text, hypothesis, specification.getTextNodeToBeSubstituted(), specification.getNewNodeInfo(), specification.getNewAdditionalNodeInformation());
	}

	@Override
	public FeatureVectorUpdater<RuleSubstituteNodeSpecification<LexicalRule>> getUpdater(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
			throws TeEngineMlException, OperationException
	{
		return updaterForLexicalRule;
	}
	

	private ImmutableSet<CanonicalLemmaAndPos> hypothesisLemmasAndCanonicalPos;
	private ImmutableSet<String> hypothesisLemmasOnly;
	private ImmutableSet<String> stopWords;
	private static UpdaterForLexicalRule updaterForLexicalRule = new UpdaterForLexicalRule();
}
