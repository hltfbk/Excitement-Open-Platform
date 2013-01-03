package ac.biu.nlp.nlp.instruments.parse.tree.dependency.view;

import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;


/**
 * 
 * @author Asher Stern
 *
 */
public interface NodeAndEdgeString<I extends Info>
{
	
	public void set(AbstractNode<? extends I, ?> node);
	
	public String getNodeStringRepresentation();
	public String getEdgeStringRepresentation();


}
