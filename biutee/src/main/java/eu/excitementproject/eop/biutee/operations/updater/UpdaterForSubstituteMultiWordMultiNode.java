package eu.excitementproject.eop.biutee.operations.updater;
import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.macro.FeatureUpdate;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.operations.operations.GenerationOperation;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


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
