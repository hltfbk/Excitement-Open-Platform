package eu.excitementproject.eop.distsim.builders;

import java.util.LinkedHashMap;

/**
 * Defines truncation of feature vectors according to some policy
 * 
 * @author Meni Adler
 * @since 05/09/2012
 *
 */
public interface VectorTruncate {
	/**
	 * Truncates a given feature vector, according to some policy
	 * 
	 * @param sortedScores an order map from featureIds to their scores, where the features are descendingly sorted according to their scores
	 * @returns truncated vector, according to some policy
	 */
	LinkedHashMap<Integer,Double> truncate(LinkedHashMap<Integer,Double> sortedScores);
}
