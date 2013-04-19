package eu.excitementproject.eop.distsim.scoring.element;

import java.util.Collection;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;


/**
 * Defines the  normalization value of a given element score as the constant number 1
 * 
 * @author Meni Adler
 * @since 28/03/2012
 *
 *
 * <P>
 * Stateless. Thread-safe
 */
public class Const implements ElementScoring {

	public Const() {}
	
	public Const(ConfigurationParams params) {
		this();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.element.ElementScoring#score(java.util.Collection)
	 */
	@Override
	public double score(Collection<Double> featuresScores) {
		return 1;
	}

}
