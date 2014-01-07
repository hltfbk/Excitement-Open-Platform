package eu.excitementproject.eop.distsim.scoring.similarity;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.scoring.FeatureScore;

/**
 * Cosine similarity of two feature vectors  
 * 
 * 
 * @author Meni Adler
 * @since 17/04/2012
 *
 */
public class Cosine extends AbstractElementSimilarityScoring {

	public Cosine(ConfigurationParams params) {
		this();
	}

	public Cosine() {
		score = 0;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.ElementSimilarityScoring#addElementFeatureScore(double, double)
	 */
	@Override
	public void addElementFeatureScore(double leftElementFeatureScore, double rightElementFeatureScore) {
		score += (leftElementFeatureScore * rightElementFeatureScore);
		
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.ElementSimilarityScoring#getSimilarityScore(double, double)
	 */
	@Override
	public double getSimilarityScore(double leftDenominator, double rightDenominator) {
		if(score == 0)
			return 0;
		
		return (score / (leftDenominator * rightDenominator));
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.ElementSimilarityScoring#getSimilarity(ac.biu.nlp.nlp.general.immutable.ImmutableIterator, ac.biu.nlp.nlp.general.immutable.ImmutableIterator)
	 */
	@Override
	public double getSimilarity(ImmutableIterator<FeatureScore> leftFeatures,ImmutableIterator<FeatureScore> rightFeatures) {
		throw new UnsupportedOperationException();
	}
	
	protected double score;
}
