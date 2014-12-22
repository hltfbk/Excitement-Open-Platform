package eu.excitementproject.eop.lap.btbpipe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//Import from same Package!
//import eu.excitementproject.eop.lap.btbpipe.BTBSentence;
//import eu.excitementproject.eop.lap.btbpipe.BTBToken;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import clark.common.ClarkRuntime;
import clark.dom.impl.DOMFactory;
import clark.internalFiles.DocInfo;
import clark.internalFiles.DocumentManager;
import clark.loader.DefaultFileFilter;
import clark.loader.Loader;
import clark.loader.XMLParser;
import clark.multiApply.ClarkProcessor;
import clark.multiApply.UniversalProcessor;

/**
 * This class calls a processing chain in the CLaRK system which carries out
 * several NLP tasks: tokenization, sentence segmentation, POS tagging and
 * lemmatization. The location of the ClaRK data directory should be specified
 * in a configuration file or directly passed to the constructor of the class.
 * 
 * @author Iliana Simova (BulTreeBank team)
 * 
 */
public class Preprocessor {

	Logger logger = Logger.getLogger(getClass().getName());

	// a config file can be used to read the location of the CLaRK data
	// directory, if the path is not provided directly to the constructor
	private static final String CONFIG = "config.properties";

	// CLaRK processor setup
	private static final String QUERY_NAME = "runAll";
	private static final String DTD_NAME = "laska.dtd";
	private ClarkProcessor processor;

	// CLaRK document
	// specifications of a CLaRK document returned after processing plain text
	// input. The document should contain sentence nodes and token nodes as
	// their child nodes.
	// sentence node:
	private static final String S_NODE = "s";
	// token node:
	private static final String TOK_NODE = "tok";
	// attribute marking beginning of a token:
	private static final String START_ATTR = "start";
	// attribute marking end of a token:
	private static final String END_ATTR = "end";
	// attribute containing the lemma of a token:
	private static final String LEMMA_ATTR = "lm";
	// attribute containing the pos tag of a token:
	private static final String POS_ATTR = "ana";
	// attribute containing a mapping of the pos to the universal pos tag:
	private static final String UPOS_ATTR = "upos";

	/**
	 * Create a new Preprocessor by directly providing the location of the CLaRK
	 * data directory, needed to initialize CLaRK
	 * 
	 * @param clarkDataDir
	 *            directory containing CLaRK data
	 * @throws Exception
	 */
	public Preprocessor(String clarkDataDir) throws Exception {
		initializeClark(clarkDataDir);
	}

	/**
	 * Create a new Preprocessor which initializes CLaRK by reading the location
	 * of the CLaRK data directory from a config file
	 * 
	 * @throws Exception
	 */
	public Preprocessor() throws Exception {
		initializeClark(getClarkDataDir());
	}

	/**
	 * Initialize CLaRK
	 * 
	 * @param clarkDataDir
	 *            directory containing CLaRK data
	 * @throws Exception
	 */
	private void initializeClark(String clarkDataDir) throws Exception {

		if (false == new File(clarkDataDir).exists()) {
			throw new FileNotFoundException(
					"CLaRK work directory could not be located (searched in "
							+ clarkDataDir + ").");
		}

		// initialize CLaRK
		ClarkRuntime.initRuntime(clarkDataDir);

		// build a CLaRK processor
		processor = buildProcessor();
	}

	/**
	 * Read the location of the CLaRK data directory from the config file
	 * 
	 * @return CLaRK data dir
	 * @throws IOException
	 */
	private String getClarkDataDir() throws IOException {
		String clarkDir = "";
		Properties extProps = new Properties();
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(CONFIG);

		try {
			extProps.load(is);
			if (extProps.containsKey("clarkData")) {
				clarkDir = extProps.getProperty("clarkData");
			}
		} catch (IOException | NullPointerException e) {
			logger.severe("Config file '" + CONFIG + "' not found. "
					+ "Make sure the file is visible on the class path.");
			throw e;
		}

		return clarkDir;
	}

