package ac.biu.nlp.nlp.engineml.rteflow.micro.perform;

import java.util.Map;

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
import ac.biu.nlp.nlp.engineml.operations.updater.UpdaterForMultiWordChainOfLexicalRules;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

public class MultiWordChainOfLexicalRulesPerformFactory extends PerformFactory<RuleSpecification>
{
	
	public MultiWordChainOfLexicalRulesPerformFactory(
			Map<String, BagOfRulesRuleBase<Info, BasicNode>> mapLexicalMultiWord)
	{
		super();
		this.mapLexicalMultiWord = mapLexicalMultiWord;
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
		BagOfRulesRuleBase<Info, BasicNode> ruleBase = mapLexicalMultiWord.get(ruleBaseName);
		return new RulesByBagOfRulesRuleBaseFinder(text, ruleBase, ruleBaseName);
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
		return updaterForMultiWordChainOfLexicalRules;
	}
	

	private Map<String,BagOfRulesRuleBase<Info, BasicNode>> mapLexicalMultiWord;
	private static UpdaterForMultiWordChainOfLexicalRules updaterForMultiWordChainOfLexicalRules = new UpdaterForMultiWordChainOfLexicalRules();
}
