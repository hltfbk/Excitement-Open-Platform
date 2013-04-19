/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.scoring;

import eu.excitementproject.eop.distsim.storage.ElementFeatureCountStorage;

/**
 * Defines the criteria for common features
 * 
 * @author Meni Adler
 * @since 05/09/2012
 *
 */
public interface CommonFeatureCriterion {
	/**
	 * Determines whether the given feature is 'common', based on its countings
	 * 
	 * @param elementFeaturecounts a storage of elements, features, and their counts
	 * @param featureId an id of some feature
	 * @return true if the feature is considered to be common
	 */
	boolean isCommon(ElementFeatureCountStorage elementFeaturecounts, int featureId);
}
