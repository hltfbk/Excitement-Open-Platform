package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;



/**
 * Used to create a string from a node ( {@link AbstractNode} ).
 * <P>
 * This interface is given to the constructor of {@link TreeStringGenerator}
 * class, in order to produce a string for each node in the tree.
 * @author Asher Stern
 *
 */
public interface NodeString<I extends Info>
{
	/**
	 * Set the node
	 * @param node
	 */
	public void set(AbstractNode<? extends I, ?> node);
	
	/**
	 * Return the node's String representation
	 * @return
	 */
	public String getStringRepresentation();
}
