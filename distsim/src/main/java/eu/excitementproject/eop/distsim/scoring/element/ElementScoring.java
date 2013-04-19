package eu.excitementproject.eop.distsim.scoring.element;

import java.util.Collection;

/**
 *  * The ElementScorer interface gives a score to an element (based on its feature vector).
 * 
 * The provided score is used for normalization.
 * 
 * @author Meni Adler
 * @since 28/03/2012
 *
 */
public interface ElementScoring {
	/**
	 * Measures a scoring weight for a given element (based on its feature vector).
	 * 
     * @param featuresScores a list of feature scores of some element 
	 * @return the combined score for the element. 
	 */
	double score(Collection<Double> featuresScores);
}