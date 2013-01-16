package eu.excitementproject.eop.transformations.operations;
import eu.excitementproject.eop.transformations.operations.operations.GenerationOperation;
import eu.excitementproject.eop.transformations.utilities.TransformationsException;


/**
 * An exception in operations code. See {@link GenerationOperation}.
 * 
 * @see GenerationOperation
 * @author Asher Stern
 * @since 2011
 *
 */
public class OperationException extends TransformationsException
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
