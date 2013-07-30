package eu.excitementproject.eop.biutee.utilities;

import eu.excitementproject.eop.common.exception.BaseException;

/**
 * 
 * @author Asher Stern
 * @since Jul 14, 2013
 *
 */
public class BiuteeException extends BaseException
{
	private static final long serialVersionUID = 6624379968749402095L;

	public BiuteeException(String message)
	{
		super(message);
	}

	public BiuteeException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
