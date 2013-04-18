/**
 * 
 */
package eu.excitementproject.eop.distsim.items;

/**
 * @author Meni Adler
 * @since 19/06/2012
 *
 * Denotes an invalid ID of some Identifiable object
 */
public class InvalidIDException extends Exception {


	/**
	 * 
	 */
	public InvalidIDException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public InvalidIDException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public InvalidIDException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidIDException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

}
