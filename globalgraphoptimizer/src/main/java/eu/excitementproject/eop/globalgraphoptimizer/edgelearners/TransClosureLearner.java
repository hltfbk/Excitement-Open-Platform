package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.util.LinkedList;

import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.AbstractRuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;

public class TransClosureLearner implements EdgeLearner {

	public TransClosureLearner(NodeGraph ioGraph, MapLocalScorer iLocalModel, double edgeCost) {
		init(ioGraph,iLocalModel);
		m_edgeCost = edgeCost;
	}

	public TransClosureLearner(double edgeCost) {
		m_edgeCost = edgeCost;
		init(null,null);
	}
	
	public void init(NodeGraph ioGraph, MapLocalScorer iLocalModel) {
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
			else
				break;
		}
		
		boolean add=true;
		while(add) {
			add = false;
			List<AbstractRuleEdge> edgesToAddList = new LinkedList<AbstractRuleEdge>();
			for(RelationNode currNode: m_nodeGraph.getGraph().getNodes()) {
				for(AbstractRuleEdge inEdge: currNode.inEdges()) {
					for(AbstractRuleEdge outEdge:currNode.outEdges()) {
						if(inEdge.from()!=outEdge.to())
							edgesToAddList.add(new RuleEdge(inEdge.from(),outEdge.to(),
									m_localModel.getEntailmentScore(inEdge.from(), outEdge.to())));
					}
				}
			}
			for(AbstractRuleEdge edge:edgesToAddList)
				add |= m_nodeGraph.getGraph().addEdge(edge);
		}	
	}


		
	public double getObjectiveFunctionValue() {
		return m_nodeGraph.getGraph().sumOfEdgeWeights()-m_edgeCost*m_nodeGraph.getGraph().getEdgeCount();
	}

	NodeGraph m_nodeGraph;
	MapLocalScorer m_localModel;
	protected double m_edgeCost;
}
