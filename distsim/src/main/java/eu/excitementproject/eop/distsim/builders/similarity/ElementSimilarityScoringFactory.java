package eu.excitementproject.eop.distsim.builders.similarity;


import eu.excitementproject.eop.distsim.scoring.similarity.ElementSimilarityScoring;
import eu.excitementproject.eop.distsim.util.CreationException;

/**
 * Defines a factory for creation of instances that implement the {@link ElementSimilarityScoring} interface, according to some policy
 * 
 * @author Meni Adler
 * @since 26/12/2012
 *
 */
public interface ElementSimilarityScoringFactory {
	ElementSimilarityScoring create() throws CreationException;
}
