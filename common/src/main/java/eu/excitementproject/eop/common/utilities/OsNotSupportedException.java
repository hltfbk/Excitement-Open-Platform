package eu.excitementproject.eop.common.utilities;




public class OsNotSupportedException extends Exception
{
	private static final long serialVersionUID = 1L;

	public OsNotSupportedException()
	{
	}

	public OsNotSupportedException(String message)
	{
		super(message);
	}
}
