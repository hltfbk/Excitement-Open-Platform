package eu.excitementproject.eop.distsim.storage;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;

import eu.excitementproject.eop.distsim.scoring.ElementFeatureScores;
import eu.excitementproject.eop.distsim.scoring.ElementScore;
import eu.excitementproject.eop.distsim.scoring.FeatureScore;
import eu.excitementproject.eop.distsim.scoring.feature.ElementFeatureData;
import eu.excitementproject.eop.distsim.domains.FilterType;

/**
 * The feature score DB contains scores for features of elements, and feature of elements
 *
 * @author Meni Adler
 * @since 29/05/2012
 *
 * 
 */
public interface ElementFeatureScoreStorage  extends Persistence {
	
	/**
	 * Get the score of a given feature of a given element 
	 * 
	 * @param elementId the id of the element
	 * @param featureId the id of the feature
	 * @return the score assigned to the given feature of the given element
	 * @throws NoScoreFoundException if no score found in the storage for the given element and feature
	 */
	double getElementFeatureScore(int elementId, int featureId) throws NoScoreFoundException;
	
	
	ElementFeatureData getElementFeatureData(int elementId, int featureId) throws NoScoreFoundException;
	
	/**
	 * Get all feature scores of the given element
	 * 
	 * @param elementId the id of the element
	 * @return all features of a given element with their scores
	 * @throws NoScoreFoundException 
	 */
	ImmutableIterator<FeatureScore> getElementFeatureScores(int elementId) throws NoScoreFoundException;
	
	/**
	 * Get the feature scores for the given element, filtered by according to filterType and filterVal:
	 * FilterType.ALL: no filtering
	 * FilterType.MIN_VAL: leaved out scores that are equal or greater than filterVal
	 * FilterType.TOP_N: leaved out top filterVal scores
	 * FilterType.TOP_PERCENT: leaved out top filterVal percent of scores
	 * 
	 * @param elementId the id of the element
	 * @param filterType the type of the filtering, e.g., TOP_N, MIN_VAL
	 * @param filterVal the value criterion of the filtering
	 * @return the filtered feature scores
	 * @throws NoScoreFoundException 
	 */
	ImmutableIterator<FeatureScore> getElementFeatureScores(int elementId,FilterType filterType, double filterVal) throws NoScoreFoundException;
	
	
	/**
	 * 
	 * Get all element-feature scores
	 * 
	 * @return an iterator for the whole set of element-feature scores 
	 */
	ImmutableIterator<ElementFeatureScores> getElementsFeatureScores();
	

	/**
	 * 
	 * Get the elements' feature scores, filtered by filterType and filterVal:
	 * FilterType.ALL: no filtering
	 * FilterType.MIN_VAL: leaved out score that are equal or greater than filterVal, for each element
	 * FilterType.TOP_N: leaved out top filterVal feature scores, for each element
	 * FilterType.TOP_PERCENT: leaved out top filterVal percent of feature scores, for each element
     *
	 * @param filterType the type of the filtering, e.g., TOP_N, MIN_VAL
	 * @param filterVal the value criterion of the filtering
	 * @return an iterator for the elements' filtered feature scores
	 */
	ImmutableIterator<ElementFeatureScores> getElementsFeatureScores(FilterType filterType, double filterVal);


	
	/**
	 * Get the score of a given feature of a given element 
	 * 
	 * @param elementId the id of the element
	 * @return the score of the given element
	 * 
	 * @throws NoScoreFoundException if no score found in the storage for the given element
	 */
	double getElementScore(int elementId) throws NoScoreFoundException;
	

	/**
	 * 
	 * Get the score of all elements
	 * 
	 * @return an iterator for the whole set of element scores
	 */
	ImmutableIterator<ElementScore> getElementScores();
	
	/**
	 * 
	 * Get all elements of the given feature
	 * 
	 * @param featureId a unique id of a feature
	 * @return a list of element ids that relate to the given feature 
	 * @throws NoElementFoundException 
	 * @throws BasicMapException 
	 */
	BasicSet<Integer> getFeatureElements(int featureId) throws NoElementFoundException, BasicMapException;
}
