package eu.excitementproject.eop.distsim.storage;

import java.util.List;

import eu.excitementproject.eop.distsim.domains.FilterType;
import eu.excitementproject.eop.distsim.domains.RuleDirection;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.scoring.ElementSimilarityMeasure;
import eu.excitementproject.eop.distsim.scoring.ElementsSimilarityMeasure;
import eu.excitementproject.eop.distsim.scoring.SimilarityMeasure;
import eu.excitementproject.eop.distsim.scoring.similarity.ElementSimilarityScoring;

/**
 * The similarity storage contains similarity scores for element pairs.
 * 
 * @author Meni Adler
 * @since 24/05/2012
 *
 *  
 */
public interface SimilarityStorage  extends Persistence {
	
	/**
	 * Get the similarity score for the given two directed elements
	 * 
	 * @param leftElement  the first element of the similarity measurement 
	 * @param rightElement the second element of the similarity measurement 
	 * @return the similarity score for the given leftElement and rightElement
	 * 
	 * @throws SimilarityNotFoundException in case the given elements are absent form the similarity db.
	 */
	SimilarityMeasure getSimilarityMeasure(Element leftElement, Element rightElement) 
		throws SimilarityNotFoundException;
	
	/**
	 * Get the similarity score for the given two directed elements. 
	 * In case the given elements do not appear in the SimilarityStorage, the similarity is computed
	 * on the fly, according to their feature vectors, given by the <i>elementFeatureScores</i> parameter
	 * 
	 * @param leftElement  the first element of the similarity measurement 
	 * @param rightElement the second element of the similarity measurement
	 * @param elementFeatureScores a DB composed of feature vector for each element - to be used on the fly, in case the given elements have no similarity score 
	 * @return the similarity score for the given leftElement and rightElement
	 * 
	 * @throws SimilarityNotFoundException in case the given elements are absent form both similarity and score dbs. 
	 */
	 SimilarityMeasure getSimilarityMeasure(Element leftElement, Element rightElement, ElementFeatureScoreStorage elementFeatureScores, ElementSimilarityScoring elementSimilarityScoring) 
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
	List<ElementSimilarityMeasure> getSimilarityMeasure(Element element, RuleDirection ruleDirection) throws SimilarityNotFoundException;
	

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
	List<ElementSimilarityMeasure> getSimilarityMeasure(Element element, RuleDirection ruleDirection, 
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
	List<ElementSimilarityMeasure> getSimilarityMeasure(Element element, RuleDirection ruleDirection, FilterType filterType, double filterVal) throws SimilarityNotFoundException;
	

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
	List<ElementSimilarityMeasure> getSimilarityMeasure(Element element, RuleDirection ruleDirection, ElementFeatureScoreStorage elementFeatureScores, ElementSimilarityScoring elementSimilarityScoring, FilterType filterType, double filterVal) throws SimilarityNotFoundException;


	
	
	/**
	 * Get the relevant similarity scores for given elements, defined by a regular expression of their string key, 
     *
	 * @param leftElementRegExp a regular expression for the left element string-key
     * @param rightElementRegExp a regular expression for the right element string-key
     *
	 * @return a list of similarity rules for the elements that matched the given right and left regular expressions, 
	 * with their similarity score and additional info, ordered by their scores (descending)
	 * 
     * @throws SimilarityNotFoundException in case the given element is absent form the similarity db. 
	 */
	List<ElementsSimilarityMeasure> getSimilarityMeasure(String leftElementRegExp, String rightElementRegExp)  throws SimilarityNotFoundException;
	
	

	/**
	 * Get the relevant similarity scores for given elements, defined by a regular expression of their string key, 
	 * the similarities for each matched element are filtered by filterType and filterVal parameters:
 	 * FilterType.ALL: no filtering
	 * FilterType.MIN_VAL: leaved out scores that are equal or greater than filterVal
	 * FilterType.TOP_N: leaved out top filterVal scores
	 * FilterType.TOP_PERCENT: leaved out top filterVal percent of scores
     *
	 * @param leftElementRegExp a regular expression for the left element string-key
     * @param rightElementRegExp a regular expression for the right element string-key
	 * @param filterType the type of the filtering, e.g., TOP_N, MIN_VAL
	 * @param filterVal the value criterion of the filtering
     *
	 * @return a list of the filtered elements, which are assigned to a similarity score according to the given regular expressions, 
	 * with their similarity score and additional info, ordered by their scores (descending)
	 * 
     * @throws SimilarityNotFoundException in case the given element is absent form the similarity db. 
	 */
	List<ElementsSimilarityMeasure> getSimilarityMeasure(String leftElementRegExp, String rightElementRegExp, FilterType filterType, double filterVal)  throws SimilarityNotFoundException;

	/**
	 * Get all relevant similarity scores for given elements, defined by a regular expression of their string key
	 * 
	 * @param elementRegExp a regular expression for the left/right (defined by ruleDirection parameter) element string-key
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
	List<ElementsSimilarityMeasure> getSimilarityMeasure(String elementRegExp, RuleDirection ruleDirection) throws SimilarityNotFoundException;

	/**
	 * Get the relevant similarity scores for given elements, defined by a regular expression of their string key, 
	 * the similarities for each matched element are filtered by filterType and filterVal parameters:
 	 * FilterType.ALL: no filtering
	 * FilterType.MIN_VAL: leaved out scores that are equal or greater than filterVal
	 * FilterType.TOP_N: leaved out top filterVal scores
	 * FilterType.TOP_PERCENT: leaved out top filterVal percent of scores
     *
	 * @param elementRegExp a regular expression for the left/right (defined by ruleDirection parameter) element string-key
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
	List<ElementsSimilarityMeasure> getSimilarityMeasure(String elementRegExp, RuleDirection ruleDirection, FilterType filterType, double filterVal)  throws SimilarityNotFoundException;
	

	/**
	 * Gets the name assigned to this knowledge (for identification and/or description)
	 * 
	 * @return the name of the knowledge resource
	 */
	String getResourceName(); 
}
