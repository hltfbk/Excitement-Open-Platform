package ac.biu.nlp.nlp.engineml.operations.updater;

import java.util.Map;

import org.apache.log4j.Logger;

import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.specifications.MoveNodeSpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.FeatureUpdate;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.PathFinder;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.PathInTree;
import ac.biu.nlp.nlp.general.immutable.ImmutableSet;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;

/**
 * 
 * @author Asher Stern
 * 
 *
 */
public class UpdaterForMove extends FeatureVectorUpdater<MoveNodeSpecification>
{
	public UpdaterForMove(PathFinder pathFinder)
	{
		this.pathFinder = pathFinder;
	}

	@Override
	public Map<Integer, Double> updateFeatureVector(Map<Integer, Double> originalFeatureVector,FeatureUpdate featureUpdate,TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,GenerationOperation<ExtendedInfo, ExtendedNode> operation,MoveNodeSpecification specification)throws TeEngineMlException
	{
		try
		{
			PathInTree path = pathFinder.findPath(specification.getTextNodeToMove(),specification.getTextNodeToBeParent());
			ExtendedNode theMovedNodeInGenerated = null;
			ImmutableSet<ExtendedNode> mappedOfMovedNode = operation.getMapOriginalToGenerated().get(specification.getTextNodeToMove());
			if (mappedOfMovedNode.size()==0)
			{
				logger.warn("Seems that node to move does not exist in the target tree.");
				theMovedNodeInGenerated = null;
			}
			else
			{
				int debug_numberOfMovedInGenerated = 0;
				for (ExtendedNode candidateMovedNodeInGenerated : mappedOfMovedNode)
				{
					if (operation.getAffectedNodes().contains(candidateMovedNodeInGenerated))
					{
						theMovedNodeInGenerated = candidateMovedNodeInGenerated;
						++debug_numberOfMovedInGenerated;
					}
				}
				if (debug_numberOfMovedInGenerated!=1) throw new TeEngineMlException("BUG: debug_numberOfMovedInGenerated = "+debug_numberOfMovedInGenerated);
			}
			

			return featureUpdate.forMove(originalFeatureVector, path, textTree, specification, theMovedNodeInGenerated);
		}
		catch(OperationException e)
		{
			throw new TeEngineMlException("Unexpected failure in the operation.",e);
		}
	}

	protected PathFinder pathFinder;
	private static final Logger logger = Logger.getLogger(UpdaterForMove.class);
}
