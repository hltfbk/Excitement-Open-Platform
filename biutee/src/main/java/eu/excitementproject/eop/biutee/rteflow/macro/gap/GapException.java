package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * An exception thrown for problems in the mechanisms which calculate gap
 * between two parse trees in "hybrid gap mode".
 * 
 * @author Asher Stern
 * @since Aug 1, 2013
 *
 */
public class GapException extends TeEngineMlException
{
	private static final long serialVersionUID = -1882700784911403150L;

	public GapException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public GapException(String message)
	{
		super(message);
	}
}
