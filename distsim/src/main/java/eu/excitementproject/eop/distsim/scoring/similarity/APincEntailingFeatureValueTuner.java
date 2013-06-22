/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring.similarity;


import eu.excitementproject.eop.distsim.scoring.feature.ElementFeatureData;
import eu.excitementproject.eop.distsim.storage.ElementFeatureScoreStorage;
import eu.excitementproject.eop.distsim.storage.NoScoreFoundException;

/**
 * An implementation of the {@link ElementFeatureValueConstructor} interface, for the entailing feature, according to the APinc method
 * [See: http://u.cs.biu.ac.il/~davidol/lilikotlerman/acl09_kotlerman.pdf ]
 *  
 * @author Meni Adler
 * @since 05/11/2012
 *
 */
public class APincEntailingFeatureValueTuner implements ElementFeatureValueConstructor {

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.FeatureValueTuner#tuneFeatureValue(double, int, int)
	 */
	@Override
	public double constructFeatureValue(ElementFeatureScoreStorage elementFeatureScoreStorage, int elementId, int featureId) throws NoScoreFoundException {
		return constructFeatureValue(elementFeatureScoreStorage.getElementFeatureData(elementId, featureId));
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.ElementFeatureValueConstructor#constructFeatureValue(org.excitement.distsim.scoring.feature.ElementFeatureData)
	 */
	@Override
	public double constructFeatureValue(ElementFeatureData data) {
		return data.getRank();
	}

}
