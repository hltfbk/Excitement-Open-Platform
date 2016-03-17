package eu.excitementproject.eop.util.runner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

/* Start imports for StAx */

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/* End imports for StAx */

import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.alignmentedas.p1eda.TEDecisionWithAlignment;
import eu.excitementproject.eop.alignmentedas.p1eda.visualization.Visualizer;
import eu.excitementproject.eop.common.TEDecision;

/**
 * Some utils for generating the results XML for display for the online demo
 * 
 * @author Vivi Nastase (FBK)
 *
 */
public class OutputUtils {
	
	
	public static HashMap<String,String> readResults(String file) {
		HashMap<String,String> results = new HashMap<String,String>();
		
		Logger logger = Logger.getLogger("eu.excitementproject.eop.util.runner.OutputUtils:readResults");
		logger.info("Reading results from file: " + file);
		
		try {
			InputStream in = Files.newInputStream(Paths.get(file));
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			Pattern p = Pattern.compile("^(.*?)\\t(.*)$");
			Matcher m;
			
			while ((line = reader.readLine()) != null) {
				m = p.matcher(line);
				if (m.matches()) {
					results.put(m.group(1), m.group(2));
//					logger.info("Added result: " + m.group(1) + " / " + m.group(2));
				}
			}
			reader.close();
			in.close();
		} catch (IOException e) {
			logger.error("Problems reading results file " + file);
			e.printStackTrace();
		}
		return results;
	}

