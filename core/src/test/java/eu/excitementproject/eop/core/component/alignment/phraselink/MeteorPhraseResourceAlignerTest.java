package eu.excitementproject.eop.core.component.alignment.phraselink;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Ignore;
import org.junit.Test;

import eu.excitement.type.alignment.LinkUtils;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerEN;

@SuppressWarnings("unused")
public class MeteorPhraseResourceAlignerTest {

	@Test
	public void test() {
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.WARN);  // to hide openNLP logs 
		Logger testlogger = Logger.getLogger(this.getClass().toString()); 

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

		Logger.getRootLogger().setLevel(Level.INFO);  // main log setting: set as DEBUG to see what's going & debug. 
		testlogger.info("This test class may take upto 30 seconds ... "); 

		// phrase candidate extract test 		
		try 
		{   List<String> candidates = null; 
			// from TEXTVIEW
			candidates = MeteorPhraseResourceAligner.getPhraseCandidatesFromSOFA(aJCas.getView(OpenNLPTaggerEN.TEXTVIEW), 6); 
			testlogger.debug(candidates.size() + " candidates found. They are; "); 
			// should be 15 candidates 
			assertEquals(candidates.size(), 15); 
			for(String s : candidates)
			{
				testlogger.debug(s); 
			}
			// from HYPOTHESISVIEw 
			candidates = MeteorPhraseResourceAligner.getPhraseCandidatesFromSOFA(aJCas.getView(OpenNLPTaggerEN.HYPOTHESISVIEW), 6); 
			testlogger.debug(candidates.size() + " candidates found. They are; "); 
			// should be 27 candidates 
			assertEquals(candidates.size(), 27); 
			for(String s : candidates)
			{
				testlogger.debug(s); 
			}

			// once more on HYPOTHESISVIEW, but with less uptoN. 
			candidates = MeteorPhraseResourceAligner.getPhraseCandidatesFromSOFA(aJCas.getView(OpenNLPTaggerEN.HYPOTHESISVIEW), 4); 
			testlogger.debug(candidates.size() + " candidates found. They are; "); 
			// should be 22 candidates 
			assertEquals(candidates.size(), 22); 
			for(String s : candidates)
			{
				testlogger.debug(s); 
			}
			
			// empty case. 
			aJCas = tokenizer.generateSingleTHPairCAS("", "This is China's new cat."); 			
			candidates = MeteorPhraseResourceAligner.getPhraseCandidatesFromSOFA(aJCas.getView(OpenNLPTaggerEN.TEXTVIEW), 4); 
			// no candidates, but shouldn't make any exception. 
			assertEquals(candidates.size(), 0); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		// main class test. 
		// load test 
		MeteorPhraseResourceAligner phraseLinker = null; 
		try {
			phraseLinker = new MeteorPhraseResourceAligner("/meteor-1.5/data/paraphrase-en", 7); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		// alignment test with one CAS 
//		 *                          1         2         3         4
//		 *                012345678901234567890123456789012345678901234567890
//		 * TEXTVIEW SOFA  He went there in person to dwell on the importance, and to dwell on the importance. 
//		 * HYPOVIEW SOFA  He went there to explain the significance and significance. 
//       (more than one match, for test) 
		try {
			aJCas = tokenizer.generateSingleTHPairCAS("He went there in person to dwell on the importance, and to dwell on the importance.", "He went there to explain the significance and significance."); 
			phraseLinker.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); // this will dump 24 (token level) links
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		// .. and on another CAS? 
	}

}
