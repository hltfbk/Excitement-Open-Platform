package eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;


/**
 * 
 * @author Asher Stern
 * @since Jul 24, 2011
 *
 */
public class LocalCreativeTreeElement implements Serializable
{
	private static final long serialVersionUID = 6470192674781809529L;
	
	public LocalCreativeTreeElement(ExtendedNode tree, TreeHistory history,
			Map<Integer, Double> featureVector, int localIteration,
			int globalIteration,
			Set<ExtendedNode> affectedNodes, double cost, double gap)
	{
		super();
		this.tree = tree;
		this.history = history;
		this.featureVector = featureVector;
		this.localIteration = localIteration;
		this.globalIteration = globalIteration;
		this.affectedNodes = affectedNodes;
		this.cost = cost;
		this.gap = gap;
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
	public int getLocalIteration()
	{
		return localIteration;
	}
	public Set<ExtendedNode> getAffectedNodes()
	{
		return affectedNodes;
	}
	public double getCost()
	{
		return cost;
	}
	public double getGap()
	{
		return gap;
	}
	public int getGlobalIteration()
	{
		return globalIteration;
	}






	private final ExtendedNode tree;
	private final TreeHistory history;
	private final Map<Integer, Double> featureVector;
	private final int localIteration;
	private final int globalIteration;
	private final Set<ExtendedNode> affectedNodes;
	private final double cost;
	private final double gap;
}
