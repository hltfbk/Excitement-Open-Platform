package eu.excitementproject.eop.common.utilities.log4j;

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * 
 * @author Asher Stern
 * @since Nov 11, 2012
 *
 */
public class BasicVerySimpleLoggerInitializer
{
	public void initLogger()
	{
		initLogger(Level.INFO);
	}
	
	public void initLogger(Level level)
	{
		synchronized(BasicVerySimpleLoggerInitializer.class)
		{
			if (!alreadyInitialized)
			{
				BasicConfigurator.configure();
				Logger.getRootLogger().setLevel(level);
				@SuppressWarnings("rawtypes")
				Enumeration enumAppenders = Logger.getRootLogger().getAllAppenders();
				while (enumAppenders.hasMoreElements())
				{
					Appender appender = (Appender) enumAppenders.nextElement();
					appender.setLayout(new VerySimpleLayout());
				}
				alreadyInitialized = true;
			}
		}
	}

	private static boolean alreadyInitialized = false;
}
