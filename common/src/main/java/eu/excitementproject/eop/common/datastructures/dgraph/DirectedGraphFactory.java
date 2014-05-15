package eu.excitementproject.eop.common.datastructures.dgraph;


/**
 * Standard factory design pattern.
 * Used to give flexibility for changing the default
 * {@link DirectedGraph} implementation.
 * 
 * @author Asher Stern
 *
 * @param <N>
 * @param <E>
 */
public class DirectedGraphFactory<N,E>
{
	public DirectedGraph<N, E> getDefaultDirectedGraph()
	{
		return new DefaultDirectedGraph<N, E>();
	}

}
