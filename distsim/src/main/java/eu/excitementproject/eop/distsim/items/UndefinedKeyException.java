package eu.excitementproject.eop.distsim.items;

/**
 * @author Meni Adler
 * @since 20/06/2012
 * 
 * Denotes an unknown key externalization of some KeyExtrenalizable object
 */
public class UndefinedKeyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public UndefinedKeyException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public UndefinedKeyException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public UndefinedKeyException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UndefinedKeyException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
