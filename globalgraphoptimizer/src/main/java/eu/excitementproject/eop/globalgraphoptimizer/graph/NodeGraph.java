package eu.excitementproject.eop.globalgraphoptimizer.graph;

import java.util.Map;

public class NodeGraph {
	
	
	public NodeGraph() {
		m_graph = new DirectedOneMappingOntologyGraph();
	}
	
	public NodeGraph(AbstractOntologyGraph iGraph) {
		m_graph = iGraph;
	}

	public  Map<String,RelationNode> getDesc2NodesMap() {
		return m_graph.getDesc2NodesMap();
	}
	
	public AbstractOntologyGraph getGraph() {
		return m_graph;
	}

	protected AbstractOntologyGraph m_graph;
}
