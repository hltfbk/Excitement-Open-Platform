package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.score.ScoreModel;

/**
 * @author Meni Adler
 * @since 17/08/2011
 * 
 * An interface for the two basic edge-learner's components: initial NodeGraph and LocalScoreModel 
 *
 */
public interface EdgeLearnerResource {
	
	/**
	 * @return an initial graph for some edge learning process
	 */
	NodeGraph getInitialNodeGraph();
	
	
	/**
	 * @return a local score model (which maps pairs of vertices to their scores)
	 */
	ScoreModel getLocalScoreModel();
}
