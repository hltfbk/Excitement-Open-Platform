package ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.xmldom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ac.biu.nlp.nlp.general.BidirectionalMap;
import ac.biu.nlp.nlp.general.SimpleBidirectionalMap;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNodeUtils;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

/**
 * Gets a parse-tree, and creates an XML element ({@link Element}) which
 * represents this parse-tree.
 * 
 * @author Asher Stern
 * @since October 2, 2012
 *
 */
public class TreeToElement
{
	public static final String CHILDREN_ELEMENT_NAME = "children";
	public static final String XML_UNIQUE_ID_ATTRIBUTE_NAME = "xml-unique-id";
	public static final String ANTECEDENT_ATTRIBUTE_NAME = "antecedent";
	public static final String COREFERENCE_GROUP_ATTRIBUTE_NAME = "coreference-group";
	
	
	public TreeToElement(BasicNode tree, Document document, TreeCoreferenceInformation<BasicNode> coreferenceInformation)
	{
		super();
		this.tree = tree;
		this.document = document;
		this.coreferenceInformation = coreferenceInformation;
	}
	
	public TreeToElement(BasicNode tree, Document document)
	{
		super();
		this.tree = tree;
		this.document = document;
		this.coreferenceInformation = null;
	}



	public void generate() throws TreeXmlException
	{
		createMapUniqueIDs();
		treeElement = generate(tree);
	}
	
	
	
	public Element getTreeElement()
	{
		return treeElement;
	}

	
	//////////////////////////// PRIVATE ////////////////////////////


	private Element generate(BasicNode node) throws TreeXmlException
	{
		Element nodeElement = document.createElement(BasicNode.class.getSimpleName());
		nodeElement.setAttribute(XML_UNIQUE_ID_ATTRIBUTE_NAME, mapNodeToUniqueId.leftGet(node));
		if (node.getAntecedent()!=null)
		{
			nodeElement.setAttribute(ANTECEDENT_ATTRIBUTE_NAME, mapNodeToUniqueId.leftGet(node.getAntecedent()));
		}
		if (coreferenceInformation!=null)
		{
			Integer corefGroupId = coreferenceInformation.getIdOf(node);
			if (corefGroupId!=null)
			{
				nodeElement.setAttribute(COREFERENCE_GROUP_ATTRIBUTE_NAME, String.valueOf(corefGroupId));
			}
		}
		
		Info info = node.getInfo();
		InfoToElement infoToElement = new InfoToElement(document, info);
		infoToElement.generate();
		Element infoElement = infoToElement.getInfoElement();
		nodeElement.appendChild(infoElement);
		
		
		if (node.hasChildren())
		{
			Element childrenElement = document.createElement(CHILDREN_ELEMENT_NAME);
			nodeElement.appendChild(childrenElement);
			for (BasicNode child : node.getChildren())
			{
				childrenElement.appendChild(generate(child));
			}
		}
		
		return nodeElement;
	}
	
	private void createMapUniqueIDs()
	{
		mapNodeToUniqueId = new SimpleBidirectionalMap<BasicNode, String>();
		int uniqueId = 1;
		for (BasicNode node : AbstractNodeUtils.treeToSet(tree))
		{
			mapNodeToUniqueId.put(node,String.valueOf(uniqueId));
			++uniqueId;
		}
	}

	// input
	private final BasicNode tree;
	private final Document document;
	private final TreeCoreferenceInformation<BasicNode> coreferenceInformation;

	// internals
	private BidirectionalMap<BasicNode, String> mapNodeToUniqueId;
	
	// output
	private Element treeElement;
}
