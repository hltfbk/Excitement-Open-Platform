package eu.excitementproject.eop.distsim.storage;

import java.util.List;

import eu.excitementproject.eop.common.component.Component;
import eu.excitementproject.eop.distsim.domains.FilterType;
import eu.excitementproject.eop.distsim.domains.RuleDirection;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.scoring.ElementsSimilarityMeasure;
import eu.excitementproject.eop.distsim.scoring.similarity.ElementSimilarityScoring;

/**
 * The similarity storage contains similarity scores for element pairs.
 * 
 * @author Meni Adler
 * @since 24/05/2012
 *
 *  
 */
public interface SimilarityStorage extends Component  {
	
	/**
	 * Get all relevant similarity scores for the given pair of elements
	 * 
	 * @param leftElement  the first element of the similarity measurement 
	 * @param rightElement the second element of the similarity measurement 
	 * @return the similarity score for the given leftElement and rightElement
	 * 
	 * @throws SimilarityNotFoundException in case the given elements are absent form the similarity db.
	 */
	List<ElementsSimilarityMeasure> getSimilarityMeasure(Element leftElement, Element rightElement) 
		throws SimilarityNotFoundException;
	
	/**
	 * Get all relevant similarity scores for the given element
	 * 
	 * @param element the left/right (defined by ruleDirection parameter) element of the similarity measurement
     *
	 * @param ruleDirection define the position of the given element:
	 * RuleDirection.LEFT_TO_RIGHT: left
	 * RuleDirection.RIGHT_TO_LEFT: right
	 * The returned element similarities are determined accordingly
	 * RuleDirection.LEFT_TO_RIGHT: right
	 * RuleDirection.RIGHT_TO_LEFT: left
	 * 
	 * @return a list of all elements, which are assigned to a similarity score with the given element, 
	 * with their similarity score and additional info, ordered by their scores (descending)
	 * 
     * @throws SimilarityNotFoundException in case the given element is absent form the similarity db. 
	 */
	List<ElementsSimilarityMeasure> getSimilarityMeasure(Element element, RuleDirection ruleDirection) throws SimilarityNotFoundException;
	

	/**
	 * Get all relevant similarity scores for the given element
	 * In case the given element is missing from the similarity db, the similarity is computed on the fly
	 * according to the element feature vector, given by the elementFeatureScores parameter
	 * 
	 * @param element the left/right (defined by ruleDirection parameter) element of the similarity measurement
     *
	 * @param ruleDirection define the position of the given element:
	 * RuleDirection.LEFT_TO_RIGHT: left
	 * RuleDirection.RIGHT_TO_LEFT: right
	 * The returned element similarities are determined accordingly
	 * RuleDirection.LEFT_TO_RIGHT: right
	 * RuleDirection.RIGHT_TO_LEFT: left
     *
	 * @param elementFeatureScores a DB composed of feature vector for each element - to be used on the fly, in case the given element is absent from the similarity db   
	 * @return a list of all elements, which are assigned to a similarity score with the given element, 
	 * with their similarity score and additional info, ordered by their scores (descending)
	 * 
	 * @throws SimilarityNotFoundException in case the given element is absent form both similarity and score dbs. 
	 */
	List<ElementsSimilarityMeasure> getSimilarityMeasure(Element element, RuleDirection ruleDirection, 
			ElementFeatureScoreStorage elementFeatureScores,
			ElementSimilarityScoring elementSimilarityScoring) throws SimilarityNotFoundException;


	
	/**
	 * Get the relevant similarity scores for the given element, filtered by filterType and filterVal parameters:
 	 * FilterType.ALL: no filtering
	 * FilterType.MIN_VAL: leaved out scores that are equal or greater than filterVal
	 * FilterType.TOP_N: leaved out top filterVal scores
	 * FilterType.TOP_PERCENT: leaved out top filterVal percent of scores
	 * 
	 * @param element the left/right (defined by ruleDirection parameter) element of the similarity measurement
     *
	 * @param ruleDirection define the position of the given element:
	 * RuleDirection.LEFT_TO_RIGHT: left
	 * RuleDirection.RIGHT_TO_LEFT: right
	 * The returned element similarities are determined accordingly
	 * RuleDirection.LEFT_TO_RIGHT: right
	 * RuleDirection.RIGHT_TO_LEFT: left
	 *
	 * @param filterType the type of the filtering, e.g., TOP_N, MIN_VAL
	 * @param filterVal the value criterion of the filtering
     *
	 * @return a list of the filtered elements, which are assigned to a similarity score with the given element, 
	 * with their similarity score and additional info, ordered by their scores (descending)
	 * 
     * @throws SimilarityNotFoundException in case the given element is absent form the similarity db. 
	 */
	List<ElementsSimilarityMeasure> getSimilarityMeasure(Element element, RuleDirection ruleDirection, FilterType filterType, double filterVal) throws SimilarityNotFoundException;
	

	/**
	 * Get the relevant similarity scores for the given element, filtered by filterType and filterVal parameters:
 	 * FilterType.ALL: no filtering
	 * FilterType.MIN_VAL: leaved out scores that are equal or greater than filterVal
	 * FilterType.TOP_N: leaved out top filterVal scores
	 * FilterType.TOP_PERCENT: leaved out top filterVal percent of scores
     *
	 * In case the given element is missing from the similarity db, the similarity is computed on the fly
	 * according to the element feature vector, given by the elementFeatureScores parameter
	 * 
	 * @param element the left/right (defined by ruleDirection parameter) element of the similarity measurement
     *
	 * @param ruleDirection define the position of the given element:
	 * RuleDirection.LEFT_TO_RIGHT: left
	 * RuleDirection.RIGHT_TO_LEFT: right
	 * The returned element similarities are determined accordingly
	 * RuleDirection.LEFT_TO_RIGHT: right
	 * RuleDirection.RIGHT_TO_LEFT: left
     *
	 * @param elementFeatureScores a DB composed of feature vector for each element - to be used on the fly, in case the given element is absent from the similarity db
	 *
	 * @param filterType the type of the filtering, e.g., TOP_N, MIN_VAL
	 * @param filterVal the value criterion of the filtering
     *
	 * @return a list of the filtered elements, which are assigned to a similarity score with the given element, 
	 * with their similarity score and additional info, ordered by their scores (descending)
	 * 
	 * @throws SimilarityNotFoundException in case the given element is absent form both similarity and score dbs. 
	 */
	List<ElementsSimilarityMeasure> getSimilarityMeasure(Element element, RuleDirection ruleDirection, ElementFeatureScoreStorage elementFeatureScores, ElementSimilarityScoring elementSimilarityScoring, FilterType filterType, double filterVal) throws SimilarityNotFoundException;
	
	/**
	 * Close and release the storage resources 
	 */
	void close();

}