	public static void generateXMLResults(String testFile, String resultsFile, String xmlFile) {
		
		HashMap<String,String> results = readResults(resultsFile);
		
		Logger logger = Logger.getLogger("eu.excitementproject.eop.util.runner.OutputUtils:generateXMLResults");
        logger.info("Generador de XML iniciado");
        logger.info("testFile: " + testFile);
        logger.info("resultsFile: " + resultsFile);
        logger.info("xmlFile: " + xmlFile);
		
		XMLInputFactory ifactory = XMLInputFactory.newFactory();
		XMLOutputFactory ofactory = XMLOutputFactory.newFactory();
		XMLEventFactory xfactory = XMLEventFactory.newFactory();
		StreamSource input = new StreamSource(testFile);
		StreamResult output = new StreamResult(xmlFile);

		try {
			XMLEventReader in = ifactory.createXMLEventReader(input);
			XMLEventWriter out = ofactory.createXMLEventWriter(output);
			while (in.hasNext()) {
                XMLEvent event = in.nextEvent();
                /* We only look for starting pairs */
                if (event.isStartElement() &&
                        ((StartElement) event).getName().getLocalPart().equalsIgnoreCase("pair")) {
                    /* Get the id attribute */
                    QName attID = new QName("id");
                    Attribute idAttribute = ((StartElement) event).getAttributeByName(attID);
                    String id = idAttribute.getValue().toString();
                    if (idAttribute != null) {
                        if (!results.containsKey(id)) {
                            out.add(event);
                            continue;
                        }
                    }

                    Iterator<Attribute> attributes = ((StartElement) event).getAttributes();
                    QName pairName = new QName("pair");
                    ArrayList attributeList = new ArrayList();

                    List nsList = Arrays.asList();
                    String[] entDec = results.get(id).split("\\t");
                    Attribute newIdAttr = xfactory.createAttribute("id", idAttribute.getValue());
                    attributeList.add(newIdAttr);
                    Attribute newEntailAttr = xfactory.createAttribute("entailment", entDec[1]);
                    attributeList.add(newEntailAttr);
                    Attribute newBenchAttr = xfactory.createAttribute("benchmark", entDec[0]);
                    attributeList.add(newBenchAttr);
                    Attribute newConfAttr = xfactory.createAttribute("confidence", entDec[2]);
                    attributeList.add(newConfAttr);
                    while (attributes.hasNext()) {
                        Attribute attribute = attributes.next();
                        if (!attribute.getName().toString().equalsIgnoreCase("id")
                                || !attribute.getName().toString().equalsIgnoreCase("entailment")
                                || !attribute.getName().toString().equalsIgnoreCase("benchmark")
                                || !attribute.getName().toString().equalsIgnoreCase("confidence")) {
                            Attribute newAttr = xfactory.createAttribute(
                                    attribute.getName().toString(), attribute.getValue());
                            attributeList.add(newAttr);
                        }
                        String attr = attributes.next().getValue();
                    }
                    event = xfactory.createStartElement(pairName, attributeList.iterator(), nsList.iterator());
                }
                out.add(event);
            }
            out.flush();
            out.close();
            in.close();
		} catch (XMLStreamException e) {
            e.printStackTrace();
        }

//        try {
//			BufferedReader reader = Files.newBufferedReader(Paths.get(testFile), StandardCharsets.UTF_8);
//			//InputStream in = Files.newInputStream(Paths.get(testFile));
//			//BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//
//			OutputStream out = Files.newOutputStream(Paths.get(xmlFile));
//			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
//
//			String line = null, id;
//			String[] entDec;
//			Pattern p = Pattern.compile("^(.*task.*) (.*pair id=\"(\\d+)\").*$");
//			Matcher m;
//
//			while ((line = reader.readLine()) != null) {
//				m = p.matcher(line);
//				if (m.matches()) {
//					logger.info("Cosas leidas: " + m.group(1) + " - " + m.group(2) + " - " + m.group(3));
//					id = m.group(2);
//					if (results.containsKey(id)) {
//						entDec = results.get(id).split("\\t");
//						line = m.group(1) + " " + "entailment=\"" + entDec[1] + "\" benchmark=\"" + entDec[0] + "\" confidence=\"" + entDec[2] + "\" " + m.group(3);
//					}
//				}
//				writer.write(line + "\n");
//			}
//			writer.close();
//			out.close();
//			reader.close();
//			//in.close();
//		} catch (IOException e) {
//			logger.error("Problems reading test file " + testFile);
//			e.printStackTrace();
//		}
		
	}
	
	
	public static void makeSinglePairXML(TEDecision decision, JCas aJCas, String xmlResultsFile, String lang) {
			
		Logger logger = Logger.getLogger("eu.excitementproject.eop.util.runner.OutputUtils:makeSinglePairXML");
		
		try {
			
			OutputStream out = Files.newOutputStream(Paths.get(xmlResultsFile));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
			
			writer.write("<entailment-corpus lang=\"" + lang + "\">\n");
			writer.write("  <pair id=\"1\" entailment=\"" + decision.getDecision().name() + "\" benchmark=\"N/A\" confidence=\"" + decision.getConfidence() + "\" task=\"EOP test\">\n");
			writer.write("    <t>" + aJCas.getView("TextView").getDocumentText() + "</t>\n");
			writer.write("    <h>" + aJCas.getView("HypothesisView").getDocumentText() + "</h>\n");
			writer.write("  </pair>\n");
			writer.write("</entailment-corpus>\n");
			writer.close();
			out.close();

			
			logger.info("Results file: " + xmlResultsFile);
			
		} catch (IOException | CASException e) {
			logger.error("Could not write to output file " + xmlResultsFile);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param aCas
	 *            the <code>JCas</code> object
	 * @return return the pairID of the T-H pair
	 */
	public static String getPairID(JCas aCas) {
		FSIterator<TOP> pairIter = aCas.getJFSIndexRepository()
				.getAllIndexedFS(Pair.type);
		Pair p = (Pair) pairIter.next();
		return p.getPairID();
	}
	
	
	/**
	 * @param aCas
	 *            the <code>JCas</code> object
	 * @return if the T-H pair contains the gold answer, return it; otherwise,
	 *         return null
	 */
	public static String getGoldLabel(JCas aCas) {
		FSIterator<TOP> pairIter = aCas.getJFSIndexRepository()
				.getAllIndexedFS(Pair.type);
		Pair p = (Pair) pairIter.next();
		if (null == p.getGoldAnswer() || p.getGoldAnswer().equals("")
				|| p.getGoldAnswer().equals("ABSTAIN")) {
			return null;
		} else {
			return p.getGoldAnswer();
		}
	}

	/**
	 * Produce the html that shows the alignment between the text and hypothesis (if an alignment EDA was used to produce the decision)
	 * 
	 * @param te -- the entailment decision as a TEDecision object
	 * @param aJCas -- a CAS object with the pair that was analyzed
	 * @param outDir -- output directory for the entire processing. The html file created will be but in <outDir>/trace
	 * @param vis -- visualizer
	 */
	public static void makeTraceHTML(TEDecision te, JCas aJCas, String outDir, Visualizer vis) {

		Logger logger = Logger.getLogger("eu.excitementproject.eop.util.runner.OutputUtils:makeTraceHTML");

		Path traceDir = Paths.get(outDir + "/trace");
		
		String pairID = OutputUtils.getPairID(aJCas);
		if (pairID == null)
			pairID = "1";
		
		String traceFile = outDir + "/trace/" + pairID + ".html";		

		try {
			if ( Files.notExists(traceDir) ) // || ( ! Files.isDirectory(traceDir)))
				Files.createDirectories(traceDir);
		
			TEDecisionWithAlignment decision = (TEDecisionWithAlignment) te;
			
			OutputStream out = Files.newOutputStream(Paths.get(traceFile));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));			
			writer.write(vis.generateHTML(decision));
			writer.close();
			out.close();
			
		} catch (Exception e) {
			logger.info("Error writing trace file for pair " + getPairID(aJCas));
			e.printStackTrace();
		}
	}
}
