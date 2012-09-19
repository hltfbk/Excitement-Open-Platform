package eu.excitementproject.eop.core.representation.parsetree;

import java.io.Serializable;

/**
 * [DELETEME_LATER: imported from BIUTEE 2.4.1 with no modification]
 * Represents the information about the edge that connects the current node to its parent.
 * <P>
 * A {@linkplain BasicNode} has an {@linkplain Info} that represents the information about
 * the node. The information is about the node itself, and the edge that connects it to
 * its parent. {@linkplain EdgeInfo} represents that information.
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
