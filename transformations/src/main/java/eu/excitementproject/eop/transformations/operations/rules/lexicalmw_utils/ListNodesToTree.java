package eu.excitementproject.eop.transformations.operations.rules.lexicalmw_utils;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;



/**
 * 
 * @author Asher Stern
 * @since Jul 4, 2011
 *
 */
public class ListNodesToTree
{
	/**
	 * Given a tree and a collection of nodes - all of them in the tree - such that
	 * there exist a sub-tree headed by one of the nodes in the collection and all
	 * other nodes from that collection exist in that sub tree:
	 * this function finds the node which is the head of that sub-tree.
	 * <BR>
	 * If this is not the case (there does not exist a sub-tree headed by one
	 * of the given nodes and contains all of the given nodes) - the output is
	 * undefined, but it is guaranteed that the output is one of the given nodes.
	 * 
	 * @param tree
	 * @param nodes
	 * @return
	 */
	public static BasicNode findHead(BasicNode tree, Collection<BasicNode> nodes)
	{
		BasicNode ret = null;
		Queue<BasicNode> queue = new LinkedList<BasicNode>();
		queue.offer(tree);
		while ( (null==ret) && (queue.size()!=0) )
		{
			BasicNode current = queue.poll();
			if (nodes.contains(current))
			{
				ret = current;
			}
			else
			{
				if (current.getChildren()!=null)
				{
					for (BasicNode child : current.getChildren())
					{
						queue.offer(child);
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Given a head of sub-tree and a collection of nodes such that all of them
	 * are in that sub-tree, and that head is in the collection:
	 * this function returns a copy of that sub-tree that contains only the
	 * nodes in the collections (all other nodes are "removed", i.e. are not
	 * in the returned sub-tree).
	 * 
	 * @param head
	 * @param nodes
	 * @return
	 */
	public static BasicNode createTree(BasicNode head, Collection<BasicNode> nodes)
	{
		BasicNode ret = new BasicNode(head.getInfo());
		if (head.getChildren()!=null)
		{
			for (BasicNode child : head.getChildren())
			{
				if (nodes.contains(child))
				{
					ret.addChild(createTree(child, nodes));
				}
			}
		}
		return ret;
	}

}
