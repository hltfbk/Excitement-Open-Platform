/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring.combine;

import java.util.List;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;


/**
 * Combines two given scores by their geometric mean
 * 
 * @author Meni Adler
 * @since 18/04/2012
 *
 * <P>
 * Stateless. Thread-safe
 */
public class GeometricMean implements SimilarityCombination {

	public GeometricMean() {}
	
	public GeometricMean(ConfigurationParams params) {
		this();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.combine.SimilarityCombination#combine(java.util.List)
	 */
	@Override
	public double combine(List<Double> scores, int requiredScoreNum) throws IlegalScoresException {
		if(scores.size() > requiredScoreNum)
			throw new IlegalScoresException("Number of variable must not exceed " + requiredScoreNum);
		if(scores.size() < requiredScoreNum)
			return 0;	

		double ret=1;		
		for (double score : scores)
			ret *= score;
		ret = Math.sqrt(ret);

		return ret;
	}
}
