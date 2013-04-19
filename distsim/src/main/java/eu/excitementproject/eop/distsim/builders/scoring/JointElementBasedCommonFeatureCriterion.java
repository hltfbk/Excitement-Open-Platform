/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.scoring;

import java.util.NoSuchElementException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.storage.ElementFeatureCountStorage;
import eu.excitementproject.eop.distsim.storage.NoElementFoundException;
import eu.excitementproject.eop.distsim.util.Configuration;

/**
 * Defines common feature as a feature with a minimal number of joint elements
 * 
 * 
 * @author Meni Adler
 * @since 05/09/2012
 *
 * Immutable
 */
public class JointElementBasedCommonFeatureCriterion implements CommonFeatureCriterion {

	public JointElementBasedCommonFeatureCriterion(double minElements) {
		this.minFeatureElementsNum  = minElements;
	}
	
	public JointElementBasedCommonFeatureCriterion(ConfigurationParams params) throws ConfigurationException {
		this(params.getInt(Configuration.MIN_FEATURE_ELEMENTS_NUM));
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.feature.CommonFeature#isCommon(org.excitement.distsim.storage.ElementFeatureCountStorage, int)
	 */
	@Override
	public boolean isCommon(ElementFeatureCountStorage elementFeaturecountsStorage, int featureId) {
		ImmutableIterator<Element> it;
		
		try {
			it = elementFeaturecountsStorage.getFeatureElements(featureId);
			for (int i = 0; i< minFeatureElementsNum; i++)
				it.next();
		} catch (NoElementFoundException e) {
			//debug
			/*System.out.println("uncommon feature: " + featureId + ", elements: ");
			try {
				it = elementFeaturecountsStorage.getFeatureElements(featureId);
				while (it.hasNext())
					System.out.println("\t" + it.next());
			} catch (Exception e1) {}*/
			return false;
		} catch (NoSuchElementException e) {
			//debug
			/*System.out.println("uncommon feature: " + featureId + ", elements: ");
			try {
				it = elementFeaturecountsStorage.getFeatureElements(featureId);
				while (it.hasNext())
					System.out.println("\t" + it.next());
			} catch (Exception e1) {}*/
			return false;
		}
		return true;
	}
	
	protected final double minFeatureElementsNum;

}