	/**
	 * Process the text in inputText to extract sentences
	 * 
	 * @param inputText
	 *            some plain text to process
	 * @throws Exception
	 */
	public List<BTBSentence> process(String inputText) throws Exception {
		List<BTBSentence> sentences = null;

		// build a CLaRK input xml document
		Document doc = buildInputDoc(inputText);

		try {
			// call the processor with the input data in xml format
			processor.processDoc(doc, "", DTD_NAME);
			doc = processor.getResult();

			// for debugging
			// printDocument(doc);
		} catch (Throwable e) {
			logger.severe("CLaRK processor failed to generate result doc: "
					+ e.getMessage());
			throw e;
		}

		try {
			// parse the result document
			sentences = parseXMLDocument(doc);

		} catch (Exception e) {
			logger.severe("Parsing CLaRK document output failed: "
					+ e.getMessage());
			throw e;
		}

		return sentences;
	}

	/**
	 * Parse the xml document returned by the preprocessor
	 * 
	 * @param doc the xml document
	 * @return
	 */
	private List<BTBSentence> parseXMLDocument(Document doc) {
		List<BTBSentence> sentences = new ArrayList<BTBSentence>();
		NodeList sentList = doc.getDocumentElement().getElementsByTagName(S_NODE);
		NodeList tokList = null;

		for (int i = 0; i < sentList.getLength(); i++) {

			Element sentEl = (Element) sentList.item(i);
			tokList = sentEl.getElementsByTagName(TOK_NODE);

			BTBSentence s = new BTBSentence();
			String sentAsString = "";
			s.setBegin(parseStart((Element) tokList.item(0), i));
			s.setEnd(parseEnd((Element) tokList.item(tokList.getLength() - 1), i));

			int prevEnd = s.getBegin();
			for (int j = 0; j < tokList.getLength(); j++) {
				Element tokEl = (Element) tokList.item(j);
				BTBToken t = new BTBToken(tokEl.getTextContent());

				int b = parseStart(tokEl, i);
				int e = parseEnd(tokEl, i);

				if (prevEnd < b) {
					// recover the original string to store in the sentence
					// text field
					sentAsString += " ";
				}
				sentAsString += tokEl.getTextContent();

				prevEnd = e;

				t.setBegin(b);
				t.setEnd(e);
				t.setuPOS(parseUPOS(tokEl, i));
				t.setBtbPOS(parsePOS(tokEl, i));
				t.setLemma(parseLemma(tokEl, i));
				s.addToken(t);
			}

			s.setSentenceText(sentAsString);
			sentences.add(s);
		}
		return sentences;
	}

	/**
	 * Parse the begin index of a token element
	 * 
	 * @param el the token element
	 * @param sentId the sentence this token belongs to
	 * @return
	 */
	private int parseStart(Element el, int sentId) {
		int id = 0;
		try {
			id = Integer.parseInt(el.getAttribute(START_ATTR));
		} catch (Exception e) {
			logger.severe("Failed to generate begin index for token \""
					+ el.getTextContent() + "\", sentence number " + sentId);
			throw e;
		}
		return id;
	}

	/**
	 * Parse the end index of a token element
	 * 
	 * @param el the token element
	 * @param sentId the sentence this token belongs to
	 * @return
	 */
	private int parseEnd(Element el, int sentId) {
		int id = 0;
		try {
			id = Integer.parseInt(el.getAttribute(END_ATTR));
		} catch (Exception e) {
			logger.severe("Failed to generate end index for token \""
					+ el.getTextContent() + "\", sentence number " + sentId);
			throw e;
		}
		return id;
	}

	/**
	 * Parse the lemma of a token element
	 * 
	 * @param el the token element
	 * @param sentId the sentence this token belongs to
	 * @return
	 */
	private String parseLemma(Element el, int sentId) {
		String lem = "";
		try {
			lem = el.getAttribute(LEMMA_ATTR).toLowerCase();
		} catch (Exception e) {
			logger.severe("Failed to generate lemma for token \""
					+ el.getTextContent() + "\", sentence number " + sentId);
			throw e;
		}
		return lem;
	}

