/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring.similarity;

/**
 * Implements the setTotalLeftFeaturesNum method of the {@linl ElementSimilarityScoring} interface
 * 
 * @author Meni Adler
 * @since Dec 10, 2013
 *
 * 
 */
public abstract class AbstractElementSimilarityScoring implements ElementSimilarityScoring {

	
	public AbstractElementSimilarityScoring() {
		totalLeftFeaturesNum = 0;
		totalRightFeaturesNum = 0;
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.scoring.similarity.ElementSimilarityScoring#setTotalLeftFeaturesNum(int)
	 */
	@Override
	public void setTotalFeaturesNum(int totalLeftFeaturesNum, int totalRightFeaturesNum) {
		this.totalLeftFeaturesNum = totalLeftFeaturesNum;
		this.totalRightFeaturesNum = totalRightFeaturesNum;
	}
	
	protected int totalLeftFeaturesNum, totalRightFeaturesNum;

}
