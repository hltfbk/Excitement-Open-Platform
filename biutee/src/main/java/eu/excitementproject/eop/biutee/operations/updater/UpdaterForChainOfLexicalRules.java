package eu.excitementproject.eop.biutee.operations.updater;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.macro.FeatureUpdate;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.operations.operations.GenerationOperation;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ChainOfLexicalRules;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ConfidenceChainItem;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.LexicalRuleWithName;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSubstituteNodeSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


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
