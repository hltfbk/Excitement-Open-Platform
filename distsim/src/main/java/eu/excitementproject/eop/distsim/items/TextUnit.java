/**
 * 
 */
package eu.excitementproject.eop.distsim.items;

/**
 * The TextUnit interface defines the text units of the corpus, e.g, words, dependency paths. 
 * The text units are context-free, so various instances of a text unit are represented by one TextUnit object. 
 * Text units are identifiable and countable.
 * 
 * @author Meni Adler
 * @since 17/05/2012
 *
 * 
 */
public interface TextUnit extends Identifiable, Countable, Externalizable {
	/**
	 * Get the data item of this text unit
	 * 
	 * @return the data item of this text unit
	 */
	Object getData();
	
	/**
	 * Duplicate the given TextUnit
	 * 
	 * @return a duplication of the given TextUnit
	 */
	TextUnit copy();
	
}
