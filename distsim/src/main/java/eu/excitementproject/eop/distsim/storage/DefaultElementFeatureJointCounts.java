package eu.excitementproject.eop.distsim.storage;


import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.storage.iterators.FeatureCountIterator;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * A simple implementation of the {@link DefaultElementFeatureJointCounts} interface
 * 
 * <p>
 * Immutable. Thread-safe
 * 
 * 
 * @author Meni Adler
 * @since 15/08/2012
 *
 */
public class DefaultElementFeatureJointCounts implements ElementFeatureJointCounts {

	public DefaultElementFeatureJointCounts(int elementId, ImmutableIterator<Pair<Integer, Double>> featureCounts) {
		this.elementId = elementId;
		this.featureCounts = new FeatureCountIterator(featureCounts);
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureJointCount#getElementId()
	 */
	@Override
	public int getElementId() {
		return elementId;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureJointCount#getFeatureCounts()
	 */
	@Override
	public ImmutableIterator<FeatureCount> getFeatureCounts() {
		return featureCounts;
	}
	
	protected final int elementId;
	protected final ImmutableIterator<FeatureCount> featureCounts;

}
