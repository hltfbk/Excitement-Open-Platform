package ac.biu.nlp.nlp.engineml.operations.updater;
import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;

import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.specifications.RuleSpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.FeatureUpdate;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jan 27, 2012
 *
 */
public class UpdaterForSubstituteMultiWordMultiNode extends FeatureVectorUpdater<RuleSpecification>
{

	@Override
	public Map<Integer, Double> updateFeatureVector(
			Map<Integer, Double> originalFeatureVector,
			FeatureUpdate featureUpdate,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			GenerationOperation<ExtendedInfo, ExtendedNode> operation,
			RuleSpecification specification) throws TeEngineMlException
	{
		return featureUpdate.forSubstitutionMultiWordAsRule(originalFeatureVector,specification);
	}

}
