/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring.similarity;

import java.util.Hashtable;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.scoring.FeatureScore;
import eu.excitementproject.eop.distsim.util.Configuration;

/**
 * 
 * Similarity according to Kotlerman eta. 2009
 * See: http://u.cs.biu.ac.il/~davidol/lilikotlerman/acl09_kotlerman.pdf 
 * 
 * @author Meni Adler
 * @since 31/10/2012
 *
 */
public class APinc implements ElementSimilarityScoring {

	public APinc(ConfigurationParams params)  {
		try {
			init(params.getDouble(Configuration.TOP_PERCENT));
		} catch (ConfigurationException e) {
			init(1);
		}
	}

	
	public APinc() {
		this(1);
	}

	public APinc(double topPercent) {
		init(topPercent);
	}

	public void init(double topPercent) {
		this.topPercent = topPercent;
		this.included = new Hashtable<Double,Double>();
		this.maxRank = 0;		
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.ElementSimilarityScoring#addElementFeatureScore(double, double)
	 */
	@Override
	public void addElementFeatureScore(double iLeftRank, double iRightRankedRel) {
		
		//debug
		//System.out.println("iLeftRank: " + iLeftRank);
		//System.out.println("iRightRankedRel: " + iRightRankedRel);
		
		included.put(iLeftRank, iRightRankedRel) ;
		if (maxRank < iLeftRank){
			maxRank = iLeftRank;
		}
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.ElementSimilarityScoring#getSimilarityScore(double, double)
	 */
	@Override
	public double getSimilarityScore(double leftDenominator, double rightDenominator) {
		
		double topFeatures = Math.floor(topPercent*maxRank);

		//debug
		//System.out.println("topPercent: " + topPercent);
		
		double score = 0, correct = 0, precision = 0;
		if (topFeatures>0){
			for (double i=1; i<topFeatures+1; i++ ){
				if(included.containsKey(i)){
					correct++;
					precision = correct / i;
					score += (precision*included.get(i));
				}
			}
			
			score /= topFeatures;
		}
		
		return score;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.ElementSimilarityScoring#getSimilarity(ac.biu.nlp.nlp.general.immutable.ImmutableIterator, ac.biu.nlp.nlp.general.immutable.ImmutableIterator)
	 */
	@Override
	public double getSimilarity(ImmutableIterator<FeatureScore> leftFeatures,
			ImmutableIterator<FeatureScore> rightFeatures) {
		throw new UnsupportedOperationException();
	}
	
	protected double topPercent;
	protected Hashtable<Double, Double> included;
	protected double maxRank;
}
