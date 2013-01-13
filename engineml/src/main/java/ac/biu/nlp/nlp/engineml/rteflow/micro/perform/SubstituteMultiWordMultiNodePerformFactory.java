package ac.biu.nlp.nlp.engineml.rteflow.micro.perform;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.finders.Finder;
import ac.biu.nlp.nlp.engineml.operations.finders.RulesByBagOfRulesRuleBaseFinder;
import ac.biu.nlp.nlp.engineml.operations.operations.ExtendedSubstitutionRuleApplicationOperation;
import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.rules.BagOfRulesRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaPosLexicalRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseEnvelope;
import ac.biu.nlp.nlp.engineml.operations.specifications.RuleSpecification;
import ac.biu.nlp.nlp.engineml.operations.updater.FeatureVectorUpdater;
import ac.biu.nlp.nlp.engineml.operations.updater.UpdaterForSubstituteMultiWordMultiNode;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

/**
 * 
 * @author Asher Stern
 * @since Jan 27, 2012
 *
 */
public class SubstituteMultiWordMultiNodePerformFactory extends PerformFactory<RuleSpecification>
{
	public SubstituteMultiWordMultiNodePerformFactory(
			BagOfRulesRuleBase<Info, BasicNode> multiWordMultiNodeRuleBase,
			String multiWordMultiNodeRuleBaseName)
	{
		super();
		this.multiWordMultiNodeRuleBase = multiWordMultiNodeRuleBase;
		this.multiWordMultiNodeRuleBaseName = multiWordMultiNodeRuleBaseName;
	}


	@Override
	public Finder<RuleSpecification> getFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
			throws TeEngineMlException, OperationException
	{
		return new RulesByBagOfRulesRuleBaseFinder(text, multiWordMultiNodeRuleBase, multiWordMultiNodeRuleBaseName);
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
		return null;
	}

	@Override
	public GenerationOperation<ExtendedInfo, ExtendedNode> getOperation(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			RuleSpecification specification) throws TeEngineMlException,
			OperationException
	{
		return new ExtendedSubstitutionRuleApplicationOperation(text, hypothesis, specification.getRule().getRule(), specification.getMapLhsToTree());
	}

	@Override
	public FeatureVectorUpdater<RuleSpecification> getUpdater(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
			throws TeEngineMlException, OperationException
	{
		return updaterForSubstituteMultiWordMultiNode;
	}

	private final BagOfRulesRuleBase<Info, BasicNode> multiWordMultiNodeRuleBase;
	private final String multiWordMultiNodeRuleBaseName;
	private static UpdaterForSubstituteMultiWordMultiNode updaterForSubstituteMultiWordMultiNode = new UpdaterForSubstituteMultiWordMultiNode();
}
