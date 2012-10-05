package eu.excitementproject.eop.lap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.FloatArrayFS;
import org.apache.uima.cas.IntArrayFS;
import org.apache.uima.cas.StringArrayFS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

import eu.excitement.type.entailment.EntailmentMetadata;
import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.lap.LAPException;


// TODO remove all relative path, which won't work in Jar. (getResource) 
// (it has at least one such path --- typeAeDescPath) 

/**
 * This class provides a few "probing" methods into CAS, that will check whether or not 
 * the given CAS follows CAS structure (views and annotations) that EXCITEMENT platform 
 * specification defined for EDA (and other component) input. 
 *   
 * It provides two static methods: 
 * - one that check a given CAS (argument is a JCas) 
 * - one that will check a file (argument is a File, that holds serialized CAS file (XMI)) 
 * 
 * 
 * @author tailblues
 *
 */
public class PlatformCASProber {

	public static void probeCas(JCas aJCas, PrintStream aOut) throws LAPException
	{
		// Okay, we got a CAS, check it for needed data. 
		// check two views 
		JCas tView = null; 
		JCas hView = null; 
		try {
			tView = aJCas.getView(TVIEW); 
			hView = aJCas.getView(HVIEW); 
		}
		catch(CASException e)
		{
			throw new LAPException("This CAS does not have two proper Views.", e); 
		}
		
		if (aOut != null)
		{
			aOut.println("The CAS has two needed Views: Okay");
		}
		
		// check entailment metadata 
		FSIterator<TOP> iter = aJCas.getJFSIndexRepository().getAllIndexedFS(EntailmentMetadata.type);
		
		if (iter.hasNext())
		{
			EntailmentMetadata m = (EntailmentMetadata) iter.next(); 
			// print metatdata content 
			if (aOut != null)
			{
				aOut.println("The Cas has EntailmentMetadata: Okay"); 
				// its content 
				aOut.printf("Language:%s\nTask:%s\n", m.getLanguage(), m.getTask()); 
				aOut.printf("Origin:%s\nChannel:%s\n", m.getOrigin(), m.getChannel()); 
				aOut.printf("TextDocumentID:%s\nTextCollectionID:%s\n", m.getTextDocumentID(), m.getTextCollectionID()); 
				aOut.printf("HypothesisDocumentID:%s\nHypothesisCollectionID:%s\n", m.getHypothesisDocumentID(), m.getHypothesisCollectionID()); 
				
				if (iter.hasNext())
				{
					aOut.println("Warn: The CAS has more than single EntailmentMetadata. The prober only checks the first one.");
				}
			}			
		}
		else
		{
			throw new LAPException("This CAS does not have EntailmentMetadata."); 
		}
		
		// check entailment pairs, loop for each pair 
		iter = aJCas.getJFSIndexRepository().getAllIndexedFS(Pair.type); 
		
		if (iter.hasNext())
		{
			if (aOut != null)
				aOut.println("The CAS has one (or more) Entailment.Pair"); 
			
			// check & print pair content 
			int i; 
			for(i=0; iter.hasNext(); i++)
			{
				Pair p = (Pair) iter.next(); 
				if (aOut != null)
					aOut.printf("PairID: %s\n", p.getPairID()); 
				String text = null; 
				String hypothesis = null; 
				try {
					text = p.getText().getCoveredText(); 
				}
				catch (Exception e)	{
					throw new LAPException("The CAS has a Pair without a proper Text", e); 
				}
				try {
					hypothesis = p.getHypothesis().getCoveredText(); 
				}
				catch (Exception e) {
					throw new LAPException("The CAS has a Pair without a proper Hypothesis", e); 
				}
				if (aOut != null)
				{
					aOut.printf("TextSOFA:%s\nHypothesisSOFA:%s\n", text, hypothesis); 
				}
			}
			if (aOut != null)
				aOut.println("The CAS has " + i + " Pair(s)"); 
		}
		else
		{
			throw new LAPException("This CAS does not have Entailment.Pair."); 			
		}
		if (aOut != null)
		{	
			// check annotations of T
			aOut.println("Checking Annotations of TextView"); 
			checkAnnotations(tView, aOut); 

			// check annotations of H 		
			aOut.println("Checking Annotations of HypothesisView"); 
			checkAnnotations(hView, aOut); 
		}
	}
	
