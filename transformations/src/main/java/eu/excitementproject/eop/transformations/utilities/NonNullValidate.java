package eu.excitementproject.eop.transformations.utilities;

/**
 * Never tested. Never used.
 * 
 * @author Asher Stern
 * @since Jun 5, 2011
 *
 */
public class NonNullValidate 
{
	@SuppressWarnings("serial")
	public static class NonNullValidateException extends Exception
	{
		public NonNullValidateException(String message)
		{
			super(message);
		}
	}
	
	public static void validateNonNull(Object ...objects) throws NonNullValidateException
	{
		int index=0;
		for (Object obj : objects)
		{
			if (null==obj)throw new NonNullValidateException("Null object (#"+index+")");
			++index;
		}
		
	}

}
