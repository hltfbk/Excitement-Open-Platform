package eu.excitementproject.eop.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;


/**
 * This class implements CommonConfig
 * 
 * @author Roberto Zanoli
 */
public class ImplCommonConfig extends CommonConfig
{
	
	private File file;
	private Document doc;
	private List<String> sectionNames = null;
	
	/**
	 * Load a common configuration from XML configuration file 
	 * @param f the configuration file
	 */
	public ImplCommonConfig(File f) throws ConfigurationException
	{
		
		this.file = f;
		doc = parse(this.file);
		
	}
	
	/**
	 * Start a new, empty configuration file that can be set and saved,  
	 * 
	 */
	public ImplCommonConfig()
	{
		file = null;
		doc = null;
	}
	
	public List<String> getSectionNames()
	{
		if (null==this.sectionNames)
		{
			if (doc != null)
			{
				synchronized(this)
				{
					final String sectionTagName = "section";
					NodeList sectionList = doc.getElementsByTagName(sectionTagName);
					List<String> listNames = new ArrayList<>(sectionList.getLength());
					for (int i = 0; i < sectionList.getLength(); i++)
					{
						Node sectionNode = sectionList.item(i);
						if (sectionNode.getNodeType() == Node.ELEMENT_NODE)
						{
							Element sectionElement = (Element)sectionNode;
							String name = sectionElement.getAttribute("name");
							if (name != null)
							{
								listNames.add(name);
							}
						}
					}
					this.sectionNames = Collections.unmodifiableList(listNames);
				}
			}
		}
		return this.sectionNames;
	}
	
	/**
	 * This method returns the name-value table that is associated with the componentName. If there is no such 
	 * section, the method will raise an exception.
	 * @param componentName the component name
	 * @return NameValueTable the table
	 * @throws ConfigurationException
	 */
	public NameValueTable getSection(String componentName) throws ConfigurationException {
		
		NameValueTable nameValueTable = new ImplNameValueTable();
		
		//false when the section of the specified component does not exists
		boolean componentNameSection = false;
		
		try {
		
			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			
			String sectionTagName = "section";
			//list of nodes with tag: tagName (i.e. section)
			NodeList sectionList = doc.getElementsByTagName(sectionTagName);
		 
			//System.out.println("----------------------------");
		    
			//reading the sections
			for (int i = 0; i < sectionList.getLength(); i++) {
				
				Node sectionNode = sectionList.item(i);
		 
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());
		 
				if (sectionNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					Element sectionElement = (Element)sectionNode;
					
					//reading the section for the specified component
					if (sectionElement.getAttribute("name").equals(componentName)) {
						
						//the section of the specified component has been found
						componentNameSection = true;
						
						//list of nodes with tag: property
						NodeList propertyList = sectionElement.getElementsByTagName("property");
						
						//reading key values pairs
						for (int j = 0; j < propertyList.getLength(); j++) {
							 
							Node propertyNode = propertyList.item(j);
							Element propertyElement = (Element) propertyNode;
							String propertyAttributeValue = propertyElement.getAttribute("name");
							String propertyNodeValue = propertyNode.getTextContent();
							//System.out.println("propertyAttributeValue:" + propertyAttributeValue);
							//System.out.println("propertyNodeValue:" + propertyNodeValue);
							nameValueTable.setString(propertyAttributeValue, propertyNodeValue);
							
						}
						
					}
		 
				}
			}
			
	    } catch (Exception e) {
	    	throw new ConfigurationException("Failed to read the configuration file contents. Please see nested exception.",e);
	    }
		
		if (componentNameSection == false)
			throw new ConfigurationException("Configuration Error: the configuration file does not contain any section for the specified component.");
		
