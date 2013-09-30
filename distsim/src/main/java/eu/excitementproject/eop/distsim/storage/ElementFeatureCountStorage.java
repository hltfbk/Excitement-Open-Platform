package eu.excitementproject.eop.distsim.storage;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;

/**
 * An interface for accessing a storage of elements and features
 * 
 * @author Meni Adler
 * @since 22/05/2012
 *
 * 
 */
public interface ElementFeatureCountStorage  extends Persistence {
	/**
	 * Get the feature with the given unique id
	 * 
	 * @param featureId a unique id of a feature
	 * @return the matched feature object
	 */
	Feature getFeature(int featureId) throws ItemNotFoundException;

	/**
	 * 
	 * Get the element with the given unique id
	 * 
	 * @param elementId a unique id of an element
	 * @return the matched element object
	 */
	Element getElement(int elementId) throws ItemNotFoundException;
	
	/**
	 * 
	 * Get all elements
	 * 
	 * @return all elements
	 */
	ImmutableIterator<Element> getAllElements();

	/**
	 * 
	 * Get the elements that appear at least minCount times
	 * 
	 * @param minCount minimal number of element occurrences
	 * @return the elements that appear at least minCount times
	 */
	ImmutableIterator<Element> getAllElements(long minCount);
	
	/**
	 * 
	 * Get all features
	 * 
	 * @return all features
	 */
	ImmutableIterator<Feature> getAllFeatures();

	/**
	 * 
	 * Get the features that appear at least minCount times
	 * 
	 * @param minCount minimal number of element occurrences
	 * @return the features that appear at least minCount times
	 */
	ImmutableIterator<Feature> getAllFeatures(long minCount);
	
	/**
	 * Get all features of the given element
	 * 
	 * @param elementId a unique id of an element
	 * @return a list of features for the given element 
	 * @throws NoFeatureFoundException 
	 */
	ImmutableIterator<Feature> getElementFeatures(int elementId) throws NoFeatureFoundException;
	
	/**
	 * Get the features of the given element that appear at least minCount times
	 * 
	 * @param elementId a unique id of an element
	 * @param minCount minimal number of feature occurrences
	 * @return a list of features for the given element which occur at least minCount times
	 * @throws NoFeatureFoundException 
	 */
	ImmutableIterator<Feature> getElementFeatures(int elementId, long minCount) throws NoFeatureFoundException;
	
	/**
	 * Get the whole mapping of features to their elements
	 * 
	 * @return a mapping of fearure-ids to their element lists
	 */
	IDKeyPersistentBasicMap<BasicSet<Integer>> getFeatureElementsMapping();	

	/**
	 * 
	 * Get all elements of the given feature
	 * 
	 * @param featureId a unique id of a feature
	 * @return a list of elements that relate to the given feature 
	 * @throws NoElementFoundException 
	 */
	ImmutableIterator<Element> getFeatureElements(int featureId) throws NoElementFoundException;

	
	/**
	 * 
	 * Get the elements of the given feature that appear at least minCount times
	 * 
	 * @param featureId a unique id of a feature
	 * @param minCount minimal number of element occurrences
	 * @return an iterator for elements that relate to the given feature that appear at least minCount times 
	 * @throws NoElementFoundException 
	 */
	ImmutableIterator<Element> getFeatureElements(int featureId, long minCount) throws NoElementFoundException;
	
	/**
	 * Get all joint counts of elements and features
	 * 
	 * @return an iterator for joint counts of elements and features  
	 */
	ImmutableIterator<ElementFeatureJointCounts> getElementFeatureJointCounts();
	

	/**
	 * Get the joint counts of elements and features that appears at least minCount times
	 * @param minCount minimal number of element-feature occurrences
	 * @return an iterator for joint counts of elements and features that appears at least minCount times  
	 */
	ImmutableIterator<ElementFeatureJointCounts> getElementFeatureJointCounts(long minCount);

	
	/**
	 * Get the total count of all elements
	 * 
	 * @return the total count of all elements
	 */
	double getTotalElementCount();
	
}
