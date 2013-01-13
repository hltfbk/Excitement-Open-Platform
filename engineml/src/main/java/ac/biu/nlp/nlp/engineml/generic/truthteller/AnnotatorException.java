package ac.biu.nlp.nlp.engineml.generic.truthteller;
import ac.biu.nlp.nlp.engineml.utilities.BIUTEEBaseException;

/**
 * 
 * @author Asher Stern
 * @since Oct 4, 2011
 *
 */
public class AnnotatorException extends BIUTEEBaseException
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
