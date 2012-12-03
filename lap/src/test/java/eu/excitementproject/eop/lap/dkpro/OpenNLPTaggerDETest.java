package eu.excitementproject.eop.lap.dkpro;

import static org.junit.Assert.*;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerDE;

public class OpenNLPTaggerDETest {

	@Test
	public void test() {
		// Generating a Single CAS 
		LAPAccess lap = null; 
		JCas aJCas = null; 

		try {
			lap = new OpenNLPTaggerDE(); 
			
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
	}

}
