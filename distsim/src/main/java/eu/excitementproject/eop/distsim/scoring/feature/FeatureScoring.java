package eu.excitementproject.eop.distsim.scoring.feature;


import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.scoring.ScoringException;


/**
 * The FeatureScorer interface gives a weight to a given <i>feature</i> of a given <i>element<i/>
 * 
 * @author Meni Adler
 * @since 20/03/2012
 *
 */ 
public interface FeatureScoring {

	/**
	 * Measures a scoring weight for a given feature of an element, based on their general, total, and join counts
	 * 
	 * 
	 * @param element an element with count 
	 * @param feature a feature  with count
	 * @param totalElementCount the total count of elements in the domain 
	 * @param jointCount the joint count of the given element and the given feature
	 * @return a weight for the given pair of element and feature, based on the given counts
	 */
	double score(Element element, Feature feature, final double totalElementCount, final double jointCount) throws ScoringException;
}
  