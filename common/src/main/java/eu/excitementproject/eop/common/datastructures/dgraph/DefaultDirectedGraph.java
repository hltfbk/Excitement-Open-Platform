package eu.excitementproject.eop.common.datastructures.dgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;


/**
 * 
 * @author Asher Stern
 *
 * @param <N>
 * @param <E>
 */
public class DefaultDirectedGraph<N,E> implements DirectedGraph<N,E>
{
	DefaultDirectedGraph()
	{
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.dgraph.DirectedGraph#addNode(java.lang.Object)
	 */
	public void addNode(N node) throws DirectedGraphException
	{
		if (nodes.contains(node))
			throw new DirectedGraphException("The node already exist");
		else
		{
			nodes.add(node);
		}
	}
	

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.dgraph.DirectedGraph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public void addEdge(N head, N tail, E edgeInfo) throws DirectedGraphException
	{
		if (!nodes.contains(head))
			throw new DirectedGraphException("head does not exist");
		if (!nodes.contains(tail))
			throw new DirectedGraphException("tail does not exist");
		HeadAndTail<N> headAndTail = new HeadAndTail<N>(head, tail);
		
		if (edges.containsKey(headAndTail))
			throw new DirectedGraphException("Edge from head to tail already exist");
		
		edges.put(headAndTail,edgeInfo);
		
		Set<HeadAndTail<N>> headSet = mapNodeToHead.get(head);
		if (null==headSet)
		{
			headSet = new HashSet<HeadAndTail<N>>();
			mapNodeToHead.put(head, headSet);
		}
		headSet.add(headAndTail);
		
		Set<HeadAndTail<N>> tailSet = mapNodeToTail.get(tail);
		if (null==tailSet)
		{
			tailSet = new HashSet<HeadAndTail<N>>();
			mapNodeToTail.put(tail, tailSet);
		}
		tailSet.add(headAndTail);
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.dgraph.DirectedGraph#removeNode(java.lang.Object)
	 */
	public void removeNode(N node) throws DirectedGraphException
	{
		if (!nodes.contains(node))
			throw new DirectedGraphException("The specified node does not exist");
		
		Set<HeadAndTail<N>> headSet = mapNodeToHead.get(node);
		if (headSet!=null)
		{
			for (HeadAndTail<N> headAndTail : headSet)
			{
				edges.remove(headAndTail);
			}
		}

		Set<HeadAndTail<N>> tailSet = mapNodeToTail.get(node);
		if (tailSet!=null)
		{
			for (HeadAndTail<N> headAndTail : tailSet)
			{
				edges.remove(headAndTail);
			}
		}
		
		nodes.remove(node);
		mapNodeToHead.remove(node);
		mapNodeToTail.remove(node);
	}
	
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.dgraph.DirectedGraph#removeEdge(java.lang.Object, java.lang.Object)
	 */
	public void removeEdge(N head, N tail) throws DirectedGraphException
	{
		HeadAndTail<N> headAndTail = new HeadAndTail<N>(head, tail);
		if (!edges.containsKey(headAndTail))
			throw new DirectedGraphException("The specified edge does not exist");
		
		edges.remove(headAndTail);
		mapNodeToHead.get(head).remove(headAndTail);
		mapNodeToTail.get(tail).remove(headAndTail);
	}
	
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.dgraph.DirectedGraph#getDirectPredecessorsOf(java.lang.Object)
	 */
	public ImmutableSet<EdgeAndNode<N, E>> getDirectPredecessorsOf(N node) throws DirectedGraphException
	{
		if (!nodes.contains(node))
			throw new DirectedGraphException("The specified node does not exist.");
		
		HashSet<EdgeAndNode<N, E>> ret = new HashSet<EdgeAndNode<N,E>>();
		Set<HeadAndTail<N>> tailSet = mapNodeToTail.get(node);
		if (tailSet!=null)
		{
			for (HeadAndTail<N> headAndTail : tailSet)
			{
				ret.add(new EdgeAndNode<N, E>(headAndTail.getHead(), edges.get(headAndTail)));
			}
		}
		
		return new ImmutableSetWrapper<EdgeAndNode<N,E>>(ret);
	}

	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.dgraph.DirectedGraph#getDirectSuccessorsOf(java.lang.Object)
	 */
	public ImmutableSet<EdgeAndNode<N, E>> getDirectSuccessorsOf(N node) throws DirectedGraphException
	{
		if (!nodes.contains(node))
			throw new DirectedGraphException("The specified node does not exist.");
		
		HashSet<EdgeAndNode<N, E>> ret = new HashSet<EdgeAndNode<N,E>>();
		Set<HeadAndTail<N>> headSet = mapNodeToHead.get(node);
		if (headSet!=null)
		{
			for (HeadAndTail<N> headAndTail : headSet)
			{
				ret.add(new EdgeAndNode<N, E>(headAndTail.getTail(), edges.get(headAndTail)));
			}
		}
		
		return new ImmutableSetWrapper<EdgeAndNode<N,E>>(ret);
		
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.dgraph.DirectedGraph#isExistNode(java.lang.Object)
	 */
	public boolean isExistNode(N node)
	{
		return nodes.contains(node);
	}
	
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.dgraph.DirectedGraph#isExistEdge(java.lang.Object, java.lang.Object)
	 */
	public boolean isExistEdge(N head, N tail) throws DirectedGraphException
	{
		if (!nodes.contains(head))
			throw new DirectedGraphException("The specified head does not exist");
		if (!nodes.contains(tail))
			throw new DirectedGraphException("The specified tail does not exist");

		return edges.containsKey(new HeadAndTail<N>(head, tail));
	}
	
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.dgraph.DirectedGraph#getEdge(java.lang.Object, java.lang.Object)
	 */
	public E getEdge(N head, N tail) throws DirectedGraphException
	{
		if (!nodes.contains(head))
			throw new DirectedGraphException("The specified head does not exist");
		if (!nodes.contains(tail))
			throw new DirectedGraphException("The specified tail does not exist");
		HeadAndTail<N> headAndTail = new HeadAndTail<N>(head, tail);
		if (!edges.containsKey(headAndTail))
			throw new DirectedGraphException("The specified edge does not exist");
		
		return edges.get(headAndTail);
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.dgraph.DirectedGraph#getAllNodes()
	 */
	public ImmutableSet<N> getAllNodes()
	{
		return new ImmutableSetWrapper<N>(nodes);
	}
	
	
	
	
	
	
	private Set<N> nodes = new HashSet<N>();
	private Map<HeadAndTail<N>,E> edges = new HashMap<HeadAndTail<N>, E>();
	private Map<N,Set<HeadAndTail<N>>> mapNodeToHead = new HashMap<N, Set<HeadAndTail<N>>>();
	private Map<N,Set<HeadAndTail<N>>> mapNodeToTail = new HashMap<N, Set<HeadAndTail<N>>>();
	

	

}
