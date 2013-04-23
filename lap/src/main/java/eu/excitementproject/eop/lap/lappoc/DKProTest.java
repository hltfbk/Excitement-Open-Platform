package eu.excitementproject.eop.lap.lappoc;

import java.io.InputStream;

import org.apache.uima.UIMAFramework;
//import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceSpecifier;
//import org.apache.uima.resource.metadata.TypeSystemDescription;
//import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.XMLInputSource;

//import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader; 
//import static org.uimafit.factory.CollectionReaderFactory.*; 

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import static org.uimafit.factory.AnalysisEngineFactory.*;
//import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import org.uimafit.component.xwriter.CASDumpWriter;
import org.uimafit.factory.AggregateBuilder;
import de.tudarmstadt.ukp.dkpro.core.treetagger.*; 
import de.tudarmstadt.ukp.dkpro.core.mstparser.MSTParser; 
//import static org.uimafit.pipeline.SimplePipeline.*;
//import static org.uimafit.factory.TypeSystemDescriptionFactory.*; 
//import static org.uimafit.factory.JCasFactory.*; 
//import static org.uimafit.factory.AnalysisEngineFactory.*; 


public class DKProTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Hello, sir.");
		
//		CollectionReader cr = createCollectionReader(
//				TextReader.class, 
//				TextReader.PARAM_PATH, "src/test/resources",
//				TextReader.PARAM_LANGUAGE, "en", 
//				TextReader.PARAM_PATTERNS, new String[] {"[+]*.txt"});
		
		AnalysisEngineDescription seg = createPrimitiveDescription(BreakIteratorSegmenter.class);
		//AnalysisEngineDescription tagger = createPrimitiveDescription(OpenNlpPosTagger.class);
		AnalysisEngineDescription lemma = createPrimitiveDescription(TreeTaggerPosLemmaTT4J.class); 
		AnalysisEngineDescription parse = createPrimitiveDescription(MSTParser.class); 
		AnalysisEngineDescription cc = createPrimitiveDescription(
			         CASDumpWriter.class,
			         CASDumpWriter.PARAM_OUTPUT_FILE, "target/output.txt");
		
		//runPipeline(cr, seg, tagger,cc);
		//runPipeline(cr, seg, cc); 
        		
		// Type System AE 
		InputStream s = DKProTest.class.getClass().getResourceAsStream("/desc/DummyAE.xml"); // This AE does nothing, but holding all types.
		//XMLInputSource in = new XMLInputSource("./src/main/resources/desc/DummyAE.xml"); // This AE does nothing, but holding all types.
		XMLInputSource in = new XMLInputSource(s, null); 
		ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);		
		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier); 
		JCas aJCas = ae.newJCas(); 

		//TypeSystemDescription typeSystemDescription = createTypeSystemDescription(); 
		//JCas aJCas = createJCas(typeSystemDescription);
		
		JCas tView = aJCas.createView("TextView"); 
		tView.setDocumentText("Bei der Lufthansa hat das Bodenpersonal mit einem Warnstreik den Flugverkehr nahezu lahmgelegt."); 
		tView.setDocumentLanguage("DE"); 
//		tView.setDocumentText("When I was young, I wanted to become a sailor."); 		
//		tView.setDocumentLanguage("EN"); 
//		aJCas.setDocumentText("When I was young, I wanted to become a sailor."); 
//		aJCas.setDocumentLanguage("EN"); 

		// Using AggregateBuilder to assign views 
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(seg, "_InitialView", "TextView");
		//builder.add(tagger, "_InitialView", "TextView"); 
		builder.add(lemma, "_InitialView", "TextView");
		builder.add(parse, "_InitialView", "TextView");  
		builder.add(cc); 
		
		AnalysisEngine textViewAE = builder.createAggregate(); 
		textViewAE.process(aJCas); 

		// DONE build "large" German MST parser model 
		// TODO (on 8G sys) pass the argument to MST parser, and make sure it works okay. 
		// TODO upload it to FBK repository 
		
		// Test for "large" model (1G!) 
		// The following will need 4G XmX Xms ... or something like that. 
		AnalysisEngineDescription parse2 = createPrimitiveDescription(MSTParser.class,
				MSTParser.PARAM_VARIANT, "long"); 
	
		AggregateBuilder b = new AggregateBuilder(); 
		b.add(seg); 
		b.add(lemma); 
		b.add(parse2); 
		b.add(cc); 

		AnalysisEngine bAE = b.createAggregate(); 
		JCas aJCas2 = ae.newJCas(); 
		aJCas2.setDocumentLanguage("DE");
		aJCas2.setDocumentText("Ich habe Hunger!"); 
		bAE.process(aJCas2); 

		//TypeSystemDescription typeSystemDescription = createTypeSystemDescription(); 
		//JCas aJCas = createJCas(typeSystemDescription);
		
//		JCas tView2 = aJCas2.createView("TextView"); 
//		tView2.setDocumentText("Ich habe Hunger!"); 
//		tView2.setDocumentLanguage("DE"); 
//			
//		textViewAE.process(aJCas2); 
		
		
		}

}
