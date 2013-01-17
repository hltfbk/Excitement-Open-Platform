package eu.excitementproject.eop.common.datastructures.dgraph;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;




/**
 * A directed graph.
 * 
 * This interface is safe from software engineering point of view,
 * because it does not give direct access to nodes and edges.
 * Each operation of adding and removing node / edge - is done
 * by the methods of this interface, and not by the user.<BR>
 * 
 * This kind of programming guarantees avoiding
 * unstable / illegal states.
 * 
 * <P>
 * <B> Not thread safe (unless explicitly specified by implementation) !!! </B>
 * 
 * @author Asher Stern
 *
 * @param <N> node type
 * @param <E> edge information type
 */
public interface DirectedGraph<N,E>
{
	/**
	 * Add a node to the graph.
	 * @param node
	 * @throws DirectedGraphException if the specified node already exist.
	 */
	public void addNode(N node) throws DirectedGraphException;
	
	/**
	 * Add an edge to the graph.<BR>
	 * Edge is X ---> Y: X is called "head". Y is called "tail".
	 * <P>
	 * To replace an existing edge, remove it and add a new one. Adding an edge that
	 * is already exist - will throw an exception.
	 * @param head
	 * @param tail
	 * @param edgeInfo
	 * @throws DirectedGraphException if nodes do not exist or edge already exist.
	 */
	public void addEdge(N head, N tail, E edgeInfo) throws DirectedGraphException;

	
	/**
	 * Removes a node.
	 * @param node
	 * @throws DirectedGraphException if the node does not exist
	 */
	public void removeNode(N node) throws DirectedGraphException;

	
	
	/**
	 * Removes an edge
	 * @param head
	 * @param tail
	 * @throws DirectedGraphException the nodes / edge do not exist
	 */
	public void removeEdge(N head, N tail) throws DirectedGraphException;

	
	
	/**
	 * A "direct predecessor" is: For an edge "X ---> Y": X is a direct
	 * predecessor of Y.
	 * @param node
	 * @return a set of {@linkplain EdgeAndNode}. Each {@linkplain EdgeAndNode}
	 * represents an edge to a predecessor, and the predecessor itself.
	 * @throws DirectedGraphException node does not exist.
	 */
	public ImmutableSet<EdgeAndNode<N, E>> getDirectPredecessorsOf(N node) throws DirectedGraphException;


	
	/**
	 * A "direct successor" is: For an edge "X ---> Y": Y is a direct
	 * successor of X.
	 * @param node
	 * @return a set of {@linkplain EdgeAndNode}. Each {@linkplain EdgeAndNode}
	 * represents an edge to a successor, and the successor itself.
	 * @throws DirectedGraphException node does not exist.
	 */
	public ImmutableSet<EdgeAndNode<N, E>> getDirectSuccessorsOf(N node) throws DirectedGraphException;

	
	/**
	 * true / false
	 * @param node
	 * @return
	 */
	public boolean isExistNode(N node);
	
	
	/**
	 * true / false
	 * @param head
	 * @param tail
	 * @return
	 * @throws DirectedGraphException if the node(s) do(es) not exist.
	 */
	public boolean isExistEdge(N head, N tail) throws DirectedGraphException;
	
	/**
	 * Returns the edge information of the specified head and tail.
	 * <P>
	 * X ---> Y is an edge for which X is the "head", and Y is the "tail".
	 * <P>
	 * If the edge does not exist - an exception will be thrown.
	 * The function does not return <code>null</code>!
	 * @param head
	 * @param tail
	 * @return
	 * @throws DirectedGraphException node(s) / edge do not exist.
	 */
	public E getEdge(N head, N tail) throws DirectedGraphException;

	/**
	 * Returns all the graph's nodes.
	 * @return
	 */
	public ImmutableSet<N> getAllNodes();

}
