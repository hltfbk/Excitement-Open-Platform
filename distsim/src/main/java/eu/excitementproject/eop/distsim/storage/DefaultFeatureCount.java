package eu.excitementproject.eop.distsim.storage;

/**
 *  A simple field-based implementation of the {@link FeatureCount} interface
 * 
 * @author Meni Adler
 * @since 26/12/2012
 *
 */
public class DefaultFeatureCount implements FeatureCount {
	
	public DefaultFeatureCount(int featureId, double count) {
		this.featureId = featureId;
		this.count = count;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.FeatureCount#getFeatureId()
	 */
	@Override
	public int getFeatureId() {
		return featureId;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.FeatureCount#getCount()
	 */
	@Override
	public double getCount() {
		return count;
	}
	
	protected final int featureId;
	protected final double count;

}
