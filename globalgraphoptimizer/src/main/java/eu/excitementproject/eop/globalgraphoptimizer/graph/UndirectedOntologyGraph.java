package eu.excitementproject.eop.globalgraphoptimizer.graph;

import java.util.HashSet;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import eu.excitementproject.eop.globalgraphoptimizer.defs.EdgeType;


public class UndirectedOntologyGraph extends AbstractOntologyGraph {
	
	public UndirectedOntologyGraph() throws Exception {
		super();
	}

	public UndirectedOntologyGraph(String description) throws Exception {
		super(description);
	}

	/**
	 * The constructor takes a directed graph and undirectes it by leaving only edges that are symmetric and assigning score "1" to every edge
	 * @param graph
	 * @throws Exception 
	 */
	public UndirectedOntologyGraph(AbstractOntologyGraph graph) throws Exception {
		super(graph.description()+"Undirected",new TreeMap<Integer, RelationNode>(),new TreeMap<String, AbstractRuleEdge>());
		
		for(RelationNode node: graph.getNodes())
			m_nodes.put(node.id(), node);
		
		for(AbstractRuleEdge edge: graph.getEdges()) {
			int fromId = edge.from().id();
			int toId = edge.to().id();
			
			if(fromId < toId) {
				if(graph.containsEdge(""+toId+RuleEdge.SYMBOL+fromId) || 
						graph.containsEdge(""+toId+ReversedRuleEdge.SYMBOL+fromId) ||graph.containsEdge(""+toId+SynonymEdge.SYMBOL+fromId))
					addEdge(m_nodes.get(fromId), m_nodes.get(toId));
			}
		}
	}
	
	public boolean addEdge(AbstractRuleEdge edge) throws Exception {
		if(!(edge instanceof SynonymEdge))
			throw new Exception("Adding directed edges is not possible in a directed one-mapping graph");
		
		if(m_edges.containsKey(edge.toString()))
			return false;
		
		RelationNode from = edge.from();
		RelationNode to = edge.to();
		
		addNode(from);
		addNode(to);
		from.addOutEdge(edge);
		to.addInEdge(edge);
		
		m_edges.put(edge.toString(), edge);
		return true;
	}
	
	public boolean addEdge(RelationNode fromNode, RelationNode toNode) throws Exception {
		
		if(m_edges.containsKey(Math.min(fromNode.id(),toNode.id())+SynonymEdge.SYMBOL+Math.max(fromNode.id(), toNode.id())))
			return false;
		
		SynonymEdge newEdge = new SynonymEdge(fromNode,toNode,1);
		
		addNode(fromNode);
		addNode(toNode);
		newEdge.from().addOutEdge(newEdge);
		newEdge.to().addInEdge(newEdge);
		
		m_edges.put(newEdge.toString(), newEdge);
		return true;
	}	
	
	/**
	 * Goes over all nodes, for each node v looks at the predecessors U and successors W and sees if they are connected with the appropriate edge:
	 * so RuleEdge(u,v) and RuleEdge(v,w) --> RuleEdge(u,w).
	 * If the degree of a node is bounded then this should take linear time. Otherwise, worst case is cubic
	 * @return
	 */
	public List<ViolatedTransitivityConstraint> findViolatedTransitivityConstraints() {
		
		List<ViolatedTransitivityConstraint> result = new LinkedList<ViolatedTransitivityConstraint>();
		
		for(RelationNode node: m_nodes.values()) {
			for(AbstractRuleEdge inEdge: node.inEdges()) {
				for(AbstractRuleEdge outEdge: node.outEdges()) {
					if(inEdge.from().id()!=outEdge.to().id() && !containsEdge(inEdge.from(), outEdge.to())) {
						result.add(new ViolatedTransitivityConstraint(inEdge.from().id(), node.id(), outEdge.to().id(), EdgeType.DIRECT, EdgeType.DIRECT));
					}
				}
			}
		}
		return result;
	}
	
