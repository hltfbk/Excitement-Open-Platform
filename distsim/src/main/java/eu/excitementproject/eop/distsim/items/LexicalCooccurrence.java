package eu.excitementproject.eop.distsim.items;

import eu.excitementproject.eop.distsim.domains.relation.TreeDependency;

/**
 * An implementation of the {@link Cooccurrence} interface for lexical composed of two lexical units and their TreeDependency relation
 * 
 * Immutable. Thread-safe
 * 
 * @author Meni Adler
 * @since 20/06/2012
 * 
 */
public class LexicalCooccurrence extends DefaultCooccurrence<TreeDependency> {

	
	private static final long serialVersionUID = 1L;

	public LexicalCooccurrence(LexicalUnit textItem1, LexicalUnit textItem2, Relation<TreeDependency> relation) {
		super(textItem1, textItem2, relation);
	}
	
	/* (non-Javadoc) 
	 * @see org.excitement.distsim.items.Cooccurrence#getRelation()
	 */
	@Override
	public Relation<TreeDependency> getRelation() {
		return super.getRelation();
	}

}
