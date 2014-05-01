/**
 * 
 */
package eu.excitementproject.eop.redis;

/**
 * @author Meni Adler
 * @since Nov 4, 2013
 *
 */
public class NoAvailablePortException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public NoAvailablePortException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public NoAvailablePortException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public NoAvailablePortException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NoAvailablePortException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public NoAvailablePortException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
