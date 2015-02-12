package eu.excitementproject.eop.alignmentedas.p1eda.visualization;

import static org.junit.Assert.*;


import java.io.File;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.Test;

import eu.excitementproject.eop.alignmentedas.p1eda.P1EDATemplate;
import eu.excitementproject.eop.alignmentedas.p1eda.TEDecisionWithAlignment;
import eu.excitementproject.eop.alignmentedas.p1eda.instances.MinimalP1EDA;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;

public class VisualizerTest {

	@Test
	public void test() {
		
		// Set Log4J for the test 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.DEBUG);  // set INFO to hide Debug  
		testlogger = Logger.getLogger(getClass().getName()); 

		// prepare a lemmatizer 
		TreeTaggerEN lap = null; 
		
		try 
		{
			lap = new TreeTaggerEN(); 
			lap.generateSingleTHPairCAS("this is a test.", "TreeTagger in sight?"); 
		}
		catch (Exception e)
		{
			// check if this is due to missing TreeTagger binary and model. 
			// In such a case, we just skip this test. 
			// (see /lap/src/scripts/treetagger/README.txt to how to install TreeTagger) 
			if (ExceptionUtils.getRootCause(e) instanceof java.io.IOException) 
			{
				testlogger.info("Skipping the test: TreeTagger binary and/or models missing. \n To run this testcase, TreeTagger installation is needed. (see /lap/src/scripts/treetagger/README.txt)");  
				Assume.assumeTrue(false); // we won't test this test case any longer. 
			}
		}

		try {
			doVisualizerTest(lap); 
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage()); 
		}
		
	}
	
	public void doVisualizerTest(TreeTaggerEN lap) throws EDAException, LAPException, VisualizerGenerationException
	{
		
		// get an instance of the EDA
		P1EDATemplate eda = new MinimalP1EDA(); 
		//new SimpleWordCoverageEN(
			//	"../core/src/main/resources/ontologies/EnglishWordNet-dict",
				//"../core/src/main/resources/VerbOcean/verbocean.unrefined.2004-05-20.txt");  
		
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
		JCas eopJCas = lap.generateSingleTHPairCAS("This is a very simple configuration.", "This is in fact a complex configuration."); 
		TEDecisionWithAlignment decision = eda.process(eopJCas); 
			
		//Test the visualizer
		Visualizer vis = new P1EdaVisualizer();
		String html = vis.generateHTML(decision);
		testlogger.info(html);
	}
	
	public static Logger testlogger; 

}
