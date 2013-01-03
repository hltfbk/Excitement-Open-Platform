package ac.biu.nlp.nlp.instruments.parse.tree.dependency.view;

import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;



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
