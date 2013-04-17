/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring.feature;


import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.scoring.ScoringException;

/**
 * A simple implementation of the FeatureScoring interface, based on the joint count of the feature and the element
 * 
 * 
 * <P>
 * Stateless. Thread-safe
 * 
 * 
 * @author Meni Adler
 * @since 13/09/2012
 *
 */
public class Count implements FeatureScoring {

	public Count() {}
	
	public Count(ConfigurationParams params) {
		this();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.feature.FeatureScoring#score(org.excitement.distsim.items.Element, org.excitement.distsim.items.Feature, double, double)
	 */
	@Override
	public double score(Element element, Feature feature, double totalElementCount, double jointCount) throws ScoringException {
		return jointCount;
	}

}
