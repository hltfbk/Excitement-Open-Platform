package eu.excitementproject.eop.biutee.utilities;

import java.io.File;

import eu.excitementproject.eop.biutee.rteflow.systems.SystemInitialization;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

/**
 * A class used for some internal experiments in BIU. It can be ignored.
 * @author Asher Stern
 * @since Jan 8, 2014
 *
 */
public class HiddenConfigurationProvider
{
	public static final String HIDDEN_CONFIGURAITON_FILE_NAME = "hidden_biutee.xml";
	public static final String HIDDEN_MODULE_NAME = "hidden";
	
	
	public static ConfigurationParams getHiddenParams() throws ConfigurationException
	{
		File file = new File(HIDDEN_CONFIGURAITON_FILE_NAME);
		if (file.exists())
		{
			ConfigurationFile configurationFile = SystemInitialization.loadConfigurationFile(file.getPath());
			ConfigurationParams params = configurationFile.getModuleConfiguration(HIDDEN_MODULE_NAME);
			ExperimentManager.getInstance().register(file);
			return params;
		}
		else
		{
			return null;
		}
		
	}

}
