package eu.excitementproject.eop.distsim.scoring;

import java.io.Serializable;

import eu.excitementproject.eop.distsim.items.AdditionalInfo;

/**
 * Defines the similarity measure of some two elements, composed by a similarity score and some additional info 
 * 
 * @author Meni Adler
 * @since 21/06/2012
 *
 */
public interface SimilarityMeasure extends Serializable {
	
	/**
	 * Get the similarity measure between some element and the compared element
	 * @return the similarity measure between some element and the compared element
	 */
	double getSimilarityMeasure();
	
	/**
	 * 
	 * Get an additional information for this similarity measurement
	 * 
	 * @return an additional information for this similarity measurement
	 */
	AdditionalInfo getAdditionalInfo();

}
