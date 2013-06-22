/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring;

/**
 * A representation of the score for a feature of an element
 * 
 * @author Meni Adler
 * @since 29/05/2012
 *
 */
public interface FeatureScore {
	
	/**
	 * @return the id of the feature
	 */
	int getFeatureId();
	
	/**
	 * @return the score for the feature of the element
	 */
	double getScore();
	
}
