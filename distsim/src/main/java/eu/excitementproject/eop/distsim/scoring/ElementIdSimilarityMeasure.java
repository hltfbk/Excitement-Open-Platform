package eu.excitementproject.eop.distsim.scoring;

/**
 * Defines the similarity measure of some element, composed of the element it is compared with, their similarity measure, and some additional info
 *  
 * @author Meni Adler
 * @since 21/06/2012
 *
 * 
 */
public interface ElementIdSimilarityMeasure extends SimilarityMeasure {
	/**
	 * Get the compared element 
	 * 
	 * @return the compared element
	 */
	int getElementID();
}
