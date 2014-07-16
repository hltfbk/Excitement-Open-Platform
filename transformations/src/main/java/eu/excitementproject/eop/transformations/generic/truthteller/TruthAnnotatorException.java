package eu.excitementproject.eop.transformations.generic.truthteller;

/**
 * Exception class thrown by {@link TruthAnnotator} in any error case.
 *
 */
public class TruthAnnotatorException extends Exception
{
	private static final long serialVersionUID = 1L;

	public TruthAnnotatorException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public TruthAnnotatorException(String message)
	{
		super(message);
	}

}
