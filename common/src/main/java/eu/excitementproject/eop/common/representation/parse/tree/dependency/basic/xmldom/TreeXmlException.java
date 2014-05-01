package eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom;

/**
 * 
 * @author Asher Stern
 * @since October 2, 2012
 *
 */
@SuppressWarnings("serial")
public class TreeXmlException extends Exception
{
	public TreeXmlException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public TreeXmlException(String message)
	{
		super(message);
	}
}
