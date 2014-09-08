package eu.excitement.type.alignment;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;
import eu.excitementproject.eop.core.component.alignment.phraselink.IdenticalLemmaPhraseLinker;
import eu.excitementproject.eop.core.component.alignment.phraselink.MeteorPhraseLinkerEN;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerEN;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;

@SuppressWarnings("unused")
public class LinkUtilsTest {

	@Ignore 
	@Test
	public void test() {		
		// Set Log4J for the test 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);  // for UIMA (hiding < INFO) 
		Logger testlogger = Logger.getLogger("eu.excitement.type.alignment.LunkUtilsTest"); 
		
		
		// prepare a lemmatizer 
		TreeTaggerEN lemmatizer = null; 
		try 
		{	
			JCas test = UimaUtils.newJcas(); 
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

			fail(e.getMessage()); 
		}
		
		// prepare aJCas with two different aligners  
		AlignmentComponent idtLinker = null; 
		AlignmentComponent phraseLinker = null; 
		JCas aJCas = null; 
		try {
			idtLinker = new IdenticalLemmaPhraseLinker(); 
			phraseLinker = new MeteorPhraseLinkerEN(); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		try {
			// RTE3 test pair 17 (some links in both Meteor & same lemma linker) 
			aJCas = lemmatizer.generateSingleTHPairCAS(
					"David Golinkin is single-handedly responsible for uncovering and re-publishing dozens of responsa of the Committee on Jewish Law and Standards of the Rabbinical Assembly, making them available to the general public in a three-volume set.",
					"David Golinkin is the author of dozen of responsa of the Committee on Jewish Law and Standards of the Rabbinical Assembly."); 
			phraseLinker.annotate(aJCas); 
			idtLinker.annotate(aJCas);
			//LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}

		// test selectLinksWith 
		try {
			selectLinksWithTest(aJCas); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
	}
	
	private void selectLinksWithTest(JCas aJCas) throws CASException
	{
		List<Link> linksWithMeteor = LinkUtils.selectLinksWith(aJCas, "MeteorPhraseLink");
		assertEquals(4, linksWithMeteor.size()); 
		//		System.out.println(linksWithMeteor.size()); 
		List<Link> linksWithIdentical = LinkUtils.selectLinksWith(aJCas, "IdenticalLemmas"); 
		assertEquals(18, linksWithIdentical.size()); 
		//	System.out.println(linksWithIdentical.size()) ;		
		
	}


}
