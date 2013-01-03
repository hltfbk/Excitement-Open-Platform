package ac.biu.nlp.nlp.general.udgraph;

/**
 * 
 * @author Asher Stern
 * @since Aug 24, 2010
 *
 * @param <N>
 * @param <E>
 */
public class UndirectedGraphFactory<N, E>
{
	public UndirectedGraph<N, E> newGraph()
	{
		return new MapBasedGraph<N, E>();
	}
}
