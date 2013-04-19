/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring;

/**
 * A representation of the feature scores of an element
 * 
 * @author Meni Adler
 * @since 29/05/2012
 *
 */
public interface ElementFeatureScore extends FeatureScore {
	
	/**
	 * @return the id of the element
	 */
	int getElementId();	
	
}
