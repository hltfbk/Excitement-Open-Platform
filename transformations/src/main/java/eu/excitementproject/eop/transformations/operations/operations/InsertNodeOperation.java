package eu.excitementproject.eop.transformations.operations.operations;
import java.util.LinkedHashSet;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.datastructures.FromBidirectionalMapValueSetMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * An on-the-fly operation that inserts a new node to a tree.
 * 
 * @author Asher Stern
 * @since 2011
 *
 */
public class InsertNodeOperation extends GenerationOperationForExtendedNode
{
	public InsertNodeOperation(TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesisTree,
			ExtendedInfo nodeToInsert,
			ExtendedNode whereToInsert) throws OperationException
	{
		super(textTree, hypothesisTree);
		this.nodeToInsert = nodeToInsert;
		this.whereToInsert = whereToInsert;
	}

	@Override
	protected void generateTheTree() throws OperationException
	{
		this.affectedNodes = new LinkedHashSet<ExtendedNode>();
		mapOrigToGenerated = new SimpleBidirectionalMap<ExtendedNode, ExtendedNode>();
		this.generatedTree = copyTree(this.textTree.getTree());
		updateAntecedents();
		
		ExtendedNode parent = mapOrigToGenerated.leftGet(whereToInsert);
		ExtendedNode insertedNode = new ExtendedNode(nodeToInsert);
		// ExtendedNode insertedNode = new ExtendedNode(new ExtendedInfo(nodeToInsert.getId(),nodeToInsert.getNodeInfo(),nodeToInsert.getEdgeInfo(),AdditionalInformationServices.emptyInformation()));
		affectedNodes.add(insertedNode);
		parent.addChild(insertedNode);
	}
	
	@Override
	protected void generateMapOriginalToGenerated() throws OperationException
	{
		if (null==this.mapOrigToGenerated) throw new OperationException("internal bug");
		this.mapOriginalToGenerated = new FromBidirectionalMapValueSetMap<ExtendedNode, ExtendedNode>(mapOrigToGenerated);
	}

	
	private ExtendedNode copyTree(ExtendedNode subtree)
	{
		ExtendedNode ret = new ExtendedNode(subtree.getInfo());
		if (subtree.getChildren()!=null)
		{
			for (ExtendedNode child : subtree.getChildren())
			{
				ExtendedNode generatedChild = copyTree(child);
				ret.addChild(generatedChild);
			}
		}
		mapOrigToGenerated.put(subtree, ret);
		return ret;
	}
	
	private void updateAntecedents()
	{
		for (ExtendedNode originalNode : mapOrigToGenerated.leftSet())
		{
			if (originalNode.getAntecedent()!=null)
			{
				mapOrigToGenerated.leftGet(originalNode).setAntecedent(mapOrigToGenerated.leftGet(originalNode.getAntecedent()));
			}
		}
	}
	

	protected ExtendedInfo nodeToInsert;
	protected ExtendedNode whereToInsert;
	
	protected BidirectionalMap<ExtendedNode, ExtendedNode> mapOrigToGenerated = null;


}
