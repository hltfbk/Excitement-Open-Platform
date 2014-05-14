/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.representation;

/**
 * @author Amnon Lotan
 *
 * @since Jul 2, 2012
 */
public class AnnotationValueException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1909627437627032796L;


	/**
	 * Ctor
	 * @param message
	 */
	public AnnotationValueException(String message) {
		super(message);
	}


	/**
	 * Ctor
	 * @param message
	 * @param cause
	 */
	public AnnotationValueException(String message, Throwable cause) {
		super(message, cause);
	}

}
