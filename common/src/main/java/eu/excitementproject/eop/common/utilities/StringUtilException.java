/**
 * 
 */
package eu.excitementproject.eop.common.utilities;

/**
 * @author Amnon Lotan
 *
 */
@SuppressWarnings("serial")
public class StringUtilException extends Exception {
	public StringUtilException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public StringUtilException(String message)
	{
		super(message);
	}
}
