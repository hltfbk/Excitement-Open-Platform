package eu.excitementproject.eop.common.representation.parse.candc.graph;

@SuppressWarnings("serial")
public class CandCMalformedOutputException extends CandCException
{
	public CandCMalformedOutputException(String message)
	{
		super(message);
	}


	public CandCMalformedOutputException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
