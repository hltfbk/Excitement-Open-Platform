package eu.excitementproject.eop.biutee.operations.updater;
import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.macro.Feature;
import eu.excitementproject.eop.biutee.rteflow.macro.FeatureUpdate;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.operations.operations.GenerationOperation;
import eu.excitementproject.eop.transformations.operations.specifications.IsASpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * @author Asher Stern
 * @since Sep 9, 2012
 *
 */
public class UpdaterForIsAByCoreference extends FeatureVectorUpdater<IsASpecification>
{

	@Override
	public Map<Integer, Double> updateFeatureVector(
			Map<Integer, Double> originalFeatureVector,
			FeatureUpdate featureUpdate,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			GenerationOperation<ExtendedInfo, ExtendedNode> operation,
			IsASpecification specification) throws TeEngineMlException
	{
		return featureUpdate.createAndUpdateFeatureVector(
				originalFeatureVector,
				Feature.IS_A_COREFERENCE.getFeatureIndex(),
				-1.0
				);
	}

}
