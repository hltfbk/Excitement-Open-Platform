/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring;

/**
 * A representation of a score of an element
 * 
 * @author Meni Adler
 * @since 29/05/2012
 *
 */
public interface ElementScore {
	
	/**
	 * @return the id of the element
	 */
	int getElementId();
	
	/**
	 * @return the score for the feature of the element
	 */
	double getScore();
	
}
