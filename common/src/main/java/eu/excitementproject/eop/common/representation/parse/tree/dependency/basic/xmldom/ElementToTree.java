package eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom;

import static eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom.TreeToElement.ANTECEDENT_ATTRIBUTE_NAME;
import static eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom.TreeToElement.CHILDREN_ELEMENT_NAME;
import static eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom.TreeToElement.COREFERENCE_GROUP_ATTRIBUTE_NAME;
import static eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom.TreeToElement.XML_UNIQUE_ID_ATTRIBUTE_NAME;

import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.xmldom.XmlDomUtilitiesException;
import eu.excitementproject.eop.common.utilities.xmldom.XmlDomUtils;


/**
 * 
 * @author Asher Stern
 * @since Oct 3, 2012
 *
 */
public class ElementToTree
{
	public ElementToTree(boolean ignoreSavedCanonicalPosTag, XmlTreePartOfSpeechFactory posFactory, Element element)
	{
		super();
		this.ignoreSavedCanonicalPosTag = ignoreSavedCanonicalPosTag;
		this.posFactory = posFactory;
		this.element = element;
	}


	public void createTree() throws TreeXmlException
	{
		try
		{
			mapNodeToUniqueId = new SimpleBidirectionalMap<BasicNode, String>();
			mapNodeToAntecedentId = new LinkedHashMap<BasicNode, String>();
			tree = createSubTree(element);
			updateAntecedents();
		}
		catch (XmlDomUtilitiesException e)
		{
			throw new TreeXmlException("Error when reading XML.",e);
		}
		catch (TreeCoreferenceInformationException e)
		{
			throw new TreeXmlException("Error in coreference attribute.",e);
		}
	}
	
	
	public BasicNode getTree() throws TreeXmlException
	{
		if (null==tree) throw new TreeXmlException("Please call createTree()");
		return tree;
	}
	
	public boolean hasCoreferenceInformation()
	{
		return (highestCorefGroupId>0);
	}
	
	public TreeCoreferenceInformation<BasicNode> getCoreferenceInformation()
	{
		return coreferenceInformation;
	}


	private BasicNode createSubTree(Element nodeElement) throws TreeXmlException, XmlDomUtilitiesException, TreeCoreferenceInformationException
	{
		String uniqueId = nodeElement.getAttribute(XML_UNIQUE_ID_ATTRIBUTE_NAME);
		if (null==uniqueId) throw new TreeXmlException("Malformed XML. Missing unique id of a node.");
		
//		NodeList nodeListInfoElement = nodeElement.getElementsByTagName(Info.class.getSimpleName());
//		if (nodeListInfoElement.getLength()!=1) throw new TreeXmlException("Malformed XML. Number of Info elements is not 1.");
//		Element infoElement = (Element) nodeListInfoElement.item(0);
		
		Element infoElement = XmlDomUtils.getChildElement(nodeElement, Info.class.getSimpleName());
		ElementToInfo elementToInfo = new ElementToInfo(ignoreSavedCanonicalPosTag, posFactory, infoElement);
		elementToInfo.createInfo();
		Info info = elementToInfo.getInfo();
		
		BasicNode node = new BasicNode(info);
		mapNodeToUniqueId.put(node,uniqueId);
		
		String antecedentId = nodeElement.getAttribute(ANTECEDENT_ATTRIBUTE_NAME);
		if (antecedentId!=null)
		{
			mapNodeToAntecedentId.put(node,antecedentId);
		}
		handleCoreferenceGroupOfNode(nodeElement,node);

//		NodeList nodeListChildrenElement = nodeElement.getElementsByTagName(CHILDREN_ELEMENT_NAME);
//		if (nodeListChildrenElement.getLength()>1) throw new TreeXmlException("Malformed XML. More than one children element.");
//		if (nodeListChildrenElement.getLength()==1)

		Element childrenElement = XmlDomUtils.getChildElement(nodeElement, CHILDREN_ELEMENT_NAME,true);
		if (childrenElement!=null)
		{
			NodeList nodeListForChildren = childrenElement.getChildNodes();//  getElementsByTagName(BasicNode.class.getSimpleName());
			for (int index=0;index<nodeListForChildren.getLength();++index)
			{
				if (nodeListForChildren.item(index) instanceof Element)
				{
					if (((Element)nodeListForChildren.item(index)).getTagName().equals(BasicNode.class.getSimpleName()))
					{
						BasicNode child = createSubTree((Element)nodeListForChildren.item(index));
						node.addChild(child);
						
					}
				}
			}
		}
		
		return node;
	}

	
	private void updateAntecedents()
	{
		for (BasicNode node : mapNodeToAntecedentId.keySet())
		{
			node.setAntecedent(mapNodeToUniqueId.rightGet(mapNodeToAntecedentId.get(node)));
		}
	}
	
	private void handleCoreferenceGroupOfNode(Element nodeElement, BasicNode node) throws TreeCoreferenceInformationException, TreeXmlException
	{
		String corefGroupIdString = nodeElement.getAttribute(COREFERENCE_GROUP_ATTRIBUTE_NAME);
		if (corefGroupIdString!=null)
		{
			corefGroupIdString = corefGroupIdString.trim();
			if (corefGroupIdString.length()>0)
			{
				try
				{
					int corefGroupId = Integer.parseInt(corefGroupIdString);
					addNodeToCorefGroup(node,corefGroupId);
				}
				catch(NumberFormatException e)
				{
					throw new TreeXmlException("Bad argument value for coreference. Value is: \""+corefGroupIdString+"\"",e);
				}
			}
		}
	}
	
	private void addNodeToCorefGroup(BasicNode node, int groupId) throws TreeCoreferenceInformationException, TreeXmlException
	{
		if (groupId<=0) throw new TreeXmlException("A wrong coreference-group-id was given: "+groupId);
		while (highestCorefGroupId<groupId)
		{
			highestCorefGroupId = coreferenceInformation.createNewGroup();
		}
		coreferenceInformation.addNodeToGroup(groupId, node);
	}

	private boolean ignoreSavedCanonicalPosTag = false;
	private XmlTreePartOfSpeechFactory posFactory;
	private Element element;
	
	private BidirectionalMap<BasicNode, String> mapNodeToUniqueId;
	private Map<BasicNode, String> mapNodeToAntecedentId;
	
	private int highestCorefGroupId = 0;
	
	private BasicNode tree = null;
	private TreeCoreferenceInformation<BasicNode> coreferenceInformation = new TreeCoreferenceInformation<BasicNode>();
}
