/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring.similarity;


import eu.excitementproject.eop.distsim.scoring.feature.ElementFeatureData;

/**
 * The ElementFeatureValueConstructor defines scoring values constructions, based on various kinds of parameter sets
 * 
 * @author Meni Adler
 * @since 05/11/2012
 *
 */
public interface ElementFeatureValueConstructor {
	/**
	 * Construct a feature value from a given ElementFeatureData
	 * 
	 * @param data represent various properties of the feature
	 * @return a score value for the given feature
	 */
	double constructFeatureValue(ElementFeatureData data);
}
