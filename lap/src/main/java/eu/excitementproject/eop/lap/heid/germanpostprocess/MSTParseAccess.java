package eu.excitementproject.eop.lap.heid.germanpostprocess;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.dkpro.MSTParserDE;

public class MSTParseAccess {

	/**
	 * Simple example to give a place to start for Julia's Postprocessing of German LAP data. 
	 * 
	 * 1. It shows how MST parser can be called 
	 * 2. It briefly shows how LAP results can be accessed (e.g. lemma, dependency node, etc) 
	 * 3. It outlines success condition of "postprocessing" as two methods. 
	 * 
	 *  You would need to read the following two, after breifly studied this example for more detail:  
	 * 
	 * - Specification UIMA section (section 3, and section 6) 
	 * - cas_access_example, at 
	 *        https://github.com/gilnoh/cas_access_example.git
	 *   (CASAccessExample1 and 2 would be suffice) 
	 * 
	 *  Best, 
	 *  Gil 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
	
		LAPAccess lap = null; 
		try {
		// this will initialize a MSTParser (Sentence breaker, TreeTagger & MSTParser) 		
		lap = new MSTParserDE();  // will load default model (smaller & faster) 
		
		// To use bigger 1 Giga byte model, use the following to initialize 
		// (slower, and needs heap space about 8G) 
		// "long" model is at least 2-3% more accurate. (the long model is the model that 
		// parsed SDEWac in the excitement project dir. 

		/* Init with Long model 
		HashMap<String,String> m = new HashMap<String,String>(); 
		m.put("PARSER_MODEL_VARIANT", "long"); 
		lap = new MSTParserDE(m); 
		*/ 
		
		}catch (Exception e)
		{
			System.out.println(e.getMessage()); 
			System.exit(1); 
			
		}
		
		// Let's try to annotate something. 
		// getting a CAS object
		JCas aJCas = null; 
		try {
			aJCas = generateNewJCas(); 
		}
		catch (Exception e)
		{
			System.out.println("Unable to create new CAS:"); 
			System.out.println(e.getMessage()); 
			System.exit(1); 			
		}
		
		// let's set language ID and text for JCas. 
		aJCas.setDocumentLanguage("DE"); // as of German 
		//aJCas.setDocumentText("Du siehst gut aus."); // Example #1 something with separable prefix (sehen <--> aussehen) 
		//test sentences for task 2
		//aJCas.setDocumentText("Es ist mir gestattet. Die Würfel sind gefallen. Er fällt den Baum. Sie hat Hunger. "); // Example #2, something with "|" in lemma (here gestatten|statten from TreeTagger) 
		//test sentences for task 1
		aJCas.setDocumentText("Er isst den Brei nie auf. Du siehst gut aus. Heute fällt die Schule aus. Er fällt den Baum. Sie fragte ihn an.");
		
		try {
			lap.addAnnotationOn(aJCas); // this takes some time.  
		}
		catch (LAPException e)
		{
			System.out.println("LAP reported error"); 
			System.out.println(e.getMessage()); 
			System.exit(1); 
		}
		
		// Okay. now JCas holds text, sentence annotation, POS annotation, lemma annotation, 
		// Dependency annotation, etc. 
		
		// Let's iterate over the tokens, and each token, let's see what lemmas and 
		// dependencies they have. 
		// (see cas_access_example for more detail about iterating over data) 
		
		AnnotationIndex<Annotation> tokenIndex = aJCas.getAnnotationIndex(Token.type);
		Iterator<Annotation> tokenItr = tokenIndex.iterator(); 

		System.out.println("start-end token/lemma/POS\n\t [DependencyRelation] --> [GovernerToken]"); 
		while (tokenItr.hasNext())
		{
			// we are getting l, 
			Token t = (Token) tokenItr.next(); 
			int begin = t.getBegin(); 
			int end = t.getEnd(); 
			Lemma l = t.getLemma();
			String tokenStr = t.getCoveredText(); 
			String lemmaStr = l.getValue(); 
			String posStr = t.getPos().getPosValue(); 
			
			// lets get Dependency type annotation, that covers this lemma 
			List<Dependency> dl = JCasUtil.selectCovered(aJCas, Dependency.class , begin, end);  
			Dependency d = dl.get(0); 
			String dTypeStr = d.getDependencyType(); 		
			String governerTokenStr = d.getGovernor().getCoveredText(); 
			
			System.out.println(begin + "-" + end + " " + tokenStr + "/" + lemmaStr + "/" + posStr);
			System.out.println("\t "+ dTypeStr + " --> " + governerTokenStr); 
			
		}
		
		// Okay. The printing is done. Check the printout for both examples. 
		// Those are the two problems. 
		
		// So the task is, writing the two following methods. 
		
		// Task 2: (must come before correction of separable verb lemmas)
		// the following method will disambiguate any lemma "something-1|something-2"
		// into the most frequent lemma. e.g "gestatten|statten" -> "gestatten" --- (hopefully gestatten is more frequent ..? )  
		correctAmbiguousLemma(aJCas); 
		
		// Task 1: write the following method that will puts back "full lemma" (e.g. aussehen, instead of sehen) 
		correctSeparableVerbLemma(aJCas);
				
		
	}

	/**
	 * This method will iterate over annotations in the JCas (the default view only), 
	 * and will correct all separable verb lemmas (wrongly) annotated by TreeTagger, 
	 * by using dependency tree to fetch the prefix of the verb. 
	 * @param aJCas
	 */
	public static void correctSeparableVerbLemma(JCas aJCas)
	{
		AnnotationIndex<Annotation> tokenIndex = aJCas.getAnnotationIndex(Token.type);
		Iterator<Annotation> tokenItr = tokenIndex.iterator(); 
		
		while (tokenItr.hasNext()) {
			Token t = (Token) tokenItr.next(); 
			List<Dependency> dl = JCasUtil.selectCovered(aJCas, Dependency.class , t.getBegin(), t.getEnd());  
			Dependency d = dl.get(0); 
			if (t.getPos().getPosValue().equals("PTKVZ") && d.getGovernor().getPos().getPosValue().equals("VVFIN") && d.getDependencyType().equals("SVP"))   {
				d.getGovernor().getLemma().setValue(t.getLemma().getValue().toString()+d.getGovernor().getLemma().getValue().toString()); 
			}
		}
		
		
	}
	
	/**
	 * This method will iterate over the lemmas, and fix ambiguous lemma, if any.  
	 * Disambiguation by choosing the first of all lemmas for each word.
	 * @param aJCas
	 */
	public static void correctAmbiguousLemma(JCas aJCas)
	{
		AnnotationIndex<Annotation> tokenIndex = aJCas.getAnnotationIndex(Token.type);
		Iterator<Annotation> tokenItr = tokenIndex.iterator(); 
		
		while (tokenItr.hasNext()) {
			Token t = (Token) tokenItr.next(); 
			if (t.getLemma().getValue().contains("|")){
				t.getLemma().setValue(t.getLemma().getValue().split(new String("\\|"))[0]);
			}
		}
	}
	
	private static JCas generateNewJCas() throws Exception 
	{
		InputStream s = MSTParseAccess.class.getResourceAsStream("/desc/DummyAE.xml"); // This AE does nothing, but holding all types.
		XMLInputSource in = new XMLInputSource(s, null); 
		ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);		
		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier); 

		// Now we have the AE. we can use it to generate CAS
		JCas jcas = ae.newJCas(); // this is the command to get a JCas. 		
		return jcas;
	}
}
