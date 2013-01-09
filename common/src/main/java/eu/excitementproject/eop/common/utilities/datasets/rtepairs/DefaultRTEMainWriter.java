package eu.excitementproject.eop.common.utilities.datasets.rtepairs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
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
import org.w3c.dom.Text;

/**
 * Implementation Of {@link RTEMainWriter} using DOM.
 * 
 * @author Asher Stern
 *
 */
public class DefaultRTEMainWriter extends RTEMainWriter
{
	// constants
	public static final String PAIR_ELEMENT_NAME = "pair";
	public static final String ATTRIBUTE_ID_NAME = "id";
	public static final String ATTRIBUTE_ENTAILMENT_NAME = "entailment";
	public static final String BOOLEAN_CLASSIFICATION_YES_VALUE = "YES";
	public static final String BOOLEAN_CLASSIFICATION_NO_VALUE = "NO";
	public static final String ATTRIBUTE_TASK_NAME = "task";
	public static final String TEXT_ELEMENT_NAME = "t";
	public static final String HYPOTHESIS_ELEMENT_NAME = "h";
	public static final String ROOT_ELEMENT_NAME = "entailment-corpus";
	
	
	
	
	
	public DefaultRTEMainWriter(File output, List<TextHypothesisPair> pairs) throws RTEMainWriterException
	{
		super(output, pairs);
	}

	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.datasets.RTEMainWriter#write()
	 */
	@Override
	public void write() throws RTEMainWriterException
	{
		try
		{
			createDocument();
			appendAll();
			writeDocumentToFile();
		}
		catch (ParserConfigurationException e)
		{
			throw new RTEMainWriterException("Could not create document",e);
		}
		catch (TransformerException e)
		{
			throw new RTEMainWriterException("could not write document to file",e);
		}
		catch (IOException e)
		{
			throw new RTEMainWriterException("could not write document to file",e);
		}
	}
	
	protected Element createPair(TextHypothesisPair pair)
	{
		Element ret = document.createElement(PAIR_ELEMENT_NAME);
		ret.setAttribute(ATTRIBUTE_ID_NAME, pair.getId().toString());
		if (pair.getClassificationType()!=null)
		{
			ret.setAttribute(ATTRIBUTE_ENTAILMENT_NAME, pair.getClassificationType().toString());
		}
		else if (pair.getBooleanClassificationType()!=null)
		{
			String entailment = null;
			if (pair.getBooleanClassificationType().booleanValue())
				entailment = BOOLEAN_CLASSIFICATION_YES_VALUE;
			else
				entailment = BOOLEAN_CLASSIFICATION_NO_VALUE;
			
			ret.setAttribute(ATTRIBUTE_ENTAILMENT_NAME, entailment);
		}
		
		if (pair.getAdditionalInfo()!=null)
		{
			ret.setAttribute(ATTRIBUTE_TASK_NAME, pair.getAdditionalInfo());
		}
		
		Element textElement = document.createElement(TEXT_ELEMENT_NAME);
		Text textText = document.createTextNode(pair.getText());
		textElement.appendChild(textText);
		ret.appendChild(textElement);
		
		Element hypothesisElement = document.createElement(HYPOTHESIS_ELEMENT_NAME);
		Text hypothesisText = document.createTextNode(pair.getHypothesis());
		hypothesisElement.appendChild(hypothesisText);
		ret.appendChild(hypothesisElement);
		
		return ret;
	}
	
	
	protected Element createRootElement()
	{
		return document.createElement(ROOT_ELEMENT_NAME);
	}
	
	protected void appendAll()
	{
		Element root = createRootElement();
		document.appendChild(root);
		for (TextHypothesisPair pair : this.pairs)
		{
			Element pairElement = createPair(pair);
			root.appendChild(pairElement);
		}
	}
	
	protected void writeDocumentToFile() throws TransformerException, IOException
	{
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		//StreamResult streamResult = new StreamResult(this.output);
		FileOutputStream outputStream = new FileOutputStream(this.output);
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
	
	protected void createDocument() throws ParserConfigurationException
	{
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        this.document = docBuilder.newDocument();
	}
	
	
	
	protected Document document;

}
