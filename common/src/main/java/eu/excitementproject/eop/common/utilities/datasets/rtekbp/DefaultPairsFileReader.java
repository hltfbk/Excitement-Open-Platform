package eu.excitementproject.eop.common.utilities.datasets.rtekbp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * 
 * @author Asher Stern
 * @since Aug 23, 2010
 *
 */
public class DefaultPairsFileReader implements PairsFileReader
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

	public void setGoldStandard(boolean isGoldStandard)
			throws RteKbpIOException
	{
		this.isGoldStandard = isGoldStandard;
	}

	public void read() throws RteKbpIOException
	{
		if (null==this.xmlFileName) throw new RteKbpIOException("xml name not set.");
		if (null==this.rootElementName) throw new RteKbpIOException("root element name not set.");
		
		File xmlFile = new File(this.xmlFileName);
		
		try
		{
			this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
			Element rootElement = document.getDocumentElement();
			NodeList nodeListChildren = rootElement.getChildNodes();
			if (null==nodeListChildren) throw new RteKbpIOException("null children!");
			
			this.pairs = new ArrayList<PairInformation>(nodeListChildren.getLength()+1); // +1 is not necessary... I know...
			for (int childrenIndex = 0; childrenIndex < nodeListChildren.getLength(); ++childrenIndex)
			{
				Node nodeChild = nodeListChildren.item(childrenIndex);
				if (nodeChild.getNodeType()==Node.ELEMENT_NODE)
				{
					Element elementChild = (Element) nodeChild;
					processElementChild(elementChild);
					
				}
			}
			this.readDone = true;
		}
		catch (SAXException e)
		{
			throw new RteKbpIOException("XML parse exception. See nested exception",e);
		}
		catch (IOException e)
		{
			throw new RteKbpIOException("XML parse exception. See nested exception",e);
		}
		catch (ParserConfigurationException e)
		{
			throw new RteKbpIOException("XML parse exception. See nested exception",e);
		}

		

	}

	public List<PairInformation> getPairs() throws RteKbpIOException
	{
		if (!readDone) throw new RteKbpIOException("Not read yet!");
		return this.pairs;
	}
	
	
	
	private void processElementChild(Element elementChild) throws RteKbpIOException
	{
		if (elementChild.getNodeName().equals(PairsFileConstants.PAIR_ELEMENT_NAME))
		{
			String pairId = elementChild.getAttribute(PairsFileConstants.PAIR_ID_ATTRIBUTE_NAME);
			String query =  elementChild.getAttribute(PairsFileConstants.QUERY_ATTRIBUTE_NAME);
			String entityType = elementChild.getAttribute(PairsFileConstants.ENTITY_TYPE_ATTRIBUTE_NAME);
			String attribute = elementChild.getAttribute(PairsFileConstants.ATTRIBUTE_ATTRIBUTE_NAME);
			String entailment = null;
			if (isGoldStandard)
				entailment = elementChild.getAttribute(PairsFileConstants.ENTAILMENT_ATTRIBUTE_NAME);
			
			if (null==pairId) throw new RteKbpIOException("missing pairId");
			if (null==query) throw new RteKbpIOException("missing query");
			if (null==entityType) throw new RteKbpIOException("missing entityType");
			if (null==attribute) throw new RteKbpIOException("missing attribute");
			if (isGoldStandard)
				if (null==entailment) throw new RteKbpIOException("missing entailment");
			
			String entity = null;
			String value = null;
			String textDocId = null;
			LinkedHashMap<Integer, String> mapHypotheses = new LinkedHashMap<Integer, String>();
			
			NodeList pairChildNodes = elementChild.getChildNodes();
			if (null==pairChildNodes) throw new RteKbpIOException("null pairChildNodes");
			for (int pairChildNodesIndex=0; pairChildNodesIndex<pairChildNodes.getLength();++pairChildNodesIndex)
			{
				Node pairChildNode = pairChildNodes.item(pairChildNodesIndex);
				if (pairChildNode.getNodeType()==Node.ELEMENT_NODE)
				{
					Element pairChildElement = (Element) pairChildNode;
					String pairChildElementName = pairChildElement.getNodeName();
					if (pairChildElementName.equals(PairsFileConstants.ENTITY_ELEMENT_NAME))
					{
						entity = getTextOfElement(pairChildElement);
					}
					else if (pairChildElementName.equals(PairsFileConstants.VALUE_ELEMENT_NAME))
					{
						value = getTextOfElement(pairChildElement);
					}
					else if (pairChildElementName.equals(PairsFileConstants.TEXT_ELEMENT_NAME))
					{
						textDocId = getTextOfElement(pairChildElement);
					}
					else if (pairChildElementName.equals(PairsFileConstants.HYPOTHESIS_ELEMENT_NAME))
					{
						String hIdStr = pairChildElement.getAttribute(PairsFileConstants.HYPOTHESIS_ID_ATTRIBUTE_NAME);
						if (null==hIdStr) throw new RteKbpIOException("hypothesis has no id! was null.");
						int hId = 0;
						try{hId = Integer.parseInt(hIdStr);}catch(NumberFormatException e){throw new RteKbpIOException("Failed to get hypothesis id. Was: \""+hIdStr+"\"",e);}
						String hypothesis = getTextOfElement(pairChildElement);
						if (null==hypothesis) throw new RteKbpIOException("null hypothesis!");
						if (mapHypotheses.containsKey(new Integer(hId))) throw new RteKbpIOException("duplicate id: "+hId+" in hypotheses, for pair: "+pairId);
						mapHypotheses.put(hId,hypothesis);
					}
				}
			}
			if (null==entity) throw new RteKbpIOException("null entity in pair: "+pairId);
			if (null==value) throw new RteKbpIOException("null value in pair: "+pairId);
			if (null==textDocId) throw new RteKbpIOException("null textDocId in pair: "+pairId);
			if (mapHypotheses.size()==0) throw new RteKbpIOException("No hypotheses in pair: "+pairId);
			
			EntailmentAnnotation annotation = null;
			// Amnon 07.09.10: this check prevents a NullPointerException, when running on Test Set (isGoldStandard == false --> entailment == null)
			// and leave annotation == null
			if (entailment != null)	
			{					
				if (entailment.equals(PairsFileConstants.ANNOTATION_YES))
					annotation = EntailmentAnnotation.YES;
				else if (entailment.equals(PairsFileConstants.ANNOTATION_NO))
					annotation = EntailmentAnnotation.NO;
				else throw new RteKbpIOException("Bad annotation: "+entailment+", in pair: "+pairId);
			}
			
			PairInformation pairInformation = 
				new PairInformation(pairId, query, entityType, attribute, annotation, entity, value, textDocId, mapHypotheses);
			
			pairs.add(pairInformation);
		}
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


	private String xmlFileName = null;
	private String rootElementName = null;
	private boolean isGoldStandard = false;
	private List<PairInformation> pairs = null;
	
	private boolean readDone = false;
	
	
	private Document document;
}