	@Override
	public List<List<Integer>> findViolatedTreeConstraints() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Undirected graphs never violate the reduced tree constraint");
	}
	
	public void addTransitiveEdgesNotEfficiently() throws Exception {

		boolean add=true;
		while(add) {
			add = false;
			List<AbstractRuleEdge> edgesToAddList = new LinkedList<AbstractRuleEdge>();
			for(RelationNode currNode: getNodes()) {
				for(AbstractRuleEdge inEdge: currNode.inEdges()) {
					for(AbstractRuleEdge outEdge:currNode.outEdges()) {
						if(inEdge.from()!=outEdge.to())
							edgesToAddList.add(new SynonymEdge(inEdge.from(),outEdge.to(),-1));
					}
				}
			}
			for(AbstractRuleEdge edge:edgesToAddList)
				add |= addEdge(edge);
		}
	}
	
	@Override
	public int potentialEdges() {
		return m_nodes.size()*(m_nodes.size()-1)/2;
	}
	
	public boolean containsEdge(RelationNode from, RelationNode to) {
		return m_edges.containsKey(Math.min(from.id(), to.id())+SynonymEdge.SYMBOL+Math.max(from.id(), to.id()));
	}
	
	public AbstractRuleEdge getEdge(RelationNode from, RelationNode to) {
		return m_edges.get(Math.min(from.id(), to.id())+SynonymEdge.SYMBOL+Math.max(from.id(), to.id()));
	}
	
	public MinCut findMinCut() throws Exception {
		
		//this cast is safe
		UndirectedOntologyGraph copy = (UndirectedOntologyGraph) this.copy();
		//init node description
		for(RelationNode n:copy.getNodes())
			n.setDescription(""+n.id());
		
		MinCut bestCut=new MinCut(new TreeSet<RelationNode>(), Integer.MAX_VALUE,-1,-1);
		
		while(copy.getNodeCount() > 1) {
			
			MinCut currCut = findPhaseCut(copy);
			if(currCut.getCutSize()<bestCut.getCutSize())
				bestCut = copyCut(currCut);
			else if(currCut.getCutSize()==bestCut.getCutSize()) {

				int originalNodeCount = getNodeCount();
				if(Math.abs(currCut.getOneSideNodes().size()-originalNodeCount/2)
						<	Math.abs(bestCut.getOneSideNodes().size()-originalNodeCount/2))
					bestCut=copyCut(currCut);
			}
			merge(copy,copy.getNode(currCut.getS()),copy.getNode(currCut.getT()));
		}
		
		return bestCut;
	}
	
	private MinCut copyCut(MinCut otherCut) {
		
		Set<RelationNode> oneSideNodes = new TreeSet<RelationNode>();
		for(RelationNode n:otherCut.getOneSideNodes()) 
			oneSideNodes.add(new RelationNode(n.id(),n.description()));
		
		return new MinCut(oneSideNodes, otherCut.getCutSize(), otherCut.getS(), otherCut.getT());
	}
	
	private static void merge(UndirectedOntologyGraph copy, RelationNode s,
			RelationNode t) throws Exception {
		
		for(AbstractRuleEdge outEdge: t.outEdges()) {
			
			if(outEdge.to()==s)
				continue;
			
			AbstractRuleEdge sEdge = copy.getEdge(s, outEdge.to());
			if(sEdge!=null) 
				sEdge.setScore(sEdge.score()+outEdge.score());
			else {
				AbstractRuleEdge newEdge = new SynonymEdge(s,outEdge.to(),outEdge.score());
				copy.addEdge(newEdge);
			}
		}
		
		for(AbstractRuleEdge inEdge: t.inEdges()) {
			
			if(inEdge.from()==s)
				continue;
			
			AbstractRuleEdge sEdge = copy.getEdge(s, inEdge.from());
			if(sEdge!=null) 
				sEdge.setScore(sEdge.score()+inEdge.score());
			else {
				AbstractRuleEdge newEdge = new SynonymEdge(s,inEdge.from(),inEdge.score());
				copy.addEdge(newEdge);
			}
		}
		
		s.setDescription(s.description()+","+t.description());
		copy.removeNode(t.id());
	}

	private static MinCut findPhaseCut(UndirectedOntologyGraph copy) {
		
		MinCut resultCut=null; 
		Set<RelationNode> added = new HashSet<RelationNode>();
		RelationNode s=null;
		
		
		RelationNode firstNode = copy.getNodes().iterator().next();
		added.add(firstNode);
		if(copy.getNodeCount()==2)
			s=firstNode;
		
		while(added.size()<copy.getNodeCount()) {
			
			RelationNode mostTightlyConnectedVertex = null;
			int tightness = Integer.MIN_VALUE;
			
			for(RelationNode tightVertexCandidate: copy.getNodes()) {
				if(!added.contains(tightVertexCandidate)) {
					int currTightness = 0;
					
					for(AbstractRuleEdge outEdge:tightVertexCandidate.outEdges())
						if(added.contains(outEdge.to()))
							currTightness+=outEdge.score();
					for(AbstractRuleEdge inEdge:tightVertexCandidate.inEdges())
						if(added.contains(inEdge.from()))
							currTightness+=inEdge.score();
					if(currTightness>tightness) {
						tightness=currTightness;
						mostTightlyConnectedVertex=tightVertexCandidate;
					}
				}
			}
			if(added.size()==copy.getNodeCount()-2)
				s = mostTightlyConnectedVertex;
			else if(added.size()==copy.getNodeCount()-1) {
				resultCut = new MinCut(added, tightness, s.id(), mostTightlyConnectedVertex.id());
				break;
			}
			added.add(mostTightlyConnectedVertex);
		}
		return resultCut;
	}
	
	public List<Set<String>> findConnectivityComponents() {
		
		List<Set<String>> result = new LinkedList<Set<String>>();
		Set<RelationNode> nonVisitedNodes = new HashSet<RelationNode>();
		for(RelationNode node: getNodes()) {
			nonVisitedNodes.add(node);
		}
		System.out.println("Number of unvisited nodes: " + nonVisitedNodes.size());
		
		int unqueuedNodes = 0;
		
		while(!nonVisitedNodes.isEmpty()) {
			
			
			Set<String> component = new HashSet<String>();
			Queue<RelationNode> queue = new LinkedList<RelationNode>();
			RelationNode componentFirstNode = nonVisitedNodes.iterator().next();
			queue.offer(componentFirstNode);
			nonVisitedNodes.remove(componentFirstNode);
			
			while(!queue.isEmpty()) {
				
				RelationNode currNode = queue.poll();
				unqueuedNodes++;
				component.add(currNode.description());
				if(unqueuedNodes % 10000 == 0) 
					System.out.println("Unqueued nodes: " + unqueuedNodes);
				
				for(AbstractRuleEdge edge: currNode.outEdges()) {		
					if(nonVisitedNodes.contains(edge.to())) {
						queue.offer(edge.to());
						nonVisitedNodes.remove(edge.to());
					}
				}
				
				for(AbstractRuleEdge edge: currNode.inEdges()) {
					if(nonVisitedNodes.contains(edge.from())) {
						queue.offer(edge.from());
						nonVisitedNodes.remove(edge.from());
					}
				}
			}
			
			
			if(component.size()>1) {
				//System.out.println("Adding component of size: " + component.size());
				result.add(component);
			}
		}
		return result;
	}

	public static class MinCut {
		
		public MinCut(Set<RelationNode> oneSideNodes, int cutSize, int s, int t) {
			m_oneSideNodes = oneSideNodes;
			m_cutSize = cutSize;
			m_s = s;
			m_t = t;
		}
		
		public Set<RelationNode> getOneSideNodes() {
			return m_oneSideNodes;
		}
		
		public int getCutSize() {
			return m_cutSize;
		}
		
		public int getS() {
			return m_s;
		}
		
		public int getT() {
			return m_t;
		}
		
		@Override
		public String toString() {
			
			StringBuilder sb = new StringBuilder();
			for(RelationNode n:m_oneSideNodes)
				sb.append(n.id()+",");
			return sb.toString()+"\t"+m_cutSize;
		}
		private final Set<RelationNode> m_oneSideNodes;
		private final int m_cutSize;
		private final int m_s;
		private final int m_t;
		
	}
}
