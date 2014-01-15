package eu.excitementproject.eop.globalgraphoptimizer.score;

import java.util.HashMap;

import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;

/**
 * A scorer for a graph that has been contracted - built from the original graph and its scorer
 * 
 * IMPORTANT: this scorer already incorporates the edge cost into the local score contrary to others.
 * 
 * @author Jonathan Berant
 *
 */
public class ContractedGraphLocalScoreModel extends MapLocalScorer {

	public ContractedGraphLocalScoreModel(MapLocalScorer scorer, Map<Integer,Set<Integer>> contractedNodes2NodesMap, double edgeCost) {
		
		m_scorer = scorer;
		m_contractedNodes2NodesMap = contractedNodes2NodesMap;
		m_edgeCost = edgeCost;
		m_rule2ScoreMap = new HashMap<Pair<Integer,Integer>, Double>();
	}
	
	@Override
	public double getEntailmentScore(RelationNode fromNode, RelationNode toNode)
			throws Exception {
		Pair<Integer,Integer> idPair = new Pair<Integer,Integer>(fromNode.id(),toNode.id());
		double score = 0;
		if(m_rule2ScoreMap.containsKey(idPair)) {
			score = m_rule2ScoreMap.get(idPair);
		}
		else {
			
			Set<Integer> fromSet = m_contractedNodes2NodesMap.get(fromNode.id());
			Set<Integer> toSet = m_contractedNodes2NodesMap.get(toNode.id());
			
			for(Integer fromId: fromSet) {
				for(Integer toId: toSet) {
					
					Pair<String,String> ruleDesc = m_scorer.getRuleDesc(new Pair<Integer,Integer>(fromId,toId));
					score+=m_scorer.getEntailmentScore(ruleDesc.getFirst(), ruleDesc.getSecond())-m_edgeCost;
				}
			}
			m_rule2ScoreMap.put(idPair, score);
		}
		return score;
	}

	@Override
	public double getEntailmentScore(String desc1, String desc2)  {
		throw new UnsupportedOperationException("Unsupported method");
	}
	
	private double m_edgeCost;
	private MapLocalScorer m_scorer;
	private Map<Integer,Set<Integer>> m_contractedNodes2NodesMap;
	private Map<Pair<Integer,Integer>,Double> m_rule2ScoreMap;
}
