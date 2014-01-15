package eu.excitementproject.eop.core;

import org.apache.uima.jcas.JCas;

//import eu.excitementproject.eop.core.component.distance.CasCreation;
//import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.cas.TOP;

import eu.excitement.type.entailment.Pair;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.lap.PlatformCASProber;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.logging.Logger;


/** This class tests Edit Distance EDA training and testing it 
 * on a small portion of the RTE-3 data set for English, German and Italian language.
 */
public class EditDistanceEDATest {

	static Logger logger = Logger.getLogger(EditDistanceEDATest.class
			.getName());
	
	@Ignore
	@Test
	public void test() {
		
		logger.info("testing EditDistanceEDA ...");
		testItalian();
		testEnglish();
		testGerman();
		
	}
	
	/**
	 * test on the Italian data set
	 * 
	 * @return
	 */
	public void testItalian() {
	
		ArrayList<String> list = new ArrayList<String>();
		
		EditDistanceEDA<EditDistanceTEDecision> editDistanceEDA = 
				new EditDistanceEDA<EditDistanceTEDecision>();
		
		try {
		
			//File configFile = new File("./src/main/resources/configuration-file/EditDistanceEDA_IT.xml");
			//String trainDir = "./src/test/resources/data-set/ITA/dev/";
			//File testDir = new File("./src/test/resources/data-set/ITA/test/");
			//File testDir = new File("/tmp/ITA/test/");
			
			File configFile = new File("./src/test/resources/configuration-file/EditDistanceEDA_IT.xml");
			File testDir = new File("./src/test/resources/data-set/ITA/test/");
			
			CommonConfig config = new ImplCommonConfig(configFile);
			
			//training
			long startTime = System.currentTimeMillis(); 
			//editDistanceEDA.setTrainDIR(trainDir);
			//editDistanceEDA.setWriteModel(false);
			editDistanceEDA.startTraining(config);
			long endTime = System.currentTimeMillis(); 
			logger.info("Time:" + (endTime - startTime)/1000);
			editDistanceEDA.shutdown();
			
			//testing
			editDistanceEDA = 
					new EditDistanceEDA<EditDistanceTEDecision>();
			editDistanceEDA.initialize(config);
			
			startTime = System.currentTimeMillis(); 
			
			for (File xmi : (testDir.listFiles())) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(xmi, null);
				EditDistanceTEDecision teDecision1 = editDistanceEDA.process(cas);
				list.add(getPairID(cas) + "\t" + getGoldLabel(cas) + "\t"  + teDecision1.getDecision().toString() + "\t" + teDecision1.getConfidence());
			}
			endTime = System.currentTimeMillis(); 
			logger.info("Time:" + (endTime - startTime)/1000);
			
			//File annotatedFileName = new File("/tmp/EditDistanceEDA_IT_Result.txt");
			//String evaluationFileName = "/tmp/EditDistanceEDA_IT_Eval.xml";

			//save(annotatedFileName, list, false);
			list.clear();
			//score(annotatedFileName, evaluationFileName);
			editDistanceEDA.shutdown();
		
		} catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	
	/**
	 * test on the English data set
	 * 
	 * @return
	 */
	public void testEnglish() {
		
		ArrayList<String> list = new ArrayList<String>();
		
		EditDistanceEDA<EditDistanceTEDecision> editDistanceEDA = 
				new EditDistanceEDA<EditDistanceTEDecision>();
		
		try {
			
			//Without lexical resources
		    //File configFile = new File("./src/main/resources/configuration-file/EditDistanceEDA_EN.xml");
	        //String trainDir = "./src/test/resources/data-set/ENG/dev/";
		    //File testDir = new File("./src/test/resources/data-set/ENG/test/");
		    //File testDir = new File("/tmp/ENG/test/");
		    
		    File configFile = new File("./src/test/resources/configuration-file/EditDistanceEDA_EN.xml");
			File testDir = new File("./src/test/resources/data-set/ENG/test/");
			
		    CommonConfig config = new ImplCommonConfig(configFile);
		    
		    //training
		    long startTime = System.currentTimeMillis(); 
		    //editDistanceEDA.setTrainDIR(trainDir);
		    //editDistanceEDA.setWriteModel(false);
		    editDistanceEDA.startTraining(config);
		    long endTime = System.currentTimeMillis(); 
		    logger.info("Time:" + (endTime - startTime)/1000);
		    editDistanceEDA.shutdown();
			
		    //testing
			editDistanceEDA = 
					new EditDistanceEDA<EditDistanceTEDecision>();
			editDistanceEDA.initialize(config);
		    
			startTime = System.currentTimeMillis(); 
			for (File xmi : (testDir.listFiles())) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(xmi, null);
				EditDistanceTEDecision teDecision1 = editDistanceEDA.process(cas);
				list.add(getPairID(cas) + "\t" + getGoldLabel(cas) + "\t"  + teDecision1.getDecision().toString() + "\t" + teDecision1.getConfidence());
			}
			endTime = System.currentTimeMillis(); 
			logger.info("Time:" + (endTime - startTime)/1000);
			
			//File annotatedFileName = new File("/tmp/EditDistanceEDA_EN_Result.txt");
			//String evaluationFileName = "/tmp/EditDistanceEDA_EN_Eval.xml";
			
			//save(annotatedFileName, list, false);
			list.clear();
			//score(annotatedFileName, evaluationFileName);
			editDistanceEDA.shutdown();
			
		} catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
	}
			
		
	/**
	 * test on the German data set
	 * 
	 * @return
	 */
	public void testGerman() {
		
		ArrayList<String> list = new ArrayList<String>();
		
		EditDistanceEDA<EditDistanceTEDecision> editDistanceEDA = 
				new EditDistanceEDA<EditDistanceTEDecision>();
		
		try {
			
			//Without lexical resources
			//File configFile = new File("./src/main/resources/configuration-file/EditDistanceEDA_DE.xml");
			//String trainDir = "./src/test/resources/data-set/GER/dev/";
			//File testDir = new File("./src/test/resources/data-set/GER/test/");
			//File testDir = new File("/tmp/GER/test/");
			
			File configFile = new File("./src/test/resources/configuration-file/EditDistanceEDA_DE.xml");
			File testDir = new File("./src/test/resources/data-set/GER/test/");
			
			CommonConfig config = new ImplCommonConfig(configFile);
			
			//training
			long startTime = System.currentTimeMillis(); 
			//editDistanceEDA.setTrainDIR(trainDir);
			//editDistanceEDA.setWriteModel(false);
			editDistanceEDA.startTraining(config);
			long endTime = System.currentTimeMillis(); 
			logger.info("Time:" + (endTime - startTime)/1000);
			editDistanceEDA.shutdown();
			
			//testing
			editDistanceEDA = 
					new EditDistanceEDA<EditDistanceTEDecision>();
			editDistanceEDA.initialize(config);
			
			startTime = System.currentTimeMillis(); 
			for (File xmi : (testDir.listFiles())) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(xmi, null);
				EditDistanceTEDecision teDecision1 = editDistanceEDA.process(cas);
				list.add(getPairID(cas) + "\t" + getGoldLabel(cas) + "\t"  + teDecision1.getDecision().toString() + "\t" + teDecision1.getConfidence());
			}
			endTime = System.currentTimeMillis(); 
			logger.info("Time:" + (endTime - startTime)/1000);
			
			//File annotatedFileName = new File("/tmp/EditDistanceEDA_GER_Result.txt");
			//String evaluationFileName = "/tmp/EditDistanceEDA_GER_Eval.xml";
			
			//save(annotatedFileName, list, false);
			list.clear();
			//score(annotatedFileName, evaluationFileName);

			editDistanceEDA.shutdown();
			
		} catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
	}
			
		
	/**
	 * @param aCas
	 *            the <code>JCas</code> object
	 * @return return the pairID of the T-H pair
	 */
	protected String getPairID(JCas aCas) {
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
	protected String getGoldLabel(JCas aCas) {
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
	 * save the results
	 */
    public void save(File file, List<String> list, boolean append) throws Exception {
    	
    	BufferedWriter writer = null;
    	
    	try {
    		
	    	writer = new BufferedWriter(new FileWriter(file));
	    	PrintWriter printout = new PrintWriter(writer, append);
    	
	    	Iterator<String> iterator = list.iterator();
	    	while(iterator.hasNext()) {
	    		printout.println(iterator.next());
	    	}
	    	printout.close();
	    	
    	} catch (Exception e) {
    		throw new Exception(e.getMessage());
    	} finally {
    		if (writer != null)
    			writer.close();
    	}

    }
    
    /**
	 * calculate the accuracy
	 */
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
	
}

