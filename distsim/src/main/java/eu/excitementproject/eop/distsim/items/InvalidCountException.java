/**
 * 
 */
package eu.excitementproject.eop.distsim.items;

/**
 * @author Meni Adler
 * @since 19/06/2012
 *
 * Denotes an invalid counting value of some Countable object
 */
public class InvalidCountException extends Exception {


	/**
	 * 
	 */
	public InvalidCountException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public InvalidCountException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public InvalidCountException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidCountException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

}
