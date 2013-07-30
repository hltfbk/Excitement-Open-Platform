package eu.excitementproject.eop.lap.biu.en.pasta.nomlex;

import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;


/**
 * 
 * @author Asher Stern
 * @since Oct 14, 2012
 *
 */
@SuppressWarnings("serial")
public class NomlexException extends PredicateArgumentIdentificationException
{
	public NomlexException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public NomlexException(String message)
	{
		super(message);
	}
}
