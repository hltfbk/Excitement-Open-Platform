package eu.excitementproject.eop.common.utilities.configuration;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import eu.excitementproject.eop.common.datastructures.KeyCaseInsensitiveHashTable;
import eu.excitementproject.eop.common.utilities.configuration.configuration_file.generated.ConfigurationType;
import eu.excitementproject.eop.common.utilities.configuration.configuration_file.generated.ModuleType;
import eu.excitementproject.eop.common.utilities.configuration.configuration_file.generated.ParameterType;
import eu.excitementproject.eop.common.utilities.configuration.configuration_file.generated.SubModuleType;


/**
 * Accesses BIU legacy configuration file.
 * 
 * 
 * The classes {@link ConfigurationFile} and {@link ConfigurationParams} use the JaxB jars {@link https://jaxb.dev.java.net/}
 * to provide a comprehensive and more user friendly Java interface to XML files.
 * They depend on the JaxB jar files in JARS.
 * 
 * <p>See examples in Demo.xml, Demo.java</p>
 * 
 * <p>
 * A good xml file for these classes would look like this:
 * <pre>
 * {@code
	   	<?xml version="1.0"?>
	
		<!DOCTYPE page [
		<!ENTITY jars "\\nlp-srv\jars\">		<!-- SOME COMMENT -->
		<!ENTITY stopwords "\\nlp-srv\Data\RESOURCES\stopwords-Eyal.txt">
		]>
		
		<configuration>
		
			<module name="logging">
				<param name="main-output-dir">\\nlp-srv\amnon\temp</param>
				<param name="experiment-name">rank dev</param>
				<param name="__COMMENTED-OUT-MODULE">mailer</param>
				<param name="log-file-max-size">500MB</param>
			</module>
			
			<!-- main module for LexicalGraph-->
			<module name="lexical inference">
				<param name="num of expansions">2</param>		<!-- number of steps when building the graph -->
				<param name="senses to use">1</param>			<!-- wn senses of seeds will be set to those sense numbers -->  
			</module>
		
		</configuration>
	}
 * </pre>
 * <p>
 * Here's an example client code snippet:
 * <pre>
 * {@code	
            ConfigurationFile conf = new ConfigurationFile(new File(fileName));
			ConfigurationParams params = conf.getModuleConfiguration("data set");
			File topDir = params.getFile("top-dir");
			File gsFile = params.getFile("gold-standard-file");
			List<String> topics = null;
			if(params.containsKey("topics")){
				topics = params.getStringList("topics");
			}
	} 
 * </pre>
 * @see ConfigurationParams
 * @author BIU NLP legacy-code. Modifications by Amnon Lotan and Asher Stern
 */
public class LegacyConfigurationFile implements UnderlyingConfigurationFile
{
	public static final String MODULE_ATTRIBUTE_NAME = "name";
	public static final String MODULE_ELEMENT_NAME = "module";

	
	///////////////////////////////// package constants /////////////////////////////
	
	static final String MODULE_NAME_SEPARATOR = "::";
	static final String MODULE_PRE = "Module" + MODULE_NAME_SEPARATOR;
	static final String XML_COMMENT_PREFIX = "_";

	//////////////////////////////////// private constants //////////////////////////////////////////// 

