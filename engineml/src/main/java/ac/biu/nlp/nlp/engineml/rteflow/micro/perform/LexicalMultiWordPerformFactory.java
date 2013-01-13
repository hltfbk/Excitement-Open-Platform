package ac.biu.nlp.nlp.engineml.rteflow.micro.perform;
import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

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
import ac.biu.nlp.nlp.engineml.operations.updater.UpdaterForRule;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;


/**
 * 
 * @author Asher Stern
 * @since Jan 25, 2012
 *
 */
public class LexicalMultiWordPerformFactory extends PerformFactory<RuleSpecification>
{
	public LexicalMultiWordPerformFactory(Map<String,BagOfRulesRuleBase<Info, BasicNode>> mapLexicalMultiWord)
	{
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
			RuleBaseEnvelope<Info, BasicNode> ruleBase, String ruleBaseName)
			throws TeEngineMlException, OperationException
	{
		return null;
	}
	
	@Override
	public Finder<RuleSpecification> getFinder(TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			String ruleBaseName) throws TeEngineMlException, OperationException
	{
		BagOfRulesRuleBase<Info, BasicNode> ruleBase = mapLexicalMultiWord.get(ruleBaseName);
		RulesByBagOfRulesRuleBaseFinder finder = new RulesByBagOfRulesRuleBaseFinder(text, ruleBase, ruleBaseName);
		return finder;
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
		return updaterForRule;
	}

	private Map<String,BagOfRulesRuleBase<Info, BasicNode>> mapLexicalMultiWord;
	private static UpdaterForRule updaterForRule = new UpdaterForRule();
}
