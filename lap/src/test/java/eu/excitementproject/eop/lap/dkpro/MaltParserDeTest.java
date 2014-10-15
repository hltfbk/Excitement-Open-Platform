package eu.excitementproject.eop.lap.dkpro;

import static org.junit.Assert.fail;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;

public class MaltParserDeTest {
	
	@Ignore
	@Test
	public void test() {
		
		// Set Log4J for the test 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);  // for UIMA (hiding < INFO) 
		Logger testlogger = Logger.getLogger("eu.excitementproject.eop.lap.dkpro.MaltParserDeTest"); 

		LAPAccess lap = null; 
		JCas aJCas = null; 

		// Generating a Single CAS 
		try {
			// linear test 
			lap = new MaltParserDE(); // same as default, which is linear
			
			// one of the LAPAccess interface: that generates single TH CAS. 
			aJCas = lap.generateSingleTHPairCAS("Freiheit und Leben kann man uns nehmen, die Ehre nicht", "Otto Wels hat das gesagt."); 

			
			//PlatformCASProber.probeCas(aJCas, System.out); 
			
			// poly model test 
			// does't work for German!
//			HashMap<String,String> m = new HashMap<String,String>(); 
//			m.put("PARSER_MODEL_VARIANT", "poly"); 
//			lap = new MaltParserDE(m); 
//			aJCas = lap.generateSingleTHPairCAS("Freiheit und Leben kann man uns nehmen, die Ehre nicht", "Otto Wels hat das gesagt."); 
		
			//PlatformCASProber.probeCas(aJCas, System.out); 
		}
		catch(LAPException e)
		{
			// check if this is due to missing TreeTagger binary and model. 
			// In such a case, we just skip this test. 
			// (see /lap/src/scripts/treetagger/README.txt to how to install TreeTagger) 
			if (ExceptionUtils.getRootCause(e) instanceof java.io.IOException) 
			{
				testlogger.info("Skipping the test: TreeTagger binary and/or models missing. \n To run this testcase, TreeTagger installation is needed. (see /lap/src/scripts/treetagger/README.txt)");  
				Assume.assumeTrue(false); // we won't test this test case any longer. 
			}
			
			// if this is some other exception, the test will fail  

			fail(e.getMessage()); 
		}
		
		try {
			JCas textCas = aJCas.getView("TextView");
			JCas hypoCas = aJCas.getView("HypothesisView");
		System.out.println("---dependency in textview---"); 	
		for (Dependency dep : JCasUtil.select(textCas, Dependency.class)) {
			System.out.println(dep.getGovernor().getCoveredText() + " -" + dep.getDependencyType() + "-> " + dep.getDependent().getCoveredText());
		}
		System.out.println("---dependency in hypoview---"); 
		for (Dependency dep : JCasUtil.select(hypoCas, Dependency.class)) {
			System.out.println(dep.getGovernor().getCoveredText() + " -" + dep.getDependencyType() + "-> " + dep.getDependent().getCoveredText());
		}
		
		} catch (Exception e) {
			
		}
	}

}
