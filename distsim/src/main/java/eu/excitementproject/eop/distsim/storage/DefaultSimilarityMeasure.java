/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;


import eu.excitementproject.eop.distsim.items.AdditionalInfo;
import eu.excitementproject.eop.distsim.scoring.SimilarityMeasure;

/**
 *  A simple field-based implementation of the {@link SimilarityMeasure} interface
 *  
 * @author Meni Adler
 * @since 09/09/2012
 *
 * Immutable
 */
public class DefaultSimilarityMeasure implements SimilarityMeasure {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DefaultSimilarityMeasure(double score, AdditionalInfo additionalInfo) {
		this.score = score;
		this.additionalInfo = additionalInfo;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.SimilarityMeasure#getSimilarityMeasure()
	 */
	@Override
	public double getSimilarityMeasure() {
		return score;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.SimilarityMeasure#getAdditionalInfo()
	 */
	@Override
	public AdditionalInfo getAdditionalInfo() {
		return additionalInfo;
	}
	
	protected final double score;
	protected final AdditionalInfo additionalInfo;

}
