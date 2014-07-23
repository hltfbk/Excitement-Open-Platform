package eu.excitementproject.eop.core.component.alignment.phraselink;

import static org.junit.Assert.*;

import java.util.Collection;

import junit.framework.Assert;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.junit.Assume;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

public class IdenticalLemmaPhraseLinkerTest {

	@Test
	public void test() {
		
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.DEBUG);  // to hide openNLP logs 
		Logger testlogger = Logger.getLogger(this.getClass().toString()); 

		// prepare a lemmatizer 
		TreeTaggerEN lemmatizer = null; 
		try 
		{
			lemmatizer = new TreeTaggerEN(); 
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

		testMaxMatchOnPositions(lemmatizer);  
		
		IdenticalLemmaPhraseLinker testInstance = new IdenticalLemmaPhraseLinker(); 
		try {
			JCas aJCas = lemmatizer.generateSingleTHPairCAS("This is China's new cat, and a new cat is a good thing.", "This is a new cat, and a thing. Gil?"); 
			testInstance.annotate(aJCas); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}

	}

	
	public void testMaxMatchOnPositions(TreeTaggerEN lemmatizer)
	{
		// okay. get it and test it. 
		JCas aJCas = null; 
		try {
			aJCas = lemmatizer.generateSingleTHPairCAS("This is China's new cat, and a new cat is a good thing.", "This is a new cat, and a thing."); 
		}
		catch (Exception e)
		{
			fail (e.getMessage()); 
		}
		
		JCas textView = null; 
		JCas hypoView = null; 
		try {
			textView = aJCas.getView(LAP_ImplBase.TEXTVIEW);
			hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
		}
		catch (CASException e)
		{
			fail(e.getMessage()); 
		}

		Collection<Token> t;  
		t = JCasUtil.select(textView, Token.class); 
		Token[] tTokens = t.toArray(new Token[t.size()]); 
		t = JCasUtil.select(hypoView, Token.class); 
		Token[] hTokens = t.toArray(new Token[t.size()]); 

		//  0    1  2    3  4   5  6 7   8 9   10  11 12 13  14   15   0    1  2 3   4  5 6   7 8    9
		//("This is China's new cat, and a new cat is a good thing.", "This is a new cat, and a thing."); 

		try {
			int t1 = IdenticalLemmaPhraseLinker.maxMatchOnPositions(0, 0, tTokens, hTokens);
			Assert.assertEquals(2, t1); 
			int t2 = IdenticalLemmaPhraseLinker.maxMatchOnPositions(4, 3, tTokens, hTokens); 
			Assert.assertEquals(5, t2); 
			int t3 = IdenticalLemmaPhraseLinker.maxMatchOnPositions(14, 8, tTokens, hTokens); 
			Assert.assertEquals(2, t3); 
			int t4 = IdenticalLemmaPhraseLinker.maxMatchOnPositions(8, 2, tTokens, hTokens); 
			Assert.assertEquals(3, t4); 
			int t5 = IdenticalLemmaPhraseLinker.maxMatchOnPositions(10, 6, tTokens, hTokens); 
			Assert.assertEquals(0, t5); 
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
