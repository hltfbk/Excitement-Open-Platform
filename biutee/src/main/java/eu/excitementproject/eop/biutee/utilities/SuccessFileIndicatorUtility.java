package eu.excitementproject.eop.biutee.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * 
 * @author Asher Stern
 * @since Dec 11, 2013
 *
 */
public class SuccessFileIndicatorUtility
{
	public static enum SuccessStatus
	{
		SUCCESS,WORKING,FAILURE;
	}
	
	public static boolean markWorking()
	{
		return mark(SuccessStatus.WORKING);
	}
	
	public static boolean markFailure()
	{
		return mark(SuccessStatus.FAILURE);
	}
	
	public static boolean markSuccess()
	{
		return mark(SuccessStatus.SUCCESS);
		
	}
	
	private static boolean mark(SuccessStatus status)
	{
		try
		{
			String filename = System.getenv().get(BiuteeConstants.ENVIRONMENT_VARIABLE_SUCCESS_FILE_NAME);
			if (filename!=null)
			{
				File file = new File(filename);
				if (file.exists())
				{
					if (!file.delete()){return false;}
				}
				try(PrintWriter writer = new PrintWriter(file))
				{
					writer.println(status.name());

				}
				catch (FileNotFoundException e)
				{
					return false;
				}
			}
			return true;
		}
		catch(Throwable t)
		{
			return false;
		}

	}
}
