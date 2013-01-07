package ac.biu.nlp.nlp.engineml.operations.operations;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.datastructures.FromBidirectionalMapValueSetMap;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.representation.AdditionalNodeInformation;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.general.BidirectionalMap;
import ac.biu.nlp.nlp.general.SimpleBidirectionalMap;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.NodeInfo;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNodeUtils;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;


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
		Set<ExtendedNode> originalTreeNodes = AbstractNodeUtils.treeToSet(textTree.getTree());
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
