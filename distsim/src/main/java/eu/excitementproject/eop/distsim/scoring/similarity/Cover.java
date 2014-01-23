package eu.excitementproject.eop.distsim.scoring.similarity;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.scoring.FeatureScore;

/**
 *
 * The cover measure quantifies the relative degree by which the contexts of u are included in those of v
 * See: http://eprints.pascal-network.org/archive/00004483/01/C08-1107.pdf
 * 
 * @author Meni Adler
 * @since 17/04/2012
 *
 * <P>
 * Thread-safe
 */
public class Cover extends AbstractElementSimilarityScoring {

	public Cover(ConfigurationParams params) {
		this();
	}

	public Cover() {
		sum = 0;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.ElementSimilarityScoring#addElementFeatureScore(double, double, org.excitement.distsim.scoring.similarity.Numerator)
	 */
	@Override
	public void addElementFeatureScore(double leftElementFeatureScore,double rightElementFeatureScore) {
		if(leftElementFeatureScore > 0 && rightElementFeatureScore > 0)
			sum += (rightElementFeatureScore);
		
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.ElementSimilarityScoring#getSimilarityScore(org.excitement.distsim.scoring.similarity.Numerator, double, double)
	 */
	@Override
	public double getSimilarityScore(double leftDenominator, double rightDenominator) {
		return sum / rightDenominator;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.ElementSimilarityScoring#getSimilarity(ac.biu.nlp.nlp.general.immutable.ImmutableIterator, ac.biu.nlp.nlp.general.immutable.ImmutableIterator)
	 */
	@Override
	public double getSimilarity(ImmutableIterator<FeatureScore> leftFeatures, ImmutableIterator<FeatureScore> rightFeatures) {
		throw new UnsupportedOperationException();
	}
	
	protected double sum;
}
