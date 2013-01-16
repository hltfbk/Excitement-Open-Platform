package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;


public class WordOnlyNodeString implements NodeString<Info>
{

	public String getStringRepresentation()
	{
		String ret = "(-)";
		if (node!=null) if (node.getInfo()!=null) if (node.getInfo().getNodeInfo()!=null) if (node.getInfo().getNodeInfo().getWord()!=null)
			ret = node.getInfo().getNodeInfo().getWord();
			
		return ret;
	}

	public void set(AbstractNode<? extends Info, ?> node)
	{
		this.node = node;
	}
	
	protected AbstractNode<? extends Info, ?> node;

}
