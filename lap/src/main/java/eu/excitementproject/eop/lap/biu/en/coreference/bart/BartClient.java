package eu.excitementproject.eop.lap.biu.en.coreference.bart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.excitementproject.eop.lap.biu.coreference.merge.WordWithCoreferenceTag;



/**
 * Used as client to BART system for fetching co-reference information
 * for a given text, and
 * return them as List (<code>java.util.List</code>) of {@link WordWithCoreferenceTag}.
 * 
 * About BART, see http://bart-anaphora.org/
 * 
 * 
 * NOTE: Due to the bug:
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6524460
 * a workaround was implemented, that removes "special" HTML strings. It is yet unknown
 * whether that workaround is complete, and whether it may reduce the accuracy.
 * <P>
 * In any case that an exception is thrown due to the bug mentioned above, the workaround
 * should be expanded to handle the case that caused the problem. 
 * 
 * @author Asher Stern
 *
 */
public class BartClient
{
	//////////////////// PUBLIC PART /////////////////////////////
	
	//http://localhost:8125/BARTDemo/ShowText/process/

	// CONSTANTS
	public static final String DEFAULT_SERVER = "localhost";
	public static final String DEFAULT_PORT = "8125";
	public static final String DEFAULT_ULR_PATH = "BARTDemo/ShowText/process/";
	public static final String ELEMENT_ROOT_NAME = "text";
	public static final String ELEMENT_SENTENCE_NAME = "s";
	public static final String ELEMENT_WORD_NAME = "w";
	public static final String ELEMENT_COREF_NAME = "coref";
	public static final String ATTRIBUTE_SET_ID_NAME = "set-id";

	// EXCEPTION CLASS
	@SuppressWarnings("serial")
	public static class BartClientException extends Exception
	{
		public BartClientException(String str){super(str);}
		public BartClientException(String str,Throwable t){super(str,t);}
	}

	// CONSTRUCTOR & METHODS
	
	
	/**
	 * Constructs {@link BartClient} with the text as string.
	 * @param text the text to be processed by BART.
	 */
	public BartClient(String text)
	{
		this.text = text;
	}
	
	/**
	 * Use this constructor to work with a server that is not local-host.
	 * @param text
	 * @param serverName
	 */
	public BartClient(String text, String serverName)
	{
		this(text);
		this.serverName = serverName;
	}
	
	/**
	 * Use this constructor for using custom server, port, URL
	 * 
	 * @param text
	 * @param serverName
	 * @param port
	 * @param urlPath
	 */
	public BartClient(String text, String serverName,String port, String urlPath)
	{
		this(text,serverName);
		this.port = port;
		this.urlPath = urlPath;
	}
	
	/**
	 * Set <tt>true</tt> to make the workaround of bug
	 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6524460
	 * active.
	 * Set <tt>false</tt> to use directly BART's output, with no workaround.
	 * Default is <tt>true</tt>
	 * @param cleanXml
	 */
	public void setCleanXml(boolean cleanXml)
	{
		this.cleanXml = cleanXml;
	}
	
	/**
	 * Calls BART, retrieving its output, and create the list of
	 * {@link WordWithCoreferenceTag} to be returned by {@link #getBartOutput()}.
	 * 
	 * @throws BartClientException
	 */
	public void process() throws BartClientException
	{
		try
		{
			createDocument();
			processDocument();
			processDone = true;
		}
		catch(IOException e)
		{
			throw new BartClientException("Technical problem using BART. See nested exception",e);
		}
		catch(SAXException e)
		{
			throw new BartClientException("Technical problem using BART. See nested exception",e);
		}
		catch(ParserConfigurationException e)
		{
			throw new BartClientException("Technical problem using BART. See nested exception",e);
		}
	}
	
	/**
	 * Returns the BART's output, as list of {@link WordWithCoreferenceTag}.
	 * First call {@link #process()} method. Then call this method.
	 * @return
	 * @throws BartClientException
	 */
	public List<WordWithCoreferenceTag> getBartOutput() throws BartClientException
	{
		if (processDone)
			return this.bartOutput;
		else
			throw new BartClientException("process() was not called.");
	}
	
	
	////////////////// PROTECTED & PRIVATE PART //////////////////////
	
	protected InputStream callBart() throws IOException
	{
		String urlString = "http://"+serverName+":"+port+"/"+urlPath;
		URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Content-Type","text/plain");
        conn.setDoOutput(true);
        PrintWriter writer = new PrintWriter(conn.getOutputStream());
        writer.println(text);
        writer.close();
        
        return conn.getInputStream();
	}
	
