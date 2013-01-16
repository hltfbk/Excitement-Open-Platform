package eu.excitementproject.eop.transformations.utilities.parsetreeutils;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;



/**
 * Given a parse-tree-node to be moved, and given the new parent of that node
 * (i.e., the parent into which the node should be moved), this class finds the
 * path (represented as {@link PathInTree}) from the node to its new parent, in the
 * original parse-tree.
 * 
 * @author Asher Stern
 * @since Jan 12, 2011
 *
 */
public class PathFinder
{
	public PathFinder(TreeAndParentMap<ExtendedInfo,ExtendedNode> tree)
	{
		super();
		this.tree = tree;
	}

	
	public PathInTree findPath(ExtendedNode textNodeToMove, ExtendedNode textNodeToBeParent)
	{
		ExtendedNode leastCommonAncestor = findLca(textNodeToMove,textNodeToBeParent);
		List<ExtendedNode> upList = findUpNodes(textNodeToMove,textNodeToBeParent,leastCommonAncestor);
		List<ExtendedNode> downList = findDownNodes(textNodeToMove,textNodeToBeParent,leastCommonAncestor);
		return new PathInTree(textNodeToMove, textNodeToBeParent, leastCommonAncestor, upList, downList);
	}
	
	
	private ExtendedNode findLca(ExtendedNode textNodeToMove, ExtendedNode textNodeToBeParent)
	{
		ExtendedNode ret = null;
		
		Set<ExtendedNode> nodes_from_root = new HashSet<ExtendedNode>();
		ExtendedNode current = textNodeToMove;
		nodes_from_root.add(current);
		while(tree.getParentMap().containsKey(current))
		{
			ExtendedNode parent = tree.getParentMap().get(current);
			nodes_from_root.add(parent);
			current = parent;
		}
		
		current = textNodeToBeParent;
		while ( (current!=null) && (!nodes_from_root.contains(current)) )
		{
			current = tree.getParentMap().get(current);
		}
		
		if (null==current)
		{
			ret = textNodeToBeParent;
		}
		else
		{
			ret = current;
		}
		return ret;
	}
	
	private List<ExtendedNode> findUpNodes(ExtendedNode textNodeToMove, ExtendedNode textNodeToBeParent, ExtendedNode leastCommonAncestor)
	{
		List<ExtendedNode> ret = new LinkedList<ExtendedNode>();
		if (leastCommonAncestor==textNodeToMove)
		{
			// do nothing - empty list
		}
		else
		{
			ExtendedNode current = tree.getParentMap().get(textNodeToMove);
			while(current != leastCommonAncestor)
			{
				ret.add(current);
				current = tree.getParentMap().get(current);
			}
		}
		return ret;
	}

	
	private List<ExtendedNode> findDownNodes(ExtendedNode textNodeToMove, ExtendedNode textNodeToBeParent, ExtendedNode leastCommonAncestor)
	{
		List<ExtendedNode> ret = new LinkedList<ExtendedNode>();
		if (leastCommonAncestor==textNodeToBeParent)
		{
			// do nothing
		}
		else
		{
			ExtendedNode current = tree.getParentMap().get(textNodeToBeParent);
			while (current!=leastCommonAncestor)
			{
				ret.add(current);
				current=tree.getParentMap().get(current);
			}
		}
		Collections.reverse(ret);
		return ret;
	}
	
	private TreeAndParentMap<ExtendedInfo,ExtendedNode> tree;
}
