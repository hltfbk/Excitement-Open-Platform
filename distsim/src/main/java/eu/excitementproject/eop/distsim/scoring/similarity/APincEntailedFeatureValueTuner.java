/**
 * 
 */
package eu.excitementproject.eop.distsim.scoring.similarity;


import eu.excitementproject.eop.distsim.scoring.feature.ElementFeatureData;

/**
 * An implementation of the {@link ElementFeatureValueConstructor} interface, for the entailed feature, according to the APinc method
 * [See: http://u.cs.biu.ac.il/~davidol/lilikotlerman/acl09_kotlerman.pdf ]
 * 
 * @author Meni Adler
 * @since 05/11/2012
 *
 */
public class APincEntailedFeatureValueTuner implements ElementFeatureValueConstructor {

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.similarity.ElementFeatureValueConstructor#constructFeatureValue(org.excitement.distsim.scoring.feature.ElementFeatureData)
	 */
	@Override
	public double constructFeatureValue(ElementFeatureData data) {
		
		double ret = 1 - (data.getRank() / (data.getSize() +1));		
		return ret;
	}
}
