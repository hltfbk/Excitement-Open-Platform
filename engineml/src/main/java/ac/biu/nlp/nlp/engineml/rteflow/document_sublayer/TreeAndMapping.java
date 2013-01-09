package ac.biu.nlp.nlp.engineml.rteflow.document_sublayer;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

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