package eu.excitementproject.eop.globalgraphoptimizer.graph;

import eu.excitementproject.eop.globalgraphoptimizer.edgelearners.EdgeLearner;


public class UntypedPredicateGraph extends NodeGraph {

	public UntypedPredicateGraph()  {
		
		m_graph = null;
		m_edgeLearner = null;
	}

	public UntypedPredicateGraph(AbstractOntologyGraph graph, EdgeLearner edgeLearner) {		
		m_graph = graph;
		m_edgeLearner = edgeLearner;
	}

	public void learn() throws Exception {
		m_edgeLearner.learn();
	}
	
	public EdgeLearner getEdgeLearner() {
		return m_edgeLearner;
	}
	
	private EdgeLearner m_edgeLearner;
}
