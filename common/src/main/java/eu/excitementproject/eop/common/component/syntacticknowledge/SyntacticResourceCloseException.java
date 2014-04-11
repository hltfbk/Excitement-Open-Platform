package eu.excitementproject.eop.common.component.syntacticknowledge;

/**
 * An exception indicating problem in closing a {@link SyntacticResource}.
 * <BR>
 * This exception can be thrown by {@link SyntacticResource#close()}.
 * 
 * @author Asher Stern
 * @since Mar 2, 2014
 *
 */
public class SyntacticResourceCloseException extends SyntacticResourceException  
{
	private static final long serialVersionUID = -4742033110994548988L;

	public SyntacticResourceCloseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SyntacticResourceCloseException(String message)
	{
		super(message);
	}
}
