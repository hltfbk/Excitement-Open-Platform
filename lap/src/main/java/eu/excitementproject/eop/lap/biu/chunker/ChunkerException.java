/**
 * 
 */
package eu.excitementproject.eop.lap.biu.chunker;

/**
 * @author Amnon Lotan
 *
 * @since 26/01/2011
 */
@SuppressWarnings("serial")
public class ChunkerException extends Exception
{

	/**
	 * @param arg0
	 */
	public ChunkerException(String arg0)
	{
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ChunkerException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
