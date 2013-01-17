/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.entailmentrules.conll;

/**
 * @author Amnon Lotan
 *
 * @since Jul 14, 2012
 */
public class ConllConverterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2207775279727747418L;

	/**
	 * Ctor
	 * @param message
	 * @param cause
	 */
	public ConllConverterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Ctor
	 * @param message
	 */
	public ConllConverterException(String message) {
		super(message);
	}

	
}
