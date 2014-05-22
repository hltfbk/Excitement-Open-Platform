package eu.excitementproject.eop.biutee.rteflow.macro.search.old_wrong_astar;
import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

/**
 * 
 * @author Asher Stern
 * @since Apr 11, 2011
 *
 */
@Deprecated
public class AStarUtilities
{
	public static final double MINIMUM_COST_PER_NODE_ESTIMATION = BiuteeConstants.INCREASE_PARAMETERS_VALUE_IN_SEARCH_CLASSIFIER;

	/**
	 * Returns a lower bound estimation about the cost in the future.
	 * 
	 * @param textTree
	 * @param hypothesis
	 * @return
	 * @throws TeEngineMlException 
	 */
	public static double futureEstimation(TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree, TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis, double estimationPerMissingNode) throws TeEngineMlException
	{
		if (estimationPerMissingNode<0) throw new TeEngineMlException("negative estimationPerMissingNode");
		double ret = 0;
		if (0==estimationPerMissingNode)
			ret = TreeUtilities.findRelationsNoMatch(textTree, hypothesis).size()*MINIMUM_COST_PER_NODE_ESTIMATION;
		else
			ret = TreeUtilities.findRelationsNoMatch(textTree, hypothesis).size()*estimationPerMissingNode;
		
		return ret;
	}
	
	public static double pastEstimation(TreeAndFeatureVector textTree, Classifier classifier) throws ClassifierException
	{
		return 0-ClassifierUtils.inverseSigmoid(classifier.classify(textTree.getFeatureVector()));
	}
	
	public static double aStarEstimation(TreeAndFeatureVector textTree, TreeAndParentMap<ExtendedInfo, ExtendedNode> textTreeAndParentMap, TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis, Classifier classifier, double estimationPerMissingNode) throws ClassifierException, TeEngineMlException
	{
		return pastEstimation(textTree, classifier)+futureEstimation(textTreeAndParentMap, hypothesis, estimationPerMissingNode);
		//return futureEstimation(textTreeAndParentMap, hypothesis);
	}
	
	public static AStarTreeElement createElement(TreeAndFeatureVector textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTreeAndParentMap,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			Classifier classifier,
			TreeHistory history,
			String sentence,
			int distance,
			AStarTreeElement generatedFrom,
			double estimationPerMissingNode
			) throws ClassifierException, TeEngineMlException
	{
		double estimation = aStarEstimation(textTree,textTreeAndParentMap,hypothesis,classifier, estimationPerMissingNode);
		
		return new AStarTreeElement(textTree.getTree(), textTreeAndParentMap.getParentMap(),history, textTree.getFeatureVector(), sentence, distance, estimation, generatedFrom);
	}
	
	
	
	

}
