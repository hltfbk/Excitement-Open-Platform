package eu.excitementproject.eop.common.datastructures.dgraph.scan;

import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraph;

/**
 * Specified the operation that should be done in the scan
 * @author Asher Stern
 *
 * @param <N> the type of the nodes in the {@link DirectedGraph}
 * @param <E> the type of the information on the edges of the {@link DirectedGraph}
 */
public interface ScanOperation<N>
{
	/**
	 * Specified the operation that should be done in the scan for the current node.
	 * 
	 * @param node a node that is currently scanned.
	 * @param from (informally, edge from "parent" or "previous" node to the current node)
	 */
	public void doOperationFor(N node, N from);

}
