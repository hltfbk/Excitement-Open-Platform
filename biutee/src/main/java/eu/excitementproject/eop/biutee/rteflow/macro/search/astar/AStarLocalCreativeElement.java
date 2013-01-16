package eu.excitementproject.eop.biutee.rteflow.macro.search.astar;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;


/**
 * 
 * @author Asher Stern
 * @since Aug 10, 2011
 *
 */
public class AStarLocalCreativeElement extends AbstractAStarElement<AStarLocalCreativeElement> implements Comparable<AStarLocalCreativeElement>
{

	public AStarLocalCreativeElement(int iteration, ExtendedNode tree,
			String originalSentence, Map<Integer, Double> featureVector,
			Specification lastSpec, TreeHistory history, AStarLocalCreativeElement parent,
			double cost, double unweightedFutureEstimation,
			double futureEstimation, boolean itIsGoal, Double costProfitGain,
			boolean compareByCostPlusFuture)
	{
		super(iteration, tree, originalSentence, featureVector, lastSpec, history,
				parent, cost, unweightedFutureEstimation, futureEstimation, itIsGoal);
		this.costProfitGain = costProfitGain;
		this.compareByCostPlusFuture = compareByCostPlusFuture;
	}

	public AStarLocalCreativeElement(int iteration, ExtendedNode tree,
			String originalSentence, Map<Integer, Double> featureVector,
			Specification lastSpec, TreeHistory history, AStarLocalCreativeElement parent,
			double cost, double unweightedFutureEstimation,
			double futureEstimation, boolean itIsGoal, Double costProfitGain)
	{
		this(iteration, tree, originalSentence, featureVector, lastSpec, history,
				parent, cost, unweightedFutureEstimation, futureEstimation, itIsGoal,
				costProfitGain,false);
	}

	public int compareTo(AStarLocalCreativeElement o)
	{
		if (compareByCostPlusFuture!=o.compareByCostPlusFuture)
			throw new RuntimeException("BUG: compareByCostPlusFuture!=o.compareByCostPlusFuture"); // I cannot throw a checked exception, since the function's signature does not contain a throw declaration
		if (!compareByCostPlusFuture)
		{
		if ( (costProfitGain==null) && (o.costProfitGain==null) )return 0;
		else if (costProfitGain==null)return 1;
		else if (o.costProfitGain==null)return -1;
		else
		{
			if (costProfitGain<o.costProfitGain)return -1;
			else if (costProfitGain==o.costProfitGain) return 0;
			else return 1;
		}
		}
		else
		{
			double costPlusFuture = this.cost+this.futureEstimation;
			double otherCostPlusFuture = o.cost+o.futureEstimation;
			if (costPlusFuture<otherCostPlusFuture) return -1;
			else if (costPlusFuture==otherCostPlusFuture) return 0;
			else return 1;
		}
	}
	
	
	
	public double getCostProfitGain()
	{
		return costProfitGain;
	}
	
	public List<AStarLocalCreativeElement> getLCChildren()
	{
		return children;
	}

	public void setLCChildren(List<AStarLocalCreativeElement> children)
	{
		this.children = children;
	}
	
	public List<AStarElement> getChildren()
	{
		throw new RuntimeException("BUG");
	}

	public void setChildren(List<AStarElement> children)
	{
		throw new RuntimeException("BUG");
	}





	private Double costProfitGain = null; // null=infinite
	private List<AStarLocalCreativeElement> children;
	
	// default - false
	protected final boolean compareByCostPlusFuture; // false - then compare by cost-profit-gain
	


}