		return nameValueTable;
		
	  }
		
	
	/**This method returns the name-value table that is associated with the componentName and instanceName.
	 * @param componentName the component name
	 * @param instanceName the instance name
	 * @return NameValueTable the table
	 * @throws ConfigurationException
	 */
	public NameValueTable getSubSection(String componentName, String instanceName) throws ConfigurationException {
		
		NameValueTable nameValueTable = new ImplNameValueTable();
		
		//false when the section of the specified component or the subsection of the specified instance does not exists
		boolean componentNameSection = false;
		
		try {
		
			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			
			String sectionTagName = "section";
			//list of nodes with tag: tagName (i.e. section)
			NodeList sectionList = doc.getElementsByTagName(sectionTagName);
		 
			//System.out.println("----------------------------");
		
			for (int i = 0; i < sectionList.getLength(); i++) {
		 
				Node sectionNode = sectionList.item(i);
		 
				//System.out.println("\nCurrent Element :" + sectionNode.getNodeName());
		 
				if (sectionNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					Element sectionElement = (Element)sectionNode;
					
					//reading the section for the specified component
					if (sectionElement.getAttribute("name").equals(componentName)) {
						
						//System.out.println("\nComponent Section :" + sectionElement.getAttribute("name"));
						
						String subsectionTagName = "subsection";
						//list of nodes with tag: subsectionTagName
						NodeList subsectionList = sectionElement.getElementsByTagName(subsectionTagName);
						
						for (int k = 0; k < subsectionList.getLength(); k++) {
							
							Node subsectionNode = subsectionList.item(k);
							Element subsectionElement = (Element) subsectionNode;
							String attributeValue = subsectionElement.getAttribute("name");
							
							//System.out.println("\nInstance Section :" + subsectionElement.getAttribute("name"));
							
							//reading the section about the specified instance
							if (attributeValue.equals(instanceName)) {
							
								componentNameSection = true;
								
								//list of nodes with tag: property
								NodeList propertyList = subsectionElement.getElementsByTagName("property");
								
								//reading key, value pairs
								for (int j = 0; j < propertyList.getLength(); j++) {
									 
									Node propertyNode = propertyList.item(j);
									Element propertyElement = (Element) propertyNode;
									String propertyAttributeValue = propertyElement.getAttribute("name");
									String propertyNodeValue = propertyNode.getTextContent();
									//System.out.println("propertyAttributeValue:" + propertyAttributeValue);
									//System.out.println("propertyNodeValue:" + propertyNodeValue);
									nameValueTable.setString(propertyAttributeValue, propertyNodeValue);

								}
						
							}
						}
					}
		 
				}
			}
			
	    } catch (Exception e) {
	    	throw new ConfigurationException("Failed to read the configuration file contents. Please see nested exception.",e);
	    }
		
		if (componentNameSection == false)
			throw new ConfigurationException("Configuration Error: the configuration file does not contain any sections for the specified component or instance");
		
		return nameValueTable;
		
	}
		
	/**
	 * This method saves current configuration to an XML file. It will save whole values as a single XML file. Note that this save method should provide a safety mechanism
that will prevent overwriting existing XML configurations (ie. only permitting generating new configuration
files, etc).
Note that saveConfiguration() method is provided mainly for user level or transduction layer level
access. The methods are not expected to be called from a entailment core component.
	 * @param f the configuration file
	 * @throws ConfigurationException
	 */
	public void saveConfiguration(File f) throws ConfigurationException {
	
		
		
	}
	
	/**
	 * This method is a convenience method that returns the
full path name of the current configuration file. Will return null, if the configuration is not originated from
a file and never saved before.
	 * @return
	 */
	public String getConfigurationFileName() {
		
		return this.file.getAbsolutePath();
		
	}
	
	
	/*
	 * parse the xml configuration file
	 * @param f the configuration file
	 * @throws ConfigurationException
	 */
	private Document parse(File file) throws ConfigurationException {
		
		Document doc = null;
		
		try {
			 
			File fXmlFile = file;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
		 
		} catch (Exception e) {
			throw new ConfigurationException("Failed to read the XML configuration file.\n" +
					"It is recommended to try to open the XML file in a web browser, and inspect the error.",e);
		}
		
		return doc;
		
	}
			
	/*
	public static void main(String[] args) {
		
		try {
		
			File f = new File("./src/test/resources/example_of_configuration_file.xml");
			ImplCommonConfig commonConfig = new ImplCommonConfig(f);
			System.out.println(commonConfig.getConfigurationFileName());
			
			NameValueTable nameValueTable = commonConfig.getSection("core.MyEDA");
			System.out.println("myLongKey:" + nameValueTable.getString("myLongKey"));
			
			nameValueTable = commonConfig.getSubSection("PhoneticDistanceComponent", "instance1");
			System.out.println("consonatScore:" + nameValueTable.getDouble("consonantScore"));
			System.out.println("alpha:" + nameValueTable.getDouble("alpha"));
			
			//System.out.println(common.getConfigurationFileName());
			
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
	}
	*/
	
} 

