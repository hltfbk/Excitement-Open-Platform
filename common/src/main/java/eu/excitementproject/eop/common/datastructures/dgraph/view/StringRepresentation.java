package eu.excitementproject.eop.common.datastructures.dgraph.view;

/**
 * Used by {@link DirectedGraphToDot}.
 * Given a directed graph, instance of {@link StringRepresentation}
 * specifies what should be printed in its nodes and edges by
 * the GraphViz program (dot).
 * <P>
 * Once you have a graph with actual parameters for N and E,
 * implement this interface and pass it to {@link DirectedGraphToDot}
 * in order to print your graph.
 * @author Asher Stern
 *
 * @param <N>
 * @param <E>
 */
public interface StringRepresentation<N,E>
{
	public String getNodeIdentifier(N n);
	public String getNodeRepresentation(N n);
	public String getEdgeRepresentation(E e);
}
