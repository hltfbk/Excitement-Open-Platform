package ac.biu.nlp.nlp.general.configuration;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ac.biu.nlp.nlp.general.KeyCaseInsensitiveHashTable;
import ac.biu.nlp.nlp.general.configuration.configuration_file.generated.ConfigurationType;
import ac.biu.nlp.nlp.general.configuration.configuration_file.generated.ModuleType;
import ac.biu.nlp.nlp.general.configuration.configuration_file.generated.ParameterType;
import ac.biu.nlp.nlp.general.configuration.configuration_file.generated.SubModuleType;



/**
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
			EL.init(conf.getModuleConfiguration("logging"));
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
 * @author BIU NLP legacy-code
 */
public class ConfigurationFile implements Serializable {

	public static final String MODULE_ATTRIBUTE_NAME = "name";
	public static final String MODULE_ELEMENT_NAME = "module";
	
	///////////////////////////////// package constants /////////////////////////////
	
	static final String MODULE_NAME_SEPARATOR = "::";
	static final String MODULE_PRE = "Module" + MODULE_NAME_SEPARATOR;
	static final String XML_COMMENT_PREFIX = "_";

	//////////////////////////////////// private constants //////////////////////////////////////////// 

	private static final long serialVersionUID = -1138823445156480837L;	// required for implementing Serializable
	private static final String GENERATED_JAXB_FILES_PACKAGE = "ac.biu.nlp.nlp.general.configuration.configuration_file.generated";
	
	///////////////////////////////////////////////////////// public section /////////////////////////////////////////////////
	
	/**
	 * Constructor
	 * @param iConfigurationXmlFile the input xml file
	 * @throws ConfigurationFileDuplicateKeyException if the iConfigurationXmlFile has duplicate keys 
	 * @throws ConfigurationException If any unexpected JAXBException errors occur while Unmarshaller unmarshalls 
	 */
	@SuppressWarnings("unchecked") // for unsafe casting of unmarshal()
	public ConfigurationFile(File iConfigurationXmlFile) throws ConfigurationFileDuplicateKeyException, ConfigurationException
	{
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
		
		enforceNoDuplicates(iConfigurationXmlFile);
		
		// insert the parameters from confE into our ConfigurationParams
		m_params = new ConfigurationParams(this);
		for (ParameterType paramE : confE.getParam()) 
			m_params.put(paramE.getName(), paramE.getValue());
		
		m_conf = new KeyCaseInsensitiveHashTable<ConfigurationParams>();
		
		// for each module in the xml, put its contents in a new ConfigurationParams, and add it to our table m_conf
		for (ModuleType module : confE.getModule()) {
			String moduleName = module.getName();
			ConfigurationParams params = new ConfigurationParams(this, moduleName);
			m_conf.put(moduleName(moduleName), params);

			// put each of the modul's parameters in its new ConfigurationParams
			for (ParameterType param : module.getParam()) 
				putParamInParams(param, params, moduleName);
			
			// put each of the module's submodules as an independent ConfigurationParams modules in m_conf 
			for (SubModuleType submodule : module.getSubmodule()) 
			{
				ConfigurationParams subModuleParams = new ConfigurationParams(this, moduleName);
				m_conf.put(moduleName(submodule.getName()), subModuleParams);
	
				// put each of the modul's parameters in its new ConfigurationParams
				for (ParameterType param : submodule.getParam()) 
					putParamInParams(param,subModuleParams, moduleName);
			}
		}
	}
		
	/**
	 * Constructor
	 * @param iFileName the xml file name
	 * @throws ConfigurationFileDuplicateKeyException if the iConfigurationXmlFile has duplicate keys 
	 * @throws ConfigurationException If any unexpected JAXBException errors occur while Unmarshaller unmarshalls 
	 */
	public ConfigurationFile(String iFileName) throws ConfigurationFileDuplicateKeyException, ConfigurationException 
	{
		this(new File(iFileName));
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
			m_conf.put(moduleName(iModuleName), new ConfigurationParams(this));
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
	 * This method throws {@link ConfigurationException} if the given configuration file
	 * contains two modules with the same name.
	 * @param xmlFile
	 * @throws ConfigurationException
	 */
	private static void enforceNoDuplicates(File xmlFile) throws ConfigurationException
	{
		try
		{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			// ASHER: work around, see: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6181020
			documentBuilderFactory.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
			DocumentBuilder documentBuilder = 
				documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(xmlFile);
			Element rootElement = document.getDocumentElement();
			NodeList moduleElements = rootElement.getChildNodes();
			Set<String> moduleNames = new HashSet<String>();
			for (int index=0;index<moduleElements.getLength();++index)
			{
				Node node = moduleElements.item(index);
				if (node instanceof Element)
				{
					Element moduleElement = (Element)node;
					if (MODULE_ELEMENT_NAME.equalsIgnoreCase(moduleElement.getNodeName()))
					{
						String moduleName = moduleElement.getAttribute(MODULE_ATTRIBUTE_NAME);
						if (null==moduleName)
							throw new ConfigurationException("Could not identify a module name for a given module");
						if (moduleNames.contains(moduleName.toLowerCase()))
							throw new ConfigurationException("a duplicate module has been detected: "+moduleName);
						moduleNames.add(moduleName.toLowerCase());
					}
				}
			}
		}
		catch(ParserConfigurationException e)
		{
			throw new ConfigurationException("Could not parse the given xml file: "+xmlFile.getPath(),e);
		}
		catch(SAXException e)
		{
			throw new ConfigurationException("Could not parse the given xml file: "+xmlFile.getPath(),e);
		}
		catch(IOException e)
		{
			throw new ConfigurationException("Could not parse the given xml file: "+xmlFile.getPath(),e);
		}
	}
	
	/**
	 * put the parameter in its ConfigurationParams
	 * @param param
	 * @param params
	 * @param moduleName
	 * @throws ConfigurationFileDuplicateKeyException if the iConfigurationXmlFile has duplicate keys
	 */
	private void putParamInParams(ParameterType param, ConfigurationParams params, String moduleName) throws ConfigurationFileDuplicateKeyException {
		
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
}