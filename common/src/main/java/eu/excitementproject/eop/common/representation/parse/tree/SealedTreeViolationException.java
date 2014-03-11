package eu.excitementproject.eop.common.representation.parse.tree;

/**
 * This exception indicates an attempt to change a parse tree after its {@link AbstractNode#seal()} method was called.
 * <P>
 * This exception is a runtime exception, not a checked exception. Making this a checked exception would require many
 * changes in many classes using parse trees.
 * 
 * @author Asher Stern
 * @since Mar 9, 2014
 *
 */
public class SealedTreeViolationException extends RuntimeException
{
	private static final long serialVersionUID = -1168562248816520740L;
	
	public static final String ERROR_MESSAGE="Error: An attepmt to modify a sealed tree has been encountered.\n"
			+ "Note: Parse tree usually should not be changed after being constructed."
			+ " In case of sealed trees - any attempt to change them will raise an exception.\n"
			+ "The right way to work with parse trees is to make a modified COPY of the tree, rather than changing the existing tree.";

	public SealedTreeViolationException()
	{
		super(ERROR_MESSAGE);
	}

	public SealedTreeViolationException(String message)
	{
		super(ERROR_MESSAGE+"\nMore details: "+message);
	}

	public SealedTreeViolationException(Throwable cause)
	{
		super(ERROR_MESSAGE,cause);
	}

	public SealedTreeViolationException(String message, Throwable cause)
	{
		super(ERROR_MESSAGE+"\nMore details: "+message, cause);
	}
}
