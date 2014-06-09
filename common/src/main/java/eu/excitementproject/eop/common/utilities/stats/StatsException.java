/**
 * 
 */
package eu.excitementproject.eop.common.utilities.stats;

/**
 * 
 * @author Amnon Lotan
 * @since 21/12/2010
 * 
 */
@SuppressWarnings("serial")
public class StatsException extends Exception {
	public StatsException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public StatsException(String message)
	{
		super(message);
	}
}
