package eu.excitementproject.eop.distsim.scoring.feature;


import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.scoring.ScoringException;

/**
 * Defines the score for a given feature and element according to their PMI value
 * See: http://www.ling.uni-potsdam.de/~gerlof/docs/npmi-pfd.pdf, Equation 5
 * 
 * <P>
 * Stateless. Thread-safe

 * @author Meni Adler
 * @since 28/03/2012
 *
 */
public class PMI implements FeatureScoring {

	public PMI() {}
	
	public PMI(ConfigurationParams params) {
		this();
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.feature.FeatureScoring#score(org.excitement.distsim.items.Element, org.excitement.distsim.items.Feature, double, double)
	 */
	@Override
	public double score(Element element, Feature feature, double totalElementCount, double jointCount) throws ScoringException {
		
		try {
			double score = Math.log(totalElementCount);
			score -= Math.log(element.getCount());
			score -= Math.log(feature.getCount());
			score += Math.log(jointCount);
	
			//debug
			//System.out.println("totalElementCount = " + totalElementCount);
			
			if(score <= 0) 
				return 0;
	
			return score;
		} catch (Exception e) {
			throw new ScoringException(e);	
		}
	}

}
