package eu.excitementproject.eop.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.junit.Assume;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.ADJ;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.NN;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.V;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
//import eu.excitementproject.eop.common.component.lexicalknowledge.TERuleRelation;
import eu.excitementproject.eop.common.exception.BaseException;
import eu.excitementproject.eop.common.representation.partofspeech.GermanPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetWrapper;


public class TreeTaggerGermaNetIntegrationTest {

	@Test
	public void test() throws UnsupportedPosTagStringException {
		GermaNetWrapper gnw=null;
		try {
			// TODO: in the future, this test code also should read from the common config. 
			gnw = new GermaNetWrapper("/path/to/GermaNet/version8.0/germanet-8.0/GN_V80_XML/");
		}
		catch (GermaNetNotInstalledException e) {
			System.out.println("WARNING: GermaNet files are not found in the given path. Please correctly install and pass the path to GermaNetWrapper");
		}
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}
		Assume.assumeNotNull(gnw); // if gnw is null, the following tests will not be run. 
		
		
		try {
			// First, get a CAS. 
			InputStream s = this.getClass().getResourceAsStream("/desc/DummyAE.xml"); // This AE does nothing, but holding all types.
			XMLInputSource in = new XMLInputSource(s, null);
			
			// This AE does nothing, but holding all types. 
			ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);
			AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier); 			
			JCas jcas1;
			jcas1 = ae.newJCas();
			
		    //Let's load an XMI file and check it in a new CAS
			File xmiFile = new File("./src/test/resources/TestTreeTaggerGermaNetIntegration.xmi");
		    FileInputStream inputStream = new FileInputStream(xmiFile);
		    XmiCasDeserializer.deserialize(inputStream, jcas1.getCas());
		    inputStream.close();
		    
		    // Create a 2-items list for both views
		    ArrayList<JCas> jCasViews = new ArrayList<JCas>();	    
		    jCasViews.add(jcas1.getView("TextView"));
			jCasViews.add(jcas1.getView("HypothesisView"));
			
		    // Iterate over the list of views (T and H) and get the 
			// needed information.
		    for (JCas jCasView : jCasViews) {
		    		
		    	// Iterate over all tokens in the current view:
		    	AnnotationIndex<Annotation> tokenIndex = jCasView.getAnnotationIndex(Token.type); 
				Iterator<Annotation> tokenIter = tokenIndex.iterator();
				while(tokenIter.hasNext())
			    {
			    	Token curr = (Token) tokenIter.next(); 
			    	String tokenText = curr.getCoveredText();  
			    	//String tokenLemma = curr.getLemma().getValue();
			    	String tokenPos = curr.getPos().getPosValue();

			    	// call GermaNet only if the current POS is either NN or ADJ 
			    	// or V (which are the three POS classes for which we are 
			    	// supposed to find information in GermaNet).
			    	if (curr.getPos().getTypeIndexID() == NN.type 
			    			|| curr.getPos().getTypeIndexID() == ADJ.type
			    			|| curr.getPos().getTypeIndexID() == V.type) 
			    	{
			    		//System.out.printf("Lemma of token '%s' (%s): %s\n", tokenText, new GermanPartOfSpeech(tokenPos), tokenLemma);
			    		
			    		// GN call for all left relations of the token/POS pair
						for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForLeft(tokenText, new GermanPartOfSpeech(tokenPos))) {
							assertTrue(rule.getConfidence() > 0);
							assertTrue(rule.getRLemma() != ""); // if a rule has been created, there must be a rLemma!
							assertTrue(rule.getRPos() != null); // if a rule has been created, there must be a rPos!							
							//System.out.println(rule.toString());
						}

			    		/*
			    		// GN call for left ENTAILMENT relations of the token/POS pair
						for (LexicalRule<? extends GermaNetInfo> rule : gnw.getRulesForLeft(tokenText, new GermanPartOfSpeech(tokenPos), TERuleRelation.Entailment)) {
							assertTrue(rule.getConfidence() > 0);
							assertTrue(rule.getLLemma() != ""); // if a rule has been created, there must be a rLemma!
							assertTrue(rule.getLPos() != null); // if a rule has been created, there must be a rPos!							
							//System.out.println(rule.toString());
						}
						*/
						
						//System.out.println();
			    	}
			    	
			    }
		    }
	    
		} catch (CASException e1) {
			e1.printStackTrace();
		} catch (ResourceInitializationException e2) {
			e2.printStackTrace();
		} catch (IOException e3) {
			e3.printStackTrace();
		} catch (InvalidXMLException e4) {
			e4.printStackTrace();
		} catch (SAXException e5) {
			e5.printStackTrace();
		} catch (LexicalResourceException e6) {
			e6.printStackTrace();
		}
	    
	}

}
