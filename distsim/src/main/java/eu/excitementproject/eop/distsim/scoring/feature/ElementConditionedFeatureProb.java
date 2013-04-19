package eu.excitementproject.eop.distsim.scoring.feature;


import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.InvalidCountException;
import eu.excitementproject.eop.distsim.scoring.ScoringException;



/**
 * An implementation of FeatureScorer interface, according to <i>P(feature|element)</i> probability
 * <p>
 * Stateless, Thread-safe
 *
 * @author Meni Adler
 * @since 28/03/2012
 *
 */
public class ElementConditionedFeatureProb implements FeatureScoring {

	public ElementConditionedFeatureProb() {}
	
	public ElementConditionedFeatureProb(ConfigurationParams params) {
		this();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.feature.FeatureScoring#score(org.excitement.distsim.items.Element, org.excitement.distsim.items.Feature, double, double)
	 */
	@Override
	public double score(Element element, Feature feature,double totalElementCount, double jointCount) throws ScoringException {
		try {
			return jointCount / element.getCount();
		} catch (InvalidCountException e) {
			throw new ScoringException(e);
		}
	}
	
}
