/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.elementfeature;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import gnu.trove.set.hash.TIntHashSet;

import java.io.IOException;
import java.util.Set;

/**
 * The IrelevantListBasedElementFeatureExtraction implements the filtering of irrelevant elements, according to a given list of relevant elements
 * 
 * @author Meni Adler
 * @since 04/09/2012
 *
 */
public abstract class IrelevantListBasedElementFeatureExtraction extends StopWordsBasedElementFeatureExtraction {

	public IrelevantListBasedElementFeatureExtraction() {
		irelevavtElementsForSimilarityCalculation = new TIntHashSet();
	}
	
	public IrelevantListBasedElementFeatureExtraction(Set<String> stopWordsFeatures) {
		super(stopWordsFeatures);
		irelevavtElementsForSimilarityCalculation = new TIntHashSet();
	}

	public IrelevantListBasedElementFeatureExtraction(ConfigurationParams params)  throws ConfigurationException, IOException {
		super(params);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.elementfeature.ElementFeatureExtraction#isRelevantElement(int)
	 */
	@Override
	public boolean isRelevantElementForCalculation(int elementId) {
		return !irelevavtElementsForSimilarityCalculation.contains(elementId);
	}

	
	protected TIntHashSet irelevavtElementsForSimilarityCalculation;

}
