package eu.excitementproject.eop.distsim.domains.relation;

import java.io.Serializable;

/**
 * Defines the 'X' and 'Y' slots of the arguments of binary predicates 
 * 
 * @author Meni Adler
 * @since 26/12/2012
 *
 */
public enum PredicateArgumentSlots implements Serializable {
	X,
	Y;
	
	public static PredicateArgumentSlots getOpposite(PredicateArgumentSlots item) {
		return (item == X ? Y : X);
	}
}