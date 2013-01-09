package ac.biu.nlp.nlp.engineml.utilities;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.common.utilities.file.FileUtils;

import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.Workarounds;
import ac.biu.nlp.nlp.engineml.utilities.legacy.ExperimentLoggerNeutralizer;
import ac.biu.nlp.nlp.engineml.version.Citation;
import ac.biu.nlp.nlp.engineml.version.License;
import ac.biu.nlp.nlp.engineml.version.Version;
import ac.biu.nlp.nlp.general.ConstantsSummary;
import ac.biu.nlp.nlp.general.ExperimentManager;
import ac.biu.nlp.nlp.log.LoggerUtilities;


/**
 * This class initializes log4j, and writes some important data to the log.
 * The user <B>does not</B> have to create a "log4j.properties" to initialize the log. That
 * file is created automatically by this class. If the file is already exist, then the
 * initialization uses the existing file. Otherwise, a default file is created.
 * <P>
 * 
 * After initializing log4j, this class writes the class-path and the contents of
 * the configuration file. In addition, it writes the constants in {@link Constants} class.
 * 
 * @author Asher Stern
 * @since Feb 21, 2011
 *
 */
public class LogInitializer
{
	public static final String LOG4J_PROPERTIES = "log4j.properties";
	
	public static final String DEFAULT_LOG4J_PROPERTIES_CONTENTS = 
		"log4j.rootLogger=warn, stdout\n"+
		"log4j.logger.ac.biu.nlp.nlp.engineml=info, logfile\n"+
		"log4j.logger.org.BIU.utils.logging.ExperimentLogger=warn\n"+
		"\n"+
		"log4j.appender.stdout = org.apache.log4j.ConsoleAppender\n"+
		"log4j.appender.stdout.layout = org.apache.log4j.PatternLayout\n"+
		"log4j.appender.stdout.layout.ConversionPattern = %-5p %d{HH:mm:ss} [%t]: %m%n\n"+
		"\n"+
		//"log4j.appender.logfile = org.apache.log4j.FileAppender\n"+
		"log4j.appender.logfile = ac.biu.nlp.nlp.log.BackupOlderFileAppender\n"+
		"log4j.appender.logfile.append=false\n"+
		"log4j.appender.logfile.layout = org.apache.log4j.PatternLayout\n"+
		"log4j.appender.logfile.layout.ConversionPattern = %-5p %d{HH:mm:ss} [%t]: %m%n\n"+
		"log4j.appender.logfile.File = logfile.log\n"
		;

	
	public LogInitializer(String configurationFileName)
	{
		this.configurationFileName = configurationFileName;
	}
	
	public void init() throws IOException, TeEngineMlException
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
		
		
		// Print the version
		logger.info(Version.getVersion().toString());
		
		// print citation instructions
		logger.info(Citation.citationInsturction());
		
		// print license
		logger.info(License.LICENSE);
		
		// Print the class path
		try
		{
			String strClassPath = System.getProperty("java.class.path");
			logger.info("System class path is:\n"+strClassPath);
		}
		catch(Exception e)
		{
			logger.warn("Could not write the class path to log file.",e);
		}

		// Print the java.library.path
		try
		{
			String javaLibraryPath = System.getProperty("java.library.path");
			logger.info("java.library.path is:\n"+javaLibraryPath);
		}
		catch(Exception e)
		{
			logger.warn("Could not write the class path to log file.",e);
		}

		// Print the contents of the configuration file
		if (configurationFileName!=null)
		{
			StringBuffer sb = new StringBuffer();
			sb.append("Configuration file ");
			sb.append(configurationFileName);
			sb.append(":\n");
			sb.append(FileUtils.loadFileToString(configurationFileName));
			logger.info(sb.toString());
		}
		
		// Print the constants values of the class "Constants"
		if (logger.isInfoEnabled())
		{
			try
			{
				Class<?>[] classesOfConstants = new Class<?>[]{Constants.class,Workarounds.class};
				for (Class<?> classOfConstants : classesOfConstants)
				{
					ConstantsSummary constantsSummary = new ConstantsSummary(classOfConstants);
					logger.info("Constants in "+classOfConstants.getName()+" class:\n"+
							constantsSummary.getSummary());
					
				}
			}
			catch(Exception e)
			{
				logger.warn("Could not print summary of constants.", e);
			}
		}
		
		new ExperimentLoggerNeutralizer().neutralize();
		
	}

	private String configurationFileName;
	private static Logger logger = Logger.getLogger(LogInitializer.class);
}
