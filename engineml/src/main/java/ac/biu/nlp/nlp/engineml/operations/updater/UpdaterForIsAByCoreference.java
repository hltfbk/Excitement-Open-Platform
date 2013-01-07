package ac.biu.nlp.nlp.engineml.operations.updater;

import java.util.Map;

import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.specifications.IsASpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.Feature;
import ac.biu.nlp.nlp.engineml.rteflow.macro.FeatureUpdate;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;

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
