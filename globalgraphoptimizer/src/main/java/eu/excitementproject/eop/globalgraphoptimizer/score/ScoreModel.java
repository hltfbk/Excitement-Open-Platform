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
	 * @param fromNode 
	 * @param toNode
	 * @return an entailemnt score for the given entailment nodes
	 * @throws Exception
	 */
	double getEntailmentScore(RelationNode fromNode, RelationNode toNode) throws Exception;
	
	/**
	 * @param desc1
	 * @param desc2
	 * @return an entailemnt score for the given entailment node descriptions
	 * @throws Exception
	 */
	double getEntailmentScore(String desc1, String desc2) throws Exception;
	
	/**
	 * @return a set of entailment pairs
	 */
	public Set<Pair<String,String>> getEntailing();
	
	/**
	 * @return a set of non-entailing pairs
	 */
	public Set<Pair<String,String>> getNonEntailing();
	
}
