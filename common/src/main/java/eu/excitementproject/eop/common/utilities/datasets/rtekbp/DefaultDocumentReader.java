package eu.excitementproject.eop.common.utilities.datasets.rtekbp;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * 
 * @author Asher Stern
 * @since Aug 23, 2010
 *
 */
public class DefaultDocumentReader implements DocumentReader
{
	public void setFileName(String fileName) throws RteKbpIOException
	{
		this.fileName = fileName;
	}

	public void read() throws RteKbpIOException
	{
		if (null==this.fileName) throw new RteKbpIOException("file name not set");
		
		File file = new File(this.fileName);
		
		try
		{
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			readContents();
			
		}
		catch (SAXException e)
		{
			throw new RteKbpIOException("Failed to read file: "+this.fileName,e);
		}
		catch (IOException e)
		{
			throw new RteKbpIOException("Failed to read file: "+this.fileName,e);
		}
		catch (ParserConfigurationException e)
		{
			throw new RteKbpIOException("Failed to read file: "+this.fileName,e);
		}

	}

	public DocumentContents getDocumentContents() throws RteKbpIOException
	{
		if (null==contents) throw new RteKbpIOException("Not read yet.");
		return this.contents;
	}

	
	private void readContents() throws RteKbpIOException
	{
		Element rootElement = document.getDocumentElement();
		if (!rootElement.getNodeName().equals(DocumentFileConstants.ROOT_ELEMENT))
			throw new RteKbpIOException("bad document root element.");
		
		String docId = null;
		String source = null;
		String docType = null;
		String dateTime = null;
		String headLine = null;
		String sentences = null;

		
		NodeList rootChildren = rootElement.getChildNodes();
		for (int index=0;index<rootChildren.getLength();++index)
		{
			if (rootChildren.item(index).getNodeType()==Node.ELEMENT_NODE)
			{
				Element child = (Element) rootChildren.item(index);
				String childName = child.getNodeName();
				if (childName.equals(DocumentFileConstants.DOCID_ELEMENT))
				{
					docId = getTextOfElement(child);
				}
				else if (childName.equals(DocumentFileConstants.DOCTYPE_ELEMENT))
				{
					source = child.getAttribute(DocumentFileConstants.DOCTYPE_SOURCE_ATTRIBUTE_NAME);
					docType = getTextOfElement(child);
				}
				else if (childName.equals(DocumentFileConstants.DATETIME_ELEMENT))
				{
					dateTime = getTextOfElement(child);
				}
				else if (childName.equals(DocumentFileConstants.BODY_ELEMENT))
				{
					NodeList bodyNodes = child.getChildNodes();
					for (int bodyIndex=0;bodyIndex<bodyNodes.getLength();++bodyIndex)
					{
						if (bodyNodes.item(bodyIndex).getNodeType()==Node.ELEMENT_NODE)
						{
							Element bodyElement = (Element) bodyNodes.item(bodyIndex);
							String bodyElementName = bodyElement.getNodeName();
							if (bodyElementName.equals(DocumentFileConstants.HEADLINE_ELEMENT))
							{
								headLine = getTextOfElement(bodyElement);
							}
							else if (bodyElementName.equals(DocumentFileConstants.TEXT_ELEMENT))
							{
								sentences = bodyElement.getTextContent();
								sentences = sentences.replaceAll("\\n\\n", ". ");
								sentences = sentences.replaceAll("\\t", " ");
								sentences = sentences.replaceAll("\\n", " ");
							}
						}
					}
				}
			}
		} // end of for (each child node)
		
//		if (null==docId) throw new RteKbpIOException("null docId");
//		if (null==source) throw new RteKbpIOException("null source");
//		if (null==docType) throw new RteKbpIOException("null docType");
//		if (null==dateTime) throw new RteKbpIOException("null dateTime");
//		if (null==headLine) throw new RteKbpIOException("null headLine");
		if (null==docId) docId="";
		if (null==source) source="";
		if (null==docType) docType="";
		if (null==dateTime) dateTime="";
		if (null==headLine) headLine="";

		if (null==sentences) throw new RteKbpIOException("no sentences");
		
		this.contents = new DocumentContents(docId, source, docType, dateTime, headLine, sentences);
	}
	
	
	private String getTextOfElement(Element element)
	{
		String ret = null;
		NodeList children = element.getChildNodes();
		if (null==children);
		else
		{
			for (int index=0;index<children.getLength();index++)
			{
				Node node = children.item(index);
				if (node.getNodeType()==Node.TEXT_NODE)
				{
					ret = node.getNodeValue().trim();
					break;
				}
			}
		}
		return ret;
	}


	
	private String fileName;
	private Document document;
	private DocumentContents contents = null;
}
