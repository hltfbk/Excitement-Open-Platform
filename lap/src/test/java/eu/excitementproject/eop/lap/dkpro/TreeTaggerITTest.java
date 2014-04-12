package eu.excitementproject.eop.lap.dkpro;

import static org.junit.Assert.*;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
//import org.junit.Ignore;
import org.junit.Test;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;

public class TreeTaggerITTest {

//	@Ignore
	@Test
	public void test() {
	
	// Set Log4J for the test 
	BasicConfigurator.resetConfiguration(); 
	BasicConfigurator.configure(); 
	Logger.getRootLogger().setLevel(Level.INFO);  // for UIMA (hiding < INFO) 
	Logger testlogger = Logger.getLogger("eu.excitementproject.eop.lap.dkpro.TreeTaggerITTest"); 

	// Generating a Single CAS 
	LAPAccess lap = null; 
	JCas aJCas = null; 

	try {
		lap = new TreeTaggerIT(); 
		
		// one of the LAPAccess interface: that generates single TH CAS. 
		aJCas = lap.generateSingleTHPairCAS(
				"La vendita venne fatta per pagare i 27,5 miliardi di dollari di tasse di Yuko. Yuganskneftegaz fu originariamente venduta per 9,4 miliardi di dollari alla poco nota compagnia Baikalfinansgroup che fu poi comperata da una compagnia petrolifera di proprietà dello stato russo, la Rosneft.",
				"Baikalfinansgroup fu venduta a Rosneft."
				); 

		// probeCas check whether or not the CAS has all needed "Entailment" information. 
		// If it does not, it raises an LAPException. 
		// It will also print the summarized data of the CAS to the PrintStream. 
		// If the second argument is null, it will only check the format and raise Exceptions, without printing 
		PlatformCASProber.probeCas(aJCas, System.out); 
		
		// To see the full content of each View, use this 
		//PlatformCASProber.probeCasAndPrintContent(aJCas, System.out); 

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

	}
}