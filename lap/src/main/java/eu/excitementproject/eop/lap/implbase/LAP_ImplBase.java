package eu.excitementproject.eop.lap.implbase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;

import eu.excitement.type.entailment.EntailmentMetadata;
import eu.excitement.type.entailment.Hypothesis;
import eu.excitement.type.entailment.Pair;
import eu.excitement.type.entailment.Text;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;

/**
 * <P>
 * An implementation base for LAP components that follows the interface LAPAccess. 
 * This implementation base don't annotate linguistic annotations (abstract method 
 * addAnnotationOn() must be provided by class that extends this base). 
 * 
 * <P> 
 * But the base annotates all data that is defined for Entailment problem description. 
 * If you provide the linguistic annotation part of this LAP (single method, 
 * addAnnotationOn()) you get a LAPAccess implementation for your language, that knows 
 * how to read input format (RTE5+) and how to generate CASes that can be consumed by EDAs. 
 * 
 * <P> 
 * This implementation intentionally uses only the "addAnnotationOn(Jcas, String)" 
 * as the main annotation method. This may be a bit inefficient, but it makes this 
 * implementation as a "generic" base. --- if you provide "addAnnotationOn()" for 
 * your own annotator, you automatically get other methods like 
 * "generateSingleTHPair()" and "processRawInputFormat()". 
 * To see examples; see ExampleLAP, or OpenNLPTaggerEN, or other classes that extends 
 * this abstract class. 
 *  
 * <P>
 * Note that generating a new CAS is an expansive operation. try to reuse 
 * existing one by cleaning it up with reset(). -- this code does so in 
 * processRawInputFormat(). 
 * 
 * <P> 
 * (If you are using existing AEs) --- Also note that when you 
 * use existing UIMA AnalysisEngines (like that of DKPro), this addAnnotationTo() is 
 * really not efficient to be used in two other methods (generateSingleTH and processRawInput). 
 * , since it initialize the AE each time addAnnotationTo is being called. 
 * In such a case, use {@link LAP_ImplBaseAE}, which only initialize AEs per view once. 
 * 
 * @author Gil 
 *
 */

public abstract class LAP_ImplBase implements LAPAccess {

