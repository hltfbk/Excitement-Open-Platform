package eu.excitementproject.eop.core;

import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.junit.Ignore;
//import static org.junit.Assert.*;
//import eu.excitementproject.eop.core.component.distance.CasCreation;
//import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.*;


import org.apache.uima.cas.FSIterator;
//import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import eu.excitement.type.entailment.Pair;

import eu.excitementproject.eop.common.IEditDistanceTEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
//import eu.excitementproject.eop.lap.LAPException;
//import eu.excitementproject.eop.lap.lappoc.ExampleLAP;
import eu.excitementproject.eop.lap.PlatformCASProber;

/* This class tests EDADistanceEDA training and testing it 
 * on the 3 different languages */
/* this test has been disabled because it requires a lot of time to do it */
public class EditDistanceEDATest {

	@Ignore
	@Test
	public void test() {
    	
		testItalian();
		testEnglish();
		testGerman();
		
	}
	
	// I T A L I A N
	// No lexical resources
	public void testItalian() {
	
		ArrayList<String> list = new ArrayList<String>();
		
		EditDistanceEDA<IEditDistanceTEDecision> editDistanceEDA = 
				new EditDistanceEDA<IEditDistanceTEDecision>();
		
		try {
		
			// I T A L I A N
			//No lexical resources
			File configFile = new File("./src/main/resources/configuration-file/EditDistanceEDA_NonLexRes_IT.xml");
			File annotatedFileName = new File("./src/main/resources/results/EditDistanceEDA_NonLexRes_IT.xml_Result.txt");
			String evaluationFileName = "./src/main/resources/results/EditDistanceEDA_NonLexRes_IT.xml_Result.txt_Eval.xml";
			File testDir = new File("./target/IT/test/");
			
			CommonConfig config = new ImplCommonConfig(configFile);
			
			editDistanceEDA.setTrain(true);
			editDistanceEDA.initialize(config);
			editDistanceEDA.startTraining(config);
			
			for (File xmi : (testDir.listFiles())) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				//System.out.print(".");
				JCas cas = PlatformCASProber.probeXmi(xmi, null);
				IEditDistanceTEDecision teDecision1 = editDistanceEDA.process(cas);
				list.add(getPairID(cas) + "\t" + getGoldLabel(cas) + "\t"  + teDecision1.getDecision().toString() + "\t" + teDecision1.getConfidence());
			}
			
			save(annotatedFileName, list, false);
			list.clear();
			EDAScorer.score(annotatedFileName, evaluationFileName);
			
			//Wordnet
			configFile = new File("./src/main/resources/configuration-file/EditDistanceEDA_Wordnet_IT.xml");
			annotatedFileName = new File("./src/main/resources/results/EditDistanceEDA_Wordnet_IT.xml_Result.txt");
			evaluationFileName = "./src/main/resources/results/EditDistanceEDA_Wordnet_IT.xml_Result.txt_Eval.xml";
			testDir = new File("./target/IT/test/");
			
			config = new ImplCommonConfig(configFile);
			editDistanceEDA.setTrain(true);
			editDistanceEDA.initialize(config);
			editDistanceEDA.startTraining(config);
			
			for (File xmi : (testDir.listFiles())) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(xmi, null);
				IEditDistanceTEDecision teDecision1 = editDistanceEDA.process(cas);
				list.add(getPairID(cas) + "\t" + getGoldLabel(cas) + "\t"  + teDecision1.getDecision().toString() + "\t" + teDecision1.getConfidence());
			}
			
