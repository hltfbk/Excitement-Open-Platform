package ac.biu.nlp.nlp.instruments.parse.tree.dependency.view;

import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;


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
