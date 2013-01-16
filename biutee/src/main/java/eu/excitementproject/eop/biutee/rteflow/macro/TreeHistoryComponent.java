package eu.excitementproject.eop.biutee.rteflow.macro;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;


/**
 * 
 * @author Asher Stern
 * @since Nov 8, 2011
 *
 */
public class TreeHistoryComponent implements Serializable
{
	private static final long serialVersionUID = 6509323409807168820L;
	
	public TreeHistoryComponent(Specification specification,
			Map<Integer, Double> featureVector, Set<ExtendedNode> affectedNodes,
			ExtendedNode tree)
	{
		super();
		this.specification = specification;
		this.featureVector = featureVector;
		this.affectedNodes = affectedNodes;
		this.tree = tree;
	}
	
	public static TreeHistoryComponent onlyFeatureVector(Map<Integer,Double> featureVector)
	{
		return new TreeHistoryComponent(null,featureVector,null,null);
	}
	
	
	
	public Specification getSpecification()
	{
		return specification;
	}
	public Map<Integer, Double> getFeatureVector()
	{
		return featureVector;
	}
	
	public ExtendedNode getTree()
	{
		return tree;
	}

	public Set<ExtendedNode> getAffectedNodes()
	{
		return affectedNodes;
	}
	
	


	public static enum TreeHistoryComponentType
	{
		SPECIFICATION,
		FEATURE_VECTOR,
		TREE,
		AFFECTED_NODES;
	}



	/**
	 * Specification of the operation done on the current tree
	 */
	private final Specification specification;
	
	/**
	 * Feature vector of the current tree
	 */
	private final Map<Integer, Double> featureVector;
	
	/**
	 * The current tree
	 */
	private final ExtendedNode tree;
	
	/**
	 * Nodes in the current tree that were affected by the operation that has been done
	 * on the tree (the operation that is described by "specification")
	 */
	private final Set<ExtendedNode> affectedNodes;
}
