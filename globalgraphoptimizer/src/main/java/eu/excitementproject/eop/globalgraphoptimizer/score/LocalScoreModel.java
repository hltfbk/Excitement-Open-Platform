package eu.excitementproject.eop.globalgraphoptimizer.score;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;


/**
 * Basic implementation of ScoreModel, based on a given edge scoring table, and a set of entailing/non-entailing edges 
 * 
 * @author Jonathan Berant
 *
 */
public abstract class LocalScoreModel implements ScoreModel {
		
	public LocalScoreModel() {
	}
	
	public LocalScoreModel(Set<Pair<String,String>> entailing, Set<Pair<String,String>> nonEntailing) {
		m_entailing = entailing;
		m_nonEntailing = nonEntailing;
		m_edgeProbCache = new HashMap<Pair<String,String>,Double>();
		initEntailing();
		initNonEntailing();	
	}
	
	private void initNonEntailing() {
		for(Pair<String,String> nonEntailingPair: m_nonEntailing)
			m_edgeProbCache.put(nonEntailingPair, 0.0000000001);
	}
	
	private void initEntailing() {
		for(Pair<String,String> entailingPair: m_entailing)
			m_edgeProbCache.put(entailingPair, 0.9999999999);
	}
	
	public Set<Pair<String,String>> getEntailing() {
		return m_entailing;
	}
	
	public Set<Pair<String,String>> getNonEntailing() {
		return m_nonEntailing;
	}
		
	protected Map<Pair<String,String>,Double> m_edgeProbCache;
	protected Set<Pair<String,String>> m_entailing;
	protected Set<Pair<String,String>> m_nonEntailing;
}
