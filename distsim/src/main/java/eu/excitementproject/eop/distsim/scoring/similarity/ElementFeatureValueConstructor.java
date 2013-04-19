/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring.similarity;


import eu.excitementproject.eop.distsim.scoring.feature.ElementFeatureData;
import eu.excitementproject.eop.distsim.storage.ElementFeatureScoreStorage;
import eu.excitementproject.eop.distsim.storage.NoScoreFoundException;

/**
 * The ElementFeatureValueConstructor defines scoring values constructions, based on various kinds of parameter sets
 * 
 * @author Meni Adler
 * @since 05/11/2012
 *
 */
public interface ElementFeatureValueConstructor {
	double constructFeatureValue(ElementFeatureScoreStorage elementFeatureScoreStorage, int elementId, int featureId) throws NoScoreFoundException;
	double constructFeatureValue(ElementFeatureData data);
}
