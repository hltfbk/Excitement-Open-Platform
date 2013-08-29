package eu.excitementproject.eop.lap.dkpro;

//FROZEN
//WE will revive MSTParser after DKPRO 1.5.0 gets stable and uploaded to Maven Central 
//
//import static org.junit.Assert.*;
//
//import org.apache.uima.jcas.JCas;
//import org.junit.Test;
//
//import eu.excitementproject.eop.lap.LAPAccess;
//import eu.excitementproject.eop.lap.LAPException;
//import eu.excitementproject.eop.lap.PlatformCASProber;
//
//public class MSTParserDETest {
//
//	@Test
//	public void test() {
//		// Generating a Single CAS 
//		LAPAccess lap = null; 
//		JCas aJCas = null; 
//
//		try {
//			lap = new MSTParserDE(); // same as ("default"); 
//			//lap = new MSTParserDE("long"); 
//			
//			// one of the LAPAccess interface: that generates single TH CAS. 
//			aJCas = lap.generateSingleTHPairCAS("Freiheit und Leben kann man uns nehmen, die Ehre nicht", "Otto Wels hat das gesagt."); 
//
//			// probeCas check whether or not the CAS has all needed "Entailment" information. 
//			// If it does not, it raises an LAPException. 
//			// It will also print the summarized data of the CAS to the PrintStream. 
//			// If the second argument is null, it will only check the format and raise Exceptions, without printing 
//			PlatformCASProber.probeCas(aJCas, System.out); // it has dependency annotations. 
//			
//			// To see the full content of each View, use this 
//			//PlatformCASProber.probeCasAndPrintContent(aJCas, System.out); 
////			aJCas.reset(); 
////			JCas aJCas2 = lap.generateSingleTHPairCAS("Heute ist Freitag.", "Heute ist nicht Montag."); 
////			PlatformCASProber.probeCas(aJCas2, System.out); 
////			
//			// Note that: the above code --- 
//			// Model loading was only done twice (once for TView, once for HView), and not duplicated 
//			// after the first call. 
//
//		}
//		catch(LAPException e)
//		{
//			fail(e.getMessage()); 
//		}
//		
//		// TODO: loading larger model test. (different variant) 
//		
//	}
//
//}
