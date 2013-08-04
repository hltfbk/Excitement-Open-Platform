package eu.excitementproject.eop.biutee.rteflow.macro;

import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * This class holds a tree, and a running index (0,1,2,...).<BR>
 * It is used for log printouts ("Processing sentence #"+tree.getIndex()").
 * 
 * @author Asher Stern
 */
public final class TreeAndIndex
{
	public TreeAndIndex(ExtendedNode tree, int index)
	{
		this.tree = tree;
		this.index = index;
	}
	public ExtendedNode getTree()
	{
		return tree;
	}
	public int getIndex()
	{
		return index;
	}

	private final ExtendedNode tree;
	private final int index;
}