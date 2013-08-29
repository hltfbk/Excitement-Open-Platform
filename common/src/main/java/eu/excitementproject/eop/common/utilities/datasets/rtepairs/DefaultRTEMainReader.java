package eu.excitementproject.eop.common.utilities.datasets.rtepairs;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Reads the RTE data-set XML file.
 * @author Asher Stern
 *
 */
public class DefaultRTEMainReader implements RTEMainReader
{
	
	////////////////// PUBLIC PART //////////////////////////////
	
	/////////////// CONSTANTS //////////////////////
	
	public static String PAIR_ELEMENT_NAME = "pair";
	public static String TEXT_ELEMENT_NAME = "t";
	public static String HYPOTHESIS_ELEMENT_NAME = "h";
	public static String ID_ATTRIBUTE_NAME = "id";
	public static String CLASSIFICATION_ATTRIBUTE_NAME = "entailment";
	public static String ALT_CLASSIFICATION_ATTRIBUTE_NAME = "value";
	public static String TASK_ATTRIBUTE_NAME = "task";
	public static Map<String,RTEClassificationType> classificationMap;
	public static Map<String,Boolean> booleanClassificationMap;
	static
	{
		classificationMap = new LinkedHashMap<String, RTEClassificationType>();
		classificationMap.put("ENTAILMENT",RTEClassificationType.ENTAILMENT);
		classificationMap.put("UNKNOWN",RTEClassificationType.UNKNOWN);
		classificationMap.put("CONTRADICTION",RTEClassificationType.CONTRADICTION);
		
		booleanClassificationMap = new LinkedHashMap<String, Boolean>();
		booleanClassificationMap.put("FALSE", new Boolean(false));
		booleanClassificationMap.put("TRUE", new Boolean(true));
		booleanClassificationMap.put("NO", new Boolean(false));
		booleanClassificationMap.put("YES", new Boolean(true));
		booleanClassificationMap.put("NONENTAILMENT", new Boolean(false));

	}
	
	//////////////////// PUBLIC METHODS /////////////////////////////

	public void setXmlFile(File xmlFile)
	{
		this.xmlFile = xmlFile;
	}
	
	public void setHasClassification()
	{
		hasClassification = true;
	}

	
	
	public void read() throws RTEMainReaderException
	{
		validateReadParameters();
		getDocument();
		getPairElementList();
		fillMapPairs();
	}

	
	public Map<Integer, TextHypothesisPair> getMapIdToPair() throws RTEMainReaderException
	{
		if (null==mapPairs)
			throw new RTEMainReaderException("Caller\'s bug. Need to call read() before calling getMapIdToPair()");
		
		return this.mapPairs;
	}

	//////////////////// PRIVATE & PROTECTED PART ///////////////////////////
	
	
	
	protected void validateReadParameters() throws RTEMainReaderException
	{
		if (null==this.xmlFile)
			throw new RTEMainReaderException("null xml file. Did you forget to call setXmlFile() method?");
		if (this.xmlFile.exists()) ;
		else throw new RTEMainReaderException("The given file: "+this.xmlFile.getAbsolutePath()+" does not exist.");
		if (this.xmlFile.isFile()) ;
		else throw new RTEMainReaderException("The given file: "+this.xmlFile.getAbsolutePath()+" is not a file.");
		
	}
	
