package eu.excitementproject.eop.util.runner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 * Utils class for the configuration file, specifically for the EOPRunner.
 * Used to obtain the value of specific attributes, needed for running the EOP platform:
 *    - the class name of the activatedEDA
 *    - the class name of the activatedLAP
 * 
 * @author Vivi Nastase (FBK)
 *
 */
public class ConfigFileUtils {
	
	public static String configTag = "PlatformConfiguration";
	public static String nameTag = "name";
	public static String sectionTag = "section";
	public static String propertyTag = "property";
	
	public static String getAttribute(File file, String attr) {

		System.out.println("Looking for a value for attribute: " + attr);
		
		Document configDoc = parse(file);
		NodeList sectionList = configDoc.getElementsByTagName(sectionTag);
		
		String value = findAttributeRec(sectionList, attr);
		
		if (value != null) {
			System.out.println("Value for attribute " + attr + " : " + value);
		}
		
		return value;
	}
		
	
	public static String getAttribute(File file, String configTag, String edaTag, String attr){
		Document configDoc = parse(file);
		NodeList sectionList = configDoc.getElementsByTagName(sectionTag);
		
		NodeList nodesList = findNodesWithTag(sectionList, configTag);
		String edaName = findAttribute(nodesList, edaTag);
				
		System.out.println("EDA class name from config file: " + edaName);
		
		return edaName;
	}
	
	
	private static NodeList findNodesWithTag(NodeList list, String tag) {

		NodeList foundNodes = null;
		int i = 0;
		boolean found = false;
		while( i < list.getLength() && !found) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.hasAttribute(nameTag) && element.getAttribute(nameTag).equals(tag)) {
					found = true;
					foundNodes = element.getChildNodes();
				}
			}			
			i++;
		}		
		return foundNodes;
	}

	
	private static String findAttribute(NodeList list, String attr) {
		String val = null;
		int i = 0;
		boolean found = false;
		while( i < list.getLength() && !found) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.hasAttribute(nameTag) && element.getAttribute(nameTag).equals(attr)) {
					found = true;
					val = element.getTextContent();
				}
			}			
			i++;
		}				
		return val;
	}
	
	private static String findAttributeRec(NodeList list, String attr) {
		String val = null;
		int i = 0;
		boolean found = false;
		while( i < list.getLength() && !found) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.hasAttribute(nameTag) && element.getAttribute(nameTag).equals(attr)) {
					found = true;
					val = element.getTextContent();
				}
			} 
			
			if (!found && node.hasChildNodes()) {
				val = findAttributeRec(node.getChildNodes(), attr);
				if (val != null) {
					found = true;
				}
			}
			i++;
		}				
		return val;
	}
	
	
	private static String getOptionValue(String tagName, EOPRunnerCmdOptions option) {
		if (tagName.equals("activatedEDA"))
			return option.eda;
		
		if(tagName.equals("language"))
			return option.language;
		
		System.out.println("Unknown user option: " + tagName);
		return "";
	}
	
	public static File editConfigFile(String baseConfigFile, File configFile, EOPRunnerCmdOptions option){

		Document configDoc = parse(new File(baseConfigFile));
		
		// find and change the user choices in the "PlatformConfiguration" section
		NodeList sectionList = configDoc.getElementsByTagName(sectionTag);
		
		int i = 0;
		boolean found = false;
		while( i < sectionList.getLength() && ! found) {
			Node sectionNode = sectionList.item(i);
			if (sectionNode.getNodeType() == Node.ELEMENT_NODE) {
				Element sectionElement = (Element) sectionNode;
				if (sectionElement.hasAttribute(nameTag) && sectionElement.getAttribute(nameTag).equals(configTag)) {
					found = true;
					NodeList configNodes = sectionElement.getChildNodes();
					for (int j = 0; j < configNodes.getLength(); i++) {
						((Element)configNodes.item(i)).setTextContent(getOptionValue(((Element)configNodes.item(i)).getAttribute(nameTag), option));
					}
				}
			}			
			i++;
		}
		
		// output the changed xml file
		try{
		 TransformerFactory transfac = TransformerFactory.newInstance();
         Transformer trans = transfac.newTransformer();
         trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
         trans.setOutputProperty(OutputKeys.INDENT, "no");

         //create string from xml tree
         StringWriter sw = new StringWriter();
         StreamResult result = new StreamResult(sw);
         DOMSource source = new DOMSource(configDoc);
         trans.transform(source, result);
         String xmlString = sw.toString();
         
         FileOutputStream fos = new FileOutputStream(configFile);
         OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8"); 
         out.write(xmlString);
         out.close();
         fos.close();
         
		} catch (Exception e) {
			System.err.println("Error writing the xml to a file");
			e.printStackTrace();
		}
		
		return configFile;
	}
	
	/*
	 * parse the xml configuration file
	 * @param file
	 * @throws ConfigurationException
	 */
	public static Document parse(File file) {

		Document configDoc = null;
		
		try {
			 
			File fXmlFile = file;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			configDoc = dBuilder.parse(fXmlFile);
			configDoc.getDocumentElement().normalize();
		 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configDoc;
	}
	
	/**
	 * Replace a value of a given attribute in the given configuration file, with the given value 
	 * 
	 * @param file -- configuration file
	 * @param attribute -- configuration attribute (e.f. model, trainDir, ...)
	 * @param newValue -- new value of the given attribute
	 */
	public static void editConfigFile(String file, String attribute, String newValue) {
		
		Path path = Paths.get(file);
		Charset charset = StandardCharsets.UTF_8;

		String content;
		try {
			content = new String(Files.readAllBytes(path), charset);
			content = content.replaceAll("<" + attribute + ">.*?<\\/" + attribute + ">", "<" + attribute + ">" + newValue + "<\\/" + attribute + ">");
			Files.write(path, content.getBytes(charset));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
