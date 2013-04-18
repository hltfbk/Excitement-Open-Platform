package eu.excitementproject.eop.distsim.items;

/**
 * An element which is based on a given lexical string
 * 
 * @author Meni Adler
 * @since 28/06/2012
 *
 * Thread-safe
 */
public class LexicalElement extends StringBasedElement {
	
	
	private static final long serialVersionUID = 1L;

	public LexicalElement(String text) {
		super(text);
	}

	public LexicalElement(String text, AggregatedContext context) {
		super(text, context);
	}

	public LexicalElement(String text, int id, long count) {
		super(text,id,count);

	}
	
	public LexicalElement(String text, AggregatedContext context, int id, long count) {
		super(text,context,id,count);
	}	

}