	public LAP_ImplBase() throws LAPException {
		// setting up the AE for type system 
		// note that you need at least one AE to get a JCAS. (valina UIMA) 
		try {
			InputStream s = this.getClass().getResourceAsStream("/desc/DummyAE.xml"); // This AE does nothing, but holding all types.
			XMLInputSource in = new XMLInputSource(s, null); 
			ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);		
			AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier); 
			this.typeAE = ae; 
		} 
		catch (InvalidXMLException e)
		{
			throw new LAPException("AE descriptor is not a valid XML", e);			
		}
		catch (ResourceInitializationException e)
		{
			throw new LAPException("Unable to initialize the AE", e); 
		}		

		// Set logger 
		//BasicConfigurator.resetConfiguration(); 
		//BasicConfigurator.configure(); 
		//Logger.getRootLogger().setLevel(Level.INFO);  // for UIMA (hiding < INFO) 
		logger = Logger.getLogger("eu.excitementproject.eop.lap.implbase.LAP_ImplBase"); 
		//logger.setLevel(Level.INFO); 

	}

	@Override
	public void processRawInputFormat(File inputFile, File outputDir)
			throws LAPException {
		JCas aJCas = null; 
		try {
			aJCas = typeAE.newJCas(); 
		} catch(ResourceInitializationException e)
		{
			throw new LAPException("Failed to create a JCAS", e); 
		}
		
		// prepare the reader  
		RawDataFormatReader input = null;
		
		try {
			input = new RawDataFormatReader(inputFile); 
		}
		catch (RawFormatReaderException e)
		{
			throw new LAPException("Failed to read XML input format", e); 
		}
			
		@SuppressWarnings("unused")
		String lang = input.getLanguage(); 
		@SuppressWarnings("unused")
		String channel = input.getChannel(); 
		
		// for each Pair data 
		while(input.hasNextPair())
		{
			RawDataFormatReader.PairXMLData pair; 
			try {
				 pair = input.nextPair(); 
			}
			catch (RawFormatReaderException e)
			{
				throw new LAPException("Failed to read XML input format", e); 
			}		
			
			// Add TE structure (Views) and types (Entailment.Pair, .Text, .Hypothesis, and .EntailmentMetadata) 
			addTEViewAndAnnotations(aJCas, pair.getText(), pair.getHypothesis(), pair.getId(), pair.getTask(), pair.getGoldAnswer()); 

			// call to addAnnotationOn() each view 
			addAnnotationOn(aJCas, TEXTVIEW);
			addAnnotationOn(aJCas, HYPOTHESISVIEW);
			
			// serialize 
			String xmiName = pair.getId() + ".xmi"; 
			File xmiOutFile = new File(outputDir, xmiName); 
			
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

			logger.info("Pair " + pair.getId() + "\twritten as " + xmiOutFile.toString() ); 
			// prepare next round
			aJCas.reset(); 
		}
	}

	@Override
	public JCas generateSingleTHPairCAS(String text, String hypothesis)
			throws LAPException {
		// get a new JCAS
		JCas aJCas = null; 
		try {
			aJCas = typeAE.newJCas(); 
		}
		catch (ResourceInitializationException e)
		{
			throw new LAPException("Unable to create new JCas", e); 
		}
		
		// prepare it with Views and Entailment.* annotations. 
		addTEViewAndAnnotations(aJCas, text, hypothesis, null, null, null); // last three args are PairId, task, and golden Answer --  which we don't fill here 
		
		// now aJCas has TextView, HypothesisView and Entailment.* types. (Pair, T and H) 
		// it is time to add linguistic annotations 
		addAnnotationOn(aJCas, TEXTVIEW);
		addAnnotationOn(aJCas, HYPOTHESISVIEW); 
		
		return aJCas; 
	}
	
	public JCas generateSingleTHPairCAS(String text, String hypothesis, String goldAnswer)
			throws LAPException {
		// get a new JCAS
		JCas aJCas = null; 
		try {
			aJCas = typeAE.newJCas(); 
		}
		catch (ResourceInitializationException e)
		{
			throw new LAPException("Unable to create new JCas", e); 
		}
		
		// prepare it with Views and Entailment.* annotations. 
		addTEViewAndAnnotations(aJCas, text, hypothesis, null, null, goldAnswer); // last three args are PairId, task, and golden Answer 
		
		// now aJCas has TextView, HypothesisView and Entailment.* types. (Pair, T and H) 
		// it is time to add linguistic annotations 
		addAnnotationOn(aJCas, TEXTVIEW);
		addAnnotationOn(aJCas, HYPOTHESISVIEW); 
		
		return aJCas; 
	}

	@Override
	public abstract void addAnnotationOn(JCas aJCas, String viewName) throws LAPException; 
	
