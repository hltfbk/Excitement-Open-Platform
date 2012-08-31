package eu.excitement.entailment.core.configuration;

import java.io.File;
import eu.excitement.entailment.core.exceptions.ConfigurationException;

public abstract class CommonConfig 
{
	abstract public NameValueTable getSection(String componentName) throws ConfigurationException;
	
	abstract public NameValueTable getSubSection(String componentName, String instanceName) throws ConfigurationException;
	
	abstract public void loadConfiguration(File f) throws ConfigurationException;
	
	abstract public void saveConfiguration(File f) throws ConfigurationException;	
} 
