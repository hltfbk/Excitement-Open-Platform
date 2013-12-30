package eu.excitementproject.eop.distsim.scoring.similarity;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.scoring.FeatureScore;

/**
  * The ElementSimilarityScoring interface defines the similarity measurement between two directed elements ('left' and 'right')  
 * according to their feature vector scores.
 *  
 * @author Meni Adler
 * @since 29/03/2012
 *
 */
public interface ElementSimilarityScoring {
	
	/**
	 * Add the score of one feature of a given left and right elements to the given numerator
	 * 
	 * @param leftElementFeatureScore a feature score of a left element
	 * @param rightElementFeatureScore a feature score of a right element
	 */
	void addElementFeatureScore(double leftElementFeatureScore, double rightElementFeatureScore);
	
	
	/**
	 * Calculate the similarity score for two elements, according to their combined feature-based numerator, and their given denominators (usually their element scores)
	 * 
	 * @param leftDenominator a denominator for the left element
	 * @param rightDenominator a denominator for the right element
	 * @return the resulted similarity score
	 */
	double getSimilarityScore(double leftDenominator, double rightDenominator);
	
	/**
	 * Set the total number of features for the elements (in contrast to the features added by the addElementFeatureScore, which may refer only to common left-right features) 
	 * 
	 * @param totalLeftFeaturesNum the total number of features in the left element
	 * @param totalRightFeaturesNum the total number of features in the right element
	 * 
	 */
	void setTotalFeaturesNum(int totalLeftFeaturesNum, int totalRightFeaturesNum);
	
	/**
	 * Get the similarity score for a given element pair, based on their feature scores
	 * 
	 * @param leftFeatures features of left element
	 * @param rightFeatures feature of right element
	 * @return a similarity score for the given two elements
	 */
	double getSimilarity(ImmutableIterator<FeatureScore> leftFeatures, ImmutableIterator<FeatureScore> rightFeatures);
}
