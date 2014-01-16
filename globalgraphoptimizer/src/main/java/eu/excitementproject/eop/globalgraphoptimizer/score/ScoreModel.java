package eu.excitementproject.eop.globalgraphoptimizer.score;

import java.util.Set;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;


/**
 * @author Meni Adler
 * @since 24/08/2011
 *
 * Basic interface for the score model used by the edge learners
 */

public interface ScoreModel {
	/**
	 * @param fromNode a source node
	 * @param toNode a target node
	 * @return an entailemnt score for the given entailment nodes
	 * @throws Exception
	 */
	double getEntailmentScore(RelationNode fromNode, RelationNode toNode) throws Exception;
	
	/**
	 * @param desc1 a description of a source node
	 * @param desc2 a description of a target node
	 * @return an entailemnt score for the given entailment node descriptions
	 * @throws Exception
	 */
	double getEntailmentScore(String desc1, String desc2) throws Exception;
	
	/**
	 * Gets a set of entailning nodes
	 * 
	 * @return a set of entailment pairs
	 */
	public Set<Pair<String,String>> getEntailing();
	
	/**
	 * Gets a set of non-entailning nodes
	 * 
	 * @return a set of non-entailing pairs
	 */
	public Set<Pair<String,String>> getNonEntailing();
	
}
