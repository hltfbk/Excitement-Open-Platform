package eu.excitementproject.eop.transformations.biu.en.predicatetruth;


/**
 * Exception class thrown by {@link PredicateTruth} in any error case.
 * @author Gabi Stanovsky
 * @since Aug 2014
 */
public class PredicateTruthException extends Exception
{
	private static final long serialVersionUID = 1L;

	public PredicateTruthException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public PredicateTruthException(String message)
	{
		super(message);
	}

}
