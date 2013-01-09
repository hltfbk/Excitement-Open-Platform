package eu.excitementproject.eop.common.datastructures.udgraph;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.Pair;
import eu.excitementproject.eop.common.datastructures.PairMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;



/**
 * Implementation of {@link UndirectedGraph}, using {@link PairMap} and <code>
 * java.util.Set</code>.
 * This implementation stores the whole graph in the memory.
 * 
 * @author Asher Stern
 * @since Aug 17, 2010
 *
 * @param <N>
 * @param <E>
 */
public class MapBasedGraph<N,E> implements UndirectedGraph<N, E>, Serializable
{
	private static final long serialVersionUID = 2063100209254952596L;
	
	public ImmutableSet<N> getNodes() throws UndirectedGraphException
	{
		return new ImmutableSetWrapper<N>(this.nodes);
	}
	public boolean isNodeExist(N node) throws UndirectedGraphException
	{
		return this.nodes.contains(node);
	}
	public boolean isEdgeExist(Pair<N> nodes) throws UndirectedGraphException
	{
		return this.edges.containsPair(nodes);
	}
	public E getEdgeContents(Pair<N> nodes) throws UndirectedGraphException
	{
		E ret = null;
		if (!this.edges.containsPair(nodes))
			throw new UndirectedGraphException("Edge does not exist.");
			
		ret = this.edges.getValueOf(nodes);
		return ret;
		
	}
	public ImmutableSet<N> getNeighbors(N node) throws UndirectedGraphException
	{
		if (!nodes.contains(node))
			throw new UndirectedGraphException("Node does not exist.");
		
		Set<N> ret = new HashSet<N>();
		
		ImmutableSet<Pair<N>> pairs = this.edges.getPairContaining(node);
		if (pairs!=null)
		{
			for (Pair<N> pair : pairs)
			{
				if (pair.toSet().size()==1) // the pair is one element (which must be the node itself).
					ret.add(pair.toSet().iterator().next());
				
				for (N nodeInPair : pair.toSet())
				{
					if (!nodeInPair.equals(node))
						ret.add(nodeInPair);
					
				}
				
			}
		}
		
		return new ImmutableSetWrapper<N>(ret);
	}
	
	
	public void addNode(N node) throws UndirectedGraphException
	{
		if (this.nodes.contains(node))
			throw new UndirectedGraphException("Node already exist.");
		
		this.nodes.add(node);
	}
	
	
	public void removeNode(N node) throws UndirectedGraphException
	{
		if (!this.nodes.contains(node))
			throw new UndirectedGraphException("Node does not exist.");
		
		if (edges.getPairContaining(node)!=null)
		{
			if (edges.getPairContaining(node).size()>0)
			{
				Set<Pair<N>> pairsNeighbors = new HashSet<Pair<N>>();
				for (Pair<N> pairsNeighbor : edges.getPairContaining(node))
				{
					pairsNeighbors.add(pairsNeighbor);
				}
				
				for (Pair<N> pairsNeighbor : pairsNeighbors)
				{
					if (edges.containsPair(pairsNeighbor))
					{
						edges.removePair(pairsNeighbor);
					}
				}
			}
		}
		
		this.nodes.remove(node);
	}
	
	
	public void addEdge(Pair<N> nodes, E edgeContents)
			throws UndirectedGraphException
	{
		boolean nodesExist = true;
		for (N node : nodes.toSet())
		{
			if (!this.nodes.contains(node))
				nodesExist = false;
		}
		
		if (!nodesExist) throw new UndirectedGraphException("Node(s) do(es) not exist.");
		
		if (this.edges.containsPair(nodes)) throw new UndirectedGraphException("Edge already exists.");
		
		this.edges.put(nodes, edgeContents);
	}
	
	public void setEdgeContents(Pair<N> nodes, E edgeContents)
			throws UndirectedGraphException
	{
		if (!this.edges.containsPair(nodes))
			throw new UndirectedGraphException("Edge does not exist.");
		
		this.edges.put(nodes, edgeContents);
		
	}
	
	public void removeEdge(Pair<N> nodes) throws UndirectedGraphException
	{
		if (!this.edges.containsPair(nodes))
			throw new UndirectedGraphException("Edge does not exist.");
		
		this.edges.removePair(nodes);
	}


	
	protected Set<N> nodes = new HashSet<N>();
	protected PairMap<N, E> edges = new PairMap<N, E>();

}
