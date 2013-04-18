package eu.excitementproject.eop.distsim.builders.elementfeature;

import java.util.List;

import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * The ElementFeatureExtraction interface defines the construction of elements and features, based on a given co-occurence
 * 
 * @author Meni Adler
 * @since 04/09/2012
 *
 */
public interface ElementFeatureExtraction {
	/**
	 * Extracts pairs of element and feature from a given co-occurrence
	 * 
	 * @param cooccurrence a co-occurrence, composed of two text units and their relation
	 * @return an extracted pair of element and feature
	 * @throws ElementFeatureExtractionException 
	 */
	List<Pair<Element,Feature>> extractElementsFeature(Cooccurrence<?> cooccurrence) throws ElementFeatureExtractionException;
	
	/**
	 * Decides whether a given element is relevant for similarity calculation.
	 * For example, the reversed predicates of Dirt can be omitted at the final similarity calculation
	 * 
	 * @param elementId id of element to be determined whether it is relevant or not
	 * @return true if the given element is relevant for similarity calculation
	 */
	boolean isRelevantElementForCalculation(int elementId);
}
