package ac.biu.nlp.nlp.engineml.operations;

import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.utilities.BIUTEEBaseException;


/**
 * An exception in operations code. See {@link GenerationOperation}.
 * 
 * @see GenerationOperation
 * @author Asher Stern
 * @since 2011
 *
 */
public class OperationException extends BIUTEEBaseException
{
	private static final long serialVersionUID = 2301892137505640927L;

	public OperationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public OperationException(String message)
	{
		super(message);
	}
	
	
	

}
