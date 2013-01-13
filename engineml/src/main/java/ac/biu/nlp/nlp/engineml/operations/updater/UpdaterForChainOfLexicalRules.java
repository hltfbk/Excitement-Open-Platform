package ac.biu.nlp.nlp.engineml.operations.updater;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.ChainOfLexicalRules;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.ConfidenceChainItem;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.LexicalRuleWithName;
import ac.biu.nlp.nlp.engineml.operations.specifications.RuleSubstituteNodeSpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.FeatureUpdate;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;


/**
 * 
 * @author Asher Stern
 * @since Jan 25, 2012
 *
 */
public class UpdaterForChainOfLexicalRules extends FeatureVectorUpdater<RuleSubstituteNodeSpecification<ChainOfLexicalRules>>
{

	@Override
	public Map<Integer, Double> updateFeatureVector(
			Map<Integer, Double> originalFeatureVector,
			FeatureUpdate featureUpdate,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			GenerationOperation<ExtendedInfo, ExtendedNode> operation,
			RuleSubstituteNodeSpecification<ChainOfLexicalRules> specification)
			throws TeEngineMlException
	{
		List<ConfidenceChainItem> chain = new ArrayList<ConfidenceChainItem>(specification.getRule().getChain().size());
		for (LexicalRuleWithName lexicalRule : specification.getRule().getChain())
		{
			chain.add(
					new ConfidenceChainItem(lexicalRule.getRuleBaseName(),lexicalRule.getRule().getConfidence()));
		}
		return featureUpdate.forChainOfRules(originalFeatureVector, new ImmutableListWrapper<ConfidenceChainItem>(chain));

	}

}
