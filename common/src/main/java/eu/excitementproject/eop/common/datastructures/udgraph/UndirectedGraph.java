package eu.excitementproject.eop.common.datastructures.udgraph;


import eu.excitementproject.eop.common.datastructures.Pair;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;

/**
 * Undirected graph.
 * Note the N is type of the node itself. Implementation of equals() and hashCode()
 * in N affect the graph behavior.
 * E is information. It can and should implement equals() and hashCode() to reflect the
 * equality of contents.
 * <P>
 * Note that for some implementations, N and E should implement Serializable.
 * 
 * @author Asher Stern
 * @since Aug 17, 2010
 *
 * @param <N>
 * @param <E>
 */
public interface UndirectedGraph<N,E>
{
	public ImmutableSet<N> getNodes() throws UndirectedGraphException;
	
	public boolean isNodeExist(N node) throws UndirectedGraphException;
	
	public boolean isEdgeExist(Pair<N> nodes) throws UndirectedGraphException;
	
	public E getEdgeContents(Pair<N> nodes) throws UndirectedGraphException;
	
	public ImmutableSet<N> getNeighbors(N node) throws UndirectedGraphException;
	
	public void addNode(N node) throws UndirectedGraphException;
	
	public void removeNode(N node) throws UndirectedGraphException;
	
	public void addEdge(Pair<N> nodes, E edgeContents) throws UndirectedGraphException;
	
	public void setEdgeContents(Pair<N> nodes, E edgeContents) throws UndirectedGraphException;
	
	public void removeEdge(Pair<N> nodes) throws UndirectedGraphException;
}
