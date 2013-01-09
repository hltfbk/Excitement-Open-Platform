
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.junit.Assume;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.exception.BaseException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetRelation;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetWrapper;
import eu.excitementproject.eop.core.representation.parsetree.GermanPartOfSpeech;
import eu.excitementproject.eop.core.representation.parsetree.UnsupportedPosTagStringException;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerDE;
import eu.excitementproject.eop.lap.lappoc.ExampleLAP;


/**
 * @author Jan Pawellek 
 *
 */
public class GermaNetWrapperTest {

//	@Test(expected=GermaNetNotInstalledException.class) 
//  [Gil: used Assume.assumeNotNull instead of expected exception.] 
	@Test
	public void test() throws UnsupportedPosTagStringException /* throws java.lang.Exception */ {
		
		GermaNetWrapper gnw=null;
		try {
			// TODO: in the future, this test code also should read from the common config. 
			gnw = new GermaNetWrapper("/mnt/resources/ontologies/germanet-7.0/GN_V70/GN_V70_XML/");
		}
		catch (GermaNetNotInstalledException e) {
			System.out.println("WARNING: GermaNet files are not found in the given path. Please correctly install and pass the path to GermaNetWrapper");
			//throw e;
		}
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}
		Assume.assumeNotNull(gnw); // if gnw is null, the following tests will not be run. 

		//TODO: introduce UIMA structure, check for POS tag, call GN resource for all
		// ADJ, N*, V*
		
		// test UIMA-structured input		
		LAPAccess lap = null;
		JCas jcas = null;  
        try {
        	lap = new TreeTaggerDE();  //TODO: is "TreeTaggerDE" correct?
        	jcas = lap.generateSingleTHPairCAS("Die Obstindustrie hat Einbrüche erlitten.", "Es werden kaum mehr Äpfel gekauft.");
        } catch (LAPException e) {
        	System.err.println(e.getMessage()); 
        }
        
		try {
			AnnotationIndex<Annotation> annoIdx = 
					jcas.getView("TextView").getAnnotationIndex(); // getSofa().getCAS().getAnnotationIndex();
			FSIterator<Annotation> annoIter = annoIdx.iterator();
			
			while (annoIter.hasNext()) {
				// TODO: how can I decide which type of annotation I want (token, lemma...)?
				Annotation currentAnno = annoIter.next();
				
				System.out.println(currentAnno.getType().getName());
				System.out.println(currentAnno.getCoveredText());
			}
			
			jcas.getView("HypothesisView");
			
			//TODO: as soon as I achieve the singular annotation values, check for each 
			// annotation of a Token if the POS is ADJ, N* or V*
			// if yes: call gnw.
			
			
		} catch (CASException e) {
			System.err.println(e.getMessage());
		}
		
		
		
		
		// Test for verbs
		try{
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForLeft("wachsen", new GermanPartOfSpeech("VINF"), GermaNetRelation.has_antonym)) {
				assertTrue(rule.getLLemma().equals("wachsen"));
				assertTrue(rule.getInfo().getLeftSynsetID() == 59751 || rule.getInfo().getLeftSynsetID() == 54357); // might only be true in GermaNet 7.0
				assertTrue(rule.getRLemma().equals("schrumpfen"));
				assertTrue(rule.getInfo().getRightSynsetID() == 59780 || rule.getInfo().getRightSynsetID() == 54511); // might only be true in GermaNet 7.0
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
		
		// Negative test for verbs to show the given POS is used
		try{
			List<LexicalRule<? extends GermaNetInfo>> rule = gnw.getRulesForLeft("wachsen", new GermanPartOfSpeech("NN"), GermaNetRelation.has_antonym);
			assertTrue(rule.isEmpty());
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}		
		
		
		// Test for common nouns
		try{
			for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForLeft("Hitze", new GermanPartOfSpeech("NN"), GermaNetRelation.has_antonym)) {
				assertTrue(rule.getLLemma().equals("Hitze"));
				assertTrue(rule.getRLemma().equals("Kälte"));
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() > 0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		//throw new GermaNetNotInstalledException("GermaNet is installed, but this exception is thrown to fulfill Test's expectations.");
	}
}

