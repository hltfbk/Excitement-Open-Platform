package eu.excitementproject.eop.alignmentedas.p1eda.insepctor;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.Before;
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

@Ignore 
public class InspectorUtilityMethodsTest {

	@Before 
	public void dataPrep() {

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
	
	@Test
	public void testDiffPairs()
	{
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

}
