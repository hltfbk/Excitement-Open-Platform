/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring;

import eu.excitementproject.eop.distsim.items.Element;

/**
 * Defines the similarity measure of two elements, composed of the left and right elements with their similarity measure, and some additional info
 * 
 * @author Meni Adler
 * @since 03/07/2012
 *
 */
public interface ElementsSimilarityMeasure extends SimilarityMeasure {
	/**
	 * Get the left element of the similarity 
	 * @return the left element of the similarity
	 */
	Element getLeftElement();

	/**
	 * Get the right element of the similarity 
	 * @return the right element of the similarity
	 */
	Element getRightElement();
}
