package eu.excitementproject.eop.biutee.small_unit_tests;
import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.utilities.LogInitializer;


public class DemoLogInitializer
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			new LogInitializer(args[0]).init();
			logger.info("after initialization");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}

	private static final Logger logger = Logger.getLogger(DemoLogInitializer.class); 
}
