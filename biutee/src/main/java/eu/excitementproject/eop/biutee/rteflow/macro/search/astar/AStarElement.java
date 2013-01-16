package eu.excitementproject.eop.biutee.rteflow.macro.search.astar;
import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;


/**
 * 
 * @author Asher Stern
 * @since Jun 17, 2011
 *
 */
public class AStarElement extends AbstractAStarElement<AStarElement> implements Comparable<AStarElement>
{
	public AStarElement(int iteration, ExtendedNode tree,
			String originalSentence, Map<Integer, Double> featureVector,
			Specification lastSpec, TreeHistory history, AStarElement parent,
			double cost, double unweightedFutureEstimation,
			double futureEstimation, boolean itIsGoal)
	{
		super(iteration, tree, originalSentence, featureVector, lastSpec, history,
				parent, cost, unweightedFutureEstimation, futureEstimation, itIsGoal);
	}

	public int compareTo(AStarElement o)
	{
		if ( (null==this.totalEstimation) || (null==o.totalEstimation) )throw new RuntimeException("BUG");
		return this.totalEstimation.compareTo(o.totalEstimation);
	}

}
