package eu.excitementproject.eop.distsim.scoring;


/**
 * 
 * A simple implementation of the {@link FeatureScore} interface
 * 
 * <P>
 * Immutable. Thread-safe
 * 
 * @author Meni Adler
 * @since 16/08/2012
 *
 */
public class DefaultFeatureScore implements FeatureScore {

	public DefaultFeatureScore(int featureId, double score) {
		this.featureId = featureId;
		this.score = score;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.FeatureScore#getFeatureId()
	 */
	@Override
	public int getFeatureId() {
		return featureId;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.FeatureScore#getScore()
	 */
	@Override
	public double getScore() {
		return score;
	}

	protected final int featureId;
	protected final double score;
}
