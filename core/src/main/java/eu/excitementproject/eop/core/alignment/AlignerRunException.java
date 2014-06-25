package eu.excitementproject.eop.core.alignment;

/**
 * Exception that signals an error when trying to align a sentence pair 
 */
public class AlignerRunException extends Exception
{
	private static final long serialVersionUID = 1L;

	public AlignerRunException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlignerRunException(String message) {
		super(message);
	}
}
