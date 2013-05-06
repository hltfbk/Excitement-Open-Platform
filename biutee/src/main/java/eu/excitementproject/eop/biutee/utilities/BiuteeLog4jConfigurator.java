package eu.excitementproject.eop.biutee.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.biutee.utilities.legacy.ExperimentLoggerNeutralizer;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.common.utilities.log4j.LoggerUtilities;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Configures (initializes) log4j for BIUTEE.
 * 
 * @author Asher Stern
 * @since May 2, 2013
 *
 */
public class BiuteeLog4jConfigurator
{
	public static final String LOG4J_PROPERTIES = "log4j.properties";
	
	public static final String DEFAULT_LOG4J_PROPERTIES_CONTENTS = 
		"log4j.rootLogger=warn, stdout\n"+
		"log4j.logger.eu.excitementproject.eop=info, logfile\n"+
		"log4j.logger.org.BIU.utils.logging.ExperimentLogger=warn\n"+
		"\n"+
		"log4j.appender.stdout = org.apache.log4j.ConsoleAppender\n"+
		"log4j.appender.stdout.layout = org.apache.log4j.PatternLayout\n"+
		"log4j.appender.stdout.layout.ConversionPattern = %-5p %d{HH:mm:ss} [%t]: %m%n\n"+
		"\n"+
		//"log4j.appender.logfile = org.apache.log4j.FileAppender\n"+
		"log4j.appender.logfile = eu.excitementproject.eop.common.utilities.log4j.BackupOlderFileAppender\n"+
		"log4j.appender.logfile.append=false\n"+
		"log4j.appender.logfile.layout = org.apache.log4j.PatternLayout\n"+
		"log4j.appender.logfile.layout.ConversionPattern = %-5p %d{HH:mm:ss} [%t]: %m%n\n"+
		"log4j.appender.logfile.File = logfile.log\n"
		;
	
	public void configure() throws FileNotFoundException, TeEngineMlException
	{
		// First, initialize log4j
		
		// Find out whether log4j.properties exists. If it exists use it as is.
		File log4jPropertiesFile = new File(LOG4J_PROPERTIES);
		boolean exist = false;
		if (log4jPropertiesFile.exists())if(log4jPropertiesFile.isFile())exist=true;
		
		// If log4j.properties does not exist - create a default.
		if (!exist)
		{
			PrintWriter log4jPropertiesWriter = new PrintWriter(log4jPropertiesFile);
			try
			{
				log4jPropertiesWriter.println(DEFAULT_LOG4J_PROPERTIES_CONTENTS);
			}
			finally
			{
				log4jPropertiesWriter.close();
			}
		}
		
		// Use the file log4j.properties to initialize log4j
		PropertyConfigurator.configure(LOG4J_PROPERTIES);
		
		// Pick the logger, and start writing log messages
		logger = Logger.getLogger(LogInitializer.class);
		
		// Register the log-file(s) (if exist(s)) as file(s) to be saved by ExperimentManager.
		for (Appender appender : LoggerUtilities.getAllAppendersIncludingParents(logger))
		{
			// cannot avoid RTTI, since current implementation of log4j provides
			// no other alternative.
			if (appender instanceof FileAppender)
			{
				File file = new File(((FileAppender)appender).getFile());
				ExperimentManager.getInstance().register(file);
			}
		}
		
		new ExperimentLoggerNeutralizer().neutralize();
	}
	
	private static Logger logger = null; // Logger.getLogger(LogInitializer.class);
}
