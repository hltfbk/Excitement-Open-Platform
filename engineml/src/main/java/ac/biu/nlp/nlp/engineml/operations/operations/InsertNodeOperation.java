package ac.biu.nlp.nlp.engineml.operations.operations;

import java.util.LinkedHashSet;

import ac.biu.nlp.nlp.engineml.datastructures.FromBidirectionalMapValueSetMap;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.general.BidirectionalMap;
import ac.biu.nlp.nlp.general.SimpleBidirectionalMap;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;

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
