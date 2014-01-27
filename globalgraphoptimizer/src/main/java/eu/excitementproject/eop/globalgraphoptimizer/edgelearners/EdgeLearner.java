package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;

/**
 * The EdgeLearner interface defines global graph optimization method, which maximizes a given graph with some scoring model
 * 
 * @author Jonathan Berant
 */
public interface EdgeLearner {
	/**
	 * Initializes a given graph and scoring model according to some initialization method
	 * 
	 * @param ioGraph A given graph to be optimized
	 * @param iLocalModel A scoring model, which assign score to each possible edge
	 * @throws Exception
	 */
	void init(NodeGraph ioGraph,MapLocalScorer iLocalModel) throws Exception;
	
	/**
	 * Optimize the initial graph according to some method
	 * 
	 * @throws Exception
	 */
	void learn() throws Exception;
	
	
	/**
	 * Returns the current value of the objective function of the learning process
	 * @return the current value of the objective function of the learning process
	 */
	double getObjectiveFunctionValue();
	
}
