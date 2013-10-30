package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;

public interface EdgeLearner {
	void init(NodeGraph ioGraph,MapLocalScorer iLocalModel) throws Exception;
	void learn() throws Exception;
	double getObjectiveFunctionValue();
	
}