//	@Override
//	public JCas addAnnotationOn(JCas aJCas, String viewName)
//			throws LAPException {
//
//		// prepare UIMA context (For "View" mapping), for the AE.  
//		UimaContextAdmin rootContext = UIMAFramework.newUimaContext(UIMAFramework.getLogger(), UIMAFramework.newDefaultResourceManager(), UIMAFramework.newConfigurationManager());
//		ResourceSpecifier desc = null; 
//		try {
//			InputStream s = this.getClass().getResourceAsStream(descClasspathName); 
//			//XMLInputSource input = new XMLInputSource(this.descPath);
//			XMLInputSource input = new XMLInputSource(s, null); 
//			desc = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(input);
//		}
////		catch (IOException e) {
////			throw new LAPException("Unable to open AE descriptor file", e); 
////		} 
//		catch (InvalidXMLException e) {
//			throw new LAPException("AE descriptor is not a valid XML", e);			
//		}
//		
//		//setup sofa name mappings using the api
//		HashMap<String,String> sofamappings = new HashMap<String,String>();
//		sofamappings.put("_InitialView", viewName);
//		UimaContextAdmin childContext = rootContext.createChild("WSSeparator", sofamappings);
//		Map<String,Object> additionalParams = new HashMap<String,Object>();
//		additionalParams.put(Resource.PARAM_UIMA_CONTEXT, childContext);
//
//		// time to run 
//		try {
//			//instantiate AE, passing the UIMA Context through the additional parameters map
//			AnalysisEngine ae =  UIMAFramework.produceAnalysisEngine(desc,additionalParams);
//			// and run the AE 
//			ae.process(aJCas); 
//		}
//		catch (ResourceInitializationException e) {
//			throw new LAPException("Unable to initialize the AE", e); 
//		} 
//		catch (AnalysisEngineProcessException e) {
//			throw new LAPException("AE reported back an Exception", e); 
//		}
//		return aJCas;
//	}

	@Override
	public void addAnnotationOn(JCas aJCas) throws LAPException {
		addAnnotationOn(aJCas, INITIALVIEW); 
	}
	
	/**
	 * 
	 * This method adds two views (TextView, HypothesisView) and set their 
	 * SOFA string (text on TextView, h on HView). 
	 * And it also annotates them with Entailment.Metadata, Entailment.Pair, 
	 * Entailment.Text and Entailment.Hypothesis. (But it adds no linguistic annotations. )  
	 * 
	 * <P>
	 * This method annotates Metadata, but only its "Language" and "task" 
	 * Other features like channel, source, task, collection and document ID, etc. 
	 * are *not* set by this method. 
	 *  
	 * They should be set by the caller, if needed. 
	 * @param aJCas 
	 * @param text
	 * @param hypothesis
	 * @return
	 */
	private void addTEViewAndAnnotations(JCas aJCas, String text, String hypothesis, String pairId, String task, String goldAnswer) throws LAPException {
		
		// generate views and set SOFA 
		JCas textView = null; JCas hypoView = null; 
		try {
			textView = aJCas.createView(TEXTVIEW);
			hypoView = aJCas.createView(HYPOTHESISVIEW); 
		}
		catch (CASException e) 
		{
			throw new LAPException("Unble to create new views", e); 
		}
		textView.setDocumentLanguage(this.languageIdentifier); 
		hypoView.setDocumentLanguage(this.languageIdentifier);
		textView.setDocumentText(text);
		hypoView.setDocumentText(hypothesis);
		
		// annotate Text (on TextView) 
		Text t = new Text(textView);
		t.setBegin(0); t.setEnd(text.length()); 
		t.addToIndexes(); 
		
		// annotate Hypothesis (on HypothesisView) 
		Hypothesis h = new Hypothesis(hypoView);
		h.setBegin(0); h.setEnd(hypothesis.length()); 
		h.addToIndexes(); 
		
		// annotate Pair (on the top CAS) 
		Pair p = new Pair(aJCas); 
		p.setText(t); // points T & H 
		p.setHypothesis(h); 
		p.setPairID(pairId); 
		
		if (goldAnswer != null && goldAnswer.length() > 0)
		{
			try {			
				p.setGoldAnswer(goldAnswer.toUpperCase()); 
			}
			catch (CASRuntimeException e)
			{
				// goldAnswer (Decision type) is string-sub-type 
				// where only some specific strings are permitted. 
				// If not permitted string was given, it raises CASRuntimeException 
				// (if the XML is validated, this try/catch is not needed. ... ) 
				throw new LAPException("Not permitted String value for Pair.goldAnswer: " + goldAnswer.toUpperCase(), e); 			
			}
		}
		p.addToIndexes(); 
		
		// annotate Metadata (on the top CAS) 
		EntailmentMetadata m = new EntailmentMetadata(aJCas); 
		m.setLanguage(this.languageIdentifier); 
		m.setTask(task); 
		// the method don't set channel, origin, etc on the metadata. If needed, the caller should set it. 
		m.addToIndexes(); 
	}
		
	/**
	 * Analysis engine that holds the type system. Note that even if you 
	 * don't call AE, (or not using any AE), you need this. AE provides .newJCas() 
	 */
	private final AnalysisEngine typeAE; 
	
	/**
	 * Language Identifier --- The component uses this to set language of CAS. 
	 * Default is "EN". One has to set this value in their constructor. 
	 */
	protected String languageIdentifier = "EN"; // EN is default value here. One 
	
	/**
	 * log4j logger
	 */
	private Logger logger; 
	
	/**
	 *  string constants 
	 */
	static public final String TEXTVIEW = "TextView";
	static public final String HYPOTHESISVIEW = "HypothesisView";
	static public final String INITIALVIEW = "_InitialView"; 

	@Override
	public String getComponentName() { 
		return this.getClass().getName(); // name of this component that is used to identify the related configuration section ... 
	}

	@Override
	public String getInstanceName() {
		return null; // this component does not support instance configuration 
	} 
}
