package eu.excitementproject.eop.lap.dkpro;

import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.Test;

import static org.junit.Assert.fail;

//import org.junit.Ignore;

public class MaltParserItTest {
	
	@Test
	public void test() {
		// Set Log4J for the test
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);  // for UIMA (hiding < INFO) 
		Logger testlogger = Logger.getLogger(this.getClass());  
		
		LAPAccess lap = null; 
		JCas aJCas = null; 

		// Generating a Single CAS 
		try {
			// linear test 
			lap = new MaltParserIT(); 
			
			// one of the LAPAccess interface: that generates single TH CAS. 
			//aJCas = lap.generateSingleTHPairCAS("Io sono un ragazzo, e ho due cani.", "Sei una ragazza, e hai un gatto nero."); 
			aJCas = lap.generateSingleTHPairCAS("L'Europa adotta una moneta unica.", "L'area economica europea si espande.");
		}
		catch(LAPException e)
		{
			// check if the exception is due to missing TreeTagger binary and model. 
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
