package eu.excitementproject.eop.globalgraphoptimizer.graph;

import java.util.Map;
import java.util.Set;



public abstract class DirectedOntologyGraph extends AbstractOntologyGraph {
	
	public DirectedOntologyGraph() {
		super();
	}
	
	public DirectedOntologyGraph(String description) {
		super(description);
	}
	
	public DirectedOntologyGraph(String description, Map<Integer,RelationNode> nodes, Map<String,AbstractRuleEdge> edges) {
		super(description,nodes,edges);
	}

	public DirectedOntologyGraph(Set<String> iNodeDescs) {
		super(iNodeDescs);
	}
	
	public DirectedOntologyGraph(String description, Map<Integer,RelationNode> nodes, Map<String,AbstractRuleEdge> edges, boolean contracted) {
		super(description, nodes, edges, contracted);
	}
	
	public boolean addEdge(RelationNode fromNode, RelationNode toNode) {
		
		if(m_edges.containsKey(fromNode.id()+RuleEdge.SYMBOL+toNode.id()))
			return false;
		
		RuleEdge newEdge = new RuleEdge(fromNode,toNode,1);
		
		addNode(fromNode);
		addNode(toNode);
		
		fromNode = getNode(fromNode.id());
		toNode = getNode(toNode.id());
		fromNode.addOutEdge(newEdge);
		toNode.addInEdge(newEdge);
		
		m_edges.put(newEdge.toString(), newEdge);
		return true;
	}	
}
