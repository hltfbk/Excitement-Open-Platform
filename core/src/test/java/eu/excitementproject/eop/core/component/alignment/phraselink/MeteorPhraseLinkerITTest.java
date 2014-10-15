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
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerIT;

public class MeteorPhraseLinkerITTest {

	@Ignore // ignore as default. (basic capability tested by super class --- just for further test.) 
	@Test
	public void test() {
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);   
		Logger testlogger = Logger.getLogger(this.getClass().toString()); 
		
		testlogger.info("This test class may take upto 30 seconds ... "); 
		
		// prepare a JCas 
		JCas aJCas = null; 
		LAPAccess tokenizer = null; 
		try 
		{
			tokenizer = new OpenNLPTaggerIT(); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		// load test 
		AlignmentComponent phraseLinker = null; 
		try {
			phraseLinker = new MeteorPhraseLinkerIT(); 
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		

		try {
			// RTE3 test pair 17 
			aJCas = tokenizer.generateSingleTHPairCAS(
					"David Golinkin è da solo responsabile per la scoperta e la ripubblicazione di dozzine di responsa del Comitato sulla Legge e gli Standard Ebraici dell'Assemblea Rabbinica, rendendole accessibili al pubblico comune in una raccolta di tre volumi.",
					"David Golinkin è l'autore di dozzine di responsa del Comitato sulla Legge e gli Standard Ebraici dell'Assemblea Rabbinica."
					); 
			phraseLinker.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 

			// RTE3 test pair 18 
			aJCas = tokenizer.generateSingleTHPairCAS(
					"Ryo Okumoto (nato a  Osaka, Japan) è un tastierista, meglio conosciuto per il suo lavoro con il gruppo progressive rock Spock's Beard.",
					"Il gruppo rock Spock's Beard viene dal Giappone."
					); 			
			phraseLinker.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 

			// RTE3 test pair 35
			aJCas = tokenizer.generateSingleTHPairCAS(
					"Una Revenue Cutter, la nave venne chiamata così per Harriet Lane, nipote del Presidente James Buchanan, che prestò servizio come hostess di Buchanan alla Casa Bianca.",
					"Harriet Lane era una parente del presidente President James Buchanan."
					); 			
			phraseLinker.annotate(aJCas); 
			LinkUtils.dumpTokenLevelLinks(aJCas, System.out); 
			
			// RTE3 test pair 2
			aJCas = tokenizer.generateSingleTHPairCAS(
					"Claude Chabrol (nato il 24 giugno 1930) è un regista francese diventato famoso negli ultimi 40 anni sin dal suo primo film, Le Beau Serge, per le sue agghiaccianti storie di omicidi, tra cui Le Boucher.",
					"Le Boucher è stato girato da un regista francese."
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
