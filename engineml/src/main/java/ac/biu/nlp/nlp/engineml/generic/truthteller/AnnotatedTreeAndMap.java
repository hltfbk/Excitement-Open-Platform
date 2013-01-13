package ac.biu.nlp.nlp.engineml.generic.truthteller;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;

/**
 * Encapsulates a parse-tree and a map of nodes from original tree to
 * generated tree.
 * Used by {@link SynchronizedAtomicAnnotator}.
 * 
 * @see SynchronizedAtomicAnnotator
 * 
 * @author Asher Stern
 * @since Nov 7, 2011
 *
 */
public class AnnotatedTreeAndMap
{
	public AnnotatedTreeAndMap(ExtendedNode annotatedTree, BidirectionalMap<ExtendedNode, ExtendedNode> mapOriginalToAnnotated)
	{
		super();
		this.annotatedTree = annotatedTree;
		this.mapOriginalToAnnotated = mapOriginalToAnnotated;
	}
	
	public ExtendedNode getAnnotatedTree()
	{
		return annotatedTree;
	}
	public BidirectionalMap<ExtendedNode, ExtendedNode> getMapOriginalToAnnotated()
	{
		return mapOriginalToAnnotated;
	}


	

	private final ExtendedNode annotatedTree;
	private final BidirectionalMap<ExtendedNode, ExtendedNode> mapOriginalToAnnotated;
}
