package eu.excitementproject.eop.biutee.rteflow.micro;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jul 24, 2011
 *
 */
public class TreeAndAffectedNodes
{
	public TreeAndAffectedNodes(ExtendedNode tree, Set<ExtendedNode> affectedNodes) throws TeEngineMlException
	{
		if (null==tree) throw new TeEngineMlException("Null tree");
		if (null==affectedNodes) throw new TeEngineMlException("Null affectedNodes");
		this.tree = tree;
		this.affectedNodes = affectedNodes;
		this.subsetMapOriginalToGenerated = null;
	}
	
	public TreeAndAffectedNodes(ExtendedNode tree, Set<ExtendedNode> affectedNodes, ValueSetMap<ExtendedNode, ExtendedNode> subsetMapOriginalToGenerated) throws TeEngineMlException
	{
		if (null==tree) throw new TeEngineMlException("Null tree");
		if (null==affectedNodes) throw new TeEngineMlException("Null affectedNodes");
		if (null==subsetMapOriginalToGenerated) throw new TeEngineMlException("Null subsetMapOriginalToGenerated");
		this.tree = tree;
		this.affectedNodes = affectedNodes;
		this.subsetMapOriginalToGenerated = subsetMapOriginalToGenerated;
	}

	
	public ExtendedNode getTree()
	{
		return tree;
	}
	
	public Set<ExtendedNode> getAffectedNodes()
	{
		return affectedNodes;
	}
	
	public ValueSetMap<ExtendedNode, ExtendedNode> getSubsetMapOriginalToGenerated()
	{
		return subsetMapOriginalToGenerated;
	}


	



	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((affectedNodes == null) ? 0 : affectedNodes.hashCode());
		result = prime
				* result
				+ ((subsetMapOriginalToGenerated == null) ? 0
						: subsetMapOriginalToGenerated.hashCode());
		result = prime * result + ((tree == null) ? 0 : tree.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeAndAffectedNodes other = (TreeAndAffectedNodes) obj;
		if (affectedNodes == null)
		{
			if (other.affectedNodes != null)
				return false;
		} else if (!affectedNodes.equals(other.affectedNodes))
			return false;
		if (subsetMapOriginalToGenerated == null)
		{
			if (other.subsetMapOriginalToGenerated != null)
				return false;
		} else if (!subsetMapOriginalToGenerated
				.equals(other.subsetMapOriginalToGenerated))
			return false;
		if (tree == null)
		{
			if (other.tree != null)
				return false;
		} else if (!tree.equals(other.tree))
			return false;
		return true;
	}






	private final ExtendedNode tree;
	private final Set<ExtendedNode> affectedNodes;
	private final ValueSetMap<ExtendedNode, ExtendedNode> subsetMapOriginalToGenerated;
}
