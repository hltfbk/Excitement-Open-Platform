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
public class AbstractAStarElement<S extends AbstractAStarElement<S>>
{
	public AbstractAStarElement(int iteration, ExtendedNode tree, String originalSentence,
			Map<Integer, Double> featureVector, Specification lastSpec,
			TreeHistory history, S parent, double cost,
			double unweightedFutureEstimation, double futureEstimation, boolean itIsGoal)
	{
		super();
		this.iteration = iteration;
		this.tree = tree;
		this.originalSentence = originalSentence;
		this.featureVector = featureVector;
		this.lastSpec = lastSpec;
		this.history = history;
		this.parent = parent;
		this.cost = cost;
		this.unweightedFutureEstimation = unweightedFutureEstimation;
		this.futureEstimation = futureEstimation;
		this.itIsGoal = itIsGoal;
		
		this.totalEstimation = cost+futureEstimation;
	}
	
	
	public void setBeliefTTL(int beliefTTL)
	{
		this.beliefTTL = beliefTTL;
	}




	public int getIteration()
	{
		return iteration;
	}
	
	public ExtendedNode getTree()
	{
		return tree;
	}
	
	public String getOriginalSentence()
	{
		return originalSentence;
	}


	public Map<Integer, Double> getFeatureVector()
	{
		return featureVector;
	}

	public Specification getLastSpec()
	{
		return lastSpec;
	}

	public TreeHistory getHistory()
	{
		return history;
	}
	
	public S getParent()
	{
		return parent;
	}

	public double getCost()
	{
		return cost;
	}
	
	public double getUnweightedFutureEstimation()
	{
		return unweightedFutureEstimation;
	}

	public double getFutureEstimation()
	{
		return futureEstimation;
	}

	public Double getTotalEstimation()
	{
		return totalEstimation;
	}

	public boolean isGoal()
	{
		return itIsGoal;
	}
	
	public int getBeliefTTL()
	{
		return beliefTTL;
	}
	
	public double getBelievedFuture()
	{
		return believedFuture;
	}

	public void setBelievedFuture(double believedFuture)
	{
		this.believedFuture = believedFuture;
	}
	
	public List<AStarElement> getChildren()
	{
		return children;
	}

	public void setChildren(List<AStarElement> children)
	{
		this.children = children;
	}









	protected final int iteration;
	protected final ExtendedNode tree;
	protected final String originalSentence;
	protected final Map<Integer,Double> featureVector;
	protected final Specification lastSpec;
	protected final TreeHistory history;
	protected final S parent;
	protected final double cost;
	protected final double unweightedFutureEstimation;
	protected final double futureEstimation;
	protected final Double totalEstimation;
	protected final boolean itIsGoal;
	
	protected int beliefTTL = 0;
	protected double believedFuture = 0;
	
	protected List<AStarElement> children = null;

}
