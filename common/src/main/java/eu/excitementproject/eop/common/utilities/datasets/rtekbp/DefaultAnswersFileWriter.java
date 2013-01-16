package eu.excitementproject.eop.common.utilities.datasets.rtekbp;

import java.io.FileOutputStream;
import java.io.IOException;
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

/**
 * 
 * @author Asher Stern
 * @since Aug 23, 2010
 *
 */
public class DefaultAnswersFileWriter implements AnswersFileWriter
{

	public void setXml(String xmlFileName) throws RteKbpIOException
	{
		this.xmlFileName = xmlFileName;
	}

	public void setRootElementName(String rootElementName)
			throws RteKbpIOException
	{
		this.rootElementName = rootElementName;
	}

	public void setPairsAnswers(List<PairAnswer> answers)
			throws RteKbpIOException
	{
		this.answers = answers;
	}

	public void write() throws RteKbpIOException
	{
		if (null==xmlFileName) throw new RteKbpIOException("xml file name not set.");
		if (null==rootElementName) throw new RteKbpIOException("rootElementName not set.");
		if (null==answers) throw new RteKbpIOException("answers not set.");
		
		createDocument();
		
		try
		{
			writeXmlFile();
		}
		catch (TransformerException e)
		{
			throw new RteKbpIOException("Could not write the xml to file.",e);
		}
		catch (IOException e)
		{
			throw new RteKbpIOException("Could not write the xml to file.",e);
		}
	}
	
	private void createDocument() throws RteKbpIOException
	{

		try
		{
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element rootElement = document.createElement(this.rootElementName);
			document.appendChild(rootElement);
			for (PairAnswer pair : answers)
			{
				if (null==pair.getPairId()) throw new RteKbpIOException("null pair id.");
				if (null==pair.getAnnotation()) throw new RteKbpIOException("null annotation.");
				Element pairElement = document.createElement(PairsFileConstants.PAIR_ELEMENT_NAME);
				pairElement.setAttribute(PairsFileConstants.PAIR_ID_ATTRIBUTE_NAME, pair.getPairId());
				String annotationString = null;
				if (pair.getAnnotation().equals(EntailmentAnnotation.YES))
					annotationString = PairsFileConstants.ANNOTATION_YES;
				else if (pair.getAnnotation().equals(EntailmentAnnotation.NO))
					annotationString = PairsFileConstants.ANNOTATION_NO;
				else throw new RteKbpIOException("Unsupported annotation for pair: "+pair.getPairId());
						
				pairElement.setAttribute(PairsFileConstants.ENTAILMENT_ATTRIBUTE_NAME, annotationString);
				
				rootElement.appendChild(pairElement);
			}
		}
		catch (ParserConfigurationException e)
		{
			throw new RteKbpIOException("Cannot create document.",e);
		}

	}
	
	private void writeXmlFile() throws TransformerException, IOException
	{
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		FileOutputStream outputStream = new FileOutputStream(this.xmlFileName);
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

	
	private String xmlFileName = null;
	private String rootElementName = null;
	private List<PairAnswer> answers = null;

	
	private Document document;
	

}
