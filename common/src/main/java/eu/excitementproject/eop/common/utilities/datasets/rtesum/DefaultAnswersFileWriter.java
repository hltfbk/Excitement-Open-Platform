package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

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


/**
 * Implementation of {@link AnswersFileWriter} using DOM.
 * 
 * Writes answers file as required in RTE6 main task.
 * 
 * @see AnswersFileWriter
 * 
 * @author Asher Stern
 *
 */
public class DefaultAnswersFileWriter implements AnswersFileWriter
{
	//////////////////// PUBLIC PART ///////////////////////
	
	public static final String ROOT_ELEMENT_NAME = "entailment_corpus";
	public static final String TOPIC_ELEMENT_NAME = "TOPIC";
	public static final String TOPIC_ID_ATTRIBUTE_NAME = "t_id";
	public static final String HYPOTHESIS_ELEMENT_NAME = "H";
	public static final String HYPOTHESIS_ID_ATTRIBUTE_NAME = "h_id";
	public static final String TEXT_ANSWER_ELEMENT_NAME = "text";
	public static final String TEXT_ANSWER_DOC_ID_ATTRIBUTE_NAME = "doc_id";
	public static final String TEXT_ANSWER_SENTENCE_ID_ATTRIBUTE_NAME = "s_id";
	public static final String TEXT_ANSWER_EVALUATION_ATTRIBUTE_NAME = "evaluation";
	public static final String TEXT_ANSWER_EVALUATION_YES_ATTRIBUTE_VALUE = "YES";
	
	
	public void setAnswers(Map<String, Map<String, Set<SentenceIdentifier>>> answers) throws Rte6mainIOException
	{
		this.answers = answers;
	}

	public void setXml(String xmlFileName) throws Rte6mainIOException
	{
		if (null==xmlFileName) throw new Rte6mainIOException("null file name");
		xmlFile = new File(xmlFileName);
	}
	
	public void setWriteTheEvaluationAttribute(boolean writeEvaluation)
	{
		this.writeEvaluation = writeEvaluation;
	}
	

	public void write() throws Rte6mainIOException
	{
		try
		{
			createAndFillDocument();
			writeDocumentToFile();
		}
		catch (ParserConfigurationException e)
		{
			throw new Rte6mainIOException("Write failed. See nested exception",e);
		}
		catch (TransformerException e)
		{
			throw new Rte6mainIOException("Write failed. See nested exception",e);
		}
		catch (IOException e)
		{
			throw new Rte6mainIOException("Write failed. See nested exception",e);
		}
	}
	
	
	
	

	protected void createAndFillDocument() throws Rte6mainIOException, ParserConfigurationException
	{
		if (null==answers) throw new Rte6mainIOException("answers were not set.");
		document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element rootElement = document.createElement(ROOT_ELEMENT_NAME);
		document.appendChild(rootElement);
		for (String topicId : answers.keySet())
		{
			Element topicElement = document.createElement(TOPIC_ELEMENT_NAME);
			topicElement.setAttribute(TOPIC_ID_ATTRIBUTE_NAME, topicId);
			Map<String,Set<SentenceIdentifier>> answersForTopic = answers.get(topicId);
			appendAnswersForTopic(topicElement,answersForTopic);
			rootElement.appendChild(topicElement);
		}
	}
	
	protected void appendAnswersForTopic(Element topicElement, Map<String,Set<SentenceIdentifier>>answersForTopic)
	{
		for (String hypothesisId : answersForTopic.keySet())
		{
			Element hypothesisElement = document.createElement(HYPOTHESIS_ELEMENT_NAME);
			hypothesisElement.setAttribute(HYPOTHESIS_ID_ATTRIBUTE_NAME, hypothesisId);
			Set<SentenceIdentifier> answersForHypothesis = answersForTopic.get(hypothesisId);
			for (SentenceIdentifier sid : answersForHypothesis)
			{
				Element answerTextElement = document.createElement(TEXT_ANSWER_ELEMENT_NAME);
				answerTextElement.setAttribute(TEXT_ANSWER_DOC_ID_ATTRIBUTE_NAME, sid.getDocumentId());
				answerTextElement.setAttribute(TEXT_ANSWER_SENTENCE_ID_ATTRIBUTE_NAME, sid.getSentenceId());
				if (writeEvaluation)
					answerTextElement.setAttribute(TEXT_ANSWER_EVALUATION_ATTRIBUTE_NAME, TEXT_ANSWER_EVALUATION_YES_ATTRIBUTE_VALUE);
				
				hypothesisElement.appendChild(answerTextElement);
			}
			
			topicElement.appendChild(hypothesisElement);
		}
	}
	
	protected void writeDocumentToFile() throws TransformerException, IOException
	{
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		FileOutputStream outputStream = new FileOutputStream(this.xmlFile);
		try
		{
			StreamResult streamResult = new StreamResult(outputStream);
			DOMSource source = new DOMSource(this.document);
			trans.transform(source, streamResult);
		}
		finally
		{
			outputStream.close();
		}
	}
	
	protected File xmlFile;
	protected Map<String, Map<String, Set<SentenceIdentifier>>> answers = null;
	protected boolean writeEvaluation = true;
	
	protected Document document;

}

