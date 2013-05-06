package eu.excitementproject.eop.biutee.utilities;
import java.io.IOException;

import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


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
	public LogInitializer(String configurationFileName)
	{
		this.configurationFileName = configurationFileName;
	}
	
	public void init() throws IOException, TeEngineMlException
	{
		new BiuteeLog4jConfigurator().configure();
		
		SystemInformationLog systemInformationLog = new SystemInformationLog(configurationFileName);
		systemInformationLog.log();
	}
	
	private String configurationFileName;
}
