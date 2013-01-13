package ac.biu.nlp.nlp.engineml.operations.updater;
import java.util.Map;

import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.specifications.SubstituteNodeSpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.FeatureUpdate;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;


/**
 * 
 * @author Asher Stern
 * @since Jan 25, 2012
 *
 */
public class UpdaterForChangePredicateTruth extends FeatureVectorUpdater<SubstituteNodeSpecification>
{
	@Override
	public Map<Integer, Double> updateFeatureVector(
			Map<Integer, Double> originalFeatureVector,
			FeatureUpdate featureUpdate,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			GenerationOperation<ExtendedInfo, ExtendedNode> operation,
			SubstituteNodeSpecification specification)
			throws TeEngineMlException
	{
		return featureUpdate.forChangePredicateTruth(originalFeatureVector);
	}

}
