package eu.excitementproject.eop.distsim.scoring.combine;

import java.util.List;


/**
 * 
 * The SimilarityCombiner interface defines the combination of various similarity measures between elements, 
 * into one final similarity score. 
 * 
 * For example, in Dirt setting, the elements are predicate templates and the features are their arguments.
 * Given two similarity measures between pairs of predicate templates, based on each of their arguments, 
 * a new similarity measure between the predicate templates can be provided, by combining the two similarity measures into one unified score
 *  
 * @author Meni Adler
 * @since 17/04/2012
 *   
 */

public interface SimilarityCombination {

	/**
	 * Combines a given list of scores into one final unified score
	 * 
	 * @param scores a list of similarity scores
	 * @param requiredScoreNum the required number of scores to be combined
	 * @return a unified score of the given similarity scores
	 * @throws IlegalScoresException if the number of the given scores does not fit the method of the unification
	 */
	public double combine(List<Double> scores, int requiredScoreNum) throws IlegalScoresException;
}
