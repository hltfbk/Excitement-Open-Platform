package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

public class LemmaOnlyNodeString implements NodeString<Info>
{

	public void set(AbstractNode<? extends Info, ?> node)
	{
		this.node = node;
	}

	public String getStringRepresentation()
	{
		String ret = "(-)";
		ret = InfoGetFields.getLemma(node.getInfo(),ret);
		return ret;
	}
	
	
	private AbstractNode<? extends Info, ?> node;
	

}
