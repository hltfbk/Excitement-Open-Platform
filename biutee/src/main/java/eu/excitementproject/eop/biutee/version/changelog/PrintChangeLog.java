package eu.excitementproject.eop.biutee.version.changelog;
import eu.excitementproject.eop.biutee.version.Version;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;

public class PrintChangeLog
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			System.out.println("Change log for: "+Version.getVersion().toString());
			System.out.println(ChangeLog.logAsString());
			
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}

	}

}
