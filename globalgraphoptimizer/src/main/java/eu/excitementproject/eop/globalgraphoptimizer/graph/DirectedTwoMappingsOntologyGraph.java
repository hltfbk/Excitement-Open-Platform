package eu.excitementproject.eop.globalgraphoptimizer.graph;

import java.util.LinkedList;

import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.globalgraphoptimizer.defs.EdgeType;

public class DirectedTwoMappingsOntologyGraph extends DirectedOntologyGraph{

	public DirectedTwoMappingsOntologyGraph() {
		super();
	}

	public DirectedTwoMappingsOntologyGraph(String description) {
		super(description);
	}
	
	public DirectedTwoMappingsOntologyGraph(String description, Map<Integer,RelationNode> nodes, Map<String,AbstractRuleEdge> edges) {
		super(description,nodes,edges);
	}
	
	public DirectedTwoMappingsOntologyGraph(String description, Map<Integer,RelationNode> nodes, Map<String,AbstractRuleEdge> edges, boolean contracted) {
		super(description, nodes, edges, contracted);
	}
	
	public boolean addEdge(AbstractRuleEdge edge) throws Exception {
		if(edge instanceof SynonymEdge)
			throw new Exception("Adding synonym edges is not possible in a directed two-mapping graph");
		
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
	
	public boolean containsDirectEdge(RelationNode from, RelationNode to) {
		return m_edges.containsKey(from.id()+RuleEdge.SYMBOL+to.id());
	}
	
	public boolean containsReversedEdge(RelationNode from, RelationNode to) {
		return m_edges.containsKey(from.id()+ReversedRuleEdge.SYMBOL+to.id());
	}
	
	/**
	 * Goes over all nodes, for each node v looks at the predecessors U and successors W and sees if they are connected with the appropriate edge:
	 * so RuleEdge(u,v) and RuleEdge(v,w) --> RuleEdge(u,w).
	 * ReversedRuleEdge(u,v) and ReversedRuleEdge(v,w) --> RuleEdge(u,w).
	 * ReversedRuleEdge(u,v) and RuleEdge(v,w) --> ReversedRuleEdge(u,w).
	 * RuleEdge(u,v) and ReversedRuleEdge(v,w) --> ReversedRuleEdge(u,w).
	 * If the degree of a node is bounded then this should take linear time. Otherwise, worst case is cubic
	 * @return
	 */
	public List<ViolatedTransitivityConstraint> findViolatedTransitivityConstraints() {
		
		List<ViolatedTransitivityConstraint> result = new LinkedList<ViolatedTransitivityConstraint>();
		
		for(RelationNode node: m_nodes.values()) {
			
			for(AbstractRuleEdge inEdge: node.inEdges()) {
				for(AbstractRuleEdge outEdge: node.outEdges()) {
					
					if(inEdge instanceof RuleEdge) {
						if(outEdge instanceof RuleEdge) {
							if(inEdge.from().id()!=outEdge.to().id() && !containsDirectEdge(inEdge.from(), outEdge.to()))
								result.add(new ViolatedTransitivityConstraint(inEdge.from().id(), node.id(), outEdge.to().id(), EdgeType.DIRECT, EdgeType.DIRECT));
						}
						else {
							if(!containsReversedEdge(inEdge.from(), outEdge.to()))
								result.add(new ViolatedTransitivityConstraint(inEdge.from().id(), node.id(), outEdge.to().id(), EdgeType.DIRECT, EdgeType.REVERSED));
						}
					}
					else {
						if(outEdge instanceof RuleEdge) {
							if(!containsReversedEdge(inEdge.from(), outEdge.to()))
								result.add(new ViolatedTransitivityConstraint(inEdge.from().id(), node.id(), outEdge.to().id(), EdgeType.REVERSED, EdgeType.DIRECT));
						}
						else {
							if(inEdge.from().id()!=outEdge.to().id() && !containsDirectEdge(inEdge.from(), outEdge.to()))
								result.add(new ViolatedTransitivityConstraint(inEdge.from().id(), node.id(), outEdge.to().id(), EdgeType.REVERSED, EdgeType.REVERSED));
						}
					}
				}
			}
		}
		return result;
	}
	
	public void addTransitiveEdgesNotEfficiently() throws Exception {
		
		boolean add=true;
		while(add) {
			add = false;
			List<AbstractRuleEdge> edgesToAddList = new LinkedList<AbstractRuleEdge>();
			for(RelationNode currNode: getNodes()) {
				for(AbstractRuleEdge inEdge: currNode.inEdges()) {

					for(AbstractRuleEdge outEdge:currNode.outEdges()) {

						if(inEdge instanceof RuleEdge) {
							if(outEdge instanceof RuleEdge) {
								if(inEdge.from()!=outEdge.to())
									edgesToAddList.add(new RuleEdge(inEdge.from(),outEdge.to(),-1));
							}
							else {
								edgesToAddList.add(new ReversedRuleEdge(inEdge.from(),outEdge.to(),-1));
							}
						}
						else {
							if(outEdge instanceof RuleEdge) {
								edgesToAddList.add(new ReversedRuleEdge(inEdge.from(),outEdge.to(),-1));
							}
							else {
								if(inEdge.from()!=outEdge.to())
									edgesToAddList.add(new RuleEdge(inEdge.from(),outEdge.to(),-1));
							}
						}
					}
				}
			}
			for(AbstractRuleEdge edge:edgesToAddList)
				add |= addEdge(edge);
		}
	}
	
	@Override
	public int potentialEdges() {
		int nodeCount = getNodeCount();
		return 2*nodeCount*nodeCount-nodeCount;
	}

	@Override
	public List<List<Integer>> findViolatedTreeConstraints()
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
}