	private static final long serialVersionUID = -1138823445156480837L;	// required for implementing Serializable
	//private static final String GENERATED_JAXB_FILES_PACKAGE = "ac.biu.nlp.nlp.general.configuration.configuration_file.generated";
	private static final String GENERATED_JAXB_FILES_PACKAGE = eu.excitementproject.eop.common.utilities.configuration.configuration_file.generated.ObjectFactory.class.getPackage().getName();
//			"eu.excitementproject.eop.common.utilities.configuration.configuration_file.generated";
	
	
	
	
	/**
	 * Constructor
	 * @param iConfigurationXmlFile the input xml file
	 * @throws ConfigurationFileDuplicateKeyException if the iConfigurationXmlFile has duplicate keys 
	 * @throws ConfigurationException If any unexpected JAXBException errors occur while Unmarshaller unmarshalls 
	 */
	@SuppressWarnings("unchecked") // for unsafe casting of unmarshal()
	public LegacyConfigurationFile(File iConfigurationXmlFile, ConfigurationFile configurationFileReference) throws ConfigurationFileDuplicateKeyException, ConfigurationException
	{
		this.configurationFileReference = configurationFileReference;
		// check input
		if (iConfigurationXmlFile == null)
			throw new ConfigurationException("ConfigurationFile constructor was called with null parameter for the xml-file");
		if (!iConfigurationXmlFile.exists())
			throw new ConfigurationException("ConfigurationFile constructor: File does not exist: \'" 
					+ iConfigurationXmlFile.getPath() + "\'");			
		
		ConfigurationType confE = null;
		m_confFile = iConfigurationXmlFile;

		// marshal the input iConfigurationXmlFile with parameters from our GENERATED_JAXB_FILES_PACKAGE, into confE
		try {
			JAXBContext jc = JAXBContext.newInstance(GENERATED_JAXB_FILES_PACKAGE);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			confE = ((JAXBElement<ConfigurationType>) unmarshaller.unmarshal(iConfigurationXmlFile)).getValue();
		} catch (JAXBException e) {
			throw new ConfigurationException("a JAXBException was raised in ConfigurationFile.java, saying: " + e, e);
		}
		
		
		// insert the parameters from confE into our ConfigurationParams
		KeyCaseInsensitiveHashTable<String> mainParametersHashTable = new KeyCaseInsensitiveHashTable<String>();
		for (ParameterType paramE : confE.getParam())
		{
			mainParametersHashTable.put(paramE.getName(), paramE.getValue());
		}
		m_params = new LegacyConfigurationParams(mainParametersHashTable, configurationFileReference, null);
		
		m_conf = new KeyCaseInsensitiveHashTable<ConfigurationParams>();
		
		// for each module in the xml, put its contents in a new ConfigurationParams, and add it to our table m_conf
		for (ModuleType module : confE.getModule()) {
			String moduleName = module.getName();
			KeyCaseInsensitiveHashTable<String> moduleParameters = new KeyCaseInsensitiveHashTable<String>();

			// put each of the modul's parameters in its new ConfigurationParams
			for (ParameterType param : module.getParam())
			{
				putParamInParams(param, moduleParameters, moduleName);
			}
			String strModuleName = moduleName(moduleName);
			if (m_conf.containsKey(strModuleName)) {throw new ConfigurationException("A duplicate module has been detected: "+moduleName);}
			m_conf.put(strModuleName, new LegacyConfigurationParams(moduleParameters, configurationFileReference, strModuleName));
			
			// put each of the module's submodules as an independent ConfigurationParams modules in m_conf 
			for (SubModuleType submodule : module.getSubmodule()) 
			{
				KeyCaseInsensitiveHashTable<String> subModulesParameters = new KeyCaseInsensitiveHashTable<String>();
	
				// put each of the modul's parameters in its new ConfigurationParams
				for (ParameterType param : submodule.getParam())
				{
					putParamInParams(param,subModulesParameters, moduleName);
				}
				String strSubmoduleName = moduleName(submodule.getName());
				m_conf.put(strSubmoduleName, new LegacyConfigurationParams(subModulesParameters, configurationFileReference, strSubmoduleName));
			}
		}
	}
		
	/**
	 * Constructor
	 * @param iFileName the xml file name
	 * @throws ConfigurationFileDuplicateKeyException if the iConfigurationXmlFile has duplicate keys 
	 * @throws ConfigurationException If any unexpected JAXBException errors occur while Unmarshaller unmarshalls 
	 */
	public LegacyConfigurationFile(String iFileName, ConfigurationFile configurationFileReference) throws ConfigurationFileDuplicateKeyException, ConfigurationException 
	{
		this(new File(iFileName),configurationFileReference);
	}
	
	
	
	
	
	
	
	
	


	/**
	 * Indicates whether the {@link ConfigurationParams}s returned by getXXX methods
	 * of this configuration-file will expand environment variables.
	 * Default - <tt>false</tt>. See {@link #setExpandingEnvironmentVariables(boolean)}.
	 * 
	 * @return <tt>true</tt> if {@link ConfigurationParams} returned by this configuration-file
	 * will expand environment variables.
	 */
	public boolean isExpandingEnvironmentVariables()
	{
		return expandingEnvironmentVariables;
	}

	/**
	 * Sets whether the {@link ConfigurationParams} returned by getXXX methods of this
	 * configuration-file are set to expand environment variables.
	 * Default - <tt>false</tt>
	 * 
	 * @param expandingEnvironmentVariables
	 */
	public void setExpandingEnvironmentVariables(boolean expandingEnvironmentVariables)
	{
		this.expandingEnvironmentVariables = expandingEnvironmentVariables;
	}
	
