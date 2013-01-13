package ac.biu.nlp.nlp.engineml.operations.updater;
import java.util.Map;

import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule;
import ac.biu.nlp.nlp.engineml.operations.specifications.RuleSubstituteNodeSpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.FeatureUpdate;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;

/**
 * 
 * @author Asher Stern
 * 
 *
 */
public class UpdaterForLexicalRule extends FeatureVectorUpdater<RuleSubstituteNodeSpecification<LexicalRule>>
{
	@Override
	public Map<Integer, Double> updateFeatureVector(
			Map<Integer, Double> originalFeatureVector,
			FeatureUpdate featureUpdate,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			GenerationOperation<ExtendedInfo, ExtendedNode> operation,
			RuleSubstituteNodeSpecification<LexicalRule> specification)
			throws TeEngineMlException
	{
		return featureUpdate.forRuleWithConfidence(originalFeatureVector, specification.getRuleBaseName()  , specification.getConfidence());
	}

}
