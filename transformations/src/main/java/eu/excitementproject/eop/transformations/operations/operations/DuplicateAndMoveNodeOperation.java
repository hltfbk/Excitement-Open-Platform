package eu.excitementproject.eop.transformations.operations.operations;
import java.util.LinkedHashSet;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeConstructor;


/**
 * Creates a new node and puts it in the specified location.
 * The duplicated node is a copy of an original node, specified as "nodeToMove".
 * Note that the node's children are not moved (in contrary to regular move operation, in which
 * the whole sub-tree is moved).
 * @author Asher Stern
 * @since Jan 17, 2011
 *
 */
public class DuplicateAndMoveNodeOperation extends GenerationOperationForExtendedNode
{

	public DuplicateAndMoveNodeOperation(TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesisTree,
			ExtendedNode nodeToMove,
			ExtendedNode newParent,
			EdgeInfo newEdgeInfo) throws OperationException
	{
		super(textTree, hypothesisTree);
		this.nodeToMove = nodeToMove;
		this.newParent = newParent;
		this.newEdgeInfo = newEdgeInfo;
	}

	@Override
	protected void generateTheTree() throws OperationException
	{
		affectedNodes = new LinkedHashSet<ExtendedNode>();
		//BidirectionalMap<AbstractNode<Info,?>, EnglishNode> copyMap = AbstractNodeUtils.copyTreeReturnMap(textTree.getTree(), new EnglishNodeConstructor());
		BidirectionalMap<ExtendedNode, ExtendedNode> copyMap = AbstractNodeUtils.strictTypeCopyTree(textTree.getTree(), new ExtendedNodeConstructor());
		
		ExtendedNode duplicatedNode = new ExtendedNode(new ExtendedInfo(new DefaultInfo(nodeToMove.getInfo().getId(), nodeToMove.getInfo().getNodeInfo(), this.newEdgeInfo), nodeToMove.getInfo().getAdditionalNodeInformation()) );
		ExtendedNode parentInGeneratedTree = copyMap.leftGet(newParent);
		parentInGeneratedTree.addChild(duplicatedNode);
		if (nodeToMove.getAntecedent()!=null)
		{
			ExtendedNode antecedentInOriginal = nodeToMove.getAntecedent();
			ExtendedNode antecedentInGenerated = copyMap.leftGet(antecedentInOriginal);
			duplicatedNode.setAntecedent(antecedentInGenerated);
		}
		affectedNodes.add(duplicatedNode);
		
		this.generatedTree = copyMap.leftGet(textTree.getTree());
		
		this.mapOriginalToGenerated = new SimpleValueSetMap<ExtendedNode, ExtendedNode>();
		for (ExtendedNode originalNode : copyMap.leftSet())
		{
			mapOriginalToGenerated.put(originalNode,copyMap.leftGet(originalNode));
		}
		mapOriginalToGenerated.put(nodeToMove,duplicatedNode);
	}

	@Override
	protected void generateMapOriginalToGenerated() throws OperationException
	{
		// do nothing. It is done in the generate() method.
	}

	
	

	
	private ExtendedNode nodeToMove;
	private ExtendedNode newParent;
	private EdgeInfo newEdgeInfo;
}
