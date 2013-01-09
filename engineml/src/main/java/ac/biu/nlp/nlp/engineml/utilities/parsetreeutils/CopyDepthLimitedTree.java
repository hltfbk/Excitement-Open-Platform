package ac.biu.nlp.nlp.engineml.utilities.parsetreeutils;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNodeConstructor;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.EdgeInfo;

/**
 * Copies a given tree, up to a given depth.<BR>
 * For example, if the tree looks like:<BR>
 * <pre>
 *            X
 *            |
 *       ------------
 *       |          |
 *       Y          Z
 *       |
 *    -------
 *    |     |
 *    W     V
 *    |     |
 *  ----  -----
 *  |  |  |   |
 *  T  U  S   Q
 * </pre>
 * And the depth is 3, then the resulting tree will be:<BR>
 * <pre>
 *            X
 *            |
 *       ------------
 *       |          |
 *       Y          Z
 *       |
 *    -------
 *    |     |
 *    W     V
 * </pre>
 * 
 * @author Asher Stern
 * @since Sep 6, 2012
 *
 * @param <T>
 * @param <S>
 */
public class CopyDepthLimitedTree
{
	////////////////////// PUBLIC /////////////////////////
	
	public CopyDepthLimitedTree(ExtendedNode tree, EdgeInfo newRootEdgeInfo)
	{
		super();
		this.tree = tree;
		this.newRootEdgeInfo = newRootEdgeInfo;
	}
	
	public CopyDepthLimitedTree(ExtendedNode tree)
	{
		this(tree,null);
	}
	
	public void copy(int depth)
	{
		mapOriginalToGenerated = new SimpleBidirectionalMap<ExtendedNode, ExtendedNode>();
		generatedTree = copy(tree,depth,newRootEdgeInfo);
	}
	
	public ExtendedNode getGeneratedTree()
	{
		return generatedTree;
	}

	public BidirectionalMap<ExtendedNode, ExtendedNode> getMapOriginalToGenerated()
	{
		return mapOriginalToGenerated;
	}


	////////////////////// PRIVATE /////////////////////////


	private ExtendedNode copy (ExtendedNode subtree, int depth, EdgeInfo edgeInfo)
	{
		ExtendedNode ret;
		if (null==edgeInfo)
		{
			ret = nodeConstructor.newNode(subtree.getInfo());
		}
		else
		{
			ExtendedInfo originalInfo = subtree.getInfo();
			if (null==originalInfo) originalInfo = ExtendedNodeConstructor.EMPTY_EXTENDED_INFO;
			ret = new ExtendedNode(new ExtendedInfo(new DefaultInfo(originalInfo.getId(), originalInfo.getNodeInfo(), edgeInfo), originalInfo.getAdditionalNodeInformation()));
		}
		mapOriginalToGenerated.put(subtree,ret);
		if ( (subtree.hasChildren()) && (depth>0) )
		{
			for (ExtendedNode child : subtree.getChildren())
			{
				ret.addChild(copy(child,depth-1,null));
			}
		}
		return ret;
	}

	private ExtendedNode tree;
	private EdgeInfo newRootEdgeInfo = null;
	private ExtendedNodeConstructor nodeConstructor = new ExtendedNodeConstructor();
	
	private ExtendedNode generatedTree;
	private BidirectionalMap<ExtendedNode, ExtendedNode> mapOriginalToGenerated;
}
