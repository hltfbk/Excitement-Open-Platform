package eu.excitementproject.eop.biutee.rteflow.document_sublayer;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * 
 * @author Asher Stern
 * @since 10 August 2012
 *
 */
public class TreeAndMapping
{
	public TreeAndMapping(ExtendedNode tree,
			BidirectionalMap<BasicNode, ExtendedNode> mapping)
	{
		super();
		this.tree = tree;
		this.mapping = mapping;
	}
	
	
	public ExtendedNode getTree()
	{
		return tree;
	}
	public BidirectionalMap<BasicNode, ExtendedNode> getMapping()
	{
		return mapping;
	}


	private final ExtendedNode tree;
	private final BidirectionalMap<BasicNode, ExtendedNode> mapping;
}
