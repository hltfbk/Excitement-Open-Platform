package eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged;
import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.SingleTreeEvaluations;


/**
 * 
 * @author Asher Stern
 * @since Sep 2, 2011
 *
 */
public class KStagedElement
{
	public KStagedElement(ExtendedNode tree, TreeHistory history,
			Map<Integer, Double> featureVector, String originalSentence,
			double cost, SingleTreeEvaluations evaluations, int iteration,
			SingleTreeEvaluations evaluationsOfOriginalTree,
			double costOfOriginalTree)
	{
		super();
		this.tree = tree;
		this.history = history;
		this.featureVector = featureVector;
		this.originalSentence = originalSentence;
		this.cost = cost;
		this.evaluations = evaluations;
		this.iteration = iteration;
		this.evaluationsOfOriginalTree = evaluationsOfOriginalTree;
		this.costOfOriginalTree = costOfOriginalTree;
	}
	public ExtendedNode getTree()
	{
		return tree;
	}
	public TreeHistory getHistory()
	{
		return history;
	}
	public Map<Integer, Double> getFeatureVector()
	{
		return featureVector;
	}
	public String getOriginalSentence()
	{
		return originalSentence;
	}
	public double getCost()
	{
		return cost;
	}
	public SingleTreeEvaluations getEvaluations()
	{
		return evaluations;
	}
	public int getIteration()
	{
		return iteration;
	}
	public SingleTreeEvaluations getEvaluationsOfOriginalTree()
	{
		return evaluationsOfOriginalTree;
	}
	public double getCostOfOriginalTree()
	{
		return costOfOriginalTree;
	}





	private final ExtendedNode tree;
	private final TreeHistory history;
	private final Map<Integer, Double> featureVector;
	private final String originalSentence;
	private final double cost;
	private final SingleTreeEvaluations evaluations;
	private final int iteration;
	
	private final SingleTreeEvaluations evaluationsOfOriginalTree;
	private final double costOfOriginalTree;
}
