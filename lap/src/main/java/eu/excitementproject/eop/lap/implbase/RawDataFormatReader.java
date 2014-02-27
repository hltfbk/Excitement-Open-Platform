package eu.excitementproject.eop.lap.implbase;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <P>
 * A very simple XML reader for Single Pair T-H RawInput Data 
 * that is defined in the specification (Basically, RTE-5 data). 
 * 
 * <P> 
 * Usage is simple. Construct a new instance by passing the XML-format file. nextPair() will 
 * return a simple structure (PairXMLData) that holds pair data. hasNextPair() will tell you 
 * whether or not additional pairs are there to fetch. 
 *  
 * <P>
 * TODO add XML validation before doing DOM parse, so don't even try to read anything but 
 * the defined format. ... 
 * 
 * @author Gil 
 */
public class RawDataFormatReader {

	/**
	 * This constructor will open up the XML file and parse it & make a DOM. 
	 * Once it is successful (without any Exception), you can call nextPair() 
	 * to get each pair iteratively.  
	 * @param xmlFile
	 * @throws RawFormatReaderException
	 */
	public RawDataFormatReader(File xmlFile) throws RawFormatReaderException
	{
		// prepare dom, and store it 
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null; 
		try {
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			//parse using builder to get DOM representation of the XML file
			dom = db.parse(xmlFile);

		}catch(ParserConfigurationException pce) {
			throw new RawFormatReaderException("unable to generate the XML parser", pce); 
		}catch(SAXException se) {
			throw new RawFormatReaderException("unable to parse the XML input file", se); 
		}catch(IOException ioe) {
			throw new RawFormatReaderException("unable to access the input file", ioe); 
		}		
		this.dom = dom; 
		
		Element docEle = dom.getDocumentElement(); 
		// check top element, get attributes  
		this.lang = docEle.getAttribute("lang"); // if not there, it will hold "" 
		this.channel = docEle.getAttribute("channel"); 
		
		NodeList nl = docEle.getElementsByTagName("pair"); 
		this.pairNodes = nl; 
		
		if (pairNodes == null ||  pairNodes.getLength() == 0) 
		{
			throw new RawFormatReaderException("No <pair> tags in the XML file"); 
		}
	}
	
	/**
	 * Returns true, if there is additional pair to be fetched by nextPair(). 
	 * @return 
	 */
	public boolean hasNextPair()
	{
		if (current < pairNodes.getLength())
			return true; 
		else
			return false; 
	}
	
	/**
	 * returns fetch a Pair. iterates into the next one. 
	 * @return PairXMLData
	 * @throws RawFormatReaderException
	 */
	public PairXMLData nextPair() throws RawFormatReaderException
	{
		// open up pairNodes(current) 
		Element pair = (Element) pairNodes.item(current); 
		
		NodeList tl = pair.getElementsByTagName("t"); 
		NodeList hl = pair.getElementsByTagName("h"); 
		
		if (tl.getLength() == 0 || hl.getLength() == 0)
			throw new RawFormatReaderException("A pair with missing T or H tag (Pair id: " + pair.getAttribute("id") + ")"); 
		
		// get text, get hypothesis, as string 
		Element t = (Element) tl.item(0); 
		String text = t.getFirstChild().getNodeValue(); 
		Element h = (Element) hl.item(0); 
		String hypothesis = h.getFirstChild().getNodeValue(); 
		
		// get attributes 
		String id = pair.getAttribute("id"); 
		String goldAnswer = pair.getAttribute("entailment"); 
		String task = pair.getAttribute("task"); 
		
		// throw exception if missing pair id 
		//if (task.length() == 0 || id.length() == 0) // note that task can actually be empty. 
		if (id.length() ==0)
			throw new RawFormatReaderException("A pair with missing pair id. Wrong format."); 
		
		// generate return data structure and return it 
		PairXMLData pairData = new PairXMLData(text, hypothesis, id, goldAnswer, task); 
		current++; 		
		return pairData; 
	}	
	
	public String getChannel()
	{
		return channel; 
	}
	
	public String getLanguage()
	{
		return lang;  
	}
	
	//
	// subclass for representing pair data 
	public class PairXMLData {
		
		PairXMLData(String text, String hypothesis, String id, String answer, String task)
		{
			this.text = text; 
			this.hypothesis = hypothesis;
			this.id = id; 
			this.goldAnswer = answer; 
			this.task = task; 
		}
		
		public String getText() {
			return text;
		}

		public String getHypothesis() {
			return hypothesis;
		}

		public String getId() {
			return id;
		}

		public String getGoldAnswer() {
			return goldAnswer;
		}

		public String getTask() {
			return task;
		}

		private final String text; 
		private final String hypothesis; 
		private final String id; 
		private final String goldAnswer; 
		private final String task; 
	}
	
	
//	// a poor test code 
//	public static void main (String[] args)
//	{
//		File testInput = new File("./output/t.xml"); 
//		RawDataFormatReader germanRTE3=null; 
//		try {
//			germanRTE3 = new RawDataFormatReader(testInput); 
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace(); 
//		}
//		
//		String lang = germanRTE3.getLanguage(); 
//		String channel = germanRTE3.getChannel(); 
//
//		System.out.println("Language: " + lang);
//		System.out.println("channel: " + channel); 
//		
//		try {
//			while(germanRTE3.hasNextPair())
//			{
//				PairXMLData p = germanRTE3.nextPair(); 
//				String text = p.getText();
//				String hypothesis = p.getHypothesis(); 
//				String id = p.getId();
//				String task = p.getTask();
//				String ent = p.getGoldAnswer(); 
//
//				System.out.println("Id: " + id); 
//				System.out.println("Task: " + task);
//				System.out.println("Gold: " + ent); 
//				System.out.println("Text: " + text);
//				System.out.println("Hypothesis: "+ hypothesis);
//				
//			}
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace(); 
//		}
//		
//	}
	
	// private members 
	@SuppressWarnings("unused")
	private final Document dom;  
	private final NodeList pairNodes; 
	private int current = 0;  
	private final String channel; 
	private final String lang; 
}
