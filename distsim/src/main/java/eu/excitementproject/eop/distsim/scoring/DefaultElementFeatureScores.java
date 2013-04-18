package eu.excitementproject.eop.distsim.scoring;

import java.util.LinkedHashMap;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.storage.iterators.FeatureScoreIterator;


/**
 * A simple implementation of the {@link ElementFeatureScores} interface
 * 
 * <P>Immutable. Thread-safe
 *
 * @author Meni Adler
 * @since 21/06/2012
 *
 */
public class DefaultElementFeatureScores implements ElementFeatureScores {
	
	public DefaultElementFeatureScores(int elementId,LinkedHashMap<Integer, Double> featureScores) {
		this(elementId, new FeatureScoreIterator(featureScores), featureScores.size());
	}
	
	public DefaultElementFeatureScores(int elementId, FeatureScoreIterator featureScores, int featureScoresNum) {
		this.elementId= elementId;
		this.featureScores = featureScores;		
		this.featureScoresNum = featureScoresNum;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.db.ElementFeatureScore#getElementId()
	 */
	@Override
	public int getElementId() {
		return elementId;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.ElementFeatureScores#getFeatureScores()
	 */
	@Override
	public ImmutableIterator<FeatureScore> getFeatureScores() {
		return featureScores;
	}


	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.ElementFeatureScores#getFeatureScoresNum()
	 */
	@Override
	public int getFeatureScoresNum() {
		return featureScoresNum;
	}

	protected final int elementId;
	protected final ImmutableIterator<FeatureScore> featureScores;
	protected int featureScoresNum;

}
