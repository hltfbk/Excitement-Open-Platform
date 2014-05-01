package eu.excitementproject.eop.common.utilities;

/**
 * Indicates that a DockedTokenFinder was strictly required to find all tokens in text,
 * and at least one token was not found.
 * 
 * @author Ofer Bronstein
 * @since June 2013
 */
@SuppressWarnings("serial")
public class TokenMissingException extends DockedTokenFinderException {
	public TokenMissingException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public TokenMissingException(String message)
	{
		super(message);
	}
}
