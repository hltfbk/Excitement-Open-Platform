package eu.excitementproject.eop.lap.dkpro;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerDE;

public class TreeTaggerDETest {

	@Test
	public void testAddAnnotationOnJCasString() {
		// Generating a Single CAS 
		LAPAccess lap = null; 
		JCas aJCas = null; 

		try {
			lap = new TreeTaggerDE(); 
			
			// one of the LAPAccess interface: that generates single TH CAS. 
			aJCas = lap.generateSingleTHPairCAS("Freiheit und Leben kann man uns nehmen, die Ehre nicht", "Otto Wels hat das gesagt."); 

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
			fail(e.getMessage()); 
		}
		
		
		// process TE data format, and produce XMI files.
		// Let's process German RTE3 data (formatted as RTE5+) as an example. 
		File input = new File("./src/test/resources/small_de.xml"); // this only holds the first 3 pairs of RTE3-test.. generate 3 XMIs 
		File outputDir = new File("./target/"); 
		try {
			lap.processRawInputFormat(input, outputDir); // outputDir will have those XMIs
		} catch (LAPException e)
		{
			fail(e.getMessage()); 
		}

		// Now time to open up the XMI files. 
		// PlatformCASProber also provides a probe method 
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
