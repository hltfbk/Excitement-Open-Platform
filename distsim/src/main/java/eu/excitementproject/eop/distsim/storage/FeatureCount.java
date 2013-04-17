/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

/**
 * Represent the count of a given feature
 * 
 * @author Meni Adler
 * @since 04/09/2012
 *
 */
public interface FeatureCount {

	/**
	 * Get the id of the feature
	 * 
	 * @return the id of the feature of the joint count
	 */
	int getFeatureId();
	
	/**
	 * Get the count of the feature
	 * 
	 * @return the element-feature joint count
	 */
	double getCount();
}
