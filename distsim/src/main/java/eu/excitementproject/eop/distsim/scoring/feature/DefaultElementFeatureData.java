/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring.feature;

/**
 * A simple field-based implementation of the {@link ElementFeatureData} interface
 * 
 * 
 * Immutable, Thread-safe
 * 
 * @author Meni Adler
 * @since 05/11/2012
 *
 */
public class DefaultElementFeatureData implements ElementFeatureData {

	public DefaultElementFeatureData(double value, int rank, int size) {
		this.value = value;
		this.rank = rank;
		this.size = size;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.feature.ElementFeatureData#getValue()
	 */
	@Override
	public double getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.feature.ElementFeatureData#getRank()
	 */
	@Override
	public double getRank() {
		return rank;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.feature.ElementFeatureData#getSize()
	 */
	@Override
	public double getSize() {
		return size;
	}

	@Override
	public String toString() {
		return "<value: " + value + ", rank: " + rank + ", size: " + size + ">";
	}
	
	protected final double value;
	protected final int rank;
	protected final int size;
}
