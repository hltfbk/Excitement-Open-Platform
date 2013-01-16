package eu.excitementproject.eop.common.datastructures.dgraph;


/**
 * 
 * @author Asher Stern
 *
 */
@SuppressWarnings("serial")
public class DirectedGraphException extends Exception {

	public DirectedGraphException(String message)
	{
		super(message);
	}

	public DirectedGraphException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
