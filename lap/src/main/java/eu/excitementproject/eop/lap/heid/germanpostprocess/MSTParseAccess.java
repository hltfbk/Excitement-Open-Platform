package eu.excitementproject.eop.lap.heid.germanpostprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
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
/**
 * This class provides 
 * a) an example how to access the annotation of a German text parsed by MSTParser
 * and b) two methods which aim to correct problems with lemmas (ambiguity and missing verb particles)
 */

public class MSTParseAccess {

	/**
	 * generates a CAS Object annotated by the MSTParser, and tests the post-preprocessing methods with some German text.
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	
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
		//post processing
		correctAmbiguousLemma(aJCas); 
		correctSeparableVerbLemma(aJCas);
	}

	/**
	 * This method iterates over annotations in the CAS, 
	 * and corrects all separable verb lemmas (wrongly) annotated by TreeTagger, 
	 * by using dependency tree to fetch the prefix of the verb, get its lemma, and add it to the verb lemma.
	 * @param aJCas CAS where lemmas need to be corrected
	 */
	public static void correctSeparableVerbLemma(JCas aJCas)
	{
		AnnotationIndex<Annotation> tokenIndex = aJCas.getAnnotationIndex(Token.type);
		Iterator<Annotation> tokenItr = tokenIndex.iterator(); 
		
		while (tokenItr.hasNext()) {
			Token t = (Token) tokenItr.next(); 
			List<Dependency> dl = JCasUtil.selectCovered(aJCas, Dependency.class , t.getBegin(), t.getEnd());  
			Dependency d = dl.get(0); 
			if (t.getPos().getPosValue().equals("PTKVZ") && d.getGovernor().getPos().getType().toString().contains("V") && d.getDependencyType().equals("SVP"))   {
				d.getGovernor().getLemma().setValue(t.getLemma().getValue().toString()+d.getGovernor().getLemma().getValue().toString()); 
			}
		}
	}
	
	/**
	 * This method iterates over the lemmas, and fixes ambiguous lemma, if any.  
	 * Disambiguation is performed by choosing the most frequent one (looked up in sdewac)
	 * @param aJCas CAS where lemmas need to be corrected
	 * @throws IOException 
	 */
	public static void correctAmbiguousLemma(JCas aJCas) throws IOException
	{
		AnnotationIndex<Annotation> tokenIndex = aJCas.getAnnotationIndex(Token.type);
		Iterator<Annotation> tokenItr = tokenIndex.iterator(); 
		HashMap <String,Integer> lemmaFreq = getLemmaFreq();
		
		while (tokenItr.hasNext()) {
			Token t = (Token) tokenItr.next(); 
			String tokenLemma = t.getLemma().getValue();
			String posType = t.getPos().getType().toString();
			//correct only content word lemmas
			if (tokenLemma.contains("|") && (posType.contains("V") || posType.contains("N") || posType.contains("ADJ"))){
				String[] lemmas = tokenLemma.split(new String("\\|"));
				//get frequencies from sdewac
				int max = 0;
				//if lemmas not found in sdewac, simply chose the first one
				String mostFreqLemma = lemmas[0];
				for (String lemma : lemmas){
					if (lemmaFreq.get(lemma) != null && lemmaFreq.get(lemma)>=max){
						max = lemmaFreq.get(lemma);
						mostFreqLemma = lemma;
					}
				}
				t.getLemma().setValue(mostFreqLemma);	
				}
			}
	}
	
	/**
	 * Creates a hashmap with lemmas and their frequencies from a corpus file
	 * @throws IOException 
	 */
	private static HashMap<String,Integer> getLemmaFreq() throws IOException {
			HashMap<String,Integer> lemmaFreq = new HashMap<String,Integer>();
		    File file = new File("src/main/resources/german_sdewac_frequencies/sdewac-mstparsed-lemmas.txt");
		    FileReader freader = null;
			try {
				freader = new FileReader(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		    BufferedReader reader = new BufferedReader(freader);
		    while(true){
		    	String line = null;
				try {
					line = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
		        if(line != null){
		        	lemmaFreq.put(line.split(new String(" "))[0], Integer.parseInt(line.split(new String(" "))[2]));
			    }
		        else break;
			}
		    reader.close();
		return lemmaFreq;
	}

	/**
	 * Generates a new CAS from dummy AE
	 * @return a newly generated CAS
	 * @throws Exception
	 */
	public static JCas generateNewJCas() throws Exception 
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
