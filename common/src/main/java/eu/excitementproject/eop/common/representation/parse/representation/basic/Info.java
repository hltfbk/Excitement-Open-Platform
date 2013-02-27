package eu.excitementproject.eop.common.representation.parse.representation.basic;

import java.io.Serializable;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;



/**
 * Represents a parse-tree-node's information:
 * <OL>
 * <LI> The node's content (word, lemma, part-of-speech, etc)</LI>
 * <LI> The information about the edge connecting this node to its parent (i.e. dependency relation (subject, object, etc.))</LI>
 * </OL>
 * This is an interface to the actual object that is used as the actual information
 * in a node ({@link BasicNode})
 * <P>
 * An easy way to get the Strings in an Info object, is by using {@link InfoGetFields}
 * <P>
 * <B>Classes that implement Info - must be immutable</B>
 * <P>
 * It is recommended to use the default implementation: {@link DefaultInfo}.
 * 
 * @see DefaultInfo
 * @see InfoGetFields
 * 
 * @author Asher Stern
 *
 */
public interface Info extends Serializable
{
	/**
	 * Returns the node's {@linkplain NodeInfo} 
	 * @return the node's {@linkplain NodeInfo}
	 */
	public NodeInfo getNodeInfo();
	
	/**
	 * Returns the {@linkplain EdgeInfo} of the edge connecting this node to
	 * its parent.
	 * 
	 * @return the {@linkplain EdgeInfo} of the edge connecting this node to
	 * its parent.
	 */
	public EdgeInfo getEdgeInfo();
	
	/**
	 * Returns the node's ID.
	 * The ID of nodes is just for easy representation. Nothing more!
	 * The ID can be any string - not just a number.
	 * There is no guarantee that the ID will be unique.
	 * It is absolutely legal that the ID will be <code>null</code>,
	 * (however, it is not recommended).
	 *  
	 * @return the node's ID.
	 */
	public String getId();
	
	public boolean equals(Object obj);
	public int hashCode();

}
