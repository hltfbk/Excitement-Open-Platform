package eu.excitementproject.eop.common.utilities;

/**
 * @author Ofer Bronstein
 * @since June 2013
 */
@SuppressWarnings("serial")
public class DockedTokenFinderException extends Exception {
	public DockedTokenFinderException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DockedTokenFinderException(String message)
	{
		super(message);
	}
}
