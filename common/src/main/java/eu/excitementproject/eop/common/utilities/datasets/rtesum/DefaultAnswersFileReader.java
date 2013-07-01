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
 * Implementation of {@link AnswersFileReader} using DOM.
 * 
 * @see AnswersFileReader
 * @author Asher Stern
 *
 */
public class DefaultAnswersFileReader implements AnswersFileReader
{
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
			this.readDone = false;
			
			this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.xmlFile);
			rootElement = document.getDocumentElement();
			if (null==rootElement) throw new Rte6mainIOException("rootElement is null. Corrupted XML.");
			String rootElementName = rootElement.getNodeName();
			if (null==rootElementName) throw new Rte6mainIOException("Bad xml.");
			if (!rootElementName.equals(AnswerFileConstants.ROOT_ELEMENT_NAME))
				throw new Rte6mainIOException("Bad root element name. was: "+rootElementName+", but ought to be: "+AnswerFileConstants.ROOT_ELEMENT_NAME);
			NodeList topics = rootElement.getChildNodes();
			if (topics==null) throw new Rte6mainIOException("Can\'t be.");
			if (goldStandard)
			{
				this.goldStandardContents = new LinkedHashMap<String, Map<String,HypothesisAnswer>>();
			}
			else
			{
				this.answersContents = new LinkedHashMap<String, Map<String,Set<SentenceIdentifier>>>();
				
			}
			for (int index=0;index<topics.getLength();index++)
			{
				Node node = topics.item(index);
				if (node.getNodeType()==Node.ELEMENT_NODE)
				{
					Element element = (Element) node;
					if (element.getNodeName().equals(AnswerFileConstants.TOPIC_ELEMENT_NAME))
					{
						processTopicElement(element);
					}
				}
			}
			this.readDone = true;
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


	public Map<String, Map<String, Set<SentenceIdentifier>>> getAnswers()
			throws Rte6mainIOException
	{
		if (goldStandard) throw new Rte6mainIOException("Answers were not retrieved since read was done in gold standard mode.");
		if (!readDone) throw new Rte6mainIOException("Caller\'s bug. The method read() was not called.");
		return this.answersContents;
	}

	public Map<String, Map<String, HypothesisAnswer>> getGoldStandard()
			throws Rte6mainIOException
	{
		if (!goldStandard) throw new Rte6mainIOException("Gold Standard was not retrieved since read was done in answers mode.");
		if (!readDone) throw new Rte6mainIOException("Caller\'s bug. The method read() was not called.");
		return this.goldStandardContents;

	}


	public void setGoldStandard(boolean goldStandard) throws Rte6mainIOException
	{
		this.goldStandard = goldStandard;
	}
	
	private void processTopicElement(Element topicElement) throws Rte6mainIOException
	{
		String topicId = topicElement.getAttribute(AnswerFileConstants.TOPIC_ID_ATTRIBUTE_NAME);
		if (null==topicId) throw new Rte6mainIOException("no topic id.");
		if (goldStandard)
		{
			if (goldStandardContents.containsKey(topicId)) throw new Rte6mainIOException("topic id: "+topicId+" was already handled.");
		}
		else
		{
			if (answersContents.containsKey(topicId)) throw new Rte6mainIOException("topic id: "+topicId+" was already handled.");
		}
		
		NodeList hypothesisNodes = topicElement.getChildNodes();
		if (null==hypothesisNodes) throw new Rte6mainIOException("Can\'t be.");
		Map<String, HypothesisAnswer> goldStandardForThisTopic = null;
		Map<String, Set<SentenceIdentifier>> answerForThisTopic = null;
		if (goldStandard)
		{
			goldStandardForThisTopic = new LinkedHashMap<String, HypothesisAnswer>();
		}
		else
		{
			answerForThisTopic = new LinkedHashMap<String, Set<SentenceIdentifier>>();
		}
		
		for (int index=0;index<hypothesisNodes.getLength();index++)
		{
			Node hypothesisNode = hypothesisNodes.item(index);
			if (hypothesisNode.getNodeType()==Node.ELEMENT_NODE)
			{
				Element hypothesisElement = (Element) hypothesisNode;
				if (hypothesisElement.getNodeName().equals(AnswerFileConstants.HYPOTHESIS_ELEMENT_NAME))
				{
					String hypothesisId = hypothesisElement.getAttribute(AnswerFileConstants.HYPOTHESIS_ID_ATTRIBUTE_NAME);
					if (goldStandard)
					{
						HypothesisAnswer hypothesisAnswer = processGoldStandardHypothesisElement(hypothesisElement);
						goldStandardForThisTopic.put(hypothesisId, hypothesisAnswer);
					}
					else
					{
						Set<SentenceIdentifier> hypothesisAnswer = processSubmissionLikeHypothesisElement(hypothesisElement);
						answerForThisTopic.put(hypothesisId, hypothesisAnswer);
					}
				}
			}
		}
		if (goldStandard)
		{
			this.goldStandardContents.put(topicId, goldStandardForThisTopic);
		}
		else
		{
			this.answersContents.put(topicId, answerForThisTopic);
		}
		
		
	}
	
	private HypothesisAnswer processGoldStandardHypothesisElement(Element hypothesisElement) throws Rte6mainIOException
	{
		boolean hypothesisSentenceFound = false;
		String hypothesisSentence = null;
		Set<TextSentenceAnswer> textAnswersSet = new LinkedHashSet<TextSentenceAnswer>();
		NodeList hypothesisChildren = hypothesisElement.getChildNodes();
		if (null==hypothesisChildren) throw new Rte6mainIOException("Can\'t be.");
		for (int index=0;index<hypothesisChildren.getLength();index++)
		{
			Node node = hypothesisChildren.item(index);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				if (node.getNodeName().equals(AnswerFileConstants.HYPOTHESIS_SENTENCE_ELEMENT_NAME))
				{
					Element hypothesisSentencesElement = (Element) node;
					hypothesisSentence = getTextOfElement(hypothesisSentencesElement);
					hypothesisSentenceFound = true;
				}
				else if (node.getNodeName().equals(AnswerFileConstants.TEXT_SENTENCE_ELEMENT_NAME))
				{
					Element textSentenceElement = (Element) node;
					String documentId = textSentenceElement.getAttribute(AnswerFileConstants.TEXT_SENTENCE_DOCUMENT_ID_ATTRIBUTE_NAME);
					if (null==documentId) throw new Rte6mainIOException("missing document id.");
					String sentenceId = textSentenceElement.getAttribute(AnswerFileConstants.TEXT_SENTENCE_SENTENCE_ID_ATTRIBUTE_NAME);
					if (null==sentenceId) throw new Rte6mainIOException("Missing sentence id.");
					String evaluation = textSentenceElement.getAttribute(AnswerFileConstants.TEXT_SENTENCE_EVALUATION_ATTRIBUTE_NAME);
					if (null==evaluation) throw new Rte6mainIOException("Missing evaluation attribute.");
					String textString = getTextOfElement(textSentenceElement);
					textAnswersSet.add(new TextSentenceAnswer(new SentenceIdentifier(documentId, sentenceId), textString));
				}
			}
		}
		if (!hypothesisSentenceFound) throw new Rte6mainIOException("Hypothesis text was not found.");
		return new HypothesisAnswer(hypothesisSentence,textAnswersSet);
	}
	
	private Set<SentenceIdentifier> processSubmissionLikeHypothesisElement(Element hypothesisElement) throws Rte6mainIOException
	{
		Set<SentenceIdentifier> ret = new LinkedHashSet<SentenceIdentifier>();
		NodeList hypothesisElementChildren = hypothesisElement.getChildNodes();
		if (null==hypothesisElementChildren) throw new Rte6mainIOException("Can\'t be!");
		for (int index=0;index<hypothesisElementChildren.getLength();index++)
		{
			Node node = hypothesisElementChildren.item(index);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				if (node.getNodeName().equals(AnswerFileConstants.TEXT_SENTENCE_ELEMENT_NAME))
				{
					Element textSentenceElement = (Element) node;
					String documentId = textSentenceElement.getAttribute(AnswerFileConstants.TEXT_SENTENCE_DOCUMENT_ID_ATTRIBUTE_NAME);
					if (null==documentId) throw new Rte6mainIOException("Missing document id.");
					String sentenceId = textSentenceElement.getAttribute(AnswerFileConstants.TEXT_SENTENCE_SENTENCE_ID_ATTRIBUTE_NAME);
					if (null==sentenceId) throw new Rte6mainIOException("Missing sentence id.");
					ret.add(new SentenceIdentifier(documentId, sentenceId));
				}
			}
		}
		return ret;
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



	private boolean goldStandard = false;
	
	private File xmlFile;
	private Document document;
	private Element rootElement;
	
	private Map<String, Map<String, HypothesisAnswer>> goldStandardContents = null;
	private Map<String, Map<String, Set<SentenceIdentifier>>> answersContents = null;
	private boolean readDone = false;
}

