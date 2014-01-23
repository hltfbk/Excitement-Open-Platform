package eu.excitementproject.eop.distsim.scoring.similarity;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.scoring.FeatureScore;

/**
 * Lin similarity of two feature vectors
 * See: http://acl.ldc.upenn.edu/J/J05/J05-4002.pdf, Section 4.6
 * 
 * @author Meni Adler
 * @since 17/04/2012
 *
 * <P>
 * Thread-safe
 */
public class Lin extends AbstractElementSimilarityScoring {

	public Lin(ConfigurationParams params) {
		this();
	}

	public Lin() {
		sum = 0;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.ElementSimilarityScoring#addElementFeatureScore(double, double, org.excitement.distsim.scoring.similarity.Numerator)
	 */
	@Override
	public void addElementFeatureScore(double leftElementFeatureScore, double rightElementFeatureScore) {
		if(leftElementFeatureScore > 0 && rightElementFeatureScore > 0)
			sum += (leftElementFeatureScore + rightElementFeatureScore);		
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.ElementSimilarityScoring#getSimilarityScore(org.excitement.distsim.scoring.similarity.Numerator, double, double)
	 */
	@Override
	public double getSimilarityScore(double leftDenominator, double rightDenominator) {
		return sum / (leftDenominator + rightDenominator);
	}

	@Override
	public double getSimilarity(ImmutableIterator<FeatureScore> leftFeatures, ImmutableIterator<FeatureScore> rightFeatures) {
		throw new UnsupportedOperationException();
	}
	

	
	protected double sum;
}
