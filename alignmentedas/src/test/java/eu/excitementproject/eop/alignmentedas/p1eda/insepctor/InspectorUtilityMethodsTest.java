package eu.excitementproject.eop.alignmentedas.p1eda.insepctor;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static eu.excitementproject.eop.alignmentedas.p1eda.inspector.CompareTwoEDAs.*; 
//import static eu.excitementproject.eop.alignmentedas.p1eda.inspector.InspectJCasAndAlignment.*; 
import eu.excitementproject.eop.alignmentedas.P1EdaRTERunner;
import eu.excitementproject.eop.alignmentedas.p1eda.P1EDATemplate;
import eu.excitementproject.eop.alignmentedas.p1eda.sandbox.WithVO;
import eu.excitementproject.eop.alignmentedas.p1eda.sandbox.WithoutVO;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

// note that, this test requires TreeTagger dependencies in LAP pom. 
// Also note that, this test case takes a long time (due to its set-related 
// method tests) --- not every build requires this test, and the test is @ignored 
// by default. 

@SuppressWarnings("unused")
public class InspectorUtilityMethodsTest {

	@BeforeClass 
	public static void testPrep() 
	{		
		// Set Log4J for the test 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.DEBUG);  // set INFO to hide Debug  
		logger = Logger.getLogger(InspectorUtilityMethodsTest.class); 
		
		logger.info("hello"); 
				
		// this prepares a few aligned data 
	}
	
	private static JCas alignedData1; 
	private static JCas alignedData2; 
	
	public void prepareTestXmis() {

		// pre-process RTE English testset for the test
		File rteTestingXML = new File("../core/src/main/resources/data-set/English_test.xml");
		File evalXmiDir = new File("target/testingXmis");
		
		try {
			LAP_ImplBase lapEN = new TreeTaggerEN(); 
			P1EdaRTERunner.runLAPForXmis(lapEN, rteTestingXML, evalXmiDir);
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
	}
	
		
	// a long test. let's ignore this by default.
	@Ignore("a long test; ignored on default.") @Test
	public void testDiffPairs()
	{
		prepareTestXmis(); 		

		try {
			P1EDATemplate withVO = new WithVO(); 
			P1EDATemplate withoutVO = new WithoutVO(); 
	
			withVO.initialize(new File("src/test/resources/withVO.cmodel")); 
			withoutVO.initialize(new File("src/test/resources/withoutVO.cmodel")); 
			
			getDiffPairs(withVO, withoutVO, new File("target/testingXmis")); 
		}
		catch (Exception e)
		{
			System.err.println("Run stopped with Exception: " + e.getMessage()); 
		}
	}	
	
	@Test
	public void testSummarizeLinks() 
	{
		fail(); // TODO 
	}
	@Test
	public void testSummarizeJCasWordLevel()
	{
		fail(); // TODO 
	}
	
	// logger 
	private static Logger logger; 
	private boolean b; 
}
