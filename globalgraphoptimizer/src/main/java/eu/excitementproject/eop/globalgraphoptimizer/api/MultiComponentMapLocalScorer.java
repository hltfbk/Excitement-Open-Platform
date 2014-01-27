package eu.excitementproject.eop.globalgraphoptimizer.api;

import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.AbstractRuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;


/**
 * A container for score models for various components
 * 
 * @author Jonathan Berant
 *
 */
public class MultiComponentMapLocalScorer {
	
	public MultiComponentMapLocalScorer(TObjectIntMap<String> pred2ComponentId,TIntObjectMap<MapLocalScorer> compId2Scorer) {
		m_pred2ComponentId = pred2ComponentId;
		m_compId2Scorer = compId2Scorer;
	}
	
	public MultiComponentMapLocalScorer(List<Set<String>> components,Iterable<AbstractRuleEdge> edges, boolean convertProb2Score,
			Set<Pair<String,String>> entailings, Set<Pair<String,String>> nonentailings, double unknownScore) {

		//initialize the component maps
		m_pred2ComponentId = new TObjectIntHashMap<String>();
		m_compId2Scorer = new TIntObjectHashMap<MapLocalScorer>();
		
		int componentId = 1;
		for(Set<String> component: components) {
			m_compId2Scorer.put(componentId, new MapLocalScorer(component,unknownScore,convertProb2Score));
			for(String predicate: component) {
				m_pred2ComponentId.put(predicate, componentId);
			}
			componentId++;
		}
		//insert all rules to their relevant component
		int j = 0;
		for (AbstractRuleEdge edge : edges) {
			
			int entailingComponentId =  m_pred2ComponentId.get(edge.from().description());
			int entailedComponentId =  m_pred2ComponentId.get(edge.to().description());
			/*
			 * We are only interested in scores for predicates in the same component.
			 * If we get value '0' this means that the predicate is not in a component>1
			 */
			if(entailedComponentId==entailingComponentId 
					&& entailingComponentId !=0 && entailedComponentId!=0) { 
				
				MapLocalScorer currentScorer = m_compId2Scorer.get(entailedComponentId);
				currentScorer.insertRuleScore(edge.from().description(), edge.to().description(), edge.score());
			}
			j++;
			if(j % 1000000 == 0)
				System.out.println("uploaded rule score number: " + j);		
		}
		
		//insert entailing and non entailing
		
		//inserting the entailing		
		for (Pair<String,String> entailing : entailings) {
			int entailingComponentId =  m_pred2ComponentId.get(entailing.getFirst());
			int entailedComponentId =  m_pred2ComponentId.get(entailing.getSecond());
			/*
			 * We are only interested in scores for predicates in the same component.
			 * If we get value '0' this means that the predicate is not in a component>1
			 */
			if(entailedComponentId==entailingComponentId 
					&& entailingComponentId !=0 && entailedComponentId!=0) { 
				MapLocalScorer currentScorer = m_compId2Scorer.get(entailedComponentId);
				currentScorer.getEntailing().add(new Pair<String,String>(entailing.getFirst(),entailing.getSecond()));
			}
			j++;
			if(j % 1000000 == 0)
				System.out.println("uploaded rule score number: " + j);					
		}
		
		//inserting the non-entailing
		for (Pair<String,String> nonentailing : nonentailings) {
			int entailingComponentId =  m_pred2ComponentId.get(nonentailing.getFirst());
			int entailedComponentId =  m_pred2ComponentId.get(nonentailing.getSecond());
			/*
			 * We are only interested in scores for predicates in the same component.
			 * If we get value '0' this means that the predicate is not in a component>1
			 */
			if(entailedComponentId==entailingComponentId 
					&& entailingComponentId !=0 && entailedComponentId!=0) { 
				MapLocalScorer currentScorer = m_compId2Scorer.get(entailedComponentId);
				currentScorer.getNonEntailing().add(new Pair<String,String>(nonentailing.getFirst(),nonentailing.getSecond()));
			}
			j++;
			if(j % 1000000 == 0)
				System.out.println("uploaded rule score number: " + j);		
		}
	}

	public MapLocalScorer getScorerForPredicate(String predicate) throws MissingComponentException {
		
		int componentId = m_pred2ComponentId.get(predicate);
		if(componentId ==0)
			throw new MissingComponentException("component does not exist for predicate: " + predicate);
		return m_compId2Scorer.get(m_pred2ComponentId.get(predicate));
	}
	
	public TIntObjectMap<MapLocalScorer> getcompId2Scorer() {
		return m_compId2Scorer;
	}
	
	public MapLocalScorer getScorer(int compId) {
		return m_compId2Scorer.get(compId);
	}

	private TObjectIntMap<String> m_pred2ComponentId;
	private TIntObjectMap<MapLocalScorer> m_compId2Scorer;
}
