package eu.excitementproject.eop.distsim.parsing;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.excitementproject.eop.distsim.util.FileUtils;

public class ReuterReader implements CorpusReader {	

	public ReuterReader(String root) throws Exception {
		files = FileUtils.getFiles(new File(root)).iterator();
		dbFactory = DocumentBuilderFactory.newInstance();
		dBuilder = dbFactory.newDocumentBuilder();
		sentences = null;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.parsing.CorpusReader#nextSentence()
	 */
	@Override
	public String nextSentence() throws Exception {
		while (next()) { 
			if (sentences.hasNext()) 
				return sentences.next();
		}
		return null;
	}
	
	protected boolean next() throws IOException, SAXException {
		
		if (sentences != null && sentences.hasNext())
			return true;
		
		if (!files.hasNext())
			return false;
		
		List<String> sentences = new LinkedList<String>();
		Document doc = dBuilder.parse(files.next());
		doc.getDocumentElement().normalize();
		NodeList texts = doc.getElementsByTagName("text");
		for (int j=0; j< texts.getLength(); j++) {
			Node text = texts.item(j);		   
			Element textElement = (Element)text;
			NodeList sents = textElement.getElementsByTagName("p");
			for (int i=0; i< sents.getLength(); i++) {
				Node sent = sents.item(i);
				Element sentElement = (Element)sent;	
				sentences.add(sentElement.getTextContent());
			}
		}
		this.sentences = sentences.iterator();
		return true;
 	}

	protected DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	protected DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	protected Iterator<File> files;
	protected Iterator<String> sentences;

}
