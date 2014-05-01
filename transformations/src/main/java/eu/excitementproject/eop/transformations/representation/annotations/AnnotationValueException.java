/**
 * 
 */
package eu.excitementproject.eop.transformations.representation.annotations;
import eu.excitementproject.eop.transformations.utilities.TransformationsException;

/**
 * @author Amnon Lotan
 * @since 08/07/2011
 * 
 */
public class AnnotationValueException extends TransformationsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9033638198036130552L;


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