	public static void probeCasAndPrintContent(JCas aJCas, PrintStream aOut) throws LAPException
	{
		probeCas(aJCas, aOut); 
		JCas tView = null; JCas hView = null; 
		try {
			tView = aJCas.getView(TVIEW); 
			hView = aJCas.getView(HVIEW); 
		}
		catch(CASException e)
		{
			throw new LAPException("Unable to get view", e); 
		}
		if (aOut == null)
			return; 
		
		aOut.println("==CONTENTS of TextView===");
	    printAnnotations(tView.getCas(), aOut); 
		aOut.println("==CONTENTS of HypothesisView===");
		printAnnotations(hView.getCas(), aOut);
	}
		
	public static JCas probeXmi(File xmiFile, PrintStream aOut) throws LAPException
	{
		//
		// 1. deserialize the XMI file 
		JCas aJCas = null; 
		try {
			// prepare AE that has the type system, and get JCas 	
			XMLInputSource in = new XMLInputSource(typeAeDescPath); // This AE does nothing, but holding all types. 
			ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);		
			AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier); 

			aJCas = ae.newJCas(); 
		}
		catch (IOException e) {
			throw new LAPException("Unable to open the AE descriptor file", e); 
		} catch (InvalidXMLException e) {
			throw new LAPException("Invalid XML descriptor for AE", e); 
		} catch (ResourceInitializationException e) {
			throw new LAPException("Failed to produce the AE for typesystem", e); 
		}
		
		try {	
			//Load the XMI to the JCas 
			FileInputStream inputStream = new FileInputStream(xmiFile);
			XmiCasDeserializer.deserialize(inputStream, aJCas.getCas()); 
			inputStream.close();
		} catch (FileNotFoundException e) {
			throw new LAPException("No such XMI file", e); 
		} catch (SAXException e) {
			throw new LAPException("XMI file failed to parse as XML. Corrupted file?", e);
		} catch (IOException e) {
			throw new LAPException("Unable to access the XMI file",e); 
		}
		
		// 2. run probeCas with it 
		probeCas(aJCas, aOut); 
		return aJCas; 
	}
	
	public static JCas probeXmiAndPrintContent(File xmilFile, PrintStream aOut) throws LAPException
	{
		// 1. run probeXmi 
		JCas aJCas = probeXmi(xmilFile, aOut);
		
		// 2. print content by using printAnnotations 
		try {
			aOut.println("==CONTENTS of TextView===");
			printAnnotations(aJCas.getView(TVIEW).getCas(), aOut);
			aOut.println("==CONTENTS of HypothesisView===");
			printAnnotations(aJCas.getView(HVIEW).getCas(), aOut);
		}
		catch(CASException e)
		{
			throw new LAPException("Unable to access the views", e); 
		}				
		return aJCas; 
	}

	//
	//
	
	private static void checkAnnotations(JCas aJCas, PrintStream aOut) throws LAPException 
	{
		// Here, we will check existence of 
		// Sentence, 
		// Token, 
		// Lemma, 
		// NER, 
		// Dependency parse
		// TODO Add other types like alignment, temporal, semrole, constituent? 
	
		try 
		{
			Sentence sent = new Sentence(aJCas); 
			int sentCount = countAnnotation(aJCas, sent.getType());
			
			Token token = new Token(aJCas); 
			int tokenCount = countAnnotation(aJCas, token.getType()); 
			
			Lemma lemma = new Lemma(aJCas);
			int lemmaCount = countAnnotation(aJCas, lemma.getType());
			
			NamedEntity ner = new NamedEntity(aJCas); 
			int nerCount = countAnnotation(aJCas, ner.getType());
			
			Dependency dep = new Dependency(aJCas);
			int depCount = countAnnotation(aJCas, dep.getType());
		
			if (aOut != null) 
			{	
				aOut.println("It has:"); 
				aOut.println(sentCount + " sentence Annotation(s)");
				aOut.println(tokenCount +" token Annotation(s)");
				aOut.println(lemmaCount +" lemma Annotation(s)");
				aOut.println(nerCount +" NER Annotation(s)");
				aOut.println(depCount +" Dependency Annotation(s)");
			}
		}
		catch (Exception e)
		{
			throw new LAPException("Integrity failure -- CAS has a problem. This exception cannot occur by user-side data. Code itself has some problem. (A type that is unknown to the CAS is queired, etc)"); 
		}
		
	}
	
	private static int countAnnotation(JCas aJCas, Type aAnnotType)
	{
		int count = 0; 
		
	    // get iterator over annotations
		FSIterator<Annotation> iter = aJCas.getAnnotationIndex(aAnnotType).iterator(); 

	    // iterate
		while(iter.hasNext())
		{
			count++; 
			iter.next(); 
		}
		
		return count; 
	}
	

	
	
	private static final String typeAeDescPath = "./src/main/resources/desc/DummyAE.xml"; 
	private static final String TVIEW = "TextView";
	private static final String HVIEW = "HypothesisView"; 

	
	//
	// PrintAnnotation and related static methods: you can use them to print 
	// a View.  
	// 
	  /**
	   * (A code that is borrowed from UIMA-library example)
	   * Prints all Annotations to a PrintStream.
	   * 
	   * @param aCAS
	   *          the CAS containing the FeatureStructures to print
	   * @param aOut
	   *          the PrintStream to which output will be written
	   */

	 public static void printAnnotations(CAS aCAS, PrintStream aOut) {
		    // get iterator over annotations
		    FSIterator<AnnotationFS> iter = aCAS.getAnnotationIndex().iterator();

		    // iterate
		    while (iter.isValid()) {
		      FeatureStructure fs = iter.get();
		      printFS(fs, aCAS, 0, aOut);
		      iter.moveToNext();
		    }
		  }
	 
	 
	  /**
	   * (a code that is borrowed from UIMA-library example) 
	   * Prints all Annotations of a specified Type to a PrintStream.
	   * 
	   * @param aCAS
	   *          the CAS containing the FeatureStructures to print
	   * @param aAnnotType
	   *          the Type of Annotation to be printed
	   * @param aOut
	   *          the PrintStream to which output will be written
	   */
	  public static void printAnnotations(CAS aCAS, Type aAnnotType, PrintStream aOut) {
	    // get iterator over annotations
	    FSIterator<AnnotationFS> iter = aCAS.getAnnotationIndex(aAnnotType).iterator();

	    // iterate
	    while (iter.isValid()) {
	      FeatureStructure fs = iter.get();
	      printFS(fs, aCAS, 0, aOut);
	      iter.moveToNext();
	    }
	  }

	  /**
	   * Prints a FeatureStructure to a PrintStream.
	   * 
	   * @param aFS
	   *          the FeatureStructure to print
	   * @param aCAS
	   *          the CAS containing the FeatureStructure
	   * @param aNestingLevel
	   *          number of tabs to print before each line
	   * @param aOut
	   *          the PrintStream to which output will be written
	   */
	  public static void printFS(FeatureStructure aFS, CAS aCAS, int aNestingLevel, PrintStream aOut) {
	    Type stringType = aCAS.getTypeSystem().getType(CAS.TYPE_NAME_STRING);

	    printTabs(aNestingLevel, aOut);
	    aOut.println(aFS.getType().getName());

	    // if it's an annotation, print the first 64 chars of its covered text
	    if (aFS instanceof AnnotationFS) {
	      AnnotationFS annot = (AnnotationFS) aFS;
	      String coveredText = annot.getCoveredText();
	      printTabs(aNestingLevel + 1, aOut);
	      aOut.print("\"");
	      if (coveredText.length() <= 64) {
	        aOut.print(coveredText);
	      } else {
	        aOut.println(coveredText.substring(0, 64) + "...");
	      }
	      aOut.println("\"");
	    }

	    // print all features
	    List<Feature> aFeatures = aFS.getType().getFeatures();
	    Iterator<Feature> iter = aFeatures.iterator();
	    while (iter.hasNext()) {
	      Feature feat = (Feature) iter.next();
	      printTabs(aNestingLevel + 1, aOut);
	      // print feature name
	      aOut.print(feat.getShortName());
	      aOut.print(" = ");
	      // prnt feature value (how we get this depends on feature's range type)
	      String rangeTypeName = feat.getRange().getName();
	      if (aCAS.getTypeSystem().subsumes(stringType, feat.getRange())) // must check for subtypes of
	                                                                      // string
	      {
	        String str = aFS.getStringValue(feat);
	        if (str == null) {
	          aOut.println("null");
	        } else {
	          aOut.print("\"");
	          if (str.length() > 64) {
	            str = str.substring(0, 64) + "...";
	          }
	          aOut.print(str);
	          aOut.println("\"");
	        }
	      } else if (CAS.TYPE_NAME_INTEGER.equals(rangeTypeName)) {
	        aOut.println(aFS.getIntValue(feat));
	      } else if (CAS.TYPE_NAME_FLOAT.equals(rangeTypeName)) {
	        aOut.println(aFS.getFloatValue(feat));
	      } else if (CAS.TYPE_NAME_STRING_ARRAY.equals(rangeTypeName)) {
	        StringArrayFS arrayFS = (StringArrayFS) aFS.getFeatureValue(feat);
	        if (arrayFS == null) {
	          aOut.println("null");
	        } else {
	          String[] vals = arrayFS.toArray();
	          aOut.print("[");
	          for (int i = 0; i < vals.length - 1; i++) {
	            aOut.print(vals[i]);
	            aOut.print(',');
	          }
	          if (vals.length > 0) {
	            aOut.print(vals[vals.length - 1]);
	          }
	          aOut.println("]\"");
	        }
	      } else if (CAS.TYPE_NAME_INTEGER_ARRAY.equals(rangeTypeName)) {
	        IntArrayFS arrayFS = (IntArrayFS) aFS.getFeatureValue(feat);
	        if (arrayFS == null) {
	          aOut.println("null");
	        } else {
	          int[] vals = arrayFS.toArray();
	          aOut.print("[");
	          for (int i = 0; i < vals.length - 1; i++) {
	            aOut.print(vals[i]);
	            aOut.print(',');
	          }
	          if (vals.length > 0) {
	            aOut.print(vals[vals.length - 1]);
	          }
	          aOut.println("]\"");
	        }
	      } else if (CAS.TYPE_NAME_FLOAT_ARRAY.equals(rangeTypeName)) {
	        FloatArrayFS arrayFS = (FloatArrayFS) aFS.getFeatureValue(feat);
	        if (arrayFS == null) {
	          aOut.println("null");
	        } else {
	          float[] vals = arrayFS.toArray();
	          aOut.print("[");
	          for (int i = 0; i < vals.length - 1; i++) {
	            aOut.print(vals[i]);
	            aOut.print(',');
	          }
	          if (vals.length > 0) {
	            aOut.print(vals[vals.length - 1]);
	          }
	          aOut.println("]\"");
	        }
	      } else // non-primitive type
	      {
	        FeatureStructure val = aFS.getFeatureValue(feat);
	        if (val == null) {
	          aOut.println("null");
	        } else {
	          printFS(val, aCAS, aNestingLevel + 1, aOut);
	        }
	      }
	    }
	  }

	  /**
	   * (a code that is borrowed from UIMA-library example) 
	   * Prints tabs to a PrintStream.
	   * 
	   * @param aNumTabs
	   *          number of tabs to print
	   * @param aOut
	   *          the PrintStream to which output will be written
	   */
	  private static void printTabs(int aNumTabs, PrintStream aOut) {
	    for (int i = 0; i < aNumTabs; i++) {
	      aOut.print("\t");
	    }
	  }

}
