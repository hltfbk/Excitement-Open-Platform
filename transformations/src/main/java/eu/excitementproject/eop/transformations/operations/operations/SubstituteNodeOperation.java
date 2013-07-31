package eu.excitementproject.eop.transformations.operations.operations;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.datastructures.FromBidirectionalMapValueSetMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;


/**
 * A simple operation that substitutes the contents of one node
 * in the parse tree by other contents.
 * <BR>
 * An example is a lexical-rule application which is a substitution of node's
 * contents according to the rule's left-hand-side and right-hand-side.
 * 
 * @author Asher Stern
 * @since Jan 16, 2011
 *
 */
public class SubstituteNodeOperation extends GenerationOperationForExtendedNode
{

	public SubstituteNodeOperation(TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree, TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesisTree, ExtendedNode nodeToSubstitute, NodeInfo newNodeInfo, AdditionalNodeInformation newAdditionalNodeInformation) throws OperationException
	{
		super(textTree, hypothesisTree);
		this.nodeToSubstitute = nodeToSubstitute;
		this.newNodeInfo = newNodeInfo;
		this.newAdditionalNodeInformation = newAdditionalNodeInformation;
	}

	@Override
	protected void generateTheTree() throws OperationException
	{
		mapOrigToGenerated = new SimpleBidirectionalMap<ExtendedNode, ExtendedNode>();
		affectedNodes = new LinkedHashSet<ExtendedNode>();
		this.generatedTree = copySubTree(this.textTree.getTree());
		updateAntecedents();
	}
	
	
	
	//////////////////////////////// PRIVATE & PROTECTED  //////////////////////////////////////
	
	@Override
	protected void generateMapOriginalToGenerated() throws OperationException
	{
		this.mapOriginalToGenerated = new FromBidirectionalMapValueSetMap<ExtendedNode, ExtendedNode>(mapOrigToGenerated);
	}

	
	private ExtendedNode copySubTree(ExtendedNode root)
	{
		ExtendedInfo generatedRootInfo = null;
		if (root==nodeToSubstitute)
		{
			generatedRootInfo = new ExtendedInfo(root.getInfo().getId(), newNodeInfo, root.getInfo().getEdgeInfo(), newAdditionalNodeInformation);
		}
		else
		{
			generatedRootInfo = root.getInfo();
		}
		
		ArrayList<ExtendedNode> generatedChildren = null;
		if (root.getChildren()!=null)
		{
			generatedChildren = new ArrayList<ExtendedNode>(root.getChildren().size());
			for (ExtendedNode child : root.getChildren())
			{
				generatedChildren.add(copySubTree(child));
			}
		}
		
		ExtendedNode generatedRoot = new ExtendedNode(generatedRootInfo);
		if (generatedChildren!=null)
		{
			for (ExtendedNode child : generatedChildren)
			{
				generatedRoot.addChild(child);
			}
		}
		
		mapOrigToGenerated.put(root, generatedRoot);
		if (root==nodeToSubstitute)
		{
			affectedNodes.add(generatedRoot);
		}
		
		return generatedRoot;
	}
	
	private void updateAntecedents()
	{
		Set<ExtendedNode> originalTreeNodes = AbstractNodeUtils.treeToLinkedHashSet(textTree.getTree());
		for (ExtendedNode originalTreeNode : originalTreeNodes)
		{
			if (originalTreeNode.getAntecedent()!=null)
			{
				ExtendedNode originalTreeNodeAntecedent = originalTreeNode.getAntecedent();
				mapOrigToGenerated.leftGet(originalTreeNode).setAntecedent(mapOrigToGenerated.leftGet(originalTreeNodeAntecedent));
			}
		}
	}

	
	
	private ExtendedNode nodeToSubstitute;
	private NodeInfo newNodeInfo;
	private AdditionalNodeInformation newAdditionalNodeInformation;
	private BidirectionalMap<ExtendedNode, ExtendedNode> mapOrigToGenerated = null;
}
