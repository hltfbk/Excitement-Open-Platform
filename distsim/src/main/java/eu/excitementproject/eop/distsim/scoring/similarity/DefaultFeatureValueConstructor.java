/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring.similarity;


import eu.excitementproject.eop.distsim.scoring.feature.ElementFeatureData;

/**
 * An implementation of the {@link ElementFeatureValueConstructor} interface which is simply based on the original scoring value of the feature 
 * 
 * @author Meni Adler
 * @since 05/11/2012
 *
 */
public class DefaultFeatureValueConstructor implements ElementFeatureValueConstructor {

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.ElementFeatureConstructor#constructFeatureValue(org.excitement.distsim.scoring.feature.ElementFeatureData)
	 */
	@Override
	public double constructFeatureValue(ElementFeatureData data) {
		return data.getValue();
	}
}
