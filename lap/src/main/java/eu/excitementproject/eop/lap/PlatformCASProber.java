package eu.excitementproject.eop.lap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
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
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLSerializer;
import org.apache.uima.fit.component.CasDumpWriter;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.xml.sax.SAXException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import eu.excitement.type.entailment.EntailmentMetadata;
import eu.excitement.type.entailment.Pair;

/**
 * A utility class that provides a set of static methods for EXCITEMENT CAS (input for EDA and other components) 
 *
 *<P>
 * This class provides a few "probing" methods into CAS, that will check whether or not 
 * the given CAS follows the structure (views and annotations) that EXCITEMENT platform 
 * specification defined for EDA (and other component) input. 
 *   
 * <P>
 * It provides basically the two sets of methods: 
 * <LI> probeCAS(): one that check a given CAS (argument is a JCas) 
 * <LI> probeXmi(): one that will check a file  -- serialized CAS. 
 * 
 * <P>
 * Both method tries to write some info (e.g. number of annotations, having correct views, etc) 
 * back to the given OutputStream. (2nd arg). If something is wrong on the CAS, it will raise Exceptions 
 * According to the type of error. 
 * 
 * Note that if 2nd arg (aOut - outputstream) is null, it won't try to print, but still checks and 
 * raise exceptions. 
 * 
 * @author Gil
 * 
 */

/**
 * @author tailblues
 *
 */
public class PlatformCASProber {

	/**
	 * Check the given JCas (aJCas), raise exceptions if doesn't have proper Entailment View structure 
	 * and type annotations. If aOut is not null, print some info there (e.g. what was the Pair ID, and 
	 * which annotation is annotated on TextView and HypothesisView, etc). 
	 *  
	 * @param aJCas
	 * @param aOut
	 * @throws LAPException
	 */
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
				{
					aOut.printf("PairID: %s\n", p.getPairID()); 
					aOut.printf("GoldAnswer: %s\n", p.getGoldAnswer()); 
				}
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
	
	/**
	 * This method first does probeCAS() (see that method). But it also prints whole 
	 * annotation content (annotation, its begin, end, pointing structures, etc etc -- beware, large) 
	 * also to the aOut, after the brief info of probeCAS(). It prints both TextView and HypothesisView 
	 * 
	 * @param aJCas
	 * @param aOut
	 * @throws LAPException
	 */
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
		
	/**
	 * Reads in an XMI file, deserialize the file, and does 
	 * probeCAS() on it. See probeCAS() for argument meaning and behavior. 
     * 
	 * Note that this static method returns the resulting JCas. So it is possible 
	 * to use this as a XMI reader, (with aOut null), which will check the format, and 
	 * does a sanity check, and returns the CAS to you ... 
	 *  (If you process more than single XMI, this is bad strategy --- in terms of efficiency, 
	 *  this method generates a new JCas every time it is called. It is a costly operation (making  
	 *  a new JCas). and not efficient. ) 
	 *  
	 * @param xmiFile
	 * @param aOut
	 * @return
	 * @throws LAPException
	 */
	public static JCas probeXmi(File xmiFile, PrintStream aOut) throws LAPException
	{
		//
		// 1. deserialize the XMI file 
		JCas aJCas = null; 
		try {			
			// prepare AE that has the type system, and get JCas 	
			InputStream s = PlatformCASProber.class.getResourceAsStream("/desc/DummyAE.xml"); // This AE does nothing, but holding all types. 
			XMLInputSource in = new XMLInputSource(s, null);
			//XMLInputSource in = new XMLInputSource("./src/main/resources/desc/DummyAE.xml");  
			ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);		
			AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier); 

			aJCas = ae.newJCas(); 
		}
//		catch (IOException e) {
//			throw new LAPException("Unable to open the AE descriptor file", e); 
//		}
		catch (InvalidXMLException e) {
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
	
	/**
	 * This method probes an XMI file, and print its content, just as probeCASAndPrintContent does, 
	 * but on the serialized file. It returns the resulting JCas back to you. 
	 * 
	 * @param xmilFile
	 * @param aOut
	 * @return
	 * @throws LAPException
	 */
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
	
	/**
	 * A static utility method: a one liner helper method to store JCAS as XMI. 
	 * 
	 * @param aJCas
	 * @param xmiOutFile
	 * @throws LAPException
	 */
	public static void storeJCasAsXMI(JCas aJCas, File xmiOutFile) throws LAPException 
	{
				
		try {
			FileOutputStream out = new FileOutputStream(xmiOutFile);
			XmiCasSerializer ser = new XmiCasSerializer(aJCas.getTypeSystem());
			XMLSerializer xmlSer = new XMLSerializer(out, false);
			ser.serialize(aJCas.getCas(), xmlSer.getContentHandler());
			out.close();
		} catch (FileNotFoundException e) {
			throw new LAPException("Unable to create/open the file" + xmiOutFile.toString(), e);
		} catch (SAXException e) {
			throw new LAPException("Failed to serialize the CAS into XML", e); 
		} catch (IOException e) {
			throw new LAPException("Unable to access/close the file" + xmiOutFile.toString(), e);
		}
	}

    /**
     * This utility method dumps the content of CAS for human readers.
     * It dumps the content of the given aJCas into a new text file with fileName.
     * If a file exists, it will be overwritten.
     *
     * @param aJCas CAS to be dumped into a file
     * @param fileName the new text file that will holds the content for human readers.
     * @throws LAPException
     */
    public static void dumpJCasToTextFile(JCas aJCas, String fileName) throws LAPException
    {
            try {
                    AnalysisEngineDescription cc = createEngineDescription(CasDumpWriter.class,
							CasDumpWriter.PARAM_OUTPUT_FILE, fileName);
                    AggregateBuilder builder = new AggregateBuilder();
                    builder.add(cc);
                    AnalysisEngine dumper = builder.createAggregate();
                    dumper.process(aJCas);
            }
            catch (ResourceInitializationException e)
            {
                    throw new LAPException("Unable to initialize CASDumpWriter AE");
            }
            catch (AnalysisEngineProcessException e)
            {
                    throw new LAPException("CASDumpWriter returned an Exception.");
            }
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
		// TODO (In future, as needed) add other types like alignment, temporal, semrole, constituent. 
	
		try 
		{
			Sentence sent = new Sentence(aJCas); 
			int sentCount = countAnnotation(aJCas, sent.getType());
			
			Token token = new Token(aJCas); 
			int tokenCount = countAnnotation(aJCas, token.getType()); 
			
			POS pos = new POS(aJCas); 
			int posCount = countAnnotation(aJCas, pos.getType());
			
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
				aOut.println(posCount + " pos Annotation(s)"); 
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
	   * (Borrowed code from UIMAJ example)
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
	      } else if (CAS.TYPE_NAME_BOOLEAN.equals(rangeTypeName)) {
			  aOut.println(aFS.getBooleanValue(feat));
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
