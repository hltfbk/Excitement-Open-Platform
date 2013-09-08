package eu.excitementproject.eop.core;

import org.apache.uima.jcas.JCas;
import org.junit.*;
import static org.junit.Assert.*;

//import org.junit.Ignore;
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

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ConfigurationException;
//import eu.excitementproject.eop.lap.LAPException;
//import eu.excitementproject.eop.lap.lappoc.ExampleLAP;
import eu.excitementproject.eop.lap.PlatformCASProber;

import java.util.logging.Logger;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;

import eu.excitementproject.eop.lap.LAPAccess;
//import eu.excitementproject.eop.lap.LAPException;
//import eu.excitementproject.eop.util.eval.EDAScorer;

/** This class tests EDADistanceEDA training and testing it 
 * on the 3 different languages
 * this test has been disabled because it requires a lot of time to do it 
 */
public class EditDistanceEDATest {

	static Logger logger = Logger.getLogger(EditDistanceEDATest.class
			.getName());
	
	@Ignore
	@Test
	public void test() {
		
		//testEditDistance();
		testItalian();
		//testEnglish();
		//testGerman();
	}
	
	
	public void testEditDistance() {
		
		EditDistanceEDA<EditDistanceTEDecision> editDistanceEDA = 
				new EditDistanceEDA<EditDistanceTEDecision>();
		
		LAPAccess lap = null;
		
		File configFile = new File("./src/main/resources/configuration-file/EditDistanceEDA_EN.xml");

		CommonConfig config = null;
		try {
			config = new ImplCommonConfig(configFile);
		} catch (ConfigurationException e) {
			logger.warning(e.getMessage());
		}
		
		try {
			editDistanceEDA.initialize(config);
			
			JCas test1Cas;
			
			lap = new TreeTaggerEN();
				
			test1Cas = lap.generateSingleTHPairCAS("Hubble is a telescope, but it is also a spacecraft.","Hubble is a telescope.");	

			logger.info("result:");
			EditDistanceTEDecision decision = editDistanceEDA.process(test1Cas);
			logger.info(decision.getDecision().toString());
			logger.info(String.valueOf(decision.getConfidence()));
			
			editDistanceEDA.shutdown();
			logger.info("shuts down.");
			
			assertTrue(null, decision.getConfidence() > 0.1);
			
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		
	}
	
	/**
	 * test on the Italian data set
	 * 
	 * @return
	 */
	public void testItalian() {
	
		ArrayList<String> list = new ArrayList<String>();
		
		//EditDistanceEDA<EditDistanceTEDecision> editDistanceEDA = 
			//	new EditDistanceEDA<EditDistanceTEDecision>();
		
		EditDistanceEDA<EditDistanceTEDecision> editDistanceEDA = 
				new EditDistanceEDA<EditDistanceTEDecision>();
		
		try {
		
			File configFile = new File("./src/main/resources/configuration-file/EditDistanceEDA_IT.xml");
			File testDir = new File("/tmp/IT/test/");
			
			CommonConfig config = new ImplCommonConfig(configFile);
			
			long startTime = System.currentTimeMillis(); 
			editDistanceEDA.startTraining(config);
			long endTime = System.currentTimeMillis(); 
			logger.info("Time:" + (endTime - startTime)/1000);
			editDistanceEDA.shutdown();
			
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
			
			String modelFileName = (new File(editDistanceEDA.getModelFile())).getName();
			File annotatedFileName = new File("./src/main/resources/results/" + modelFileName + "_Result.txt");
			//String evaluationFileName = "./src/main/resources/results/" + modelFileName + "_Eval.xml";

			save(annotatedFileName, list, false);
			list.clear();
			//EDAScorer.score(annotatedFileName, evaluationFileName);
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
		
		EditDistancePSOEDA<EditDistanceTEDecision> editDistanceEDA = 
				new EditDistancePSOEDA<EditDistanceTEDecision>();
		
		try {
			
			//Without lexical resources
		    File configFile = new File("./src/main/resources/configuration-file/EditDistanceEDA_EN.xml");
	       // File annotatedFileName = new File("./src/main/resources/results/EditDistanceEDA_EN.xml_Token_Result.txt");
		    //String evaluationFileName = "./src/main/resources/results/EditDistanceEDA_EN.xml_Token_Result.txt_Eval.xml";	
		    File testDir = new File("/tmp/ENG/test/");
			
		    CommonConfig config = new ImplCommonConfig(configFile);
		    
		    long startTime = System.currentTimeMillis(); 
		    editDistanceEDA.startTraining(config);
		    long endTime = System.currentTimeMillis(); 
		    logger.info("Time:" + (endTime - startTime)/1000);
		    
			editDistanceEDA.shutdown();
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
			
			String modelFileName = (new File(editDistanceEDA.getModelFile())).getName();
			File annotatedFileName = new File("./src/main/resources/results/" + modelFileName + "_Result.txt");
			//String evaluationFileName = "./src/main/resources/results/" + modelFileName + "_Eval.xml";
			
			save(annotatedFileName, list, false);
			list.clear();
			//EDAScorer.score(annotatedFileName, evaluationFileName);
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
			File configFile = new File("./src/main/resources/configuration-file/EditDistanceEDA_DE.xml");
			//File annotatedFileName = new File("./src/main/resources/results/EditDistanceEDA_DE.xml_Token_Result.txt");
			//String evaluationFileName = "./src/main/resources/results/EditDistanceEDA_DE.xml_Token_Result.txt_Eval.xml";
			File testDir = new File("/tmp/GER/test/");
			
			CommonConfig config = new ImplCommonConfig(configFile);
			
			long startTime = System.currentTimeMillis(); 
			editDistanceEDA.startTraining(config);
			long endTime = System.currentTimeMillis(); 
			logger.info("Time:" + (endTime - startTime)/1000);
			
			editDistanceEDA.shutdown();
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
			
			String modelFileName = (new File(editDistanceEDA.getModelFile())).getName();
			File annotatedFileName = new File("./src/main/resources/results/" + modelFileName + "_Result.txt");
			//String evaluationFileName = "./src/main/resources/results/" + modelFileName + "_Eval.xml";
			
			save(annotatedFileName, list, false);
			list.clear();
			//EDAScorer.score(annotatedFileName, evaluationFileName);

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
	
}

