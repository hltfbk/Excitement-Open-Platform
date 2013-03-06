/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.scoring;



import eu.excitementproject.eop.distsim.builders.Builder;
import eu.excitementproject.eop.distsim.storage.ElementFeatureCountStorage;
import eu.excitementproject.eop.distsim.storage.PersistenceDevice;

/**
  * Building of a database composed of all elements and features with their scores, based on elements and features counts
  *
  * @author Meni Adler
 * @since 29/05/2012
 *
 * 
 * 
 */
public interface ElementFeatureScorer extends Builder {
	/**
	 * 
	 * Calculates an element-feature scores, based on given countings of elements and features, and stores them in given output devices
	 * 
	 * @param elementFeaturecounts general, total and joint countings of elements and features
	 * @param elementFeatureScoreDevice output storage device for element-feature scores
	 * @param elementScoreDevice output storage device for element scores 
	 */
	void scoreElementsFeatures(ElementFeatureCountStorage elementFeaturecounts, PersistenceDevice elementFeatureScoreDevice, PersistenceDevice elementScoreDevice) throws ElementFeatureScorerException;

}
