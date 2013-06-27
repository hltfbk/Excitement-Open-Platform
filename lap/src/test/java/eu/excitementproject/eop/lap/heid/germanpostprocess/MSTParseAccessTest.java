package eu.excitementproject.eop.lap.heid.germanpostprocess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.junit.Assert;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.dkpro.MSTParserDE;
/**
 * test class for MSTParseAccess 
 * checks whether post-processing works well 
 * @author Julia Kreutzer
 */
public class MSTParseAccessTest {

	@Test
	public void test() throws IOException {

		//text for testing
		String testtext = "Er isst den Brei nie auf, weil er ihm nicht schmeckt. Sie sagte: 'Du siehst gut aus'. Heute fällt er den Baum, der stört. Sie fragte ihn an.";
		
		LAPAccess lap = null; 
		try {
		// this will initialize a MSTParser (Sentence breaker, TreeTagger & MSTParser) 		
		lap = new MSTParserDE();  // will load default model (smaller & faster) 
		
		}catch (Exception e)
		{
			System.out.println(e.getMessage()); 
			System.exit(1); 
			
		}
		
		JCas aJCas = null; 
		try {
			aJCas = MSTParseAccess.generateNewJCas(); 
		}
		catch (Exception e)
		{
			System.out.println("Unable to create new CAS:"); 
			System.out.println(e.getMessage()); 
			System.exit(1); 			
		}
		
		aJCas.setDocumentLanguage("DE"); 
		//example test sentences
		aJCas.setDocumentText(testtext);
		
		try {
			lap.addAnnotationOn(aJCas);   
		}
		catch (LAPException e)
		{
			System.out.println("LAP reported error"); 
			System.out.println(e.getMessage()); 
			System.exit(1); 
		}
		
		AnnotationIndex<Annotation> tokenIndex = aJCas.getAnnotationIndex(Token.type);
		Iterator<Annotation> tokenItr = tokenIndex.iterator(); 
		ArrayList<String> ambiguousLemmas = new ArrayList<String>();
		ArrayList<String> separableVerbLemmas = new ArrayList<String>();
		
		//print annotations for test text
		System.out.println("token\tlemma\tPOS\tGovenorToken\tDependencyRelation\n===============================================================");
		while (tokenItr.hasNext())
		{
			Token t = (Token) tokenItr.next(); 
			int begin = t.getBegin(); 
			int end = t.getEnd(); 
			Lemma l = t.getLemma();
			String tokenStr = t.getCoveredText(); 
			String lemmaStr = l.getValue(); 
			String posStr = t.getPos().getPosValue(); 
			
			List<Dependency> dl = JCasUtil.selectCovered(aJCas, Dependency.class , begin, end);  
			Dependency d = dl.get(0); 
			String dTypeStr = d.getDependencyType(); 		
			String govenorTokenStr = d.getGovernor().getCoveredText(); 
			
			System.out.println(tokenStr + "\t" + lemmaStr + "\t" + posStr + "\t" + govenorTokenStr + "\t" + dTypeStr);
			
			
			if (lemmaStr.contains("|")){
				ambiguousLemmas.add(lemmaStr);
			}
			if (posStr.equals("PTKVZ") && d.getGovernor().getPos().getType().toString().contains("V") && dTypeStr.equals("SVP")){
				separableVerbLemmas.add(d.getGovernor().getLemma().getValue()+" + "+lemmaStr);
			}			
		}
		
		//print lemmas that need to be corrected
		System.out.println("\nambiguous lemmas: ");
		for (String alemma : ambiguousLemmas){
			System.out.println(alemma);
		}
		
		System.out.println("\nverb lemmas with do not contain their particles: ");
		for (String plemma : separableVerbLemmas){
			System.out.println(plemma);
		}
					
		//post processing 
		MSTParseAccess.correctAmbiguousLemma(aJCas); 		
		MSTParseAccess.correctSeparableVerbLemma(aJCas);
		
		Iterator<Annotation> tokenItr1 = tokenIndex.iterator(); 
		while (tokenItr1.hasNext())
		{
			Token t = (Token) tokenItr1.next(); 
			//no ambiguous lemmas should be found
			Assert.assertFalse(t.getPos().getPosValue().contains("|"));
			//check for lemma "fallen" if disambiguation has chosen the more probable one
			if (t.getCoveredText().equals("fällt")){ //this token must be found in example sentences above!
				Assert.assertTrue(t.getLemma().getValue().equals("fallen")); //"fallen" is more probable
			}
			if (t.getCoveredText().equals("siehst")){
				Assert.assertFalse(t.getLemma().getValue().equals("sehen")); //"sehen" gets corrected to "aussehen"
				Assert.assertTrue(t.getLemma().getValue().equals("aussehen"));
			}
		}
		
		//create another JCas which does not get corrected - for comparison 
		JCas bJCas = null; 
		try {
			bJCas = MSTParseAccess.generateNewJCas(); 
		}
		catch (Exception e)
		{
			System.out.println("Unable to create new CAS:"); 
			System.out.println(e.getMessage()); 
			System.exit(1); 			
		}
		
		bJCas.setDocumentLanguage("DE"); 
		//example test sentences -> should be the same as for aJCas
		bJCas.setDocumentText(testtext);
		
		try {
			lap.addAnnotationOn(bJCas); 
		}
		catch (LAPException e)
		{
			System.out.println("LAP reported error"); 
			System.out.println(e.getMessage()); 
			System.exit(1); 
		}
		
		//bJCas contains original annotations
		AnnotationIndex<Annotation> btokenIndex = bJCas.getAnnotationIndex(Token.type);
		Iterator<Annotation> btokenItr = btokenIndex.iterator(); 
		Iterator<Annotation> tokenItr5 = tokenIndex.iterator();
		
		System.out.println("\nPost-process corrections:");
		
		//compare corrected annotations with original annotations
		while (btokenItr.hasNext() && tokenItr5.hasNext()){
			Token at = (Token) tokenItr5.next();
			Token bt = (Token) btokenItr.next();
			
			if (!at.toString().equals(bt.toString())){
				if (!at.getLemma().getValue().equals(bt.getLemma().getValue()))
				System.out.println(bt.getLemma().getValue()+" -> "+at.getLemma().getValue());
			}
		}
	}

}
