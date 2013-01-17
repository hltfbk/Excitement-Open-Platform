package eu.excitementproject.eop.common.datastructures.dgraph.scan;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraph;
import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraphException;
import eu.excitementproject.eop.common.datastructures.dgraph.EdgeAndNode;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;


/**
 * 
 * @author Asher Stern
 *
 * @param <N>
 * @param <E>
 */
public class BfsDirectedGraphScan<N,E> extends DirectedGraphScan<N, E>
{
	public BfsDirectedGraphScan(ScanOperation<N> operation, DirectedGraph<N, E> graph) throws DirectedGraphScanException
	{
		super(operation, graph);
	}

	@Override
	protected void doScan(N startNode) throws DirectedGraphException, DirectedGraphScanException
	{
		//Queue<N> bfsQueue = new ArrayDeque<N>();
		Queue<N> bfsQueue = new LinkedList<N>(); // I am changing to LinkedList, because the ArrayEdque is not supported by JDK5
		visitedNodes = new HashSet<N>();
		if (!bfsQueue.offer(startNode)) throw new DirectedGraphScanException("queue failure");
		while (bfsQueue.peek()!=null)
		{
			N node = bfsQueue.remove();
			ImmutableSet<EdgeAndNode<N, E>> setSuccessors = graph.getDirectSuccessorsOf(node);
			if (setSuccessors!=null)
			{
				for (EdgeAndNode<N, E> successorEdgeAndNode : setSuccessors)
				{
					if (visitedNodes.contains(successorEdgeAndNode.getNode()))
						;
					else
					{
						operation.doOperationFor(successorEdgeAndNode.getNode(), node);
						visitedNodes.add(successorEdgeAndNode.getNode());
						if (!bfsQueue.offer(successorEdgeAndNode.getNode())) throw new DirectedGraphScanException("queue failure");
					}
				}
			}
		} // end of while loop
	}
	
	private Set<N> visitedNodes = null; 

}
