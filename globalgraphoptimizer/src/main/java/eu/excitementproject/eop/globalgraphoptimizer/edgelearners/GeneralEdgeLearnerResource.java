package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.io.InputStream;

import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.score.ScoreModel;

/**
 * @author Meni Adler
 * @since 17/08/2011
 *
 */
public abstract class GeneralEdgeLearnerResource implements EdgeLearnerResource {	
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.graph.edgelearners.EdgeLearnerResource#getInitialNodeGraph()
	 */
	@Override
	public NodeGraph getInitialNodeGraph() {
		return nodeGraph;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.graph.edgelearners.EdgeLearnerResource#getLocalScoreModel()
	 */
	@Override
	public ScoreModel getLocalScoreModel() {
		return scoreModel;
	}
	
	protected abstract void loadResource(InputStream in) throws Exception;
	
	ScoreModel scoreModel;
	NodeGraph nodeGraph;
}
