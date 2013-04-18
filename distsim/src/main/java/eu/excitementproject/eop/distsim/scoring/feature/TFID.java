package eu.excitementproject.eop.distsim.scoring.feature;



import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.InvalidCountException;
import eu.excitementproject.eop.distsim.scoring.ScoringException;


/**
 * 
 * Scores a given feature of a given element according to their tf-idf value
 * See: http://en.wikipedia.org/wiki/Tf%E2%80%93idf
 * 
 * <P>
 * Stateless. Thread-safe
 *
 * @author Meni Adler
 * @since 28/03/2012
 *
 */
public class TFID implements FeatureScoring {

	public TFID() {}
	
	public TFID(ConfigurationParams params) {
		this();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.feature.FeatureScoring#score(org.excitement.distsim.items.Element, org.excitement.distsim.items.Feature, double, double)
	 */
	@Override
	public double score(Element element, Feature feature,double totalElementCount, double jointCount) throws ScoringException {
		try {
			return jointCount / (1.0 + Math.log(feature.getCount()));
		} catch (InvalidCountException e) {
			throw new ScoringException(e);
		}
	}

}
