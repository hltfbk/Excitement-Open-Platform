package eu.excitementproject.eop.transformations.generic.truthteller;
import eu.excitementproject.eop.transformations.utilities.TransformationsException;

/**
 * 
 * @author Asher Stern
 * @since Oct 4, 2011
 *
 */
public class AnnotatorException extends TransformationsException
{
	private static final long serialVersionUID = -2330279604033662767L;

	public AnnotatorException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public AnnotatorException(String message)
	{
		super(message);
	}
}
