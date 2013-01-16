package eu.excitementproject.eop.common.representation.parse.tree.dependency.basic;

import java.util.LinkedList;
import java.util.Queue;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;

public class BasicNodeUtils
{
	public static BidirectionalMap<BasicNode, BasicNode> resetId(BasicNode root)
	{
		BidirectionalMap<BasicNode, BasicNode> ret = new SimpleBidirectionalMap<BasicNode, BasicNode>();
		if (root != null)
		{
			int id = 1;
			Queue<BasicNode> queue = new LinkedList<BasicNode>();
			queue.offer(root);
			while (queue.peek()!=null)
			{
				BasicNode current = queue.poll();
				BasicNode newCurrent = null;
				if (null==current.getInfo())
				{
					newCurrent = new BasicNode(new DefaultInfo(String.valueOf(id), null, null));
				}
				else
				{
					newCurrent = new BasicNode(new DefaultInfo(String.valueOf(id), current.getInfo().getNodeInfo(), current.getInfo().getEdgeInfo()));
				}
				ret.put(current, newCurrent);
				
				if (current.getChildren()!=null)
				{
					for (BasicNode child : current.getChildren())
					{
						queue.offer(child);
					}
				}
				id++;
			}
			setChildren(ret, root);
			
			
		}
		return ret;
	}
	
	private static void setChildren(BidirectionalMap<BasicNode, BasicNode> map, BasicNode root)
	{
		if (root.getChildren()!=null)
		{
			BasicNode mappedToRoot = map.leftGet(root);
			for (BasicNode child : root.getChildren())
			{
				BasicNode mappedToChild = map.leftGet(child);
				mappedToRoot.addChild(mappedToChild);
			}
			for (BasicNode child : root.getChildren())
			{
				setChildren(map, child);
			}
		}
		
	}
	

}
