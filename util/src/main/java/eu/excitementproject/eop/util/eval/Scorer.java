package eu.excitementproject.eop.util.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Assume;
import org.junit.Test;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * This class implements a basic scorer that can be used for evaluating the produced entailment annotations.
 * It supports multi-class annotation problems evaluation (e.g., Entailment, Contradiction, Unknown).
 * 
 * Given a file containing the pair id (1st column), the gold annotation (2nd column), the predicted
 * annotation (3rd column) and the annotation confidence (4th column) it calculates Precision, Recall
 * and F1 measure for each of the gold labels (e.g. Entailment, NonEntailment) in the annotated file. The
 * overall Accuracy is another measure produced by the scorer. 
 * 
 * The following is an example of valid input file:
 * 
 * 747     NONENTAILMENT   NonEntailment   0.21761363636362963
 * 795     ENTAILMENT      Entailment      0.5823863636363704
 * 60      ENTAILMENT      Entailment      0.3601641414141482
 * 546     NONENTAILMENT   NonEntailment   0.14488636363635687
 * 
 * while the following is an example of final report:
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <results>
 *   <label id="NONENTAILMENT">
 *     <Precision>66.182</Precision>
 *     <Recall>46.667</Recall>
 *     <F1>54.737</F1>
 *     <ContingencyTable FN="208" FP="93" TN="317" TP="182"/>
 *   </label>
 *   <label id="ENTAILMENT">
 *     <Precision>60.381</Precision>
 *     <Recall>77.317</Recall>
 *     <F1>67.807</F1>
 *     <ContingencyTable FN="301" FP="301" TN="499" TP="499"/>
 *   </label>
 *   <Accuracy>62.375</Accuracy>
 *   <Precision>62.375</Precision>
 *   <Recall>62.375</Recall>
 *   <F1micro>62.375</F1micro>
 *   <ContingencyTable FN="301" FP="301" TN="499" TP="499"/>
 * </results>
 *
 * 
 * @author Roberto Zanoli
 * 
 * @since February 2015
 * 
 */
public class Scorer {
	
	//the logger
	static Logger logger = Logger.getLogger(Scorer.class.getName());
	
	/*
	 * This structure contains the annotated labels (e.g. ENTAILMENT, NONENTAILMENT) -the key-
	 * and a contingency table for each of the labels -the value- 

	 */
	static HashMap<String, ContingencyTable> LIST_OF_LABELS = new HashMap<String, ContingencyTable>();
	
    /*
     * For printing the results with a variable precision
     */
    static NumberFormat FORMATTER = new DecimalFormat("#0.000");
	
	/**
	 * Evaluate the annotated file
	 * 
	 * @param annotatedFile the annotated file containing the annotation to be evaluated
	 * @param outputFile the output file containing the produced evaluation
	 * 
	 */
	public static void score(File annotatedFile, String outputFile) {
		
		// read the annotated files and save the gold labels
		initLabels(annotatedFile);
		// fill the contingency table of the labels 
		createContingencyTable(annotatedFile);
		// print the evaluation
		print(outputFile);
		
	}
	
	/**
	 * Read the annotated file containing the gold labels (e.g. ENTAILMENT, NONENTAILMENT)
	 * Then a contingency table is first created and then assigned to each label.
	 * 
	 * @param annotatedFile the file containing the gold annotation and the predicted one
	 *  
	 */
	private static void initLabels(File annotatedFile) {
		
		BufferedReader input = null;
		
		try {
			
			input = new BufferedReader(new InputStreamReader(new FileInputStream(annotatedFile), "UTF-8"));
			
			String line = "";
			while ((line = input.readLine()) != null) {
				
				if (line.trim().length() == 0) {
					logger.warning("Empty line. Ignore...");
					continue;
				}
				
				String[] items = line.split("\t");
				
				//the gold annotation is on the 2nd column
				String goldLabel = items[1];
				if (!LIST_OF_LABELS.containsKey(goldLabel)) {
					ContingencyTable contingencyTable = new ContingencyTable();
					LIST_OF_LABELS.put(goldLabel, contingencyTable);
				}
				
			}
			
		} catch (Exception e) {
			
			logger.warning(e.getMessage());
			
		}
		
		finally {

			try {
				input.close();
			 } catch (IOException e) {
				 logger.warning(e.getMessage());
	         }
			
		}
		
	}
	
