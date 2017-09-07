package eu.excitementproject.eop.alignmentedas.scorers;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.Test;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.LinkUtils;
import eu.excitementproject.eop.alignmentedas.p1eda.scorers.SimpleWordCoverageCounter;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.core.component.alignment.phraselink.IdenticalLemmaPhraseLinker;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;

public class SimpleWordCoverageCounterTest {

	@Test
	public void test() {
		
		// Set Log4J for the test 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.DEBUG);  // for UIMA (hiding < INFO) 
		Logger testlogger = Logger.getLogger(getClass().getName()); 
		
		// prepare a lemmatizer 
		TreeTaggerEN lemmatizer = null; 
		JCas aJCas = null; 
		try 
		{
			lemmatizer = new TreeTaggerEN(); 
			aJCas = lemmatizer.generateSingleTHPairCAS("Lovely TreeTagger test is in sight, or lovely goes not?", "Lovely goes a test."); 
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
			
		// annotate with identity 
		try {
			AlignmentComponent idtLinker = new IdenticalLemmaPhraseLinker(); 
			idtLinker.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 

		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		// get first token of H, and test the method
		// filterLinksWithTargetsIncluding 

		try {
			JCas hView = aJCas.getView("HypothesisView"); 
			Collection<Token> tokens = JCasUtil.select(hView, Token.class); 
			List<Link> links = LinkUtils.selectLinksWith(aJCas, (String) null); 
			Iterator<Token> ti = tokens.iterator(); 
			ti.next(); // first token 
			Token t = ti.next(); // second token
			List<Link> filteredLinks = LinkUtils.filterLinksWithTargetsIncluding(links, t, Link.Direction.TtoH); 
			//System.out.println(filteredLinks.size()); 
			assertEquals(1, filteredLinks.size()); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		// Okay, Let's do some coverage test. 
		ScoringComponent count1 = new SimpleWordCoverageCounter(null); // count all 
		try {
			Vector<Double> v = count1.calculateScores(aJCas); 
			testlogger.info(v.get(0)); 
			testlogger.info(v.get(1)); 
			testlogger.info(v.get(2)); 
			testlogger.info(v.get(3)); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
	}

}
