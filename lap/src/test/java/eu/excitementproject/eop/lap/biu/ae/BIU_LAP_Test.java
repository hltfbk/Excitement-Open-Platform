package eu.excitementproject.eop.lap.biu.ae;

import org.apache.uima.jcas.JCas;
import org.junit.Assert;
import org.junit.Test;

import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.lap.LAPAccess;

/**
 *  <b>NOTE:</b> This class was adapted from eu.excitementproject.eop.lap.lappoc.OpenNLPTaggerENTest<br>
 */
public class BIU_LAP_Test {
	
	private static final String TEXT = "Tom likes to eat apples at home, and Julie likes to drink juice.";
	private static final String HYPOTHESIS = "Tom likes to eat fruit.";

	@Test
	public void test() throws Exception {
		try {
			LAPAccess lap = new BIUFullLAP(); 
			JCas jcas = lap.generateSingleTHPairCAS(TEXT, HYPOTHESIS);
			jcas.equals(null); //just to avoid warning for now
			Assert.fail("Test not fully implemented yet.");
		}
		catch (Exception e) {
			ExceptionUtil.outputException(e, System.out);
			throw e;
		}
	}
}
