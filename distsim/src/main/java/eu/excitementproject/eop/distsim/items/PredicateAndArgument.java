package eu.excitementproject.eop.distsim.items;

import eu.excitementproject.eop.distsim.domains.relation.PredicateArgumentSlots;

/**
 * Defines a predicate and one of its arguments. The slot of the argument (x\y) is given by the relation.
 * 
 * @author Meni Adler
 * @since 28/06/2012
 *
 */
public class PredicateAndArgument extends DefaultCooccurrence<PredicateArgumentSlots> {

	
	private static final long serialVersionUID = 1L;

	public PredicateAndArgument(Predicate predicate, LexicalUnit argument, Relation<PredicateArgumentSlots> relation) {
		super(predicate, argument, relation);
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.Cooccurrence#getRelation()
	 */
	@Override
	public Relation<PredicateArgumentSlots> getRelation() {
		return super.getRelation();
	}

}
