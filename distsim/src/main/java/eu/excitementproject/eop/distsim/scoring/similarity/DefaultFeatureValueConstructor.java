/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring.similarity;


import eu.excitementproject.eop.distsim.scoring.feature.ElementFeatureData;
import eu.excitementproject.eop.distsim.storage.ElementFeatureScoreStorage;
import eu.excitementproject.eop.distsim.storage.NoScoreFoundException;

/**
 * An implementation of the {@link ElementFeatureValueConstructor} interface which is simply based on the original scoring value of the feature 
 * 
 * @author Meni Adler
 * @since 05/11/2012
 *
 */
public class DefaultFeatureValueConstructor implements ElementFeatureValueConstructor {

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.FeatureValueTuner#tuneFeatureValue(double, int, int)
	 */
	@Override
	public double constructFeatureValue(ElementFeatureScoreStorage elementFeatureScoreStorage, int elementId, int featureId) throws NoScoreFoundException {
		return elementFeatureScoreStorage.getElementFeatureScore(elementId, featureId);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.ElementFeatureConstructor#constructFeatureValue(org.excitement.distsim.scoring.feature.ElementFeatureData)
	 */
	@Override
	public double constructFeatureValue(ElementFeatureData data) {
		return data.getValue();
	}
}
