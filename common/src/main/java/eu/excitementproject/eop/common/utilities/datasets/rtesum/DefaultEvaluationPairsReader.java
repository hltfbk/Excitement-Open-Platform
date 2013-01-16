package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Implementation of {@link EvaluationPairsReader} using DOM.
 * @see EvaluationPairsReader
 * @author Asher Stern
 *
 */
public class DefaultEvaluationPairsReader implements EvaluationPairsReader
{
	public static final String ROOT_ELEMENT_NAME = "topic";
	public static final String TOPIC_ID_ATTRIBUTE_NAME = "id";
	public static final String HYPOTHEIS_ELEMENT_NAME = "H";
	public static final String HYPOTHEIS_ID_ATTRIBUTE_NAME = "h_id";
	public static final String CANDIDATE_ELEMENT_NAME = "CANDIDATE";
	public static final String CANDIDATE_DOCUMENT_ID_ATTRIBUTE_NAME = "doc_id";
	public static final String CANDIDATE_SENTENCE_ID_ATTRIBUTE_NAME = "s_id";
	
	
	
	
	
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
			if (rootElement.getNodeName().equals(ROOT_ELEMENT_NAME)) ;
			else throw new Rte6mainIOException("Bad root element name. Bad XML.");
			this.topicId = rootElement.getAttribute(TOPIC_ID_ATTRIBUTE_NAME);
			if (null==topicId) throw new Rte6mainIOException("No topic id.");
			NodeList hypothesisNodes = rootElement.getChildNodes();
			for (int index=0;index<hypothesisNodes.getLength();index++)
			{
				Node hypothesisNode = hypothesisNodes.item(index);
				if (hypothesisNode.getNodeType()==Node.ELEMENT_NODE)
				{
					Element hypothesisElement = (Element) hypothesisNode;
					if (!hypothesisElement.getNodeName().equals(HYPOTHEIS_ELEMENT_NAME))
						throw new Rte6mainIOException("Bad xml. Unrecognized element where hypothesis element is expected.");
					processHypothesisElement(hypothesisElement);
				}
			}
			readDone=true;
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



	public String getTopicId() throws Rte6mainIOException
	{
		if (!readDone) throw new Rte6mainIOException("Caller\'s bug. read() was not called.");
		return this.topicId;
	}
	
	public Map<String, Set<SentenceIdentifier>> getCandidateSentencesMap() throws Rte6mainIOException
	{
		if (!readDone) throw new Rte6mainIOException("Caller\'s bug. read() was not called.");
		return this.candidateSentencesMap;
	}


	private void processHypothesisElement(Element hypothesisElement) throws Rte6mainIOException
	{
		String hypothesisId = hypothesisElement.getAttribute(HYPOTHEIS_ID_ATTRIBUTE_NAME);
		if (null==hypothesisId) throw new Rte6mainIOException("Bad (null) hypothesis id.");
		if (this.candidateSentencesMap.containsKey(hypothesisId))
			throw new Rte6mainIOException("The hypothesis: id="+hypothesisId+" already was handled. Seems like bad XML.");
		Set<SentenceIdentifier> candidateSentencesSet = new LinkedHashSet<SentenceIdentifier>();
		NodeList candidateSentencesNodeList = hypothesisElement.getChildNodes();
		for (int index=0;index<candidateSentencesNodeList.getLength();index++)
		{
			Node candidateNode = candidateSentencesNodeList.item(index);
			if (candidateNode.getNodeType()==Node.ELEMENT_NODE)
			{
				Element candidateElement = (Element) candidateNode;
				String candidateElementName = candidateElement.getNodeName();
				if (null==candidateElementName) throw new Rte6mainIOException("Bad (null) candidate name");
				if (!candidateElementName.equals(CANDIDATE_ELEMENT_NAME))
					throw new Rte6mainIOException("bad candidate name");
				String documentId = candidateElement.getAttribute(CANDIDATE_DOCUMENT_ID_ATTRIBUTE_NAME);
				if (null==documentId) throw new Rte6mainIOException("Bad (null) document id.");
				String sentenceId = candidateElement.getAttribute(CANDIDATE_SENTENCE_ID_ATTRIBUTE_NAME);
				if (null==sentenceId) throw new Rte6mainIOException("Bad (null) sentence id.");
				
				candidateSentencesSet.add(new SentenceIdentifier(documentId, sentenceId));
			}
		}
		this.candidateSentencesMap.put(hypothesisId, candidateSentencesSet);
	}


	private File xmlFile;
	private Document document;
	private Element rootElement;
	private String topicId;
	private Map<String, Set<SentenceIdentifier>> candidateSentencesMap = new LinkedHashMap<String, Set<SentenceIdentifier>>();
	private boolean readDone=false; 






}

