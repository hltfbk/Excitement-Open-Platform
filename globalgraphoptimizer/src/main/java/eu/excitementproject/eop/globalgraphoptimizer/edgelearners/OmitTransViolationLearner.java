package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.AbstractRuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;

public class OmitTransViolationLearner  implements EdgeLearner {

	private Logger logger = Logger.getLogger(OmitTransViolationLearner.class);
	
	public OmitTransViolationLearner(NodeGraph ioGraph, MapLocalScorer iLocalModel, double edgeCost)  {
		m_edgeCost = edgeCost;
		init(ioGraph,iLocalModel);
	}

	public OmitTransViolationLearner(double edgeCost) {
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
		
		boolean remove=true;
		int numOfEdgesRemoved = 0;
		
		while(remove) {
			remove = false;
			
			for(RelationNode currNode: m_nodeGraph.getGraph().getNodes()) {
				
				Map<String,Integer> edgesInViolationCount = new HashMap<String,Integer>();
				
				for(AbstractRuleEdge inEdge: currNode.inEdges()) {
					for(AbstractRuleEdge outEdge:currNode.outEdges()) {

						if(inEdge.from()!=outEdge.to() && 
								!m_nodeGraph.getGraph().containsEdge(new RuleEdge(inEdge.from(),outEdge.to(),-1))) {
							
							inc(edgesInViolationCount, inEdge.toString());
							inc(edgesInViolationCount,outEdge.toString());
							
						}
					}
				}
				
				if(edgesInViolationCount.size()>0) {
					remove = true;
					numOfEdgesRemoved++;
					String edgeToRemove = null;
					for(String edgeInViolation: edgesInViolationCount.keySet()) {
						
						if(edgeToRemove==null)
							edgeToRemove = edgeInViolation;
						else {
							if(get(edgesInViolationCount,edgeInViolation) > get(edgesInViolationCount,edgeToRemove)) 
									edgeToRemove=edgeInViolation;
						}
						
					}
					m_nodeGraph.getGraph().removeEdge(edgeToRemove);
				}
				
		
			}
		}
		logger.info("number of edges removed: " + numOfEdgesRemoved);
	}

	private void inc(Map<String,Integer> map, String key) {
		Integer val = map.get(key);
		if (val == null)
			val = 1;
		map.put(key, val+1);
	}
	
	private int get(Map<String,Integer> map, String key) {
		Integer val = map.get(key);
		if (val == null)
			val = 0;
		return val;
	}
		
	public double getObjectiveFunctionValue() {
		return m_nodeGraph.getGraph().sumOfEdgeWeights()-m_edgeCost*m_nodeGraph.getGraph().getEdgeCount();
	}

	NodeGraph m_nodeGraph;
	MapLocalScorer m_localModel;
	protected double m_edgeCost;

}
