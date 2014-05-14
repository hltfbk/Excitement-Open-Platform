/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.entailmentrules;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;

/**
 * @author Amnon Lotan
 *
 * @since May 16, 2012
 */
public class EntailmentCompilationException extends CompilationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2811780134833501756L;

	/**
	 * Ctor
	 * @param arg0
	 */
	public EntailmentCompilationException(String arg0) {
		super(arg0);
	}

	/**
	 * Ctor
	 * @param string
	 * @param e
	 */
	public EntailmentCompilationException(String string, Exception e) {
		super(string, e);
	}

	
}
