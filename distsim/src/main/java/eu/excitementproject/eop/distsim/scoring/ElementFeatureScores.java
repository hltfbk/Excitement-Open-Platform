/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;


/**
 * A representation of the score for a feature of an element
 * 
 * @author Meni Adler
 * @since 29/05/2012
 *
 */
public interface ElementFeatureScores {
	
	/**
	 * @return the id of the element
	 */
	int getElementId();	
	
	/**
	 * Get an iterator for the element's features and their joint counts
	 * 
	 * @return an iterator for the element's features and their joint counts
	 */
	ImmutableIterator<FeatureScore> getFeatureScores();
	
	/**
	 * @return the number of feature scores for this element
	 */
	int getFeatureScoresNum();
}
