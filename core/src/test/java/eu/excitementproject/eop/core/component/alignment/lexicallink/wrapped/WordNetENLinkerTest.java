package eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped;

import static org.junit.Assert.*;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.Test;

import eu.excitement.type.alignment.LinkUtils;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;

public class WordNetENLinkerTest {

	@Test
	public void test() {
		
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);  
		Logger testlogger = Logger.getLogger(this.getClass().toString()); 
		

		// prepare a lemmatizer 
		TreeTaggerEN lemmatizer = null; 
		try 
		{
			lemmatizer = new TreeTaggerEN(); 
			lemmatizer.generateSingleTHPairCAS("this is a test.", "TreeTagger in sight?"); 
		}
		catch (Exception e)
		{
			// check if this is due to missing TreeTagger binary and model. 
			// In such a case, we just skip this test. 
			// (see /lap/src/scripts/treetagger/README.txt to how to install TreeTagger) 
			if (ExceptionUtils.getRootCause(e) instanceof java.io.IOException) 
			{
				testlogger.info("Skipping the test: TreeTagger binary and/or models missing. \n To run this testcase, TreeTagger installation is needed. (see /lap/src/scripts/treetagger/README.txt)");  
				Assume.assumeTrue(false); // we won't test this test case any longer. 
			}
		}


		try {
			// prepare the alinger 
			AlignmentComponent wnLinker = new WordNetENLinker("src/main/resources/ontologies/EnglishWordNet-dict");
			
			String t1 = "The assassin was convicted and sentenced to death penalty";
			String h1 = "The killer has been accused of murder and doomed to capital punishment";
			JCas aJCas = lemmatizer.generateSingleTHPairCAS(t1, h1); 
			
			wnLinker.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 
		
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
	}

	

}
