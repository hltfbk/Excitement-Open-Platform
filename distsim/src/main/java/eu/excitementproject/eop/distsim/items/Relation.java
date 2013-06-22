/**
 * 
 */
package eu.excitementproject.eop.distsim.items;

/**
 * Binary relation between two {@link TextUnit}s, e.g., obj/subj for two words, left/right argument of dependency path.
 * Relations are identifiable and countable.
 * 
 * @author Meni Adler
 * @since 17/05/2012
 *
 * 
 * @param <T> the enum type of the relation, which defines its value domain
 */
public interface Relation<T> extends Externalizable {
	/**
	 * @return the enum type value which represents this relation, ,<i> e.g</i> TreeDependency.OBJECT, PredicateArgumentSlots.Y
	 */
	T getValue();
}
