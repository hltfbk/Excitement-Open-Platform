package eu.excitementproject.eop.common.datastructures.dgraph;


/**
 * Represents a node, and information about an edge.
 * There is no interpretation about how the node and the edge are
 * related. This class is merely a "holder" for the edge and
 * the node.
 * <P>
 * The interpretation is specified by the method that returns
 * this object (or collection of these objects).
 * 
 * @author Asher Stern
 *
 * @param <N> should be immutable
 * @param <E> should be immutable
 */
public final class EdgeAndNode<N,E>
{
	public EdgeAndNode(N node, E edgeInfo)
	{
		this.node = node;
		this.edgeInfo = edgeInfo;
	}
	
	public N getNode()
	{
		return node;
	}
	
	public E getEdgeInfo()
	{
		return edgeInfo;
	}
	
	
	
	
	@Override
	public int hashCode() {
		// This is thread safe, since the class is immutable, so hashCodeValue
		// can get exactly one value.
		if (hashCodeSet)
			return hashCodeValue;
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((edgeInfo == null) ? 0 : edgeInfo.hashCode());
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		hashCodeValue = result;
		hashCodeSet = true;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		EdgeAndNode other = (EdgeAndNode) obj;
		if (edgeInfo == null) {
			if (other.edgeInfo != null)
				return false;
		} else if (!edgeInfo.equals(other.edgeInfo))
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}




	private N node;
	private E edgeInfo;
	
	transient private boolean hashCodeSet = false;
	transient private int hashCodeValue = 0;
	
	

}