	/**
	 * return the ConfigurationFile's main ConfigurationParams  
	 * @return the ConfigurationFile's main ConfigurationParams 
	 */
	public ConfigurationParams getParams()
	{
		m_params.setExpandingEnvironmentVariables(this.expandingEnvironmentVariables);
		return m_params;
	}


	/**
	 * return the ConfigurationParams that match the given module
	 * @param iModuleName
	 * @return the ConfigurationParams for the given module
	 * @throws ConfigurationException if iModuleName doesn't exist in this ConfigurationFile
	 */
	public ConfigurationParams getModuleConfiguration(String iModuleName) throws ConfigurationException
	{
		if (!m_conf.containsKey(moduleName(iModuleName)))
			throw new ConfigurationException("Tried to retrieve a module that doesn't exist: " + moduleName(iModuleName) );
		
		ConfigurationParams ret = m_conf.get(moduleName(iModuleName));
		if(ret == null)
			throw new ConfigurationException("Module name " + moduleName(iModuleName) + " has no module to match");
		
		if (expandingEnvironmentVariables)
			ret.setExpandingEnvironmentVariables(true);
		return ret;
	}
	
	public boolean isModuleExist(String moduleName) throws ConfigurationException
	{
		return m_conf.containsKey(moduleName(moduleName));
	}

	/**
	 * create a new entry in the ConfigurationParams table, under the given module name, without content 
	 * @param iModuleName
	 * @throws ConfigurationException if a module already exists by the same name
	 */
	public void addModuleConfiguration(String iModuleName) throws ConfigurationException
	{
		if (iModuleName == null || "".equals(iModuleName))
			throw new ConfigurationException("Empty/Null module name was given");
		
		if (!m_conf.containsKey(moduleName(iModuleName)))
		{
			String strModuleName = moduleName(iModuleName);
			m_conf.put(strModuleName, new LegacyConfigurationParams(new KeyCaseInsensitiveHashTable<String>(),configurationFileReference,strModuleName));
		}
		else
			throw new ConfigurationException("Can't create a module by the name " + iModuleName + ", because it already exists");
	}

	/**
	 * Remove the given module from the ConfigurationParams modules table
	 * @param iModuleName
	 * @throws ConfigurationException if any parameter is null
	 */
	public void removeModuleConfiguration(String iModuleName) throws ConfigurationException
	{			
		m_conf.remove(moduleName(iModuleName));
	}

	/**
	 * return the file the configuration was read from
	 * @return the file the configuration was read from
	 */
	public File getConfFile()
	{
		return m_confFile;
	}

	
	
	
	
	
	
	
	
	
	///////////////////////////////// private methods ///////////////////////////////////////////////


	
	/**
	 * put the parameter in its ConfigurationParams
	 * @param param
	 * @param params
	 * @param moduleName
	 * @throws ConfigurationFileDuplicateKeyException if the iConfigurationXmlFile has duplicate keys
	 */
	private void putParamInParams(ParameterType param, KeyCaseInsensitiveHashTable<String> params, String moduleName) throws ConfigurationFileDuplicateKeyException {
		
		String paramName = param.getName();

		// ignore parameters that start with "_"
		if (!paramName.startsWith(XML_COMMENT_PREFIX))
		{	
			// prevent duplicate parameters
			if (params.containsKey(paramName)) {
				throw new ConfigurationFileDuplicateKeyException("Duplicate parameter '"
						+ paramName + "' in configuration module: '"
						+ moduleName + "'");
			}
			params.put(paramName, param.getValue());
		}
	}
	
	/**
	 * return the module's fully qualified xml name
	 * @param iModuleName
	 * @return the module's fully qualified xml name
	 * @throws ConfigurationException if any parameter is null
	 */
	private String moduleName(String iModuleName) throws ConfigurationException
	{
		checkForNullModuleName(iModuleName);

		return MODULE_PRE + iModuleName;
	}
	
	private void checkForNullModuleName(String iModuleName) throws ConfigurationException
	{
		if (iModuleName == null || "".equals(iModuleName))
			throw new ConfigurationException("Empty/Null module name given");		
	}
	
	/*
	 * 
	 * private fields
	 * 
	 */	
	private KeyCaseInsensitiveHashTable<ConfigurationParams> m_conf = null;
	private ConfigurationParams m_params = null;
	private File m_confFile;
	private boolean expandingEnvironmentVariables = false;
	
	private final ConfigurationFile configurationFileReference;
	
}