	/**
	 * Parse the universal POS of a token element
	 * 
	 * @param el the token element
	 * @param sentId the sentence this token belongs to
	 * @return
	 */
	private String parseUPOS(Element el, int sentId) {
		String lem = "";
		try {
			lem = el.getAttribute(UPOS_ATTR);
		} catch (Exception e) {
			logger.severe("Failed to generate universal POS for token \""
					+ el.getTextContent() + "\", sentence number " + sentId);
			throw e;
		}
		return lem;
	}

	/**
	 * Parse the BTB POS of a token element
	 * 
	 * @param el the token element
	 * @param sentId the sentence this token belongs to
	 * @return
	 */
	private String parsePOS(Element el, int sentId) {
		String lem = "";
		try {
			lem = el.getAttribute(POS_ATTR);
		} catch (Exception e) {
			logger.severe("Failed to generate part-of-speech for token \""
					+ el.getTextContent() + "\", sentence number " + sentId);
			throw e;
		}
		return lem;
	}

	/**
	 * Create an XML document from the input data
	 * 
	 * @param inputData
	 * @return
	 * @throws Exception
	 */
	private Document buildInputDoc(String inputData) throws Exception {
		Document doc = DOMFactory.getEmptyDocument();
		try {
			XMLParser parser = new XMLParser();
			parser.setPreserveWhitespaceNodes(true);
			parser.parse(doc, "<root>" + inputData + "</root>", null, 0);
		} catch (Exception e) {
			logger.severe("Input string parsing failed: " + e.getMessage());
			throw e;
		}
		return doc;
	}

	/**
	 * Build a CLaRK processor for the specified query
	 * 
	 * @return
	 * @throws Exception
	 */
	private ClarkProcessor buildProcessor() throws Exception {
		DocumentManager manager = DocumentManager.getManager();
		DocInfo infoQ = manager.getDocInfo("Root", QUERY_NAME);
		if (infoQ == null) {
			String m = "There is no such query(" + QUERY_NAME
					+ ") in the system!";
			logger.severe(m);
			throw new Exception(m);
		}

		Document queryDoc = null;
		try {
			queryDoc = Loader.loadDocumentAux(
					ClarkRuntime.getWorkPath() + File.separator + "data"
							+ File.separator + infoQ.getFileName(), null,
					DefaultFileFilter.UTF_16BE, false);
		} catch (Exception e) {
			logger.severe("Error loading query '" + QUERY_NAME + "'. "
					+ e.getMessage());
			throw e;
		}

		ClarkProcessor p = null;

		try {
			p = UniversalProcessor.constructProcessor(queryDoc, null,
					ClarkProcessor.CONTEXT_SOURCE);
		} catch (Exception e) {
			logger.severe("Processor could not be built " + e.getMessage());
			throw e;
		}

		return p;
	}

	/**
	 * For debugging: pretty print an xml document
	 * 
	 * @param doc
	 * @throws IOException
	 * @throws TransformerException
	 */
	@SuppressWarnings("unused")
	private void printDocument(Document doc) throws IOException,
			TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");

		transformer.transform(new DOMSource(doc), new StreamResult(
				new OutputStreamWriter(System.out, "UTF-8")));
	}

	public static void main(String[] args) throws Exception {
		
		// Example usage:

		// initialize preprocessor
		String clarkDataDir = Preprocessor.class.getResource(
				"/btbPipeData/clarkData").getPath();
		Preprocessor p = new Preprocessor(clarkDataDir);

		// process some input
		String input = "Момичето яде сладолед. Момчето чете книга.";
		System.out.println("\nInput text: " + input);
		
		List<BTBSentence> sents = p.process(input);
		
		System.out.println("\nResult sentences+tokens:\n");
		
		for (BTBSentence s : sents) {
			System.out.println(s.toString());
			for (BTBToken t : s.getTokens()) {
				System.out.println("\t" + t.toString());
			}
		}
	}
}
