package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.util.Set;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;
import eu.excitementproject.eop.globalgraphoptimizer.score.ScoreModel;


public class GreedyScoreEdgeLearner extends GreedyEdgeLearner implements EdgeLearner {

	public GreedyScoreEdgeLearner(NodeGraph ioGraph, ScoreModel iLocalModel, double edgeCost) throws Exception {
		super(ioGraph, iLocalModel);
		m_edgeCost = edgeCost;
	}

	public GreedyScoreEdgeLearner(double edgeCost) {
		super();
		m_edgeCost = edgeCost;
	}
	
	public void init(NodeGraph ioGraph, MapLocalScorer iLocalModel) throws Exception {
		super.init(ioGraph, iLocalModel);
	}

	
	@Override
	protected double multipleRelationsMultiplicativeChange(Set<Pair<RelationNode, RelationNode>> edgeCandidates) throws Exception {		
		double score=0;	
		for(Pair<RelationNode,RelationNode> edgeCandidate: edgeCandidates) 
			score+=singleRelationMultiplicativeChange(edgeCandidate);
		return score;
	}

	@Override
	public boolean toStop(double score) {
		if(score>0)
			return false;
		return true;
	}
	
	private double singleRelationMultiplicativeChange(Pair<RelationNode,RelationNode> iEdgeCandidate) throws Exception {
		return m_localModel.getEntailmentScore(iEdgeCandidate.getFirst(),iEdgeCandidate.getSecond())-m_edgeCost;
	}
	
	public double getObjectiveFunctionValue() {
		return m_nodeGraph.getGraph().sumOfEdgeWeights()-m_edgeCost*m_nodeGraph.getGraph().getEdgeCount();
	}
	
	protected double m_edgeCost;
}
