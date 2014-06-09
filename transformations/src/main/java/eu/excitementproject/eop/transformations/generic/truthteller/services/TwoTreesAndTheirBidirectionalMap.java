/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.services;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeCopier;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;

/**
 * Meant to hold the information produced by a {@link TreeCopier}: A tree, a copied Tree, and the {@link BidirectionalMap}
 * between them
 * <p>
 * 
 * <b>MUTABLE</b> because of the {@link BidirectionalMap}.
 * @author Amnon Lotan
 *
 * @since May 10, 2012
 */
public class TwoTreesAndTheirBidirectionalMap<N extends AbstractNode<?, N>, M extends AbstractNode<?,M>> {
	private final N originalTree;
	private final M generatedTree;
	private final BidirectionalMap<N, M> bidiMap;
	/**
	 * Ctor
	 * @param originalTree
	 * @param generatedTree
	 * @param bidiMap
	 * @throws AnnotatorException 
	 */
	public TwoTreesAndTheirBidirectionalMap(N originalTree, M generatedTree, BidirectionalMap<N, M> bidiMap) throws AnnotatorException {
		super();
		this.originalTree = originalTree;
		this.generatedTree = generatedTree;
		this.bidiMap = bidiMap;
		
		if (bidiMap.leftGet(originalTree) != generatedTree)
			throw new AnnotatorException("The two roots do not map in the given bidirectional map:\n " + originalTree +"\n"+generatedTree);
	}
	
	/**
	 * @return the originalTree
	 */
	public N getOriginalTree() {
		return originalTree;
	}
	/**
	 * @return the generatedTree
	 */
	public M getGeneratedTree() {
		return generatedTree;
	}
	/**
	 * @return the bidiMap
	 */
	public  BidirectionalMap<N, M> getBidiMap() {
		return bidiMap;
	}
	
	
}
