package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;


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
