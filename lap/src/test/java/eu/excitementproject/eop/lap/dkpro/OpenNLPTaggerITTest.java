package eu.excitementproject.eop.lap.dkpro;

import static org.junit.Assert.*;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;

public class OpenNLPTaggerITTest {

	@Test
	public void test() {
		// Generating a Single CAS and test it only 
		// other capabilities are (hopefully) all tested in EN version. 
		// Let's check model loading, and tagging all works okay for IT versions 
		LAPAccess lap = null; 
		JCas aJCas = null; 

		try {
			lap = new OpenNLPTaggerIT(); 
			
			// one of the LAPAccess interface: that generates single TH CAS. 
			// (I don't know what it means... just a rather long sentences from a news paper... ) 
			aJCas = lap.generateSingleTHPairCAS("Sopravvivere con i lupi di Misha Defonseca, fu un bestseller negli anni 90. Ma la storia, una bambina sopravvissuta alla Shoah, era inventata. Dubbi sono stati avanzati sull'autenticità della vicenda autobiografica raccontata in Educazione siberiana di Nicolai Lilin.", "E dietro la firma di Emma Mars, autrice della Trilogia delle stanze, di prossima uscita in Italia si nasconde un celebre scrittore francese."); 

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
