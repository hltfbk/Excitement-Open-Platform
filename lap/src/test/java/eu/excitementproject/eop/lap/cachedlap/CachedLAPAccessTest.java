package eu.excitementproject.eop.lap.cachedlap;

import static org.junit.Assert.*;

import org.junit.Test;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.lap.cachedlap.CachedLAPAccess; 
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerEN;

/**
 * 
 * This test class shows a simple usage example of CachedLAPAccess. 
 * 1) generate a normal LAPAccess
 * 2) provide this LAP to cachedLAP and make a cachedLAPAccess (it is a kind of wrapper) 
 * 3) Call it in a specific way --- reusing JCas as much as possible. 
 * 
 * For data like Entailment Graphs (e.g. same nodes again and again passed as Hypothesis or Text), 
 * the cached version was far faster (saves 99% of time, etc) than uncached version. 
 * 
 * @author Gil
 *
 */
public class CachedLAPAccessTest {

	@Test
	public void test() {

		// Set Log4J
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO); // for UIMA (hiding < INFO)

		// prepare one underlying LAP, say, TreeTagger
		LAPAccess underlyingLAP = null;
		CachedLAPAccess cachedLAP = null;

		// here's our text and hypothesis.
		String text = "This is a pipe.";
		String hypo = "Holy, this is not a pipe!";

		try {
			underlyingLAP = new OpenNLPTaggerEN(); // tree tagger
			//underlyingLAP = new MaltParserEN();
			cachedLAP = new CachedLAPAccess(underlyingLAP); // and cached LAP that works with this LAP
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


		//
		// here's the usage.
		//

		// First, "previously", how you used LAP without cache.
		JCas originalCAS = null;
		try {
			originalCAS = underlyingLAP.generateSingleTHPairCAS(text, hypo);
			// as you see in this call, it is the LAP that generates
			// one CAS and gives you back.
			// making a new CAS takes some time (10 - 20 ms?)
			// So, if we can skip
			// by providing already existing CAS, it would be better.
			// next example shows how you can do this.
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		// here's the "new usage"
		//
		// First, you need one CAS that will be used again and again for
		// the cachedLAP. We reuse it all the time.
		JCas workJCas = null;
		try
		{
			workJCas = cachedLAP.createNewJCas(); 
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		// Okay. we have one CAS.
		// now, we ask nicely to the cachedLAP to use this CAS
		// to annotate text and hypothesis.
		// and the LAP do not make a new CAS.
		try {
			// note that you don't need to .reset() the CAS. The first thing that
			// this cachedLAP does is "reset()" that CAS to make it a empty, clean CAS.
			cachedLAP.annotateSingleTHPairCAS(text, hypo, workJCas);
			// note that this method, unlike generateSingleTHPair, gets CAS as argument. 
			// this is important, since making a new JCas takes some time. 
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//
		// usage example ENDS
		//


		//
		// Some test codes

		JCas cachedCAS = null;
		// caching test
		try {
			cachedCAS = cachedLAP.generateSingleTHPairCAS(text, hypo);
			cachedCAS = cachedLAP.generateSingleTHPairCAS(hypo, text);
			cachedCAS = cachedLAP.generateSingleTHPairCAS(hypo, text);
			originalCAS = underlyingLAP.generateSingleTHPairCAS(hypo, text);
			PlatformCASProber.probeCas(originalCAS, null); 
			PlatformCASProber.probeCas(cachedCAS, null); 
			
			//PlatformCASProber.probeCasAndPrintContent(originalCAS, System.out);
			//PlatformCASProber.probeCasAndPrintContent(cachedCAS, System.out);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}	

		// use the following codes to test "speed" on repeated calls. 
		// 
//		try {
//			JCas a = cachedLAP.createNewJCas(); 
//			
//			for(int i=0; i < 10000; i++)
//			{
//				//underlyingLAP.generateSingleTHPairCAS(hypo, text); // with TREETAGGER EN 22.5 seconds
//				//cachedLAP.generateSingleTHPairCAS(hypo, text); // TREETAGGER EN 18.118
//				cachedLAP.annotateSingleTHPairCAS(hypo, text, a); // TREETAGGER EN 4.687
//			}
//		}
//		catch (Exception e)
//		{
//			fail(e.getMessage());
//		}

	}

}