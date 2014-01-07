/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring.feature;

/**
 * Defines the required data for element-feature vector entry
 * 
 * @author Meni Adler
 * @since 05/11/2012
 *
 */
public interface ElementFeatureData {

	/**
	 * Gets the scoring value of this entry
	 * 
	 * @return the scoring value of this entry
	 */
	double getValue();
	
	/**
	 * 
	 * Gets the rank of this entry with refer to the other entries' values
	 * 
	 * @return the rank of this entry with refer to the other entries' values
	 */
	double getRank();
	
	
	/**
	 * Gets the number of entruies in the vector
	 * 
	 * @return the number of entruies in the vector
	 */
	double getSize();
}
