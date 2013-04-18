package eu.excitementproject.eop.distsim.scoring;

import eu.excitementproject.eop.distsim.items.Element;

/**
 * Defines the similarity measure for two elements
 *  
 * @author Meni Adler
 * @since 21/06/2012
 *
 * 
 */
public interface SimilarityRule extends SimilarityMeasure {
	/**
	 * Get the left element id
	 * 
	 * @return the left element id
	 */
	Element getLeftElement();
	
	/**
	 * Get the left element id
	 * 
	 * @return the left element id
	 */
	Element getRightElement();
}