	protected String getCleanedXml(InputStream inputStream) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuffer ret = new StringBuffer();
		String line = reader.readLine();
		while (line !=null)
		{
			line = line.replaceAll("&.*;", "\'");
			ret.append(line);
			ret.append("\n");
			line = reader.readLine();
		}
		reader.close();
		return ret.toString();
	}
	
	protected void createDocument() throws SAXException, IOException, ParserConfigurationException
	{
		InputStream bartInputStream = callBart();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//System.out.println(factory.getClass().getName());
		//factory.setValidating(false);
		//factory.setExpandEntityReferences(false);
		//SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		//SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
		//Schema schema = schemaFactory.newSchema();
		//factory.setSchema(schema);
		//factory.setValidating(false);
		//factory.setExpandEntityReferences(false);
		
		if (cleanXml)
		{
			String cleanedXml = getCleanedXml(bartInputStream);
			StringReader reader = new StringReader(cleanedXml);
			InputSource inputSource = new InputSource(reader);
			this.document = factory.newDocumentBuilder().parse(inputSource);
			reader.close();
		}
		else
		{
			this.document = factory.newDocumentBuilder().parse(bartInputStream);
		}
		bartInputStream.close();
	}
	
	protected void processDocument() throws BartClientException
	{
		try
		{
			Element docElement = document.getDocumentElement();
			NodeList sentencesElements = docElement.getChildNodes();
			for (int sentenceIndex=0;sentenceIndex<sentencesElements.getLength();sentenceIndex++)
			{
				Node sentenceElementCandidate = sentencesElements.item(sentenceIndex);
				if (sentenceElementCandidate.getNodeType()==Node.ELEMENT_NODE)
				{
					Element sentenceElement = (Element) sentenceElementCandidate;
					processWordsContainer(sentenceElement);
				}
			}
		}
		catch(NullPointerException e)
		{
			throw new BartClientException("BART returned a malformed output. See nested exception",e);
		}
	}

	
	protected static boolean isCorefNode(Node node)
	{
		boolean ret = false;
		if (node.getNodeType()==Node.ELEMENT_NODE)
		{
			Element element = (Element) node;
			if (element.getNodeName().equals(ELEMENT_COREF_NAME))
				ret = true;
		}
		return ret;
	}

	protected static boolean isWordNode(Node node)
	{
		boolean ret = false;
		if (node.getNodeType()==Node.ELEMENT_NODE)
		{
			Element element = (Element) node;
			if (element.getNodeName().equals(ELEMENT_WORD_NAME))
				ret = true;
		}
		return ret;
	}

	
	protected static String getCorefSetId(Element corefElement) throws BartClientException
	{
		String corefSetId = corefElement.getAttribute(ATTRIBUTE_SET_ID_NAME);
		if (corefSetId.length()<1)
			throw new BartClientException("Malformed BART output. Coref element has no value for coreference set id.");
		return corefSetId;
	}
	
	protected static String getWord(Element wordElement) throws BartClientException
	{
		String wordString = null;
		NodeList textCandidatesNodeList = wordElement.getChildNodes();
		boolean textNodeFound = false;
		for (int textCandidatesIndex=0;textCandidatesIndex<textCandidatesNodeList.getLength();textCandidatesIndex++)
		{
			Node textCandidate = textCandidatesNodeList.item(textCandidatesIndex);
			if ((!textNodeFound)&&(textCandidate.getNodeType()==Node.TEXT_NODE))
			{
				textNodeFound = true;
				Text wordText = (Text)textCandidate;
				wordString = wordText.getWholeText();
			}
		}
		if (!textNodeFound)
			throw new BartClientException("Malformed BART output. Word with no text.");
		
		return wordString;
	}
	
	protected void processWordElement(Element wordElement) throws BartClientException
	{
		String wordString = getWord(wordElement);
		WordWithCoreferenceTag wwct = null;
		if (stackCoreferenceTags.empty())
		{
			wwct = new WordWithCoreferenceTag(wordString, null);
		}
		else
		{
			String corefSetId = stackCoreferenceTags.peek();
			wwct = new WordWithCoreferenceTag(wordString, corefSetId);
		}
		bartOutput.add(wwct);
	}
	
	protected void processWordsContainer(Element containerElement) throws BartClientException
	{
		NodeList children = containerElement.getChildNodes();
		for (int childrenIndex=0;childrenIndex<children.getLength();++childrenIndex)
		{
			Node childNode = children.item(childrenIndex);
			if (isWordNode(childNode))
			{
				Element childElement = (Element) childNode;
				processWordElement(childElement);
			}
			else if (isCorefNode(childNode))
			{
				Element childCorefElement = (Element) childNode;
				String corefSetId = getCorefSetId(childCorefElement);
				stackCoreferenceTags.push(corefSetId);
				processWordsContainer(childCorefElement); // recursive call
				stackCoreferenceTags.pop();
			}
		}
	}
	
	
	
	
	
	
	
	
	protected String serverName = DEFAULT_SERVER;
	protected String port = DEFAULT_PORT;
	protected String urlPath = DEFAULT_ULR_PATH;
	
	protected boolean cleanXml = true;
	
	protected Document document;
	protected Stack<String> stackCoreferenceTags = new Stack<String>();
	
	protected String text;
	
	protected List<WordWithCoreferenceTag> bartOutput = new LinkedList<WordWithCoreferenceTag>();
	protected boolean processDone = false;
	
}
