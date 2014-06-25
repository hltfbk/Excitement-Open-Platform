package eu.excitementproject.eop.common.component.alignment;

import eu.excitementproject.eop.common.exception.ComponentException;


/**
 * This is the exception prepared for PairAnnotatorComponent. 
 * @author Tae-Gil Noh
 */
public class PairAnnotatorComponentException extends ComponentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1068712599109788009L;

	/**
	 * @param message
	 */
	public PairAnnotatorComponentException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public PairAnnotatorComponentException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PairAnnotatorComponentException(String message, Throwable cause) {
		super(message, cause);
	}

}
