/**
 * 
 */
package eu.excitementproject.eop.distsim.items;

/**
 * LexicalUnit defines a lexical word or phrase of a text
 *
 * @author Meni Adler
 * @since 19/06/2012
 *
 * 
 * <P>
 * Thread-safe
 * 
 */
public class LexicalUnit extends StringBasedTextUnit {
	
	private static final long serialVersionUID = 1L;

	public LexicalUnit(String data) {
		super(data);
	}
	
	public LexicalUnit(String data, long count) {
		super(data, count);
	}

	public LexicalUnit(String data, int id, long count) {
		super(data, id,count);
	}

}
