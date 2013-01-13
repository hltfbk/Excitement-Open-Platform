package eu.excitementproject.eop.common.representation.parse.representation.basic;

import java.io.Serializable;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;



/**
 * Represents the information about the edge that connects the current node to its parent.
 * <P>
 * A {@linkplain BasicNode} has an {@linkplain Info} that represents the information about
 * the node. The information is about the node itself, and the edge that connects it to
 * its parent. {@linkplain EdgeInfo} represents that information (i.e. the information about the edge from the node to its parent).
 * 
 * It is recommended to use the default implementation: {@link DefaultEdgeInfo}
 * <P>
 * <B>All implementations must be immutable!!!</B>
 * 
 * @see Info
 * @see DefaultEdgeInfo
 * 
 * 
 * @author Asher Stern
 *
 */
public interface EdgeInfo extends Serializable
{
	public DependencyRelation getDependencyRelation();
	
	public boolean isEqualTo(EdgeInfo other);
	
	public int hashCode();
	public boolean equals(Object obj);

}
