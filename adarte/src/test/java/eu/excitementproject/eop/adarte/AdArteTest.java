package eu.excitementproject.eop.adarte;

import org.apache.uima.jcas.JCas;

import java.io.*;

import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.adarte.EditDistanceTEDecision;
import eu.excitementproject.eop.adarte.AdArte;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.MaltParserEN;

import java.util.logging.Logger;

import org.junit.*;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.cas.TOP;
import org.apache.commons.lang.exception.ExceptionUtils;

import static org.junit.Assert.*;


/** This class tests Edit Distance EDA training and testing it 
 * on a small portion of the RTE-3 data set for English, German and Italian language.
 */
public class AdArteTest {

	static Logger logger = Logger.getLogger(AdArteTest.class
			.getName());
	

	@Test
	public void test() {
		
		logger.info("testing AdArte ...");
		testEnglish();
		
	}
	
	/**
	 * test on the English data set
	 * 
	 * @return
	 */
	public void testEnglish() {
		
		AdArte<EditDistanceTEDecision> tdEDA;
		
		CommonConfig config = null;
		
		try {
		
			// the configuration file
			File configFile = new File("./src/test/resources/configuration-file/AdArte_EN.xml");
			
			// creating an instance of AdArte
			tdEDA = new AdArte<EditDistanceTEDecision>();
			
			// loading the configuration file
			config = new ImplCommonConfig(configFile);
			
			// the LAP
			LAPAccess lap = null;
			
			// data set for training and test
			File input = new File("./src/test/resources/dataset/SICK_EN_EXAMPLE.xml");
			
			// tmp directory for storing the pre-processed files
			File outputDir  = new File("/tmp/AdArte_Test/"); 
			//build it if it does not exist
			if (outputDir.exists() == false)
				outputDir.mkdir();
			
			// the LAP based on MaltParser using TreeTagger
			lap = new MaltParserEN();
			// pre-processing the data set
			lap.processRawInputFormat(input, outputDir);
		
			// initialization and start training
			tdEDA.startTraining(config);
			
			// shutdown
			tdEDA.shutdown();
				
			// initialization for testing
			tdEDA.initialize(config);
				
			double correct = 0;
			double examples = 0;
			//double example_CONTRADICTION = 0;
			//double example_ENTAILMENT = 0;
			//double example_UNKNOWN = 0;
			//double correct_CONTRADICTION = 0;
			//double correct_ENTAILMENT = 0;
			//double correct_UNKNOWN = 0;
			
			// cycle on the files to be annotated
			for (File xmi : outputDir.listFiles()) {
				
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
			
				// the cas containing the T/H pair to be annotated
				JCas jcas = PlatformCASProber.probeXmi(xmi, null);
				// annotate
				EditDistanceTEDecision edtedecision = tdEDA.process(jcas);
				
				examples++;
				if (getGoldLabel(jcas).equalsIgnoreCase(edtedecision.getDecision().toString()))
						correct++;
						
				/*
				if (getGoldLabel(jcas).equals("UNKNOWN")) {
					example_UNKNOWN++;
					if (getGoldLabel(jcas).equalsIgnoreCase(edtedecision.getDecision().toString()))
						correct_UNKNOWN++;
				}
				
				else if (getGoldLabel(jcas).equals("CONTRADICTION")) {
					example_CONTRADICTION++;
					if (getGoldLabel(jcas).equalsIgnoreCase(edtedecision.getDecision().toString()))
						correct_CONTRADICTION++;
				}
				
				else if (getGoldLabel(jcas).equals("ENTAILMENT")) {
					example_ENTAILMENT++;
					if (getGoldLabel(jcas).equalsIgnoreCase(edtedecision.getDecision().toString()))
						correct_ENTAILMENT++;
				}
				*/
						
			}
			
			// shutdown
			tdEDA.shutdown();
			
			// the accuracy has to be 1.0
			assertTrue(correct/examples == 1.0);

			//logger.info("accuracy:" + correct/examples);
			//System.err.println("accuracy UNKNOWN:" + correct_UNKNOWN/example_UNKNOWN);
			//System.err.println("accuracy CONTRADICTION:" + correct_ENTAILMENT/example_ENTAILMENT);
			//System.err.println("accuracy ENTAILMENT:" + correct_CONTRADICTION/example_CONTRADICTION);
			//System.err.println("examples:" + examples);
			
		} catch(LAPException e) {
			// check if this is due to missing TreeTagger binary and model. 
			// In such a case, we just skip this test. 
			// (see /lap/src/scripts/treetagger/README.txt to how to install TreeTagger) 
			if (ExceptionUtils.getRootCause(e) instanceof java.io.IOException)  {
					logger.info("Skipping the test: TreeTagger binary and/or models missing. \n To run this testcase, TreeTagger installation is needed. (see /lap/src/scripts/treetagger/README.txt)");  
					Assume.assumeTrue(false); // we won't test this test case any longer. 
			}
		} catch (Exception e) {
			// if this is some other exception, the test will fail  
			fail(e.getMessage()); 
		}
	
	}

	/**
	 * @param aCas
	 *            the <code>JCas</code> object
	 * @return if the T-H pair contains the gold answer, return it; otherwise,
	 *         return null
	 */
	private String getGoldLabel(JCas aCas) {
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

	