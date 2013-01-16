package eu.excitementproject.eop.transformations.utilities.parsetreeutils;
import java.util.List;

import eu.excitementproject.eop.transformations.representation.ExtendedNode;



/**
 * A "path-in-tree" is a path from one node in the tree to another node in
 * the same tree.
 * <P>
 * The information about such a path is
 * <UL>
 * <LI>The node in which the path starts</LI>
 * <LI>The node in which the path ends, the least common ancestor in that path (i.e.
 * the whole path exists in the sub tree rooted by that least-common-ancestor)</LI>
 * <LI>And all the nodes along the path</LI>
 * </UL>
 * 
 * 
 * @author Asher Stern
 * @since Jan 12, 2011
 *
 */
public class PathInTree
{
	public PathInTree(ExtendedNode from, ExtendedNode to,
			ExtendedNode leastCommonAncestor, List<ExtendedNode> upNodes,
			List<ExtendedNode> downNodes)
	{
		super();
		this.from = from;
		this.to = to;
		this.leastCommonAncestor = leastCommonAncestor;
		this.upNodes = upNodes;
		this.downNodes = downNodes;
	}
	
	
	
	public ExtendedNode getFrom()
	{
		return from;
	}
	public ExtendedNode getTo()
	{
		return to;
	}
	public ExtendedNode getLeastCommonAncestor()
	{
		return leastCommonAncestor;
	}
	public List<ExtendedNode> getUpNodes()
	{
		return upNodes;
	}
	public List<ExtendedNode> getDownNodes()
	{
		return downNodes;
	}



	private final ExtendedNode from; // the text node to be moved
	private final ExtendedNode to; // the new parent
	private final ExtendedNode leastCommonAncestor;
	private final List<ExtendedNode> upNodes; // not including from, to, leastCommonAncestor
	private final List<ExtendedNode> downNodes; // not including from, to, leastCommonAncestor
}
