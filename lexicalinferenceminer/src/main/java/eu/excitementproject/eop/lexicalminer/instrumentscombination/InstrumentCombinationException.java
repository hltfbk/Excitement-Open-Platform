package eu.excitementproject.eop.lexicalminer.instrumentscombination;

/**
 * 
 * @author Eyal Shnarch
 * @since 05/07/2011
 *
 */
public class InstrumentCombinationException extends Exception
{
	
	private static final long serialVersionUID = 7236870951485913229L;

	public InstrumentCombinationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InstrumentCombinationException(String message)
	{
		super(message);
	}
}
