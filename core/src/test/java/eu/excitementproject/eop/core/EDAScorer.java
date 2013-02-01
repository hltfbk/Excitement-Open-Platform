package eu.excitementproject.eop.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Assume;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is temporarily written for the evaluation for the demo in Feb. 2013.
 * 
 * Only the two-way RTE annotations are considered, Entailment vs. NonEntailment (ignoring the upper/lower cases)
 * 
 * @author Rui
 */
public class EDAScorer {
	static Logger logger = Logger.getLogger(EDAScorer.class
			.getName());
	
	public static void score(File resultFile, String outputFile) {
		BufferedReader input;
		float pos_corrt = 0f;
		float pos_wrong = 0f;
		float neg_corrt = 0f;
		float neg_wrong = 0f;
		try {
			input = new BufferedReader(new InputStreamReader(new FileInputStream(resultFile), "UTF-8"));
			String line = "";
			while ((line = input.readLine()) != null) {
				if (line.trim().length() == 0) {
					logger.warning("Empty line. Ignore...");
					continue;
				}
				String[] items = line.split("\t");
				if (items.length != 4) {
					logger.warning("Wrong format! Ignore the line...");
					continue;
				}
				if (items[1].equalsIgnoreCase("Entailment")) {
					if (items[2].equalsIgnoreCase("Entailment")) {
						pos_corrt += 1f;
					} else if (items[2].equalsIgnoreCase("NonEntailment")) {
						pos_wrong += 1f;
					} else {
						logger.warning("Wrong format! Ignore the line...");
						continue;
					}
				} else if (items[1].equalsIgnoreCase("NonEntailment")) {
					if (items[2].equalsIgnoreCase("NonEntailment")) {
						neg_corrt += 1f;
					} else if (items[2].equalsIgnoreCase("Entailment")) {
						neg_wrong += 1f;
					} else {
						logger.warning("Wrong format! Ignore the line...");
						continue;
					}
				} else {
					logger.warning("Wrong format! Ignore the line...");
					continue;
				}
			}
			input.close();
//			logger.info(String.valueOf(pos_corrt));
//			logger.info(String.valueOf(pos_wrong));
//			logger.info(String.valueOf(neg_corrt));
//			logger.info(String.valueOf(neg_wrong));
			
			float EntailmentGold = pos_corrt + pos_wrong;
			float NonEntailmentGold = neg_corrt + neg_wrong;
			float Sum = EntailmentGold + NonEntailmentGold;
			float EntailmentPrecision = pos_corrt / (pos_corrt + neg_wrong);
			float EntailmentRecall = pos_corrt / EntailmentGold;
			float EntailmentFMeasure = 2 * EntailmentPrecision * EntailmentRecall / (EntailmentPrecision + EntailmentRecall);
			float NonEntailmentPrecision = neg_corrt / (neg_corrt + pos_wrong);
			float NonEntailmentRecall = neg_corrt / NonEntailmentGold;
			float NonEntailmentFMeasure = 2 * NonEntailmentPrecision * NonEntailmentRecall / (NonEntailmentPrecision + NonEntailmentRecall);
			float Accuracy = (pos_corrt + neg_corrt) / Sum;
			
			// output the result into an XML file
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element root = doc.createElement("Result");
			Attr attr_EDA = doc.createAttribute("EDA_Configuration");
			attr_EDA.setValue(resultFile.getName());
			root.setAttributeNode(attr_EDA);
			doc.appendChild(root);
	 
			Element pairs = doc.createElement("Total_Pairs");
			pairs.appendChild(doc.createTextNode(String.valueOf((int)Sum)));
			root.appendChild(pairs);
			
			Element acc = doc.createElement("Accuracy");
			acc.appendChild(doc.createTextNode(String.valueOf(Accuracy)));
			root.appendChild(acc);
			
			// positive cases
			Element pos = doc.createElement("Positive_Pairs");
			Attr attr_pos = doc.createAttribute("Number");
			attr_pos.setValue(String.valueOf((int)EntailmentGold));
			pos.setAttributeNode(attr_pos);
			root.appendChild(pos);
			
			Element pos_pre = doc.createElement("Precision");
			pos_pre.appendChild(doc.createTextNode(String.valueOf(EntailmentPrecision)));
			pos.appendChild(pos_pre);
			
			Element pos_rec = doc.createElement("Recall");
			pos_rec.appendChild(doc.createTextNode(String.valueOf(EntailmentRecall)));
			pos.appendChild(pos_rec);
			
			Element pos_f = doc.createElement("F_Measure");
			pos_f.appendChild(doc.createTextNode(String.valueOf(EntailmentFMeasure)));
			pos.appendChild(pos_f);
			
			Element pos_cor = doc.createElement("Classified_As_Positive");
			pos_cor.appendChild(doc.createTextNode(String.valueOf((int)pos_corrt)));
			pos.appendChild(pos_cor);
			
			Element pos_wro = doc.createElement("Classified_As_Negative");
			pos_wro.appendChild(doc.createTextNode(String.valueOf((int)pos_wrong)));
			pos.appendChild(pos_wro);
			
			// negative cases
			Element neg = doc.createElement("Negative_Pairs");
			Attr attr_neg = doc.createAttribute("Number");
			attr_neg.setValue(String.valueOf((int)NonEntailmentGold));
			neg.setAttributeNode(attr_neg);
			root.appendChild(neg);
			
			Element neg_pre = doc.createElement("Precision");
			neg_pre.appendChild(doc.createTextNode(String.valueOf(NonEntailmentPrecision)));
			neg.appendChild(neg_pre);
			
			Element neg_rec = doc.createElement("Recall");
			neg_rec.appendChild(doc.createTextNode(String.valueOf(NonEntailmentRecall)));
			neg.appendChild(neg_rec);
			
			Element neg_f = doc.createElement("F_Measure");
			neg_f.appendChild(doc.createTextNode(String.valueOf(NonEntailmentFMeasure)));
			neg.appendChild(neg_f);
			
			Element neg_wro = doc.createElement("Classified_As_Positive");
			neg_wro.appendChild(doc.createTextNode(String.valueOf((int)neg_wrong)));
			neg.appendChild(neg_wro);
			
			Element neg_cor = doc.createElement("Classified_As_Negative");
			neg_cor.appendChild(doc.createTextNode(String.valueOf((int)neg_corrt)));
			neg.appendChild(neg_cor);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(outputFile));
	 
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
	 
			logger.info("File saved!");
			
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
	}
	
	@Test
	public void test() {
//		File resultFile = new File("./src/test/resources/MaxEntClassificationEDA_AllLexRes_DE.xml_Result.txt");
		File resultFile = new File("./src/test/resources/MaxEntClassificationEDA_AllLexResPos_DE.xml_Result.txt");
//		File resultFile = new File("./src/test/resources/MaxEntClassificationEDA_AllLexRes_EN.xml_Result.txt");
//		File resultFile = new File("./src/test/resources/MaxEntClassificationEDA_NonLexRes_DE.xml_Result.txt");
//		File resultFile = new File("./src/test/resources/MaxEntClassificationEDA_NonLexRes_EN.xml_Result.txt");
		Assume.assumeTrue(resultFile.exists());
		score(resultFile, resultFile.getAbsolutePath() + "_Eval.xml");
	}
}
