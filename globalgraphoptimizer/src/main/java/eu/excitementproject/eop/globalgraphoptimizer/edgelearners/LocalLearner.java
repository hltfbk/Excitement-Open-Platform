package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.util.Map;


import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;

/**
 * The local learner learns a graph composed of edges with scores, according to the given score model, greater than a given minimal score   
 * 
 *
 */
public class LocalLearner implements EdgeLearner {

	public LocalLearner(NodeGraph ioGraph, MapLocalScorer iLocalModel, double edgeCost) {
		m_edgeCost = edgeCost;
		init(ioGraph,iLocalModel);
	}

	public LocalLearner(double edgeCost) {

		m_nodeGraph = null;
		m_localModel = null;
		m_edgeCost = edgeCost;
	}
	
	public void init(NodeGraph ioGraph, MapLocalScorer iLocalModel)  {

		m_nodeGraph = ioGraph;
		m_localModel = iLocalModel;
	}
	
	@Override
	public void learn() throws Exception {
		
		m_nodeGraph.getGraph().clearEdges(); //we delete all original edges

		Map<String,RelationNode> desc2NodeMap = m_nodeGraph.getDesc2NodesMap();

		//adding candidates from score file
		Map<Pair<Integer,Integer>,Double> ruleScoreMap = m_localModel.getRule2ScoreMap();
		for(Pair<Integer,Integer> rule: ruleScoreMap.keySet()) {

			double score = ruleScoreMap.get(rule);
			if(score>m_edgeCost) {
				Pair<String,String> ruleDesc = m_localModel.getRuleDesc(rule);
				m_nodeGraph.getGraph().addEdge(new RuleEdge(desc2NodeMap.get(ruleDesc.getFirst()), desc2NodeMap.get(ruleDesc.getSecond()), score));
			}
			//else 
				//break;
		}	
	}


		
	public double getObjectiveFunctionValue() {
		return m_nodeGraph.getGraph().sumOfEdgeWeights()-m_edgeCost*m_nodeGraph.getGraph().getEdgeCount();
	}

	NodeGraph m_nodeGraph;
	MapLocalScorer m_localModel;
	protected double m_edgeCost;

}
