package eu.excitementproject.eop.common.exception;

/**
 * The top-level abstract exception of the EXCITEMENT platform. 
 * All exceptions of the excitement platform should inherit this exception. 
 * @author Gil
 */
public abstract class BaseException extends Exception {

	private static final long serialVersionUID = -5653200767268354645L;

	public BaseException() {
		super(); 
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(Throwable cause) {
		super(cause);
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

//	public BaseException(String message, Throwable cause,
//			boolean enableSuppression, boolean writableStackTrace) {
//		super(message, cause, enableSuppression, writableStackTrace);
//	}
}
