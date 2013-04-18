package eu.excitementproject.eop.distsim.scoring.element;

import java.util.Collection;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;



/**
 * Defines the normalization value of a given element, as the L2 norm of its features' scores
 * See: http://mathworld.wolfram.com/L2-Norm.html
 * 
 * <P>
 * Stateless, Thread-safe

 * @author Meni Adler
 * @since 29/03/2012
 *
 *
 */
public class L2Norm implements ElementScoring {

	public L2Norm() {}
	
	public L2Norm(ConfigurationParams params) {
		this();
	}

	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.element.ElementScoring#score(java.util.Collection)
	 */
	@Override
	public double score(Collection<Double> featureScores) {
		double sum=0;
		for (Double featureScore : featureScores) 
			sum += (featureScore * featureScore);
		return sum;
	}

}
