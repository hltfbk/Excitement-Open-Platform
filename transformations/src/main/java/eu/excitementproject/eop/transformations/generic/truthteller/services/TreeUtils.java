/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.services;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeCopier;
import eu.excitementproject.eop.common.representation.parse.tree.TreeCopier.InfoConverter;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.representation.ExtendedConstructionNode;
import eu.excitementproject.eop.transformations.representation.ExtendedConstructionNodeConstructor;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeConstructor;

/**
 * 
 * @author Amnon Lotan
 * @since 27 ���� 2011
 * 
 */
public class TreeUtils {

	/**
	 * @param constructionTree
	 * @return
	 * @throws AnnotatorException 
	 */
	public static TwoTreesAndTheirBidirectionalMap<ExtendedConstructionNode, ExtendedNode> dupConstructionTreeToTree(ExtendedConstructionNode constructionTree)
			throws AnnotatorException {
		InfoConverter<ExtendedConstructionNode, ExtendedInfo> constructionNodeToNodeInfoConverter = new InfoConverter<ExtendedConstructionNode, ExtendedInfo>() {
			@Override
			public ExtendedInfo convert(ExtendedConstructionNode node) {
				return node.getInfo();
			}
		};
		TreeCopier<ExtendedInfo, ExtendedConstructionNode, ExtendedInfo, ExtendedNode> treeCopier = 
				new TreeCopier<ExtendedInfo, ExtendedConstructionNode, ExtendedInfo, ExtendedNode>(constructionTree, constructionNodeToNodeInfoConverter, 
						new ExtendedNodeConstructor());  
		treeCopier.copy();
		try {
			return  new TwoTreesAndTheirBidirectionalMap<ExtendedConstructionNode, ExtendedNode>(constructionTree, treeCopier.getGeneratedTree(), treeCopier.getNodesMap());
		} catch (AnnotatorException e) {
			throw new AnnotatorException("Error duplicating " + constructionTree, e);
		}
	}

	/**
	 * @param tree
	 * @return
	 * @throws AnnotatorException 
	 */
	public static TwoTreesAndTheirBidirectionalMap<ExtendedNode, ExtendedConstructionNode> dupTreeToConstructionTree(	ExtendedNode tree) throws AnnotatorException {
		InfoConverter<ExtendedNode, ExtendedInfo> nodeToConstructionInfoConverter = new InfoConverter<ExtendedNode, ExtendedInfo>() {
			@Override
			public ExtendedInfo convert(ExtendedNode node) {
				return node.getInfo();
			}
		};
		TreeCopier<ExtendedInfo, ExtendedNode, ExtendedInfo, ExtendedConstructionNode> treeCopier = 
				new TreeCopier<ExtendedInfo, ExtendedNode, ExtendedInfo, ExtendedConstructionNode>(tree, nodeToConstructionInfoConverter, 
						new ExtendedConstructionNodeConstructor());  
		treeCopier.copy();
		try {
			return  new TwoTreesAndTheirBidirectionalMap<ExtendedNode, ExtendedConstructionNode>(tree, treeCopier.getGeneratedTree(), treeCopier.getNodesMap());
		} catch (AnnotatorException e) {
			throw new AnnotatorException("Error duplicating " + tree, e);
		}
	}



	/**
	 * make a deep copy of the tree
	 * @param tree
	 * @return
	 */
	public static ExtendedNode dupTree(ExtendedNode tree) {
		
		// a private anonymous class that takes nodes and returns them with new annotations 
		InfoConverter<ExtendedNode, ExtendedInfo> dummyInfoConverter = new InfoConverter<ExtendedNode, ExtendedInfo>() {
			@Override
			public ExtendedInfo convert(ExtendedNode node) {
				return new ExtendedInfo(node.getInfo(), node.getInfo().getAdditionalNodeInformation());
			}
		};
		
		TreeCopier<ExtendedInfo, ExtendedNode, ExtendedInfo, ExtendedNode> treeCopier = 
			new TreeCopier<ExtendedInfo, ExtendedNode, ExtendedInfo, ExtendedNode>(tree, dummyInfoConverter, new ExtendedNodeConstructor());  
		treeCopier.copy();
		return treeCopier.getGeneratedTree();
	}

	/**
	 * Get two {@link BidirectionalMap}s, where the right of the first map is assumed to be the left side fo the second map.
	 * Cross ref the two {@link BidirectionalMap}s and return the map with the left of the first map, and the right side of the second map.
	 * <p>
	 * in case some node in the first map is not in the second, raise an exception. 
	 * @param bidiMap1
	 * @param bidiMap2
	 * @return
	 * @throws AnnotatorException 
	 */
	public static <N extends AbstractNode<?, N>, O extends AbstractNode<?, O>, P extends AbstractNode<?, P>> 
		BidirectionalMap<N, P> crossRefMaps(BidirectionalMap<N, O> bidiMap1, BidirectionalMap<O, P> bidiMap2) throws AnnotatorException {
	
		//		* In case the first map is null, return the second map (empty intersection).<br>
		
		if (bidiMap1 == null)
			throw new AnnotatorException("null bidiMap1");
		if (bidiMap2 == null)
			throw new AnnotatorException("null bidiMap2");
		if (bidiMap1.size() != bidiMap2.size())
			throw new AnnotatorException("bidiMap1 has " + bidiMap2.size() + " mappings while bidimap2 has "+ bidiMap2.size());
		
		BidirectionalMap<N, P> intersectedBidiMap = new SimpleBidirectionalMap<N, P>();
		
		for (O node : bidiMap1.rightSet())
			// sanity
			if (!bidiMap2.leftContains(node))
				throw new AnnotatorException("These two bidirectional maps do not intersect properly:\n" + bidiMap1 +"\n" + bidiMap2);
			// intersect
			else
				intersectedBidiMap.put(bidiMap1.rightGet(node), bidiMap2.leftGet(node));
				
		return intersectedBidiMap;
	}
	
}
