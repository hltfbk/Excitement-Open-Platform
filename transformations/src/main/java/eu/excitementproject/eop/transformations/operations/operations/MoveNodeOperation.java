package eu.excitementproject.eop.transformations.operations.operations;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.datastructures.FromBidirectionalMapValueSetMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;


/**
 * An on-the-fly operation that moves a node from one location to another location in the tree.
 *  
 * @author Asher Stern
 * @since Dec 30, 2010
 *
 */
public class MoveNodeOperation extends GenerationOperationForExtendedNode
{

	public MoveNodeOperation(TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesisTree,
			ExtendedNode nodeToMove,
			ExtendedNode newParent,
			EdgeInfo newEdgeInfo) throws OperationException
	{
		super(textTree, hypothesisTree);
		
		if (hypothesisTree.getTree()==nodeToMove) throw new OperationException("Moving the root is illegal.");
		
		this.nodeToMove = nodeToMove;
		this.newParent = newParent;
		this.newEdgeInfo = newEdgeInfo;
	}
	

	@Override
	public boolean discardTheGeneratedTree() throws OperationException
	{
		if (null==this.generatedTree) throw new OperationException("The operation has not been performed yet!");
		boolean ret = false;
		if (isNodeToMoveNotExistInGeneratedTree())
		{
			ret = true;
		}
		return ret;
	}
	

	@Override
	protected void generateTheTree() throws OperationException
	{
		if (textTree.getTree()==nodeToMove) throw new OperationException("Moving the root is illegal.");
		affectedNodes = new LinkedHashSet<ExtendedNode>();
		mapOrigToCopy = new SimpleBidirectionalMap<ExtendedNode, ExtendedNode>();
		this.generatedTree = recursiveGenerate(textTree.getTree(),null,false);
		updateAntecedentInformation();
		this.nodeToMoveNotExistInGeneratedTree = !nodeHasAlreadyBeenMoved;
	}
	
	
	/**
	 * Returns <tt>true</tt> in an abnormal case which the nodeToMove
	 * was not moved, and does not exist at all in the generated tree.
	 * That abnormal case occurs when newParent is descendant of nodeToMove
	 * in the original tree.
	 * 
	 * @return <tt>true</tt> if nodeToMove does not exist in the generated tree.
	 */
	public boolean isNodeToMoveNotExistInGeneratedTree()
	{
		return nodeToMoveNotExistInGeneratedTree;
	}
	
	@Override
	public Set<ExtendedNode> getAffectedNodes() throws OperationException
	{
		if (null==affectedNodes) throw new OperationException("affectedNodes is null");
		if (affectedNodes.size()==0)
		{
			// if the node to move does not exist in the generated tree,
			// then indeed there are no affected-nodes. Otherwise - it is a bug.
			boolean bug=true;
			if (isNodeToMoveNotExistInGeneratedTree())
				bug=false;
			if (bug)
				throw new OperationException("affectedNodes is empty");
		}
		return affectedNodes;

	}


	@Override
	protected void generateMapOriginalToGenerated() throws OperationException
	{
		if (null==mapOrigToCopy) throw new OperationException("internal bug");
		this.mapOriginalToGenerated = new FromBidirectionalMapValueSetMap<ExtendedNode, ExtendedNode>(mapOrigToCopy);
	}

	
	/**
	 * A recursive method that creates a copy of the given sub-tree.<BR>
	 * The copy differs from the original one, only in the location of the <tt>nodeToMove</tt>.
	 * 
	 * @param node
	 * @param otherEdgeInfo
	 * @return
	 */
	protected ExtendedNode recursiveGenerate(final ExtendedNode node,EdgeInfo otherEdgeInfo, boolean copyingMovedSubTree)
	{
		ExtendedInfo newInfo = null;
		if (otherEdgeInfo!=null)
		{
			newInfo = new ExtendedInfo(node.getInfo().getId(), node.getInfo().getNodeInfo(), otherEdgeInfo, node.getInfo().getAdditionalNodeInformation());
		}
		else
		{
			newInfo = node.getInfo();
		}
		ExtendedNode newNode = new ExtendedNode(newInfo);
		List<ExtendedNode> newChildren = null;
		if (node.getChildren()!=null)
		{
			newChildren = new ArrayList<ExtendedNode>(node.getChildren().size());
			for (ExtendedNode originalChild : node.getChildren())
			{
				if (nodeToMove!=originalChild) // don't copy this child. This is not its location in the generated tree.
				{
					ExtendedNode newChild = recursiveGenerate(originalChild,null,copyingMovedSubTree);
					newChildren.add(newChild);
				}
			}
		}
		if (newChildren!=null)
		{
			for (ExtendedNode newchild : newChildren)
			{
				newNode.addChild(newchild);
			}
		}
		if ((!nodeHasAlreadyBeenMoved)&&(node==newParent)) // This is the new parent. nodeToMove should be its child.
		{
			nodeHasAlreadyBeenMoved=true;
			ExtendedNode copyOfNodeToMove = recursiveGenerate(nodeToMove,this.newEdgeInfo,true);
			newNode.addChild(copyOfNodeToMove);
		}
		
		mapOrigToCopy.put(node, newNode);
		
		// Asher 4-January-2012. Changing the logic here. Only the nodeToMove should be treated as "affected node".
		// if (copyingMovedSubTree)
		if (nodeToMove==node)
		{
			affectedNodes.add(newNode);
		}
		return newNode;
	}
	
	protected void updateAntecedentInformation()
	{
		for (ExtendedNode newNode : mapOrigToCopy.rightSet())
		{
			ExtendedNode originalNode = mapOrigToCopy.rightGet(newNode);
			if (originalNode.getAntecedent()!=null)
			{
				ExtendedNode originalNodeAntecedent = originalNode.getAntecedent();
				ExtendedNode newNodeAntecedent = mapOrigToCopy.leftGet(originalNodeAntecedent);
				newNode.setAntecedent(newNodeAntecedent);
			}
		}
	}
	
	
	protected ExtendedNode nodeToMove;
	protected ExtendedNode newParent;
	protected EdgeInfo newEdgeInfo;
	
	protected BidirectionalMap<ExtendedNode, ExtendedNode> mapOrigToCopy = null;
	
	protected boolean nodeHasAlreadyBeenMoved = false;

	/**
	 * The normal case is that nodeToMove is moved to somewhere in the tree.
	 * However, consider the case that nodeToMove is an ancestor of newParent.
	 * In this case, when copying the tree and nodeToMove is encountered - it
	 * is not copied (i.e. its subtree is not copier), but since thus newParent
	 * is never encountered in the copy process - then nodeToMove will not be
	 * moved.
	 * I let that anomaly to occur, but this flag will indicate the occurence
	 * of such an anomaly.
	 */
	protected boolean nodeToMoveNotExistInGeneratedTree = false;

}
