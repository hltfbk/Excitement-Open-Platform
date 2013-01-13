package ac.biu.nlp.nlp.engineml.rteflow.macro.search.astar;
import java.util.Map;

import ac.biu.nlp.nlp.engineml.operations.specifications.Specification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TreeHistory;

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
