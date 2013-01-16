package eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom;

import static eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom.ListTreesToXml.CORPUS_INFORMATION_ELEMENT_NAME;
import static eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom.ListTreesToXml.SENTENCE_ELEMENT_NAME;
import static eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom.ListTreesToXml.TREE_AND_SENTENCE_ELEMENT_NAME;
import static eu.excitementproject.eop.common.utilities.xmldom.XmlDomUtils.getChildElement;
import static eu.excitementproject.eop.common.utilities.xmldom.XmlDomUtils.getTextOfElement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.xmldom.XmlDomUtilitiesException;
import eu.excitementproject.eop.common.utilities.xmldom.XmlDomUtils;


/**
 * 
 * @author Asher Stern
 * @since Oct 3, 2012
 *
 */
public class XmlToListTrees
{
	public XmlToListTrees(String xmlFileName,
			XmlTreePartOfSpeechFactory posFactory)
	{
		super();
		this.xmlFileName = xmlFileName;
		this.posFactory = posFactory;
	}



	public void createListTrees() throws TreeXmlException
	{
		try
		{
			getDocument();
			readDocument();
		}
		catch (XmlDomUtilitiesException e)
		{
			throw new TreeXmlException("Error when reading XML",e);
		}
		catch (TreeCoreferenceInformationException e)
		{
			throw new TreeXmlException("An error occurred when handling coreference information.",e);
		}
	}
	
	
	
	public List<TreeAndSentence> getListTrees() throws TreeXmlException
	{
		if (null==listTrees) throw new TreeXmlException("Please call createListTrees()");
		return listTrees;
	}
	
	public String getCorpusInformation() throws TreeXmlException
	{
		if (null==listTrees) throw new TreeXmlException("Please call createListTrees()");
		return corpusInformation;
	}
	
	public boolean hasCoreferenceInformation()
	{
		return (this.highestCorefGroupId>0);
	}
	
	public TreeCoreferenceInformation<BasicNode> getCoreferenceInformation()
	{
		return coreferenceInformation;
	}



	private void readDocument() throws TreeXmlException, XmlDomUtilitiesException, TreeCoreferenceInformationException
	{
		Element documentElement = document.getDocumentElement();
		
		Element corpusInformationElement = getChildElement(documentElement,CORPUS_INFORMATION_ELEMENT_NAME,true);
		if (corpusInformationElement!=null)
		{
			corpusInformation  = getTextOfElement(corpusInformationElement);
		}
		else
		{
			corpusInformation=null;
		}

		List<Element> allSentences = XmlDomUtils.getChildElements(documentElement, TREE_AND_SENTENCE_ELEMENT_NAME);
		listTrees = new ArrayList<TreeAndSentence>(allSentences.size());
		for (Element treeAndSentenceElement : allSentences)
		{
			addElementToList(treeAndSentenceElement);
		}
	}
	
	private void addElementToList(Element treeAndSentenceElement) throws TreeXmlException, XmlDomUtilitiesException, TreeCoreferenceInformationException
	{
		String sentence = getTextOfElement(getChildElement(treeAndSentenceElement, SENTENCE_ELEMENT_NAME));
		Element treeElement = getChildElement(treeAndSentenceElement, BasicNode.class.getSimpleName());
		ElementToTree elementToTree = new ElementToTree(posFactory,treeElement);
		elementToTree.createTree();
		BasicNode tree = elementToTree.getTree();
		if (elementToTree.hasCoreferenceInformation())
		{
			mergeCoreference(elementToTree.getCoreferenceInformation());
		}
		
		TreeAndSentence treeAndSentence = new TreeAndSentence(sentence, tree);
		listTrees.add(treeAndSentence);
	}
	
	private void mergeCoreference(TreeCoreferenceInformation<BasicNode> sentenceInformation) throws TreeCoreferenceInformationException
	{
		if (null==this.coreferenceInformation)
		{
			this.coreferenceInformation = new TreeCoreferenceInformation<BasicNode>();
		}
		for (Integer groupId : sentenceInformation.getAllExistingGroupIds())
		{
			while (highestCorefGroupId < groupId)
			{
				highestCorefGroupId = this.coreferenceInformation.createNewGroup();
			}
			for (BasicNode node : sentenceInformation.getGroup(groupId))
			{
				this.coreferenceInformation.addNodeToGroup(groupId, node);
			}
		}
	}
	
	private void getDocument() throws TreeXmlException
	{
		try
		{
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(xmlFileName));
		}
		catch(ParserConfigurationException e)
		{
			throw new TreeXmlException("read xml file failed. See nested exception.",e);
		} catch (SAXException e)
		{
			throw new TreeXmlException("read xml file failed. See nested exception.",e);
		}
		catch (IOException e)
		{
			throw new TreeXmlException("read xml file failed. See nested exception.",e);
		}
	}

	private String xmlFileName;
	private XmlTreePartOfSpeechFactory posFactory;
	
	private Document document;
	private int highestCorefGroupId = 0;
	
	private String corpusInformation = null;
	private List<TreeAndSentence> listTrees = null;
	private TreeCoreferenceInformation<BasicNode> coreferenceInformation = null;

}
