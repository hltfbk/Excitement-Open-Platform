package eu.excitementproject.eop.distsim.scoring;


/**
 * 
 * A simple implementation of the {@link ElementScore} interface
 * 
 * <P>
 * Immutable. Thread-safe
 * 
 * @author Meni Adler
 * @since 16/08/2012
 *
 */
public class DefaultElementScore implements ElementScore {

	public DefaultElementScore(int elementId, double score) {
		this.elementId = elementId;
		this.score = score;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.ElementScore#getFeatureId()
	 */
	@Override
	public int getElementId() {
		return elementId;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.ElementScore#getScore()
	 */
	@Override
	public double getScore() {
		return score;
	}

	protected final int elementId;
	protected final double score;
}
