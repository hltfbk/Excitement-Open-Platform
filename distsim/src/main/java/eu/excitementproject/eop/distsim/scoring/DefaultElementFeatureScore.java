package eu.excitementproject.eop.distsim.scoring;


/**
 * A simple implementation of the {@link ElementFeatureScores} interface
 * 
 * <P>Immutable. Thread-safe
 *
 * @author Meni Adler
 * @since 21/06/2012
 *
 */
public class DefaultElementFeatureScore extends DefaultFeatureScore implements ElementFeatureScore {
	
	public DefaultElementFeatureScore(int featureId, int elementId, double score) {
		super(featureId,score);
		this.elementId= elementId;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.db.ElementFeatureScore#getElementId()
	 */
	@Override
	public int getElementId() {
		return elementId;
	}

	protected final int elementId;

}
