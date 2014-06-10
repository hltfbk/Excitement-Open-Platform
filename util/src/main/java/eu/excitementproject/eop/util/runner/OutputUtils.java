package eu.excitementproject.eop.util.runner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import eu.excitement.type.entailment.Pair;
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
					logger.info("Added result: " + m.group(1) + " / " + m.group(2));
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
		
		try {
			BufferedReader reader = Files.newBufferedReader(Paths.get(testFile), StandardCharsets.UTF_8);
			//InputStream in = Files.newInputStream(Paths.get(testFile));
			//BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			OutputStream out = Files.newOutputStream(Paths.get(xmlFile));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
			
			String line = null, id;
			String[] entDec;
			Pattern p = Pattern.compile("^(.*pair id=\"(\\d+)\") .* (task.*)$");
			Matcher m;
			
			while ((line = reader.readLine()) != null) {
				m = p.matcher(line);
				if (m.matches()) {
					id = m.group(2);
					if (results.containsKey(id)) {
						entDec = results.get(id).split("\\t");
						line = m.group(1) + " " + "entailment=\"" + entDec[1] + "\" benchmark=\"" + entDec[0] + "\" confidence=\"" + entDec[2] + "\" " + m.group(3);
					}
				}
				writer.write(line + "\n");
			}
			writer.close();
			out.close();
			reader.close();
			//in.close();
		} catch (IOException e) {
			logger.error("Problems reading test file " + testFile);
			e.printStackTrace();
		}
		
	}
	
	
	public static void makeSinglePairXML(TEDecision decision, JCas aJCas, String outDir, String lang) {
		
		String xmlResultsFile = outDir + "/results.xml";
		
		Logger logger = Logger.getLogger("eu.excitementproject.eop.util.runner.OutputUtils:makeSinglePairXML");
		
		try {
			
			OutputStream out = Files.newOutputStream(Paths.get(xmlResultsFile));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
			
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
}
