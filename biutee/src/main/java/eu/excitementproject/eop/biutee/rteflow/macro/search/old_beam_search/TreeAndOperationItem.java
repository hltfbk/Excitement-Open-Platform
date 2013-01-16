package eu.excitementproject.eop.biutee.rteflow.macro.search.old_beam_search;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.script.SingleOperationItem;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * Used for caching purposes in {@link BeamSearchTextTreesProcessor}.
 * 
 * @author Asher Stern
 * @since Jun 3, 2011
 *
 */
public class TreeAndOperationItem
{
	public TreeAndOperationItem(TreeAndFeatureVector tree,
			SingleOperationItem operationItem) throws TeEngineMlException
	{
		super();
		if (null==tree)throw new TeEngineMlException("Null tree");
		if (null==operationItem)throw new TeEngineMlException("Null operationItem");
		this.tree = tree;
		this.operationItem = operationItem;
	}
	
	public TreeAndFeatureVector getTree()
	{
		return tree;
	}
	public SingleOperationItem getOperationItem()
	{
		return operationItem;
	}
	
	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((operationItem == null) ? 0 : operationItem.hashCode());
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
		TreeAndOperationItem other = (TreeAndOperationItem) obj;
		if (operationItem == null)
		{
			if (other.operationItem != null)
				return false;
		} else if (!operationItem.equals(other.operationItem))
			return false;
		if (tree == null)
		{
			if (other.tree != null)
				return false;
		} else if (!tree.equals(other.tree))
			return false;
		return true;
	}



	private final TreeAndFeatureVector tree;
	private final SingleOperationItem operationItem;
}