			save(annotatedFileName, list, false);
			list.clear();
			EDAScorer.score(annotatedFileName, evaluationFileName);
		
		} catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	
	// E N G L I S H
	// No lexical resources
	public void testEnglish() {
		
		ArrayList<String> list = new ArrayList<String>();
		
		EditDistanceEDA<IEditDistanceTEDecision> editDistanceEDA = 
				new EditDistanceEDA<IEditDistanceTEDecision>();
		
		try {
			
		    File configFile = new File("./src/main/resources/configuration-file/EditDistanceEDA_NonLexRes_EN.xml");
	        File annotatedFileName = new File("./src/main/resources/results/EditDistanceEDA_NonLexRes_EN.xml_Result.txt");
		    String evaluationFileName = "./src/main/resources/results/EditDistanceEDA_NonLexRes_EN.xml_Result.txt_Eval.xml";	
		    File testDir = new File("./target/ENG/test/");
			
		    CommonConfig config = new ImplCommonConfig(configFile);
			editDistanceEDA.setTrain(true);
			editDistanceEDA.initialize(config);
			editDistanceEDA.startTraining(config);
		    
			for (File xmi : (testDir.listFiles())) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(xmi, null);
				IEditDistanceTEDecision teDecision1 = editDistanceEDA.process(cas);
				list.add(getPairID(cas) + "\t" + getGoldLabel(cas) + "\t"  + teDecision1.getDecision().toString() + "\t" + teDecision1.getConfidence());
			}
			
			save(annotatedFileName, list, false);
			list.clear();
			EDAScorer.score(annotatedFileName, evaluationFileName);
			
			//Wordnet
			configFile = new File("./src/main/resources/configuration-file/EditDistanceEDA_Wordnet_EN.xml");
			annotatedFileName = new File("./src/main/resources/results/EditDistanceEDA_Wordnet_EN.xml_Result.txt");
			evaluationFileName = "./src/main/resources/results/EditDistanceEDA_Wordnet_EN.xml_Result.txt_Eval.xml";
			testDir = new File("./target/ENG/test/");
			
			config = new ImplCommonConfig(configFile);
			editDistanceEDA.setTrain(true);
			editDistanceEDA.initialize(config);
			editDistanceEDA.startTraining(config);
			
			for (File xmi : (testDir.listFiles())) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(xmi, null);
				IEditDistanceTEDecision teDecision1 = editDistanceEDA.process(cas);
				list.add(getPairID(cas) + "\t" + getGoldLabel(cas) + "\t"  + teDecision1.getDecision().toString() + "\t" + teDecision1.getConfidence());
			}
			
			save(annotatedFileName, list, false);
			list.clear();
			EDAScorer.score(annotatedFileName, evaluationFileName);

		} catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
	}
			
		
	// G E R M A N
	// No lexical resources
	public void testGerman() {
		
		ArrayList<String> list = new ArrayList<String>();
		
		EditDistanceEDA<IEditDistanceTEDecision> editDistanceEDA = 
				new EditDistanceEDA<IEditDistanceTEDecision>();
		
		try {
			
			File configFile = new File("./src/main/resources/configuration-file/EditDistanceEDA_NonLexRes_DE.xml");
			File annotatedFileName = new File("./src/main/resources/results/EditDistanceEDA_NonLexRes_DE.xml_Result.txt");
			String evaluationFileName = "./src/main/resources/results/EditDistanceEDA_NonLexRes_DE.xml_Result.txt_Eval.xml";
			File testDir = new File("./target/GER/test/");
			
			CommonConfig config = new ImplCommonConfig(configFile);
			editDistanceEDA.setTrain(true);
			editDistanceEDA.initialize(config);
			editDistanceEDA.startTraining(config);
			
			for (File xmi : (testDir.listFiles())) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(xmi, null);
				IEditDistanceTEDecision teDecision1 = editDistanceEDA.process(cas);
				list.add(getPairID(cas) + "\t" + getGoldLabel(cas) + "\t"  + teDecision1.getDecision().toString() + "\t" + teDecision1.getConfidence());
			}
			
			save(annotatedFileName, list, false);
			list.clear();
			EDAScorer.score(annotatedFileName, evaluationFileName);
			
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
	
	
    public void save(File file, List<String> list, boolean append) throws Exception {
    	
    	BufferedWriter writer = null;
    	
    	try {
    		
	    	writer = new BufferedWriter(new FileWriter(file));
	    	// ... che incapsulo in un PrintWriter
	    	PrintWriter printout = new PrintWriter(writer, append);
    	
	    	Iterator<String> iterator = list.iterator();
	    	while(iterator.hasNext()) {
	    		printout.println(iterator.next());
	    	}
	    	printout.close();
	    	
    	} catch (Exception e) {
    		System.err.println(e.getMessage());
    		throw new Exception(e.getMessage());
    	} finally {
    		if (writer != null)
    			writer.close();
    	}

    }
	
}

