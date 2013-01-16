package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DefaultHypothesisFileReader implements HypothesisFileReader
{
	public static final String ROOT_ELEMENT_NAME = "HYPOTHESES";
	public static final String HYPOTHESIS_ELEMENT_NAME = "H";
	public static final String HYPOTHESIS_ID_ATTRIBUTE_NAME = "h_id";
	public static final String HYPOTHESIS_TEXT_ELEMENT_NAME = "text";
	public static final String HYPOTHESIS_REF_ELEMENT_NAME = "ref";
	public static final String HYPOTHESIS_REF_DOCUMENT_ID_ATTRIBUTE_NAME = "doc_id";
	public static final String HYPOTHESIS_REF_SENTENCE_ID_ATTRIBUTE_NAME = "s_id";
	
	
	
	
	public void setXml(String xmlFileName) throws Rte6mainIOException
	{
		if (null==xmlFileName) throw new Rte6mainIOException("null==xmlFileName");
		this.xmlFile = new File(xmlFileName);
		if (!xmlFile.exists()) throw new Rte6mainIOException("XML file does not exist ("+xmlFile.getAbsolutePath()+").");
		if (!xmlFile.isFile()) throw new Rte6mainIOException("Given XML file ("+xmlFile.getAbsolutePath()+") is not a file");
	}

	public void read() throws Rte6mainIOException
	{
		try
		{
			this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.xmlFile);
			rootElement = document.getDocumentElement();
			if (null==rootElement) throw new Rte6mainIOException("rootElement is null. Corrupted XML.");
			String rootElementName = rootElement.getNodeName();
			if (null==rootElementName) throw new Rte6mainIOException("Bad xml.");
			if (!rootElementName.equals(ROOT_ELEMENT_NAME))
				throw new Rte6mainIOException("Bad root element name. Was: "+rootElementName);
			NodeList hypothesisNodes = rootElement.getChildNodes();
			if (null==hypothesisNodes) throw new Rte6mainIOException("BUG");
			for (int index=0;index<hypothesisNodes.getLength();index++)
			{
				Node hypothesisNode = hypothesisNodes.item(index);
				if (hypothesisNode.getNodeType()==Node.ELEMENT_NODE)
				{
					Element hypotehsisElement = (Element) hypothesisNode;
					if (!hypotehsisElement.getNodeName().equals(HYPOTHESIS_ELEMENT_NAME))
						throw new Rte6mainIOException("Bad xml. Unrecognized element where hypothesis element is expected.");
					processHypothesisElement(hypotehsisElement);
				}
				
			}
			

		}
		catch (SAXException e)
		{
			throw new Rte6mainIOException("Problem when reading XML file: "+this.xmlFile.getAbsolutePath()+". See nested exception.");
		}
		catch (IOException e)
		{
			throw new Rte6mainIOException("Problem when reading XML file: "+this.xmlFile.getAbsolutePath()+". See nested exception.");			
		}
		catch (ParserConfigurationException e)
		{
			throw new Rte6mainIOException("Problem when reading XML file: "+this.xmlFile.getAbsolutePath()+". See nested exception.");			
		}
		
	}



	public Map<String, HypothesisRef> getHypothesisRefMap()
			throws Rte6mainIOException
	{
		return this.mapHypothesisRef;
	}

	public Map<String, String> getHypothesisTextMap()
			throws Rte6mainIOException
	{
		return this.mapHypothesisText;
	}
	
	
	private void processHypothesisElement(Element hypotehsisElement) throws Rte6mainIOException
	{
		String hypothesisId = hypotehsisElement.getAttribute(HYPOTHESIS_ID_ATTRIBUTE_NAME);
		if (mapHypothesisText.containsKey(hypothesisId))
			throw new Rte6mainIOException("duplicate hypothesis id: "+hypothesisId);

		boolean textFound = false;
		boolean refFound = false;
		String hypothesisText = null;
		HypothesisRef ref = null;
		NodeList hypothesisElementChildren = hypotehsisElement.getChildNodes();
		if (null==hypothesisElementChildren) throw new Rte6mainIOException("bad hypothesis element. null children.");
		for (int index=0;index<hypothesisElementChildren.getLength();index++)
		{
			Node hypothesisChildNode = hypothesisElementChildren.item(index);
			if (hypothesisChildNode.getNodeType()==Node.ELEMENT_NODE)
			{
				Element hypothesisChildElement = (Element) hypothesisChildNode;
				if (hypothesisChildElement.getNodeName().equals(HYPOTHESIS_TEXT_ELEMENT_NAME))
				{
					hypothesisText = getTextOfElement(hypothesisChildElement);
					if (hypothesisText==null) throw new Rte6mainIOException("no text for hypothesis");
					mapHypothesisText.put(hypothesisId, hypothesisText);
					textFound = true;
				}
				else if (hypothesisChildElement.getNodeName().equals(HYPOTHESIS_REF_ELEMENT_NAME))
				{
					String docId = hypothesisChildElement.getAttribute(HYPOTHESIS_REF_DOCUMENT_ID_ATTRIBUTE_NAME);
					if (docId==null) throw new Rte6mainIOException("Bad ref. No doc id.");
					String sentenceId =  hypothesisChildElement.getAttribute(HYPOTHESIS_REF_SENTENCE_ID_ATTRIBUTE_NAME);
					if (sentenceId==null) throw new Rte6mainIOException("Bad ref. No sentence id.");
					String refText = getTextOfElement(hypothesisChildElement);
					ref = new HypothesisRef(new SentenceIdentifier(docId, sentenceId), refText);
					mapHypothesisRef.put(hypothesisId,ref);
					refFound = true;
				}
			}
		}
		if (!textFound) throw new Rte6mainIOException("Text not found for hypothesis: "+hypothesisId);
		if (!refFound) throw new Rte6mainIOException("Ref not found for hypothesis: "+hypothesisId);
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
	

	private File xmlFile;
	private Document document;
	private Element rootElement;
	
	private Map<String,String> mapHypothesisText = new LinkedHashMap<String, String>();
	private Map<String,HypothesisRef> mapHypothesisRef = new LinkedHashMap<String, HypothesisRef>();

}


