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
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerDE;

public class MeteorPhraseLinkerDETest {

	@Ignore // ignore as default. (basic capability tested by super class --- just for further test.) 
	@Test
	public void test() {
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.DEBUG);  // to hide openNLP logs 
		Logger testlogger = Logger.getLogger(this.getClass().toString()); 

		// prepare a JCas 
		JCas aJCas = null; 
		LAPAccess tokenizer = null; 
		try 
		{
			tokenizer = new OpenNLPTaggerDE(); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		testlogger.info("This test class may take upto 30 seconds ... "); 

		// main class test. 
		// load test 
		AlignmentComponent phraseLinker = null; 
		try {
			phraseLinker = new MeteorPhraseLinkerDE(); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}

		try {
			// RTE3 test pair 17
			aJCas = tokenizer.generateSingleTHPairCAS(
					"David Golinkin ist ganz allein für die Entdeckung und Neuveröffentlichung Dutzender von Erwiderungen des Ausschusses für jüdische Gesetze und Normen der Rabbinerversammlung verantwortlich, so dass sie nun der breiten Öffentlichkeit in einer dreibändigen Reihe zugänglich sind.",
					"David Golinkin ist der Autor Dutzender von Erwiderungen des Ausschusses für jüdische Gesetze und Normen der Rabbinerversammlung."
					); 
			phraseLinker.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 

			// RTE3 test pair 18 
			aJCas = tokenizer.generateSingleTHPairCAS(
					"Ryo Okumoto (geboren in Osaka, Japan) ist ein Keyboarder, der für seine Arbeit mit der progressiven Rockgruppe Spocks Beard bekannt ist.",
					"Die Rockgruppe Spocks Beard kommt aus Japan."
					); 			
			phraseLinker.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 

			// RTE3 test pair 35
			aJCas = tokenizer.generateSingleTHPairCAS(
					"Das Schiff, ein Zollkutter, wurde nach Harriet Lane benannt, der Nichte des Präsidenten James Buchanan, die im Weißen Haus als Buchanans Hausherrin diente.", 
					"Harriet Lane war eine Verwandte des Präsidenten James Buchanan."
					); 			
			phraseLinker.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 
			
			// RTE3 test pair 2
			aJCas = tokenizer.generateSingleTHPairCAS(
					"Claude Chabrol (geboren am 24. Juni 1930) ist ein französischer Regisseur und wurde in den 40er Jahren nach seinem ersten Film, 'Le Beau Serge', berühmt für seine schaurigen Mordgeschichten, wie 'Le Boucher'.",
					"Le Boucher wurde von einem französischen Regisseur geleitet."
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
