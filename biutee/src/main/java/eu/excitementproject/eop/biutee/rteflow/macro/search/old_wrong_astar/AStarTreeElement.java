package eu.excitementproject.eop.biutee.rteflow.macro.search.old_wrong_astar;
import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;


/**
 * 
 * @author Asher Stern
 * @since Apr 11, 2011
 *
 */
@Deprecated
public class AStarTreeElement implements Comparable<AStarTreeElement>
{
	public AStarTreeElement(ExtendedNode tree,
			Map<ExtendedNode,ExtendedNode> parentMap, TreeHistory history,
			Map<Integer, Double> featureVector,
			String originalSentence, int distance, double aStarEstimation,
			AStarTreeElement generatedFrom)
	{
		this.tree = tree;
		this.parentMap = parentMap;
		this.history = history;
		this.featureVector = featureVector;
		this.originalSentence = originalSentence;
		this.distance = distance;
		this.aStarEstimation = aStarEstimation;
		this.generatedFrom = generatedFrom;
	}




	public int compareTo(AStarTreeElement o)
	{
		if (aStarEstimation<o.aStarEstimation)
			return -1;
		else if (aStarEstimation==o.aStarEstimation)
			return 0;
		else
			return 1;
	}
	
	

	
	public ExtendedNode getTree()
	{
		return tree;
	}
	public Map<ExtendedNode, ExtendedNode> getParentMap()
	{
		return parentMap;
	}
	public TreeHistory getHistory()
	{
		return history;
	}
	public Map<Integer, Double> getFeatureVector()
	{
		return featureVector;
	}
	public int getDistance()
	{
		return distance;
	}
	public double getaStarEstimation()
	{
		return aStarEstimation;
	}
	
	public String getOriginalSentence()
	{
		return originalSentence;
	}
	public AStarTreeElement getGeneratedFrom()
	{
		return generatedFrom;
	}







	private ExtendedNode tree;
	private Map<ExtendedNode,ExtendedNode> parentMap;
	private TreeHistory history;
	private Map<Integer, Double> featureVector;
	private String originalSentence;
	private int distance;
	private double aStarEstimation;
	private AStarTreeElement generatedFrom;


}
