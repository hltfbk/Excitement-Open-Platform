package ac.biu.nlp.nlp.instruments.parse.tree.dependency.view;

import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;

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
