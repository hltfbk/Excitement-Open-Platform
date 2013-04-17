/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.similarity;

import java.util.List;

import eu.excitementproject.eop.distsim.scoring.combine.SimilarityCombination;
import eu.excitementproject.eop.distsim.storage.PersistenceDevice;

/**
 * Integrates a given set of similarity DB, into one combined DB with unified similarity scores.
 *
 * @author Meni Adler
 * @since 29/05/2012
 *
 * 
 */
public interface ElementSimilarityCombiner {
	/**
	 * Combines a set of similarity score devices into one unified  scoring device
	 * 
	 * @param devices a set of similarity storage devices
	 * @param similarityCombination a method for combining similarity measures into one score
	 * @param combinedStorage a storage device for the combined scores
	 * @throws SimilarityCombinationException 
	 */
	void combinedScores(List<PersistenceDevice> devices, SimilarityCombination similarityCombination, PersistenceDevice combinedStorage) throws SimilarityCombinationException;
}
