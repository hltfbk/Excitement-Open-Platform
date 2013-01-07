package ac.biu.nlp.nlp.engineml.rteflow.micro.perform.dummy;

import java.util.Map;

import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.specifications.Specification;
import ac.biu.nlp.nlp.engineml.operations.updater.FeatureVectorUpdater;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.FeatureUpdate;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;

public final class DummyUpdater<T extends Specification> extends FeatureVectorUpdater<T>
{
	@Override
	public Map<Integer, Double> updateFeatureVector(
			Map<Integer, Double> originalFeatureVector,
			FeatureUpdate featureUpdate,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			GenerationOperation<ExtendedInfo, ExtendedNode> operation,
			T specification) throws TeEngineMlException
	{
		throw new TeEngineMlException("DummyUpdater should not be created");
	}
}
