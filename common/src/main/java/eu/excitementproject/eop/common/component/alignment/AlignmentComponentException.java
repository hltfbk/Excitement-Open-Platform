/**
 * 
 */
package eu.excitementproject.eop.common.component.alignment;

/**
 * The exception type for AlignmentComponents 
 * 
 * @author Tae-Gil Noh 
 *
 */
public class AlignmentComponentException extends PairAnnotatorComponentException {

	private static final long serialVersionUID = 2157830765388558808L;

	/**
	 * @param message
	 */
	public AlignmentComponentException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public AlignmentComponentException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AlignmentComponentException(String message, Throwable cause) {
		super(message, cause);
	}

}
