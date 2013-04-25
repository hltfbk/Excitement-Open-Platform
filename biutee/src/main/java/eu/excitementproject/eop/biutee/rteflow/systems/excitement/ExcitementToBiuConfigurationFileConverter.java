package eu.excitementproject.eop.biutee.rteflow.systems.excitement;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import eu.excitementproject.eop.common.utilities.xmldom.XmlDomUtilitiesException;
import eu.excitementproject.eop.common.utilities.xmldom.XmlDomUtils;

/**
 * Converts Excitement configuration file into (old) BIU configuration file.
 *  
 * @author Asher Stern
 * @since Feb 26, 2013
 *
 */
public class ExcitementToBiuConfigurationFileConverter
{
	public static final String EXCITEMENT_TOP_ELEMENT = "configuration";
	public static final String BIU_TOP_ELEMENT = "configuration";
	
	public static final String EXCITEMENT_SECTION_ELEMENT = "section";
	public static final String BIU_MODULE_ELEMENT = "module";

	public static final String EXCITEMENT_PROPERTY_ELEMENT = "property";
	public static final String BIU_PARAM_ELEMENT = "param";
	
	public static final String EXCITEMENT_SUBSECTION_ELEMENT = "subsection";
	
	
	
	public static final class ExcitementToBiuConfigurationFileConverterException extends Exception
	{
		private static final long serialVersionUID = -7710383802478958691L;

		public ExcitementToBiuConfigurationFileConverterException(String message, Throwable cause)
		{
			super(message, cause);
		}
		public ExcitementToBiuConfigurationFileConverterException(String message)
		{
			super(message);
		}
	}
	
	
	public ExcitementToBiuConfigurationFileConverter(File excitementFile,
			File biuFile)
	{
		super();
		this.excitementFile = excitementFile;
		this.biuFile = biuFile;
	}


	public void convert() throws ExcitementToBiuConfigurationFileConverterException 
	{
		try
		{
			createDocuments();
			createRootElements();
			addAllSections();
			XmlDomUtils.writeDocumentToFile(biuXmlDocument, biuFile);
		}
		catch (XmlDomUtilitiesException | ParserConfigurationException e)
		{
			throw new ExcitementToBiuConfigurationFileConverterException("Failed to convert. See nested exception.",e);
		}
	}
	
	
	private void createDocuments() throws XmlDomUtilitiesException, ParserConfigurationException
	{
		excitementXmlDocument = XmlDomUtils.getDocument(excitementFile);
		biuXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	}
	
	private void createRootElements() throws ExcitementToBiuConfigurationFileConverterException
	{
		excitementRootElement = excitementXmlDocument.getDocumentElement();
		if (!EXCITEMENT_TOP_ELEMENT.equals(excitementRootElement.getNodeName()))
			throw new ExcitementToBiuConfigurationFileConverterException("Bad Excitement configuration file. Top element is: "+excitementRootElement.getNodeName()+". Expected: "+EXCITEMENT_TOP_ELEMENT+".");
		biuRootElement = biuXmlDocument.createElement(BIU_TOP_ELEMENT);
		biuXmlDocument.appendChild(biuRootElement);
	}
	
	private void addAllSections() throws ExcitementToBiuConfigurationFileConverterException, XmlDomUtilitiesException
	{
		List<Element> allSections =
				XmlDomUtils.getChildElements(excitementRootElement, EXCITEMENT_SECTION_ELEMENT);
		for (Element sectionElement : allSections)
		{
			String sectionName = sectionElement.getAttribute("name");
			if (null==sectionName) throw new ExcitementToBiuConfigurationFileConverterException("Null section name in Excitement configuration file");
			Element moduleElement = biuXmlDocument.createElement(BIU_MODULE_ELEMENT);
			moduleElement.setAttribute("name", sectionName);
			biuRootElement.appendChild(moduleElement);
			
			addSection(sectionName, sectionElement,moduleElement);
		}
	}
	
	private void addSection(String sectionName, Element sectionElement, Element moduleElement) throws ExcitementToBiuConfigurationFileConverterException, XmlDomUtilitiesException
	{
		if (XmlDomUtils.getChildElements(sectionElement, EXCITEMENT_SUBSECTION_ELEMENT).size()!=0)
		{
			throw new ExcitementToBiuConfigurationFileConverterException("Cannot convert the given Excitement configuration file to BIU configuration file. " +
					"Section \""+sectionName+"\" has a subsection.");
		}
		try
		{
			List<Element> propertiesElements = XmlDomUtils.getChildElements(sectionElement, EXCITEMENT_PROPERTY_ELEMENT);
			for (Element propertyElement : propertiesElements)
			{
				String propertyName = propertyElement.getAttribute("name");
				if (null==propertyName) throw new ExcitementToBiuConfigurationFileConverterException("Bad Excitement configuration file. Property has no name in section : "+sectionName);
				String propertyValue = XmlDomUtils.getTextOfElement(propertyElement,false);

				Element paramElement = biuXmlDocument.createElement(BIU_PARAM_ELEMENT);
				moduleElement.appendChild(paramElement);
				paramElement.setAttribute("name", propertyName);
				if (propertyValue!=null)
				{
					Text paramValueText = biuXmlDocument.createTextNode(propertyValue);
					paramElement.appendChild(paramValueText);
				}
			}
		}
		catch(ExcitementToBiuConfigurationFileConverterException | XmlDomUtilitiesException e)
		{
			throw new ExcitementToBiuConfigurationFileConverterException("Failure when converting section \""+sectionName+"\". See nested exception.",e);
		}
	}

	private final File excitementFile;
	private final File biuFile;
	
	private Document excitementXmlDocument;
	private Document biuXmlDocument;
	
	private Element excitementRootElement;
	private Element biuRootElement;
}
