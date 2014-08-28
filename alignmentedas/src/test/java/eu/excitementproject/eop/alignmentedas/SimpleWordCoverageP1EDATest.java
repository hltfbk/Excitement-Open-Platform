package eu.excitementproject.eop.alignmentedas;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.alignmentedas.p1eda.SimpleWordCoverageP1EDA;
import eu.excitementproject.eop.alignmentedas.p1eda.TEDecisionWithAlignment;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;

public class SimpleWordCoverageP1EDATest {

	@Test
	public void test() {
		
		// Set Log4J for the test 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.DEBUG);  // set INFO to hide Debug info 
		testlogger = Logger.getLogger(getClass().getName()); 

		try {
			doMinimalTest(); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
	}
	
	public void doMinimalTest() throws EDAException, LAPException
	{
		
		// get an instance of the EDA
		SimpleWordCoverageP1EDA eda = new SimpleWordCoverageP1EDA(); 
		
		// get the LAP for this. 
		TreeTaggerEN lap = new TreeTaggerEN(); 
		
		// Make the "very simple", "minimal" two training data. 
		JCas cas1 = lap.generateSingleTHPairCAS("The train was uncomfortable", "the train was comfortable", "NONENTAILMENT"); 
		JCas cas2 = lap.generateSingleTHPairCAS("The person is hired as a postdoc.","The person is hired as a postdoc.", "ENTAILMENT"); 

		File xmiDir = new File("target/xmis/"); 
		if (!xmiDir.exists())
		{
			xmiDir.mkdirs(); 
		}
		File modelBaseName = new File("target/simple"); 
		
		PlatformCASProber.storeJCasAsXMI(cas1, new File("target/xmis/train1.xmi")); 
		PlatformCASProber.storeJCasAsXMI(cas2, new File("target/xmis/train2.xmi")); 
		
		// Okay. Start Training 
		eda.startTraining(xmiDir,  modelBaseName); 
		
		// ask something?
		JCas eopJCas = lap.generateSingleTHPairCAS("This was hello world.", "This is hello world."); 
		eda.process(eopJCas); 
		
		// ask another 
		eopJCas = lap.generateSingleTHPairCAS("This is a very simple configuration.", "This is in fact a complex configuration."); 
		TEDecisionWithAlignment d1 = eda.process(eopJCas); 
		
		// load Model test 
		SimpleWordCoverageP1EDA eda2 = new SimpleWordCoverageP1EDA(); 
		eda2.initialize(modelBaseName); 
		TEDecisionWithAlignment d2 = eda2.process(eopJCas); 
		assertEquals(d2.getDecision(), d1.getDecision()); 
		assertEquals(d2.getConfidence(), d1.getConfidence(), 0.01);

	}
	
	public static Logger testlogger; 

}
