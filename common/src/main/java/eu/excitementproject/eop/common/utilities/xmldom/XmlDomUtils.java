package eu.excitementproject.eop.common.utilities.xmldom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;


/**
 * Utilities for the DOM API of XML I/O (package org.w3c.dom in J2SE standard library).
 * @author Asher Stern
 * @since Oct 3, 2012
 *
 */
public class XmlDomUtils
{
	/**
	 * Returns an element which is a direct child of the given element, and has
	 * the name "childName".
	 * 
	 * @param element A given element (we need its child)
	 * @param childName The name of the child-element that we are looking for.
	 * @return
	 * @throws TreeXmlException
	 */
	public static Element getChildElement(Element element, String childName) throws XmlDomUtilitiesException
	{
		return getChildElement(element,childName,false);
	}
	
	/**
	 * Returns an element which is a direct child of the given element, and has
	 * the name "childName".
	 * <P>
	 * If <code>optional</code> is false, then if the required element does not exist,
	 * the function will throw an exception.
	 * 
	 * @param element A given element (we need its child)
	 * @param childName The name of the child-element that we are looking for.
	 * @param optional Indicates whether this element must exist, and if it does not
	 * exist it is an error and an exception will be thrown (optional==false), or not (optional==true).
	 * @return The child-element, which is a direct child of the given element, and named "<code>childName</code>".
	 * @throws TreeXmlException
	 */
	public static Element getChildElement(Element element, String childName, boolean optional) throws XmlDomUtilitiesException
	{
		Element childElement = null;
		NodeList nodeList = element.getChildNodes();
		for (int index=0;index<nodeList.getLength();++index)
		{
			Node node = nodeList.item(index);
			if (node instanceof Element)
			{
				Element elementOfNode = (Element)node;
				if (elementOfNode.getTagName().equals(childName))
				{
					if (childElement!=null) throw new XmlDomUtilitiesException("More than one child: "+childName);
					childElement = elementOfNode;
				}
				
			}
		}
		
		if ( (!optional) && (null==childElement) )
		{
			throw new XmlDomUtilitiesException("Missing child: "+childName);
		}
		
		return childElement;
	}
	
	/**
	 * Given an XML element, this method returns nested elements which are direct
	 * children of the given element - but only those that have the element-name "childrenName" (the second parameter).
	 * @param element
	 * @param childrenName
	 * @return
	 */
	public static List<Element> getChildElements(Element element, String childrenName)
	{
		List<Element> ret = new LinkedList<Element>();
		NodeList nodeList = element.getChildNodes();
		for (int index=0;index<nodeList.getLength();++index)
		{
			Node node = nodeList.item(index);
			if (node instanceof Element)
			{
				Element elementOfNode = (Element)node;
				if (elementOfNode.getTagName().equals(childrenName))
				{
					ret.add(elementOfNode);
				}
				
			}
		}
		return ret;
	}
	
	/**
	 * Returns the text of an XML element, as String.
	 * It usually looks like (please look at the following lines in an HTML viewer)<BR>
	 * &lt;element-name&gt;<BR>
	 * some text<BR>
	 * &lt;/element-name&gt;<BR>
	 * <BR>
	 * Then this method will return the "some text"
	 * 
	 * @param element
	 * @return
	 * @throws TreeXmlException
	 */
	public static String getTextOfElement(Element element) throws XmlDomUtilitiesException
	{
		return getTextOfElement(element,true);
	}
	
	/**
	 * Returns the text of an XML element, as String.<BR>
	 * Please see the comment of method {@link #getTextOfElement(Element)}.
	 * <P>
	 * The boolean parameter (textMustExist) indicates whether
	 * it is legal that no text exists for the element. If it is legal,
	 * and there is no text, then the return value is null.
	 * If it is not legal, and there is no text, then an XmlDomUtilitiesException
	 * exception is thrown.
	 * 
	 * @param element the element which we want to get its text.
	 * @param textMustExist <tt>true</tt> if it is not legal that the
	 * given element has no text.
	 * 
	 * @return the text of the element. Null if the text does not exist,
	 * and textMustExist is <tt>false</tt>.
	 * @throws XmlDomUtilitiesException
	 */
	public static String getTextOfElement(Element element, boolean textMustExist) throws XmlDomUtilitiesException
	{
		String foundString = null;
		NodeList nodeList = element.getChildNodes();
		for (int index=0;index<nodeList.getLength();index++)
		{
			Node node = nodeList.item(index);
			if (node instanceof Text)
			{
				Text text = (Text) node;
				String str = text.getNodeValue();
				str = str.trim();
				if (str.length()>0)
				{
					if (foundString!=null) throw new XmlDomUtilitiesException("Unexpected XML contents. Element has more than one text.");
					foundString=str;
				}
			}
		}
		if (textMustExist)
		{
			if (null==foundString) throw new XmlDomUtilitiesException("Unexpected XML contents. Text was not found for element: "+element.toString());
		}
		return foundString;
	}

	
	/**
	 * Returns {@link Document} that represents the given XML file.
	 * @param file an XML file.
	 * @return a {@link Document} that represents it.
	 * @throws XmlDomUtilitiesException
	 */
	public static Document getDocument(File file) throws XmlDomUtilitiesException
	{
		try
		{
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
		}
		catch(ParserConfigurationException e)
		{
			throw new XmlDomUtilitiesException("read xml file failed. See nested exception.",e);
		}
		catch (SAXException e)
		{
			throw new XmlDomUtilitiesException("read xml file failed. See nested exception.",e);
		}
		catch (IOException e)
		{
			throw new XmlDomUtilitiesException("read xml file failed. See nested exception.",e);
		}
	}


	public static void writeDocumentToFile(Document document, File file) throws XmlDomUtilitiesException
	{
		try
		{
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			//StreamResult streamResult = new StreamResult(this.output);
			FileOutputStream outputStream = new FileOutputStream(file);
			try
			{
				StreamResult streamResult = new StreamResult(outputStream);
				DOMSource source = new DOMSource(document);
				trans.transform(source, streamResult);
			}
			finally
			{
				outputStream.close();
			}
		}
		catch(IOException | TransformerException e)
		{
			throw new XmlDomUtilitiesException("Failed to write into file: "+file.getPath());
		}
	}
}
