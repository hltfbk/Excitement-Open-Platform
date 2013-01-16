/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.application.merge;
import eu.excitementproject.eop.transformations.representation.annotations.AnnotationValueException;

/**
 * @author Amnon Lotan
 *
 * @since Jun 20, 2012
 */
public class AnnotationsMergerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6231082013269827352L;
	
	 /**
	 * Ctor
	 */
	public AnnotationsMergerException(String msg) {
		super(msg);
	}

	/**
	 * Ctor
	 * @param string
	 * @param e
	 */
	public AnnotationsMergerException(String string, AnnotationValueException e) {
		super(string, e);
	}
	
	

}
