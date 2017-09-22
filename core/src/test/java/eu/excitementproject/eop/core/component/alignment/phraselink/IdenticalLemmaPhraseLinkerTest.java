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
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.LinkUtils;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

public class IdenticalLemmaPhraseLinkerTest {

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

			fail(e.getMessage()); 
		}

		testMaxMatchOnPositions(lemmatizer);  
		
		IdenticalLemmaPhraseLinker testInstance = null; 
		try {
			testInstance = new IdenticalLemmaPhraseLinker(); 
			JCas aJCas = lemmatizer.generateSingleTHPairCAS("This is China's new cat, and a new cat is a good thing.", "This is a new cat, and a thing. Gil?"); 
			testInstance.annotate(aJCas); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}

		LAPAccess tokenizer = lemmatizer; 
		JCas aJCas = null; 
		// Some RTE pairs, as test. 
		try {
			// RTE3 test pair 17 (some links) 
			aJCas = tokenizer.generateSingleTHPairCAS(
					"David Golinkin is single-handedly responsible for uncovering and re-publishing dozens of responsa of the Committee on Jewish Law and Standards of the Rabbinical Assembly, making them available to the general public in a three-volume set.",
					"David Golinkin is the author of dozen of responsa of the Committee on Jewish Law and Standards of the Rabbinical Assembly."); 
			testInstance.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 

			// RTE3 test pair 18 (0 links...) 
			aJCas = tokenizer.generateSingleTHPairCAS(
					"Ryo Okumoto (born in Osaka, Japan) is a keyboardist, best known for his work with progressive rock group Spock's Beard.", 
					"The rock group Spock's Beard comes from Japan."
					); 			
			testInstance.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 

			// RTE3 test pair 35
			aJCas = tokenizer.generateSingleTHPairCAS(
					"A Revenue Cutter, the ship was named for Harriet Lane, niece of President James Buchanan, who served as Buchanan's White House hostess.",
					"Harriet Lane was a relative of President James Buchanan."
					); 			
			testInstance.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 
			
			// RTE3 test pair 2
			aJCas = tokenizer.generateSingleTHPairCAS(
					"Claude Chabrol (born June 24, 1930) is a French movie director and has become well-known in the 40 years since his first film, Le Beau Serge , for his chilling tales of murder, including Le Boucher.",
					"Le Boucher was made by a French movie director."
					); 			
			testInstance.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 
			
			
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		// some German ones ... 
		try {
			// RTE3 test pair 17
			aJCas = tokenizer.generateSingleTHPairCAS(
					"David Golinkin ist ganz allein für die Entdeckung und Neuveröffentlichung Dutzender von Erwiderungen des Ausschusses für jüdische Gesetze und Normen der Rabbinerversammlung verantwortlich, so dass sie nun der breiten Öffentlichkeit in einer dreibändigen Reihe zugänglich sind.",
					"David Golinkin ist der Autor Dutzender von Erwiderungen des Ausschusses für jüdische Gesetze und Normen der Rabbinerversammlung."
					); 
			testInstance.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 

			// RTE3 test pair 18 
			aJCas = tokenizer.generateSingleTHPairCAS(
					"Ryo Okumoto (geboren in Osaka, Japan) ist ein Keyboarder, der für seine Arbeit mit der progressiven Rockgruppe Spocks Beard bekannt ist.",
					"Die Rockgruppe Spocks Beard kommt aus Japan."
					); 			
			testInstance.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 

			// RTE3 test pair 35
			aJCas = tokenizer.generateSingleTHPairCAS(
					"Das Schiff, ein Zollkutter, wurde nach Harriet Lane benannt, der Nichte des Präsidenten James Buchanan, die im Weißen Haus als Buchanans Hausherrin diente.", 
					"Harriet Lane war eine Verwandte des Präsidenten James Buchanan."
					); 			
			testInstance.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 
			
			// RTE3 test pair 2
			aJCas = tokenizer.generateSingleTHPairCAS(
					"Claude Chabrol (geboren am 24. Juni 1930) ist ein französischer Regisseur und wurde in den 40er Jahren nach seinem ersten Film, 'Le Beau Serge', berühmt für seine schaurigen Mordgeschichten, wie 'Le Boucher'.",
					"Le Boucher wurde von einem französischen Regisseur geleitet."
					); 			
			testInstance.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 
			
			
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		// Some problematic one ... 
		try {
    		aJCas = tokenizer.generateSingleTHPairCAS("Claude Chabrol divorced Agnes, his first wife, to marry the actress Stéphane Audran. His third wife is Aurore Paquiss.", "Aurore Paquiss married Chabrol."); 
			testInstance.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 

			
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
