package eu.excitementproject.eop.distsim.items;

/**
 * Defines co-occurrence of two text units under some relation.
 * The co-occurrences are context-free, where various instances of co-occurrence in the corpus are represented by one Cooccurrence object. 
 * Cooccurrences are identifiable and countable.
 * 
 * @author Meni Adler
 * @since 17/05/2012
 *
 *
 * @param <R> the enum type of the relation domain, as defined by {@link Relation} interface 
 */
public interface Cooccurrence<R> extends Identifiable, Countable, Externalizable {
	/**
	 * @return the first text item of the co-occurrence
	 */
	TextUnit getTextItem1();
	
	/**
	 * @return the second text item of the co-occurrence
	 */
	TextUnit getTextItem2();
	
	/**
	 * @return the relation between the two text items of the co-occurrence
	 */	
	Relation<R> getRelation();
	
}
