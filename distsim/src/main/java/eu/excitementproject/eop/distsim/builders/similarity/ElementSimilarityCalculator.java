/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.similarity;

import eu.excitementproject.eop.distsim.storage.ElementFeatureScoreStorage;
import eu.excitementproject.eop.distsim.storage.LoadingStateException;
import eu.excitementproject.eop.distsim.storage.PersistenceDevice;
import eu.excitementproject.eop.distsim.util.SerializationException;


/**
 * Building of a database, composed of element pairs and their similarity measures
 *
 * @author Meni Adler
 * @since 29/05/2012
 *
 */

public interface ElementSimilarityCalculator {
	/**
	 * 
	 * Builds a data base, composed of all relevant pairs of elements and their similarity measure
	 * 
	 * @param elementFeatureScores a database of features and elements scores
	 * @param measurement a method for measuring the similarity between two elements, based on their feature vectors  
	 * @return a data base composed of all relevant pairs of elements and their similarity measure
	 * @throws LoadingStateException 
	 * @throws SerializationException 
	 */
	//BasicMap<Integer,? extends Object> measureElementSimilarity(ElementFeatureScoreStorage elementFeatureScores) throws ElementSimilarityException; 

	/**
	 * 
	 * Calculate all relevant pairs of elements and their similarity measure, and write them to the given persistence device
	 * 
	 * @param elementFeatureScores a database of features and elements scores
	 * @param outR2LDevice a persistence device to store the elements' similarities, where each entailed element is assigned to its entailing elements with their similarity scores
	 * @param outL2RDevice a persistence device to store the elements' similarities, where each entailing element is assigned to its entailed elements with their similarity scores 
	 * @throws LoadingStateException 
	 * @throws SerializationException 
	 */
	void measureElementSimilarity(ElementFeatureScoreStorage elementFeatureScores, PersistenceDevice outR2LDevice, PersistenceDevice outL2RDevice) throws ElementSimilarityException; 

	/**
	 * 
	 * Builds a set of data bases, composed of all relevant pairs of elements and their similarity measures, according to various kind of similarity methods
	 * 
	 * @param elementFeatureScores a database of features and elements scores
	 * @param measurements a list of methods for measuring the similarity between two elements, based on their feature vectors  
	 * @return a list of data bases composed of all relevant pairs of elements and their similarity measures, where the ith database contains similarity measures according to the ith method of the measurements param 
	 */
	//List<SimilarityStorage> measureElementSimilarities(ElementFeatureScoreStorage elementFeatureScores, List<ElementSimilarityScoring> measurements); 
}
