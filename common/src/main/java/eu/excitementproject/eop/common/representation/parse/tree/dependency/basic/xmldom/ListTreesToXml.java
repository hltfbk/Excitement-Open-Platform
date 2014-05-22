package eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom;

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

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;

/**
 * This class gets a list of parse-trees, and saves them into an XML
 * 
 * @see XmlToListTrees
 * 
 * @author Asher Stern
 * @since October 2, 2012
 *
 */
public class ListTreesToXml
{
	public static final String ROOT_ELEMENT_NAME = "xml";
	public static final String TREE_AND_SENTENCE_ELEMENT_NAME = "single-tree";
	public static final String SENTENCE_ELEMENT_NAME = "original-sentence";
	public static final String CORPUS_INFORMATION_ELEMENT_NAME = "corpus-information";
	
	/**
	 * Constructor which gets the list of the trees and the XML into which they should be written.
	 * This constructor also gets the coreference information (resolved in the document level), to be
	 * written into the XML file (as attributes on the tree-nodes) as well.
	 * @param listTrees A list of parse-trees
	 * @param corpusInformation A description of the list-of-trees.
	 * @param filename The XML file name, which is the file that the trees will be written to.
	 * @param coreferenceInformation Coreference information (in the document level) to be written as well.
	 */
	public ListTreesToXml(List<TreeAndSentence> listTrees, String corpusInformation, String filename, TreeCoreferenceInformation<BasicNode> coreferenceInformation)
	{
		super();
		this.listTrees = listTrees;
		this.filename = filename;
		this.corpusInformation = corpusInformation;
		this.coreferenceInformation = coreferenceInformation;
	}
	
	/**
	 * Constructor which gets the list of the trees and the XML into which they should be written.
	 * @param listTrees A list of parse-trees
	 * @param corpusInformation A description of the list-of-trees.
	 * @param filename The XML file name, which is the file that the trees will be written to.
	 */
	public ListTreesToXml(List<TreeAndSentence> listTrees, String corpusInformation, String filename)
	{
		this(listTrees,corpusInformation,filename,null);
	}

	/**
	 * Create a new XML file, and writes all the given trees into it.
	 * @throws TreeXmlException
	 */
	public void create() throws TreeXmlException
	{
		try
		{
			createDocument();
			appendAll();
			writeDocumentToFile();
		}
		catch (ParserConfigurationException e)
		{
			throw new TreeXmlException("Failed to write XML",e);
		}
		catch (TransformerException e)
		{
			throw new TreeXmlException("Failed to write XML",e);
		}
		catch (IOException e)
		{
			throw new TreeXmlException("Failed to write XML",e);
		}
		catch(DOMException e)
		{
			throw new TreeXmlException("Failed to write XML",e);
		}
	}

	/////////////////////// PRIVATE ///////////////////////
	
	private void createDocument() throws ParserConfigurationException
	{
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        this.document = docBuilder.newDocument();
	}
	
	private void appendAll() throws DOMException, TreeXmlException
	{
		Element rootElement = createRootElement();
		document.appendChild(rootElement);
		if (corpusInformation!=null)
		{
			Element corpusInformationElement = document.createElement(CORPUS_INFORMATION_ELEMENT_NAME);
			rootElement.appendChild(corpusInformationElement);
			Text corpusInformationText = document.createTextNode(corpusInformation);
			corpusInformationElement.appendChild(corpusInformationText);
		}
		
		for (TreeAndSentence treeAndSentence : listTrees)
		{
			rootElement.appendChild(createElementSingleTree(treeAndSentence));
		}
	}
	
	private Element createRootElement()
	{
		return document.createElement(ROOT_ELEMENT_NAME);
	}
	
	private Element createElementSingleTree(TreeAndSentence treeAndSentence) throws TreeXmlException
	{
		if (null==treeAndSentence.getTree()) throw new TreeXmlException("null tree in the list");
		
		Element treeAndSentenceElement = document.createElement(TREE_AND_SENTENCE_ELEMENT_NAME);
		
		Element sentenceElement = document.createElement(SENTENCE_ELEMENT_NAME);
		treeAndSentenceElement.appendChild(sentenceElement);
		Text sentenceText = document.createTextNode((treeAndSentence.getSentence()!=null)?treeAndSentence.getSentence():"null");
		sentenceElement.appendChild(sentenceText);
		
		TreeToElement treeToElement;
		if (this.coreferenceInformation!=null)
		{
			treeToElement = new TreeToElement(treeAndSentence.getTree(), document, this.coreferenceInformation);
		}
		else
		{
			treeToElement = new TreeToElement(treeAndSentence.getTree(), document);	
		}
		treeToElement.generate();
		Element treeElement = treeToElement.getTreeElement();
		treeAndSentenceElement.appendChild(treeElement);
		
		return treeAndSentenceElement;
	}
	

	
	private void writeDocumentToFile() throws TransformerException, IOException
	{
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		FileOutputStream outputStream = new FileOutputStream(new File(filename));
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



	// input
	/**
	 * List of trees to be stored in the XML file 
	 */
	private List<TreeAndSentence> listTrees;
	
	/**
	 * Optional coreference information on this document
	 */
	private TreeCoreferenceInformation<BasicNode> coreferenceInformation;
	
	/**
	 * A description of the list of trees (typically, a name of a document)
	 */
	private String corpusInformation;
	
	/**
	 * The XML file name, into which the trees will be stored.
	 */
	private String filename;
	
	
	// internal field
	private Document document;
}
