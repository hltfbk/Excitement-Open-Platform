package eu.excitementproject.eop.transformations.utilities.parsetreeutils;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeConstructor;

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
