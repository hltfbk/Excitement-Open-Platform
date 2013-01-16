package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Implementation of {@link CorpusDocumentReader} using DOM.
 * 
 * @see CorpusDocumentReader
 * @author Asher Stern
 *
 */
public class DefaultCorpusDocumentReader implements CorpusDocumentReader
{
	public static String DOC_ROOT_ELEMENT_NAME = "DOC";
	public static String HEADLINE_ELEMENT_NAME = "HEADLINE";
	public static String DATELINE_ELEMENT_NAME = "DATELINE";
	public static String TEXT_ELEMENT_NAME = "TEXT";
	public static String SENTENCE_ELEMENT_NAME = "S";
	public static String SENTENCE_ID_ATTRIBUTE_NAME = "s_id";
	public static String DOCUMENT_ID_ATTRIBUTE_NAME = "doc_id";
	public static String DOCUMENT_TYPE_ATTRIBUTE_NAME = "type";
	
	
	
	public void setXml(String xmlFileName) throws Rte6mainIOException
	{
		if (null==xmlFileName) throw new Rte6mainIOException("null==xmlFileName");
		this.xmlFile = new File(xmlFileName);
		if (!xmlFile.exists()) throw new Rte6mainIOException("file: "+xmlFileName+" ("+xmlFile.getAbsolutePath()+")"+" does not exist");
		if (!xmlFile.isFile()) throw new Rte6mainIOException("file: "+xmlFileName+" ("+xmlFile.getAbsolutePath()+")"+" is not a file");
	}

	public void read() throws Rte6mainIOException
	{
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.document = builder.parse(this.xmlFile);
			readDocument();
		}
		catch (ParserConfigurationException e)
		{
			throw new Rte6mainIOException("xml read failure. See nested exception",e);
		}
		catch (SAXException e)
		{
			throw new Rte6mainIOException("xml read failure. See nested exception",e);
		}
		catch (IOException e)
		{
			throw new Rte6mainIOException("xml read failure. See nested exception",e);
		}
	}

	public String getType() throws Rte6mainIOException
	{
		if (!read) throw new Rte6mainIOException("not read");
		return this.documentType;
	}


	public String getDateline() throws Rte6mainIOException
	{
		if (!read) throw new Rte6mainIOException("not read");
		return this.dateline;
	}
	public String getDocId() throws Rte6mainIOException
	{
		if (!read) throw new Rte6mainIOException("not read");
		return this.documentId;
	}
	public String getHeadline() throws Rte6mainIOException
	{
		if (!read) throw new Rte6mainIOException("not read");
		return this.headline;
	}
	
	public LinkedHashMap<Integer, String> getMapSentences()
			throws Rte6mainIOException
	{
		if (!read) throw new Rte6mainIOException("not read");
		return this.sentences;
	}
	
	private void readDocument() throws Rte6mainIOException
	{
		rootElement = document.getDocumentElement();
		if (rootElement.getNodeName().equals(DOC_ROOT_ELEMENT_NAME)) ;
		else throw new Rte6mainIOException("Illegal node name");
		
		this.documentId = rootElement.getAttribute(DOCUMENT_ID_ATTRIBUTE_NAME);
		if (this.documentId==null) throw new Rte6mainIOException("document id missing.");
		this.documentType = rootElement.getAttribute(DOCUMENT_TYPE_ATTRIBUTE_NAME);
		if (this.documentType==null) throw new Rte6mainIOException("document type is missing.");
		
		
		this.headline = getHeadLine();
		try{this.dateline = getDateLine();}
		catch(Rte6mainIOException e){this.dateline = null;}
		
		NodeList listTextElements = rootElement.getElementsByTagName(TEXT_ELEMENT_NAME);
		if (listTextElements.getLength()!=1) throw new Rte6mainIOException("text");
		Node textNode = listTextElements.item(0);
		if (textNode.getNodeType()!=Node.ELEMENT_NODE) throw new Rte6mainIOException("text");
		Element textElement = (Element) textNode;
		NodeList sentencesNodeList = textElement.getElementsByTagName(SENTENCE_ELEMENT_NAME);
		if (sentencesNodeList.getLength()<1) throw new Rte6mainIOException("sentences");
		for (int index=0;index<sentencesNodeList.getLength();index++)
		{
			Node sentenceNode = sentencesNodeList.item(index);
			if (sentenceNode.getNodeType()!=Node.ELEMENT_NODE) throw new Rte6mainIOException("sentence");
			Element sentenceElement = (Element) sentenceNode;
			fillSentence(sentenceElement);
		}
		read = true;
	}
	
	private String getHeadLine() throws Rte6mainIOException
	{
		return getTextElement(HEADLINE_ELEMENT_NAME);
	}
	
	private String getDateLine() throws Rte6mainIOException
	{
		return getTextElement(DATELINE_ELEMENT_NAME);
	}
	
	private String getTextElement(String elementName) throws Rte6mainIOException
	{
		NodeList nodelist = rootElement.getElementsByTagName(elementName);
		if (nodelist.getLength()<1) throw new Rte6mainIOException(elementName);
		Node nodeElement = nodelist.item(0);
		if (nodeElement.getNodeType()!=Node.ELEMENT_NODE) throw new Rte6mainIOException(elementName);
		Element element = (Element) nodeElement;
		NodeList children = element.getChildNodes();
		boolean found = false;
		String ret = null;
		for (int index=0;index<children.getLength();index++)
		{
			Node node = children.item(index);
			if (node.getNodeType()==Node.TEXT_NODE)
			{
				ret = node.getNodeValue().trim();
				found = true;
			}
		}
		if (!found) throw new Rte6mainIOException(elementName);
		return ret;
		
	}
	private void fillSentence(Element sentenceElement) throws Rte6mainIOException
	{
		Integer id = null;
		try
		{
			id = Integer.parseInt(sentenceElement.getAttribute(SENTENCE_ID_ATTRIBUTE_NAME));
		}
		catch(Exception e){throw new Rte6mainIOException("sentence id.",e);}
		if (sentences.containsKey(id)) throw new Rte6mainIOException("duplicate sentence: "+id);
		boolean textFound = false;
		NodeList sentenceChildren = sentenceElement.getChildNodes();
		for (int index=0;index<sentenceChildren.getLength();index++)
		{
			Node child = sentenceChildren.item(index);
			if (child.getNodeType()==Node.TEXT_NODE)
			{
				String textString = child.getNodeValue().trim();
				sentences.put(id, textString);
				textFound = true;
			}
		}
		if (!textFound) throw new Rte6mainIOException("sentence text");
	}
	
	private File xmlFile;
	private Document document;
	private Element rootElement;
	private LinkedHashMap<Integer, String> sentences = new LinkedHashMap<Integer, String>();
	private String headline;
	private String dateline;
	private String documentId;
	private String documentType;
	private boolean read = false;
}
