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

public class DefaultEvaluationPairsWriter implements EvaluationPairsWriter
{

	public void setXml(String xmlFileName) throws Rte6mainIOException
	{
		if (null==xmlFileName) throw new Rte6mainIOException("null xml file name");
		this.xmlFile = new File(xmlFileName);
	}

	public void setTopicId(String topicId) throws Rte6mainIOException
	{
		if (null==topicId) throw new Rte6mainIOException("Null topic id");
		this.topicId = topicId;
	}

	public void setCandidates(Map<String, Set<SentenceIdentifier>> candidates)
			throws Rte6mainIOException
	{
		if (null==candidates) throw new Rte6mainIOException("null candidates");
		this.candidates = candidates;
	}

	public void write() throws Rte6mainIOException
	{
		try
		{
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element rootElement = document.createElement(DefaultEvaluationPairsReader.ROOT_ELEMENT_NAME);
			rootElement.setAttribute(DefaultEvaluationPairsReader.TOPIC_ID_ATTRIBUTE_NAME, topicId);
			document.appendChild(rootElement);
			for (String hypothesisId : candidates.keySet())
			{
				appendHypothesisCandidates(rootElement,hypothesisId);
			}
			
			writeXmlFile();
		}
		catch (ParserConfigurationException e)
		{
			throw new Rte6mainIOException("DOM problem.",e);
		}
		catch (TransformerException e)
		{
			throw new Rte6mainIOException("DOM write problem.",e);
		}
		catch (IOException e)
		{
			throw new Rte6mainIOException("DOM write problem.",e);
		}
		
		
	}
	
	private void appendHypothesisCandidates(Element rootElement, String hypothesisId)
	{
		Element hypothesisElement = document.createElement(DefaultEvaluationPairsReader.HYPOTHEIS_ELEMENT_NAME);
		hypothesisElement.setAttribute(DefaultEvaluationPairsReader.HYPOTHEIS_ID_ATTRIBUTE_NAME, hypothesisId);
		
		Set<SentenceIdentifier> sentences = candidates.get(hypothesisId);
		for (SentenceIdentifier sid : sentences)
		{
			Element candidateElement = document.createElement(DefaultEvaluationPairsReader.CANDIDATE_ELEMENT_NAME);
			candidateElement.setAttribute(DefaultEvaluationPairsReader.CANDIDATE_DOCUMENT_ID_ATTRIBUTE_NAME, sid.getDocumentId());
			candidateElement.setAttribute(DefaultEvaluationPairsReader.CANDIDATE_SENTENCE_ID_ATTRIBUTE_NAME, sid.getSentenceId());
			hypothesisElement.appendChild(candidateElement);
		}
		
		rootElement.appendChild(hypothesisElement);
	}
	
	private void writeXmlFile() throws TransformerException, IOException
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

	private File xmlFile;
	private String topicId;
	private Map<String, Set<SentenceIdentifier>> candidates;
	
	private Document document;
}
