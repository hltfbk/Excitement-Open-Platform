package eu.excitementproject.eop.distsim.scoring.element;

import java.util.Collection;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;


/**
 * Defines the normalization value of a given element, as the sum of its features' scores
 * 
 * <P>
 * Stateless, Thread-safe
 * 
 * 
 * @author Meni Adler
 * @since 23/09/2012
 *
 */
public class L1Norm implements ElementScoring {

	public L1Norm() {}
	
	public L1Norm(ConfigurationParams params) {
		this();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.element.ElementScoring#score(java.util.Collection)
	 */
	@Override
	public double score(Collection<Double> featureScores) {
		double sum=0;
		for (Double featureScore : featureScores) 
			sum += featureScore;
		return sum;
	}

}
