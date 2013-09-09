package eu.excitementproject.eop.biutee.utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.version.Citation;
import eu.excitementproject.eop.biutee.version.License;
import eu.excitementproject.eop.biutee.version.Version;
import eu.excitementproject.eop.common.utilities.ConstantsSummary;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.GlobalMessages;
import eu.excitementproject.eop.transformations.utilities.Constants.Workarounds;

/**
 * 
 * @author Asher Stern
 * @since Jan 24, 2013
 *
 */
public class SystemInformationLog
{
	public SystemInformationLog(String configurationFileName)
	{
		this.configurationFileName = configurationFileName;
	}

	public void log() throws IOException
	{
		log(false);
	}
	
	public void log(boolean force) throws IOException
	{
		synchronized(SystemInformationLog.class)
		{
			if ( (!alreadyLogged) || (force) )
			{
				try
				{
					doLog();
				}
				finally
				{
					alreadyLogged = true;
				}
			}
		}
	}
	
	public void doLog() throws IOException
	{
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

		// Print environment variables
		if (logger.isInfoEnabled())
		{
			try
			{
				StringBuilder sbEnvVars = new StringBuilder();
				sbEnvVars.append("OS environment variables (sorted alphabetically): \n");
				Map<String, String> mapEnvironmentVariables = System.getenv();
				List<String> environmentVariableNames = new ArrayList<String>(mapEnvironmentVariables.keySet().size());
				environmentVariableNames.addAll(mapEnvironmentVariables.keySet());
				Collections.sort(environmentVariableNames);
				for (String environmentVariableName : environmentVariableNames)
				{
					sbEnvVars.append(environmentVariableName).append(" = ").append( mapEnvironmentVariables.get(environmentVariableName) ).append("\n");
				}
				logger.info(sbEnvVars.toString());
			}
			catch(RuntimeException e)
			{
				logger.warn("Failed to print environment variables due to the following exception:",e);
			}
		}
		
		if (logger.isInfoEnabled())
		{
			logger.info("Default locale is: "+Locale.getDefault());
		}
		try{ if (!(Locale.ENGLISH.getLanguage().equals(Locale.getDefault().getLanguage())))
		{
			GlobalMessages.globalWarn("Default locale is not English.", logger);
		}} catch(Exception e){GlobalMessages.globalWarn("Could not find out default locale.", logger);}


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
				Class<?>[] classesOfConstants = new Class<?>[]{
						Constants.class,Workarounds.class,
						BiuteeConstants.class,BiuteeConstants.Workarounds.class
						};
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
	}
	

	private String configurationFileName;
	private static boolean alreadyLogged = false;
	
	private static final Logger logger = Logger.getLogger(SystemInformationLog.class);
}
