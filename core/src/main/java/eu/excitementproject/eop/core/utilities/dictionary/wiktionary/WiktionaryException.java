/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wiktionary;


/**
 * @author Amnon Lotan
 * @since 21/06/2011
 * 
 */
public class WiktionaryException extends Exception {



	/**
	 * 
	 */
	private static final long serialVersionUID = -7803554942075867990L;


	/**
	 * Ctor
	 * @param arg0
	 */
	public WiktionaryException(String arg0) {
		super(arg0);
	}


	/**
	 * Ctor
	 * @param arg0
	 * @param arg1
	 */
	public WiktionaryException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
