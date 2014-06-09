package eu.excitementproject.eop.lap.dkpro;

import static org.junit.Assert.fail;

import java.io.File;
//import java.util.HashMap;

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
import eu.excitementproject.eop.lap.PlatformCASProber;

public class MaltParserEnTest {
	
	@Ignore
	@Test
	public void test() {		
		// Set Log4J for the test 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);  // for UIMA (hiding < INFO) 
		Logger testlogger = Logger.getLogger("eu.excitementproject.eop.lap.dkpro.MaltParserEnTest"); 

		LAPAccess lap = null; 
		JCas aJCas = null; 

		// Generating a Single CAS 
		try {
			// linear test 
			lap = new MaltParserEN(); // same as default, which is linear
			
			// one of the LAPAccess interface: that generates single TH CAS. 
			aJCas = lap.generateSingleTHPairCAS("Bush used his weekly radio address to try to build support for his plan to allow workers to divert part of their Social Security payroll taxes into private investment accounts", "Mr. Bush is proposing that workers be allowed to divert their payroll taxes into private accounts."); 
			
			PlatformCASProber.probeCas(aJCas, System.out); 
			
			// poly model test 
			// This will load poly model, and trace output will show "poly" too. 
			//lap = new MaltParserEN("poly"); 
			//aJCas = lap.generateSingleTHPairCAS("Bush used his weekly radio address to try to build support for his plan to allow workers to divert part of their Social Security payroll taxes into private investment accounts", "Mr. Bush is proposing that workers be allowed to divert their payroll taxes into private accounts."); 			
		
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
		for (Dependency dep : JCasUtil.select(textCas, Dependency.class)) {
			System.out.println(dep.getGovernor().getCoveredText() + " -" + dep.getDependencyType() + "-> " + dep.getDependent().getCoveredText());
		}
		for (Dependency dep : JCasUtil.select(hypoCas, Dependency.class)) {
			System.out.println(dep.getGovernor().getCoveredText() + " -" + dep.getDependencyType() + "-> " + dep.getDependent().getCoveredText());
		}
		
		} catch (Exception e) {
			
		}
		
		// process TE data format, and produce XMI files.
		// Let's process English RTE3 data (formatted as RTE5+) as an example. 
		File input = new File("./src/test/resources/small.xml"); // this only holds the first 3 of them.. generate 3 XMIs (first 3 of t.xml) 
		//File input = new File("./src/test/resources/t.xml");  // this is full, and will generate 800 XMIs (serialized CASes)
		File outputDir = new File("./target/"); 
		try {
			lap.processRawInputFormat(input, outputDir); // outputDir will have those XMIs
		} catch (LAPException e)
		{
			fail(e.getMessage()); 
		}

		// Now time to open up the XMI files. 
		// PlatformCASPRober also provides a probe method 
		// for XMI files: probeXmi() --- this does the same thing 
		// of probeCas(), but on XMI. 
		File testXmi = new File("./target/3.xmi"); // you can pick and probe any XMI..  
		try {
			PlatformCASProber.probeXmi(testXmi, System.out);
		} catch (LAPException e) {
			fail(e.getMessage()); 
		} 
	}

}
