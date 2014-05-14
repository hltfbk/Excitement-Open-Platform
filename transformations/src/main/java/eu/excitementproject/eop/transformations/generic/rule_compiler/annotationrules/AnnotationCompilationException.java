/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.annotationrules;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;

/**
 * @author Amnon Lotan
 *
 * @since May 16, 2012
 */
public class AnnotationCompilationException extends CompilationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2811780134833501756L;

	/**
	 * Ctor
	 * @param arg0
	 */
	public AnnotationCompilationException(String arg0) {
		super(arg0);
	}

	/**
	 * Ctor
	 * @param string
	 * @param e
	 */
	public AnnotationCompilationException(String string, Exception e) {
		super(string, e);
	}

	
}