	/**
	 * Read the annotated file and fill the contingency table associated to each label
	 * 
	 * @param annotatedFile the annotated file
	 */
	public static void createContingencyTable(File annotatedFile) {
		
		BufferedReader input = null;
		
		try {
			
			input = new BufferedReader(new InputStreamReader(new FileInputStream(annotatedFile), "UTF-8"));
			
			String line = "";
			while ((line = input.readLine()) != null) {
				
				String[] items = line.split("\t");
				
				// the gold label is on the 2nd column
				String goldLabel = items[1];
				// the predicted label is on the 3rd column
				String predictedLabel = items[2];
				
				Iterator<String> iterator = LIST_OF_LABELS.keySet().iterator();
				while (iterator.hasNext()) {
					
					//the current label
					String label_i = iterator.next();
					
					//the contingency table for the current label
					//Gold/Predicted  Yes | No |
					//               -----------
					//            Yes| TP | FN |
					//            No | FP | TN |
		            //               -----------
					ContingencyTable contingencyTable_label_i = LIST_OF_LABELS.get(label_i);
					if (label_i.equalsIgnoreCase(goldLabel)) { //Gold=Yes
						if (goldLabel.equalsIgnoreCase(predictedLabel)) // Gold=Yes, Predicted=Yes
							contingencyTable_label_i.incTp(); //increment TP
						else //Gold=Yes, Predicted=False
							contingencyTable_label_i.incFn(); //increment FN 
					}
					else { // Gold=False
						if (label_i.equalsIgnoreCase(predictedLabel)) //Gold=False, Predicted=True
							contingencyTable_label_i.incFp(); //increment FP
						else //Gold=False, Predicted=False
							contingencyTable_label_i.incTn(); //increment TN 
					}
				}
				
			}
			

		} catch (Exception e) {
			
			logger.warning(e.getMessage());
			
		}
		
		finally {

			try {
				input.close();
			 } catch (IOException e) {
				 logger.warning(e.getMessage());
	         }
			
		}
		
	}
	
	
	/**
	 * Print the evaluation; Precision, Recall and F1 measure are printed for each label.
	 * In addition the overall Accuracy, Precision, Recall and F1 micro are printed too. 
	 * 
	 * @param outputFile
	 */
	public static void print(String outputFile) {
	
		try {
			
			//This is the contingency table for measuring the total Accuracy, Precision, recall
			//and F1 among the labels
			ContingencyTable globalContingencyTable = new ContingencyTable();
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("results");
			doc.appendChild(rootElement);
			
			Iterator<String> iterator = LIST_OF_LABELS.keySet().iterator();
			while (iterator.hasNext()) {
				String label_i = iterator.next();
				ContingencyTable contingencyTable_label_i = LIST_OF_LABELS.get(label_i);
				
				//update the global Contingency Table
				globalContingencyTable.incTp(contingencyTable_label_i.getTp());
				globalContingencyTable.incFn(contingencyTable_label_i.getFn());
				globalContingencyTable.incFp(contingencyTable_label_i.getFp());
				globalContingencyTable.incTn(contingencyTable_label_i.getTn());
				
				//calculate the precision
				double precision = (double)contingencyTable_label_i.getTp()/
						(double)(contingencyTable_label_i.getTp() + contingencyTable_label_i.getFp()) * 100;
				//calculate the recall
				double recall = (double)contingencyTable_label_i.getTp()/
						(double)(contingencyTable_label_i.getTp() + contingencyTable_label_i.getFn()) * 100;
				//calculate the F1 micro
				double f1_micro = 2*precision*recall/(precision+recall);
				
				// label element
				Element label = doc.createElement("label");
				rootElement.appendChild(label);
			
				// set attribute to label element
				Attr attrId = doc.createAttribute("id");
				attrId.setValue(label_i);
				label.setAttributeNode(attrId);
				
				//int labelOccurrences = contingencyTable_label_i.getTp() + contingencyTable_label_i.getFn();
				// set attribute to labelOccurrences element
				//Attr attrOccurrences = doc.createAttribute("occurrences");
				//attrOccurrences.setValue(String.valueOf(labelOccurrences));
				//label.setAttributeNode(attrOccurrences);
				// precision element
				Element elementPrecision = doc.createElement("Precision");
				elementPrecision.appendChild(doc.createTextNode(String.valueOf(FORMATTER.format(precision))));
				label.appendChild(elementPrecision);
				// recall element
				Element elementRecall = doc.createElement("Recall");
				elementRecall.appendChild(doc.createTextNode(String.valueOf(FORMATTER.format(recall))));
				label.appendChild(elementRecall);
				// f1 element
				Element elementF1 = doc.createElement("F1");
				elementF1.appendChild(doc.createTextNode(String.valueOf(FORMATTER.format(f1_micro))));
				label.appendChild(elementF1);
				
				// contingency table element
				Element elementContingencyTable = doc.createElement("ContingencyTable");
				label.appendChild(elementContingencyTable );
				// add attribute tp to contingency table element
				Attr attr_tp = doc.createAttribute("TP");
				attr_tp.setValue(String.valueOf(globalContingencyTable.getTp()));
				elementContingencyTable.setAttributeNode(attr_tp);
				// add attribute fn to contingency table element
				Attr attr_fn = doc.createAttribute("FN");
				attr_fn.setValue(String.valueOf(globalContingencyTable.getFn()));
				elementContingencyTable.setAttributeNode(attr_fn);
				// add attribute fp to contingency table element
				Attr attr_fp = doc.createAttribute("FP");
				attr_fp.setValue(String.valueOf(globalContingencyTable.getFp()));
				elementContingencyTable.setAttributeNode(attr_fp);
				// add attribute tn to contingency table element
				Attr attr_tn = doc.createAttribute("TN");
				attr_tn.setValue(String.valueOf(globalContingencyTable.getTn()));
				elementContingencyTable.setAttributeNode(attr_tn);
				
			}
			
			//calculate the global accuracy
			double accuracy = (double)(globalContingencyTable.getTp() + globalContingencyTable.getTn())/
					(double)(globalContingencyTable.getTp() + globalContingencyTable.getFp() +
							globalContingencyTable.getFn() + globalContingencyTable.getTn()) * 100;
			//calculate the global precision
			double precision = (double)globalContingencyTable.getTp()/
					(double)(globalContingencyTable.getTp() + globalContingencyTable.getFp()) * 100;
			//calculate the global recall
			double recall = (double)globalContingencyTable.getTp()/
					(double)(globalContingencyTable.getTp() + globalContingencyTable.getFn()) * 100;
			//calculate the global F1
			double f1Micro = 2*precision*recall/(precision + recall);
			
			// Add Accuracy, Precision, Recall and F1 measure
			// accuracy element
			Element elementAccuracy = doc.createElement("Accuracy");
			elementAccuracy.appendChild(doc.createTextNode(String.valueOf(FORMATTER.format(accuracy))));
			rootElement.appendChild(elementAccuracy);
			// Precision element
			Element elementPrecision = doc.createElement("Precision");
			elementPrecision.appendChild(doc.createTextNode(String.valueOf(FORMATTER.format(precision))));
			rootElement.appendChild(elementPrecision);				
			// Precision element
			Element elementRecall = doc.createElement("Recall");
			elementRecall.appendChild(doc.createTextNode(String.valueOf(FORMATTER.format(recall))));
			rootElement.appendChild(elementRecall);
			// F1 micro element
			Element elementF1Micro = doc.createElement("F1micro");
			elementF1Micro.appendChild(doc.createTextNode(String.valueOf(FORMATTER.format(f1Micro))));
			rootElement.appendChild(elementF1Micro);
			
			// Add Contingency Table
			// contingency table element
			Element elementContingencyTable = doc.createElement("ContingencyTable");
			rootElement.appendChild(elementContingencyTable );
			// add attribute tp to contingency table element
			Attr attr_tp = doc.createAttribute("TP");
			attr_tp.setValue(String.valueOf(globalContingencyTable.getTp()));
			elementContingencyTable.setAttributeNode(attr_tp);
			// add attribute fn to contingency table element
			Attr attr_fn = doc.createAttribute("FN");
			attr_fn.setValue(String.valueOf(globalContingencyTable.getFn()));
			elementContingencyTable.setAttributeNode(attr_fn);
			// add attribute fp to contingency table element
			Attr attr_fp = doc.createAttribute("FP");
			attr_fp.setValue(String.valueOf(globalContingencyTable.getFp()));
			elementContingencyTable.setAttributeNode(attr_fp);
			// add attribute tn to contingency table element
			Attr attr_tn = doc.createAttribute("TN");
			attr_tn.setValue(String.valueOf(globalContingencyTable.getTn()));
			elementContingencyTable.setAttributeNode(attr_tn);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			//indent the produced xml file
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			//write
			StreamResult result = new StreamResult(new File(outputFile));
			// Output to console for testing
			//StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);
		 
			logger.info("The results are in:" + outputFile);
			
		} catch (ParserConfigurationException pce) {
			logger.warning(pce.getMessage());
		} catch (TransformerException tfe) {
			logger.warning(tfe.getMessage());
		}	
			
	}
	
	
	@Test
	/**
	 * Test the scorer
	 */
	public void test() {
		
		File annotatedFile = new File("../core/src/main/resources/results/EditDistanceEDA_EN.xml_Result.txt");
		Assume.assumeTrue(annotatedFile.exists());
		score(annotatedFile, annotatedFile.getAbsolutePath() + "_Eval.xml");
		
	}
	
	
	/**
	 * This class implements the Contingency Table to be used for calculating Accuracy,
	 * Precision, Recall and F1 measure
	 */
	private static class ContingencyTable {
	
		int tp; //true positive
		int fn; //false negative
		int fp; //false positive
		int tn; //true negative
		
		ContingencyTable() {
			
			tp = 0;
			fn = 0;
			fp = 0;
			tn = 0;
			
		}
		
		void incTp (int incr) {
			
			tp = tp + incr;
			
		}
		
		void incFn (int incr) {
			
			fn = fn + incr;
			
		}
		
		void incFp (int incr) {
			
			fp = fp + incr;
			
		}
		
		void incTn (int incr) {
			
			tn = tn + incr;
			
		}
		
		void incTp () {
		
			tp++;
			
		}
		
		void incFn () {
			
			fn++;
			
		}
		
		void incFp () {
			
			fp++;
			
		}
		
		void incTn () {
			
			tn++;
			
		}
		
		int getTp() {
			
			return tp;
			
		}
		
		int getFn() {
			
			return fn;
			
		}
		
		int getFp() {
			
			return fp;
			
		}
		
		int getTn() {
			
			return tn;
			
		}
		
		
	}
		
}
