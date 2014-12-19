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

public class VerbOceanENLinkTest {

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
			//AlignmentComponent wnLinker = new WordNetENLinker("src/main/resources/ontologies/EnglishWordNet-dict");
			//AlignmentComponent voLinker = new VerbOceanENLinker("src/main/resources/VerbOcean/verbocean.unrefined.2004-05-20.txt"); 
			AlignmentComponent voLinker = new VerbOceanENLinker("../core/src/main/resources/VerbOcean/verbocean.unrefined.2004-05-20.txt"); 
		
			
			String t1 = "Kennedy was killed in Dallas";
			String h1 = "Kennedy was wounded and died in Texas";

			JCas aJCas = lemmatizer.generateSingleTHPairCAS(t1, h1); 
			
			voLinker.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 
		
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
	}

}
