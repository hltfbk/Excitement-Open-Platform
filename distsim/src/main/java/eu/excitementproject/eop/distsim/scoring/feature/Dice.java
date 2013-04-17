package eu.excitementproject.eop.distsim.scoring.feature;


import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.InvalidCountException;
import eu.excitementproject.eop.distsim.scoring.ScoringException;


/**
 * FeatureScoring implementation, based on Dice Coefficient (Weeds and Weir 1992).
 * http://acl.ldc.upenn.edu/J/J05/J05-4002.pdf, Section 4.1 
 * 
 * <P>
 * Stateless. Thread-safe
 * 
 * 
 * @author Meni Adler
 * @since 23/09/2012
 *
 */
public class Dice implements FeatureScoring {

	public Dice() {}
	
	public Dice(ConfigurationParams params) {
		this();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.feature.FeatureScoring#score(org.excitement.distsim.items.Element, org.excitement.distsim.items.Feature, double, double)
	 */
	@Override
	public double score(Element element, Feature feature, double totalElementCount, double jointCount) throws ScoringException {
		
		try {
			return (2 * jointCount) / (element.getCount() + feature.getCount());
		} catch (InvalidCountException e) {
			throw new ScoringException(e);
		}
		
	}
}
