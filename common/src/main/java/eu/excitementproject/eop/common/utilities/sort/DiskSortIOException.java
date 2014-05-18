/**
 * 
 */
package eu.excitementproject.eop.common.utilities.sort;

/**
 * 
 * @author Amnon Lotan
 * @since Dec 7, 2010
 *
 */
public class DiskSortIOException extends Exception {

	private static final long serialVersionUID = 2550683252189650378L;

	public DiskSortIOException(String msg)
	{
		super(msg);
	}
	
	public DiskSortIOException(String msg, Exception cause)
	{
		super(msg, cause);
		
	}
}
