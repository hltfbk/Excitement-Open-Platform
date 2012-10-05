package eu.excitementproject.eop.lap.lappoc;

import java.io.File;

import org.apache.uima.jcas.JCas;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber; 

public class UsageExample1 {

	// TODO remove all "relative path", which won't work in Jars. 
	// Well, this isn't really important since this is just an example... 
	
	/**
	 * Simple usage example of sample LAP, and also that of PlatformCASProber.  
	 * LAP main class is WSTokenizerEN, which uses an AE WSSeparatorAE. 
	 * 
	 * If you re-implement a method in WSTokenizerEN (don't need to be an AE) 
	 * you automatically gets all LAPAccess interfaces. see WSTokenizerEN.java 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		WSTokenizerEN lap = null; 
		JCas aJCas = null; 
		
		// Generating a Single CAS 
		try {
			lap = new WSTokenizerEN(); 
			
			// one of the LAPAccess interface: that generates single TH CAS. 
			aJCas = lap.generateSingleTHPairCAS("This is Something.", "This is something else."); 

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
			System.err.println(e.getMessage()); 
			e.printStackTrace();
			System.exit(1); 
		}
		
		
		// process TE data format, and produce XMI files.
		File input = new File("./src/test/resources/small.xml"); 
		File outputDir = new File("./target/"); 
		try {
			lap.processRawInputFormat(input, outputDir);
		} catch (LAPException e)
		{
			e.printStackTrace(); 
		}
	
		// PlatformCASPRober also provides a probe method 
		// for XMI files: probeXmi() --- this does the same thing 
		// of probeCas(), but on XMI. 
		File testXmi = new File("./target/3.xmi"); 
		try {
			PlatformCASProber.probeXmi(testXmi, System.out);
		} catch (LAPException e) {
			e.printStackTrace();
		} 
		
	}
	
}
