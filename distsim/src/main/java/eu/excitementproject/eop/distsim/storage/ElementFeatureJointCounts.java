package eu.excitementproject.eop.distsim.storage;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;

/**
 * A representation of an element-feature joint count
 *
 * @author Meni Adler
 * @since 22/05/2012
 *
 * 
 */
public interface ElementFeatureJointCounts {
	/**
	 * Get the id of the element
	 * 
	 * @return the id of the element of the joint count
	 */
	int getElementId();
	
	
	/**
	 * Get an iterator for the element's features and their joint counts
	 * 
	 * @return an iterator for the element's features and their joint counts
	 */
	ImmutableIterator<FeatureCount> getFeatureCounts();
	
	/**
	 * Return the number of the features
	 * 
	 * @return the number of the features
	 */
	int getFeaturesSize();

}
