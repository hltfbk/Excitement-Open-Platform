package eu.excitementproject.eop.common.datastructures.dgraph.scan;

@SuppressWarnings("serial")
public class DirectedGraphScanException extends Exception
{
	public DirectedGraphScanException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DirectedGraphScanException(String message)
	{
		super(message);
	}
}
