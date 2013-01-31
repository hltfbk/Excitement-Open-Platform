package eu.excitementproject.eop.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

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

public class ConfigFileUtils {
	
	public static String configTag = "PlatformConfiguration";
	public static String nameTag = "name";
	public static String sectionTag = "section";
	
	
	public static String getAttribute(File file, String configTag, String edaTag, String attr){
		Document configDoc = parse(file);
		NodeList sectionList = configDoc.getElementsByTagName(sectionTag);
		
		NodeList nodesList = findNodesWithTag(sectionList, configTag);
		String edaName = findAttribute(nodesList, edaTag);
		
		System.out.println("EDA class name from config file: " + edaName);
		
		return edaName;
		
//		nodesList = findNodesWithTag(sectionList, edaTag);
//		return findAttribute(nodesList, attr);
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
	
	
	private static String getOptionValue(String tagName, DemoCmdOptions option) {
		if (tagName.equals("activatedEDA"))
			return option.activatedEDA;
		
		if(tagName.equals("language"))
			return option.language;
		
		if(tagName.equals("resource"))
			return option.resource;
		
		if (tagName.equals("distance"))
			return option.distance;
		
		System.out.println("Unknown user option: " + tagName);
		return "";
	}
	
	public static File editConfigFile(String baseConfigFile, File configFile, DemoCmdOptions option){

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
	 * @param f
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
	
	
	
}
