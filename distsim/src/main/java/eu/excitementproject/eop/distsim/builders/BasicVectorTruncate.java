/**
 * 
 */
package eu.excitementproject.eop.distsim.builders;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Configuration;

/**
 * Truncates vectors according to a given percent of elements and maximal size of vectors 
 * 
 * @author Meni Adler
 * @since 05/09/2012
 *
 */
public class BasicVectorTruncate implements VectorTruncate {


	
	public BasicVectorTruncate(int topN) {
		this(1, topN, Double.MIN_VALUE);
	}

	
	public BasicVectorTruncate(double percent, int topN) {
		this(percent, topN, Double.MIN_VALUE);
	}
	
	/**
	 * @param percent a number between 0 and 1
	 * @param topN
	 * @param minScore
	 */
	public BasicVectorTruncate(double percent, int topN, double minScore) {
		init(percent, topN, minScore);
	}
	
	public BasicVectorTruncate(ConfigurationParams params) throws ConfigurationException {
		double minScore = Double.MIN_VALUE;
		try {
			minScore = params.getDouble(Configuration.MIN_SCORE); 
		} catch (ConfigurationException e) {
		}
		
		double percent = 1;
		try {
			percent = params.getDouble(Configuration.PERCENT); 
		} catch (ConfigurationException e) {
			
		}
		
		init(percent,params.getInt(Configuration.TOPN),minScore);
	}
	
	protected void init(double percent, int topN, double minScore) {
		if (percent <  0 || percent > 1)
			throw new IllegalArgumentException("the percent parameter value should be at the range [0,1]");
		this.percent = percent;
		this.topN = topN;
		this.minScore = minScore;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.scoring.VectorTruncater#truncate(java.util.LinkedHashMap)
	 */
	@Override
	public LinkedHashMap<Integer, Double> truncate(LinkedHashMap<Integer, Double> sortedScores) {
		if (percent >= 1 && topN >= sortedScores.size() && minScore == Double.MIN_VALUE) {
			return sortedScores;
		}		
		LinkedHashMap<Integer, Double> ret = new LinkedHashMap<Integer, Double>();
		int numOfItems = (int)Math.min(topN, Math.ceil(sortedScores.size()*percent));
		
		int i=1;
		for (Entry<Integer, Double> entry : sortedScores.entrySet()) {
			if (i > numOfItems || entry.getValue() < minScore)
				break;
			ret.put(entry.getKey(),entry.getValue());
			i++;
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "topN = " + topN + ", percent = " + percent + ", min score = " + minScore;
	}
	
	protected double percent;
	protected int topN;
	protected double minScore;

}
