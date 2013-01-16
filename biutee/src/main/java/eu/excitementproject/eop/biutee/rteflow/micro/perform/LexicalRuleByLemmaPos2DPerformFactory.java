package eu.excitementproject.eop.biutee.rteflow.micro.perform;
import eu.excitementproject.eop.biutee.operations.updater.FeatureVectorUpdater;
import eu.excitementproject.eop.biutee.operations.updater.UpdaterForLexicalRule;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.datastructures.CanonicalLemmaAndPos;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.finders.Finder;
import eu.excitementproject.eop.transformations.operations.finders.Substitution2DLexicalRuleByLemmaPosFinder;
import eu.excitementproject.eop.transformations.operations.finders.Substitution2DLexicalRuleByLemmaPosNerFinder;
import eu.excitementproject.eop.transformations.operations.operations.GenerationOperation;
import eu.excitementproject.eop.transformations.operations.operations.SubstituteNodeOperation;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseEnvelope;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseWithNamedEntities;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSubstituteNodeSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

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
