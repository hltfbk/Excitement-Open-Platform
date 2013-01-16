package eu.excitementproject.eop.biutee.rteflow.micro.perform;
import java.util.Map;

import eu.excitementproject.eop.biutee.operations.updater.FeatureVectorUpdater;
import eu.excitementproject.eop.biutee.operations.updater.UpdaterForMultiWordChainOfLexicalRules;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.finders.Finder;
import eu.excitementproject.eop.transformations.operations.finders.RulesByBagOfRulesRuleBaseFinder;
import eu.excitementproject.eop.transformations.operations.operations.ExtendedSubstitutionRuleApplicationOperation;
import eu.excitementproject.eop.transformations.operations.operations.GenerationOperation;
import eu.excitementproject.eop.transformations.operations.rules.BagOfRulesRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseEnvelope;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


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
