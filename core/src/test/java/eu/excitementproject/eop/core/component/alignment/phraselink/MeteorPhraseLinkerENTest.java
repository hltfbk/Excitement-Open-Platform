package eu.excitementproject.eop.core.component.alignment.phraselink;

import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Ignore;
import org.junit.Test;

import eu.excitement.type.alignment.LinkUtils;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerEN;

public class MeteorPhraseLinkerENTest {

	@Ignore // ignore as default. (basic capability tested by super class --- just for further test.) 
	@Test
	public void test() {
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.DEBUG);   
		Logger testlogger = Logger.getLogger(this.getClass().toString()); 
		
		testlogger.info("This test class may take upto 30 seconds ... "); 

		// prepare a JCas 
		JCas aJCas = null; 
		OpenNLPTaggerEN tokenizer = null; 
		try 
		{
			tokenizer = new OpenNLPTaggerEN(); 
			aJCas = tokenizer.generateSingleTHPairCAS("This is a cat.", "This is China's new cat."); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}

		// main class test. 
		// load test 
		AlignmentComponent phraseLinker = null; 
		try {
			phraseLinker = new MeteorPhraseLinkerEN(); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}

		try {
			// RTE3 test pair 17 (some links) 
			aJCas = tokenizer.generateSingleTHPairCAS(
					"David Golinkin is single-handedly responsible for uncovering and re-publishing dozens of responsa of the Committee on Jewish Law and Standards of the Rabbinical Assembly, making them available to the general public in a three-volume set.",
					"David Golinkin is the author of dozen of responsa of the Committee on Jewish Law and Standards of the Rabbinical Assembly."); 
			phraseLinker.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 

			// RTE3 test pair 18 (0 links...) 
			aJCas = tokenizer.generateSingleTHPairCAS(
					"Ryo Okumoto (born in Osaka, Japan) is a keyboardist, best known for his work with progressive rock group Spock's Beard.", 
					"The rock group Spock's Beard comes from Japan."
					); 			
			phraseLinker.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 

			// RTE3 test pair 35
			aJCas = tokenizer.generateSingleTHPairCAS(
					"A Revenue Cutter, the ship was named for Harriet Lane, niece of President James Buchanan, who served as Buchanan's White House hostess.",
					"Harriet Lane was a relative of President James Buchanan."
					); 			
			phraseLinker.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 
			
			// RTE3 test pair 2
			aJCas = tokenizer.generateSingleTHPairCAS(
					"Claude Chabrol (born June 24, 1930) is a French movie director and has become well-known in the 40 years since his first film, Le Beau Serge , for his chilling tales of murder, including Le Boucher.",
					"Le Boucher was made by a French movie director."
					); 			
			phraseLinker.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 
			
			
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}

	}

}
