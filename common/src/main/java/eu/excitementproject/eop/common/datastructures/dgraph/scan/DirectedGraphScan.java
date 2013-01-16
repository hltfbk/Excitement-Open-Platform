package eu.excitementproject.eop.common.datastructures.dgraph.scan;

import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraph;
import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraphException;

/**
 * A super class for {@link DirectedGraph} scanning.
 * The subclasses should implement {@link #doScan(Object)} method.
 * <P>
 * The scan may be DFS, BFS, or any other type of scan. A starting node should
 * be specified, and what to do with the scan - i.e. what is the meaning of "scanning" a node.
 * For example, an operation to do during the scan may be to print the node to the screen. It may
 * be storing its distance from the start node (reasonable for BFS), etc.
 * In order to define "what to do with the scan", an implementation of {@link ScanOperation} should
 * be specified.
 * @author Asher Stern
 *
 * @param <N> the type of the nodes in the {@link DirectedGraph}
 * @param <E> the type of the information on the edges of the {@link DirectedGraph}
 */
public abstract class DirectedGraphScan<N,E>
{
	////////////////// PUBLIC PART ///////////////////////////
	
	public DirectedGraphScan(ScanOperation<N> operation, DirectedGraph<N, E> graph) throws DirectedGraphScanException
	{
		if (null==operation)
			throw new DirectedGraphScanException("null==operation");
		if (null==graph)
			throw new DirectedGraphScanException("null==graph");

		this.operation = operation;
		this.graph = graph;
	}
	
	public void scan(N startNode) throws DirectedGraphException, DirectedGraphScanException
	{
		if (null==startNode)
			throw new DirectedGraphScanException("null==startNode");
		if (!graph.isExistNode(startNode))
			throw new DirectedGraphScanException("The given start node does not exist in the graph.");
		
		doScan(startNode);
	}
	
	
	////////////////// PROTECTED PART ///////////////////////////
	
	protected abstract void doScan(N startNode) throws DirectedGraphException, DirectedGraphScanException;
	
	protected ScanOperation<N> operation;
	protected DirectedGraph<N, E> graph;
}
