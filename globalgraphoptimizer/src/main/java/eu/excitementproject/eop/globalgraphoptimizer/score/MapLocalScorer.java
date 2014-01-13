package eu.excitementproject.eop.globalgraphoptimizer.score;

import java.util.HashSet;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.globalgraphoptimizer.defs.Constants;
import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;

/**
 * 
 * 
 * @author Jonathan Berant
 * 
 */
public class MapLocalScorer extends LocalScoreModel {
	
	public MapLocalScorer() {		
	}
	
	public MapLocalScorer(Set<String> component, double unknownScore, boolean convertProb2Score) {
		
		m_desc2idMap = new SimpleBidirectionalMap<String,Integer>();
		
		int id = 1;
		for(String predicate:component)
			m_desc2idMap.put(predicate, id++);
		
		m_rule2ScoreMap = new LinkedHashMap<Pair<Integer,Integer>, Double>();
		m_entailing = new HashSet<Pair<String,String>>();
		m_nonEntailing = new HashSet<Pair<String,String>>();
		m_convertProb2Score = convertProb2Score;
		m_unknownScore = prob2Score(unknownScore);
	}

	public MapLocalScorer(Set<Pair<String,String>> entailing, Set<Pair<String,String>> nonEntailing,BidirectionalMap<String, Integer> desc2idMap, Map<Pair<Integer,Integer>,Double> rule2ScoreMap,boolean convertProb2Score, double unknownScore) throws Exception {
		super(entailing,nonEntailing);
		m_desc2idMap = desc2idMap;		
		m_rule2ScoreMap = rule2ScoreMap;
		m_convertProb2Score = convertProb2Score;	
		m_unknownScore = unknownScore;
	}
	
	@Override
	public double getEntailmentScore(RelationNode fromNode, RelationNode toNode) throws Exception {
		return getEntailmentScore(fromNode.description(), toNode.description());
	}

	@Override
	public double getEntailmentScore(String desc1, String desc2)  {

		Pair<Integer,Integer> ruleId = new Pair<Integer, Integer>(m_desc2idMap.leftGet(desc1),m_desc2idMap.leftGet(desc2));
		if (m_rule2ScoreMap.containsKey(ruleId))
			return m_rule2ScoreMap.get(ruleId);
		else
			return m_unknownScore;
	}
	
	public Map<Pair<Integer,Integer>,Double> getRule2ScoreMap() {
		return m_rule2ScoreMap;
	}
	
	public void insertRuleScore(String from, String to, double score) {
		Pair<Integer,Integer> rule = new Pair<Integer, Integer>(m_desc2idMap.leftGet(from),m_desc2idMap.leftGet(to));
		m_rule2ScoreMap.put(rule, prob2Score(score));
	}
	
	public Pair<String,String> getRuleDesc(Pair<Integer,Integer> rule) {
		return new Pair<String, String>(m_desc2idMap.rightGet(rule.getFirst()),m_desc2idMap.rightGet(rule.getSecond()));
	}
	
	private double prob2Score(double score) {
		
		double result = score;
		
		if(m_convertProb2Score) {
			if(result>=1.0)
				result = 1-Constants.EPSILON;
			else if(result<=0.0)
				result = Constants.EPSILON;
			result = Math.log(result/(1-result));		
		}
		return result;
	}

	private double m_unknownScore;
	private boolean m_convertProb2Score;
	private BidirectionalMap<String, Integer> m_desc2idMap;
	private Map<Pair<Integer,Integer>,Double> m_rule2ScoreMap;
}
