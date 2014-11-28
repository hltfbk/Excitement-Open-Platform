package eu.excitementproject.eop.alignmentedas.p1eda.insepctor;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.excitementproject.eop.alignmentedas.p1eda.P1EDATemplate;
import eu.excitementproject.eop.alignmentedas.p1eda.TEDecisionWithAlignment;
import eu.excitementproject.eop.alignmentedas.p1eda.inspector.InspectorForSimpleWordCoverageP1EDA;
import eu.excitementproject.eop.alignmentedas.p1eda.sandbox.FNR_EN;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

public class InspectorForSimpleWordCoverageP1EDATest {

	
	@BeforeClass
	public static void testPrep()
	{
		// Set Log4J for the test 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);  // set INFO to hide Debug  
		logger = Logger.getLogger(InspectorUtilityMethodsTest.class); 
	}
	
	@Before
	public void prepP1EDAInstance()
	{
		try {
			// prepare lap and eda
			theLap = new TreeTaggerEN(); 
			theEda = new FNR_EN();
			theEda.initialize(new File("./src/test/resources/FNR_EN.cmodel"));  
		}
		catch(Exception e)
		{
			e.printStackTrace(); 
			fail(e.getMessage()); 
		}
	}
	
	@Test
	public void test() {
		treeTaggerCheck(); 
		
		try {
			JCas aTHPair = theLap.generateSingleTHPairCAS("A bus collision with a truck in Uganda has resulted in at least 30 fatalities and has left a further 21 injured.", "30 die in a bus collision in Uganda."); 
			TEDecisionWithAlignment decision = theEda.process(aTHPair); 
			InspectorForSimpleWordCoverageP1EDA.inspectDecision(decision, System.out); 
		}
		catch(Exception e)
		{
			e.printStackTrace(); 
			fail(e.getMessage()); 			
		}
	}
	
	
	//
	//
	//
	
	private void treeTaggerCheck()
	{
		try 
		{
			theLap.generateSingleTHPairCAS("this is a test.", "TreeTagger in sight?"); 
		}
		catch (Exception e)
		{
			// check if this is due to missing TreeTagger binary and model. 
			// In such a case, we just skip this test. 
			// (see /lap/src/scripts/treetagger/README.txt to how to install TreeTagger) 
			if (ExceptionUtils.getRootCause(e) instanceof java.io.IOException) 
			{
				logger.info("Skipping the test: TreeTagger binary and/or models missing. \n To run this testcase, TreeTagger installation is needed. (see /lap/src/scripts/treetagger/README.txt)");  
				Assume.assumeTrue(false); // we won't test this test case any longer. 
			}
		}
	}
	
	
	private P1EDATemplate theEda; 
	private LAP_ImplBase theLap; 
	private static Logger logger;

}
