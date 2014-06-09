package eu.excitementproject.eop.common.utilities.corpora.reuters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.corpora.CorporaException;
import eu.excitementproject.eop.common.utilities.corpora.DocumentReader;
import eu.excitementproject.eop.common.utilities.xmldom.XmlDomUtilitiesException;
import eu.excitementproject.eop.common.utilities.xmldom.XmlDomUtils;


/**
 * A {@link DocumentReader} that reads the Reuters-corpus documents.
 * Each document is an XML file, with some meta-data, and the document contents.
 * 
 * @see ReutersCorpusReader
 * 
 * @author Asher Stern
 * @since Oct 18, 2012
 *
 */
public class ReutersDocumentReader implements DocumentReader
{
	public static final String TEXT_ELEMENT_NAME = "text";
	public static final String PARAGRAPH_ELEMENT_NAME = "p";
	
	public ReutersDocumentReader(File reutersDocumentFile)
	{
		super();
		this.reutersDocumentFile = reutersDocumentFile;
	}

	public void read() throws CorporaException
	{
		try
		{
			document = XmlDomUtils.getDocument(reutersDocumentFile);
			Element textElement = getTextElement(); 
			List<String> paragraphs = getParagraphs(textElement);
			documentContents = StringUtil.joinIterableToString(paragraphs, " ");
		}
		catch (XmlDomUtilitiesException e)
		{
			throw new CorporaException("Failed to read XML file. See nested exception.",e);
		}
	}

	public String getDocumentContents() throws CorporaException
	{
		if (null==documentContents) throw new CorporaException("XML was not read.");
		return this.documentContents;
	}
	
	
	protected Element getTextElement() throws XmlDomUtilitiesException
	{
		Element rootElement = document.getDocumentElement();
		return XmlDomUtils.getChildElement(rootElement, TEXT_ELEMENT_NAME);
	}
	
	protected List<String> getParagraphs(Element textElement) throws XmlDomUtilitiesException
	{
		List<Element> paragraphElements =
				XmlDomUtils.getChildElements(textElement, PARAGRAPH_ELEMENT_NAME);
		ArrayList<String> ret = new ArrayList<String>(paragraphElements.size());
		for (Element paragraphElement : paragraphElements)
		{
			try
			{
				String textOfElement = XmlDomUtils.getTextOfElement(paragraphElement,false);
				if (textOfElement!=null)
				{
					ret.add(textOfElement);
				}
			}
			catch(XmlDomUtilitiesException e)
			{
				if (ret.size()>0)
				{
					throw new XmlDomUtilitiesException("Failed to read a paragraph. Last read paragraph was: \""+ret.get(ret.size()-1)+"\".\nSee nested exception.",e);
				}
				else
				{
					throw new XmlDomUtilitiesException("Failed to read the first paragraph. See nested exception",e);
				}
			}
		}
		return ret;
	}
	
	

	
	

	// input
	protected File reutersDocumentFile;
	
	// internals
	protected Document document;
	
	// output
	protected String documentContents = null;
}