	protected void getDocument() throws RTEMainReaderException
	{
		try
		{
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.xmlFile);
		}
		catch(ParserConfigurationException e)
		{
			throw new RTEMainReaderException("read xml file failed. See nested exception.",e);
		} catch (SAXException e)
		{
			throw new RTEMainReaderException("read xml file failed. See nested exception.",e);
		}
		catch (IOException e)
		{
			throw new RTEMainReaderException("read xml file failed. See nested exception.",e);
		}
	}
	
	protected void getPairElementList()
	{
		listAllPairsElements = document.getDocumentElement().getElementsByTagName(PAIR_ELEMENT_NAME);
	}
	
	protected TextHypothesisPair getPairOf(Element element) throws RTEMainReaderException
	{
		TextHypothesisPair ret = null;
		RTEClassificationType classificationType = null;
		Boolean booleanClassificationType = null;
		String text;
		String hypothesis;
		String task;
		Integer id;
		
		String classificationString = null;
		if (element.hasAttribute(CLASSIFICATION_ATTRIBUTE_NAME))
		{
			classificationString = element.getAttribute(CLASSIFICATION_ATTRIBUTE_NAME);
		}
		else if (element.hasAttribute(ALT_CLASSIFICATION_ATTRIBUTE_NAME))
		{
			classificationString = element.getAttribute(ALT_CLASSIFICATION_ATTRIBUTE_NAME);
		}
		if (classificationString!=null)
		{
			if (classificationMap.containsKey(classificationString))
			{
				classificationType = classificationMap.get(classificationString);
			}
			else if (booleanClassificationMap.containsKey(classificationString))
			{
				booleanClassificationType = booleanClassificationMap.get(classificationString);
			}
			else if (classificationString.isEmpty())
			{
				if (hasClassification)
					throw new RTEMainReaderException("No classification is specified, though that file should contain classification for every pair.");
			}
			else throw new RTEMainReaderException("bad classification attribute value: "+classificationString);
		}
		else if (hasClassification)
			throw new RTEMainReaderException("No classification is specified, though that file should contain classification for every pair.");
		 
		
		
		String idString = element.getAttribute(ID_ATTRIBUTE_NAME);
		try
		{
			id = new Integer(idString);
		}
		catch(NumberFormatException e)
		{
			throw new RTEMainReaderException("bad ID: "+idString+" see nested exception.",e);
		}
		task = element.getAttribute(TASK_ATTRIBUTE_NAME);
		
		try
		{
			NodeList textNodeList = element.getElementsByTagName(TEXT_ELEMENT_NAME);
			if (textNodeList.getLength()!=1)
				throw new RTEMainReaderException("Malformed XML. Number text elements is not 1.");
			Element textElement = (Element) textNodeList.item(0);
			
			NodeList hypothesisNodeList = element.getElementsByTagName(HYPOTHESIS_ELEMENT_NAME);
			if (hypothesisNodeList.getLength()!=1)
				throw new RTEMainReaderException("Malformed XML. Number hypothesis elements is not 1.");
			Element hypothesisElement = (Element) hypothesisNodeList.item(0);
			
			
			org.w3c.dom.Text textText = (org.w3c.dom.Text) textElement.getFirstChild();
			org.w3c.dom.Text hypothesisText = (org.w3c.dom.Text) hypothesisElement.getFirstChild();
			text = textText.getNodeValue().trim();
			hypothesis = hypothesisText.getNodeValue().trim();
			
			if (classificationType!=null)
				ret = new TextHypothesisPair(text, hypothesis, id, classificationType, task);
			else if (booleanClassificationType!=null)
				ret = new TextHypothesisPair(text, hypothesis, id, booleanClassificationType, task);
			else
				ret = new TextHypothesisPair(text, hypothesis, id, task);
		}
		catch(Exception e)
		{
			throw new RTEMainReaderException("Malformed XML file "+this.xmlFile+". See nested exception.",e);
		}
		
		
		return ret;
	}
	
	protected void fillMapPairs() throws RTEMainReaderException
	{
		mapPairs = new LinkedHashMap<Integer, TextHypothesisPair>();
		LinkedHashSet<Integer> alreadyReadIds = new LinkedHashSet<Integer>();
		for (int index=0;index<listAllPairsElements.getLength();++index)
		{
			try
			{
				Element pairElement = (Element) listAllPairsElements.item(index);
				TextHypothesisPair pair = getPairOf(pairElement);
				if (alreadyReadIds.contains(pair.getId()))
					throw new RTEMainReaderException("The same Id appears twice. id = "+pair.getId().toString());
				alreadyReadIds.add(pair.getId());
				this.mapPairs.put(pair.getId(), pair);
			}
			catch(Exception e)
			{
				throw new RTEMainReaderException("Malformed XML "+this.xmlFile.getAbsolutePath()+". See nested exception.",e);
			}
		}
		
		
	}

	///////////// PROTECTED & PRIVATE FIELDS //////////////////////
	
	protected File xmlFile;
	
	/**
	 * true means - if classification is not specified, then throw an exception
	 */
	protected boolean hasClassification = false; 
 
	protected Document document;
	protected NodeList listAllPairsElements;
	protected Map<Integer, TextHypothesisPair> mapPairs = null;

}
