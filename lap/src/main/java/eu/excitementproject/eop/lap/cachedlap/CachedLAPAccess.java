package eu.excitementproject.eop.lap.cachedlap;

import java.io.File;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
//import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import eu.excitement.type.entailment.EntailmentMetadata;
import eu.excitement.type.entailment.Hypothesis;
import eu.excitement.type.entailment.Pair;
import eu.excitement.type.entailment.Text;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;
//import eu.excitementproject.tl.laputils.CASUtils;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
//import java.util.Set;
import org.apache.uima.util.CasCopier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

/**
 *
 * This is a special wrapper class that provides "caching" ability to "any" LAPAccess
 * implementation.
 *
 * The class is designed to provide faster performance when you are expected to
 * call the same Text or Hypothesis string multiple times to generate T-H pairs
 * via LAPAccess. (e.g. Builidng Entailment graph)
 *
 * The class gets one initialized LAPAccess implementation, and behaves as if the
 * wrapper itself is a LAPAccess. It first lookup its hash that any cached results
 * are there: if it is, it uses cache. If it is not, it calls the underlying LAPAccess.
 *
 * Thus, it provides (relatively) transparent cache for LAPAccess.
 *
 * It adds one more method that is fastest: annotateSingleTHPairCAS(), which is not
 * part of LAPAccess interface. This methods gets one CAS, and uses that CAS to annotate,
 * and this reduces the time needed to generate a new CAS.
 *
 * @author Tae-Gil Noh
 *
 */
public class CachedLAPAccess implements LAPAccess {

	public JCas workJCas;

	public CachedLAPAccess(LAPAccess underlyingLAP) throws LAPException
	{
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

		// setting basic
		this.underlyingLAP = underlyingLAP;
		this.textviewCache = new HashMap<String, JCas>();
		this.hypoviewCache = new HashMap<String, JCas>();
		this.receivedCall = 0;
		this.actualCall = 0;
		theLogger = Logger.getLogger("eu.excitementproject.tl.laputils.CachedLAPAccess");

		// make a call and get language ID (loading models also...)
		JCas test = underlyingLAP.generateSingleTHPairCAS("This is a text.", "This is a hypothesis.");
		actualCall++;

		try {
			this.languageId = test.getView(LAP_ImplBase.TEXTVIEW).getDocumentLanguage();
		}
		catch (CASException e)
		{
			throw new LAPException("Unable to get language ID from underlying LAP annotation result!:" + e.getMessage());
		}

		if (this.languageId==null)
		{
			throw new LAPException("Unable to get language ID from underlying LAP annotation result!");
		}

		// initialize our working CAS
		workJCas = createNewJCas(); 

	}

	//
	// Cached method. only one.

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.lap.LAPAccess#generateSingleTHPairCAS(java.lang.String, java.lang.String)
	 */
	@Override
	public JCas generateSingleTHPairCAS(String arg0, String arg1) throws LAPException {
		// generate a new CAS
		//JCas aJCas = CASUtils.createNewInputCas();
		JCas aJCas = null;
		try
		{
			aJCas = this.typeAE.newJCas();
		}
		catch (ResourceInitializationException e)
		{
			throw new LAPException("Unable to create new JCas", e);
		}	
		// actual work is done here, with all caching.
		annotateSingleTHPairCAS(arg0, arg1, aJCas);

		return aJCas;
	}


	// the "MAIN" method. This is not part of LAPAccess interface, but this method
	// is the real implementation that actually enables generatesingleTHPairCAS() to be
	// cached.
	//
	// note that, if the cachedLAP user directly calls this method, the user can save some
	// extra time that requires to generate new CAS. (assuming that the user
	// handles the CAS creation, and reuses it everytime he calls cachedLAP.)
	public void annotateSingleTHPairCAS(String text, String hypothesis, JCas aJCas) throws LAPException
	{
		// reset the CAS (clears all)
		aJCas.reset();

		receivedCall ++; // for checking number of saved calls to underlying LAP

		// if we don't have the cache for text input and/or hypothesis input
		// First we fill the cache with them.

		// if text is not in cache;
		if (!textviewCache.containsKey(text))
		{
			// we make a cached CAS that holds both views
			// that has "text", and put it in the both cache.
			JCas aCacheJCas = createNewJCas(); 
			prepareCacheContent(aCacheJCas, text);
			textviewCache.put(text, aCacheJCas);
			hypoviewCache.put(text, aCacheJCas);
		}

		if (!hypoviewCache.containsKey(hypothesis))
		{
			JCas aCacheJCas = createNewJCas(); 
			prepareCacheContent(aCacheJCas, hypothesis);
			textviewCache.put(hypothesis, aCacheJCas);
			hypoviewCache.put(hypothesis, aCacheJCas);
		}

		// // note that we simply put both text/string on both cache.
		// // assuming that text will be used as hypothesis on some other cases
		// // (e.g. mainly designed for entailment graphs)
		// if ( (!textviewCache.containsKey(text)) || (!hypoviewCache.containsKey(hypothesis)) )
		// {
		// // requires adding of caching element on (at least) one of the side.
		// JCas thJCas = underlyingLAP.generateSingleTHPairCAS(text, hypothesis);
		// actualCall ++; // for checking number of saved calls to underlying LAP
		//
		// if (!textviewCache.containsKey(text))
		// {
		// textviewCache.put(text, thJCas);
		// }
		// if (!hypoviewCache.containsKey(hypothesis))
		// {
		// hypoviewCache.put(hypothesis, thJCas);
		// }
		// }

		// Okay. we are fully sure that each annotated view exist in the cache.
		// make up CAS by using the cache value

		CAS textCas = textviewCache.get(text).getCas();
		CAS hypoCas = hypoviewCache.get(hypothesis).getCas();
		CAS aCas = aJCas.getCas();

		CasCopier textCopier = new CasCopier(textCas, aCas);
		CasCopier hypoCopier = new CasCopier(hypoCas, aCas);

		textCopier.copyCasView(textCas.getView(LAP_ImplBase.TEXTVIEW), true);
		hypoCopier.copyCasView(hypoCas.getView(LAP_ImplBase.HYPOTHESISVIEW), true);

		// Now annotations are copied.
		// The only thing missing is ENTAILMENT annotation. Add it.

		// get Text and Hypothesis from each view.
		Text tAnnot = null;
		Hypothesis hAnnot = null;
		String languageId = null;
		try
		{
			JCas tview = aJCas.getView(LAP_ImplBase.TEXTVIEW);
			JCas hview = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
			// Text annotation
			{
				FSIterator<Annotation> tai= tview.getAnnotationIndex(Text.type).iterator();
				tAnnot = (Text) tai.next();
			}
			// Hypothesis annotation
			{
				FSIterator<Annotation> hai= hview.getAnnotationIndex(Hypothesis.type).iterator();
				hAnnot = (Hypothesis) hai.next();
			}
			languageId = tview.getDocumentLanguage();

		} catch (CASException e)
		{
			throw new LAPException("Unable to get views or Text/Hypothesis annotation from the view-copied CAS. Internal integrity failure. You shouldn't see this exception!" + e.getMessage());
		}
		// annotate Entailment.Pair (on the top CAS)
		Pair p = new Pair(aJCas);
		p.setText(tAnnot); // points T & H
		p.setHypothesis(hAnnot);
		p.setPairID(null);
		p.addToIndexes();

		// annotate EntailmentMetadata

		EntailmentMetadata m = new EntailmentMetadata(aJCas);
		m.setLanguage(languageId.toUpperCase());
		m.setTask(null);
		m.addToIndexes();

		// DONE!
		// now the aJCas has "annotated" views and Entailment Pair annotation.
		theLogger.info("Number of actual call / received call: " + Integer.toString(actualCall) + " / " + Integer.toString(receivedCall) );

	}
	//
	// Un-cached methods - directly calls underlying LAP
	@Override
	public String getComponentName() {
		return underlyingLAP.getComponentName();
	}

	@Override
	public String getInstanceName() {
		return underlyingLAP.getInstanceName();
	}

	@Override
	public void addAnnotationOn(JCas arg0) throws LAPException {
		underlyingLAP.addAnnotationOn(arg0);
	}

	@Override
	public void processRawInputFormat(File arg0, File arg1) throws LAPException {
		underlyingLAP.processRawInputFormat(arg0, arg1);
	}

	@Override
	public void addAnnotationOn(JCas arg0, String arg1) throws LAPException {
		underlyingLAP.addAnnotationOn(arg0, arg1);
	}

	//
	// private methods
	public JCas createNewJCas() throws LAPException 
	{
		// get a new JCAS
		JCas aJCas = null; 
		try {
			aJCas = typeAE.newJCas(); 
		}
		catch (ResourceInitializationException e)
		{
			throw new LAPException("Unable to create new JCas", e); 
		}

		return aJCas; 
	}


	private void prepareCacheContent(JCas aJCas, String input) throws LAPException {

		// generate views and set SOFA
		JCas textView = null; JCas hypoView = null;
		try {
			textView = aJCas.createView(LAP_ImplBase.TEXTVIEW);
			hypoView = aJCas.createView(LAP_ImplBase.HYPOTHESISVIEW);
		}
		catch (CASException e)
		{
			throw new LAPException("Unble to create new views", e);
		}
		textView.setDocumentLanguage(this.languageId);
		hypoView.setDocumentLanguage(this.languageId);
		textView.setDocumentText(input);
		hypoView.setDocumentText(input);

		// annotate Text (on TextView)
		Text t = new Text(textView);
		t.setBegin(0); t.setEnd(input.length());
		t.addToIndexes();

		// annotate Hypothesis (on HypothesisView)
		Hypothesis h = new Hypothesis(hypoView);
		h.setBegin(0); h.setEnd(input.length());
		h.addToIndexes();

		// okay. now call underlyingLAP to annotate
		underlyingLAP.addAnnotationOn(aJCas, LAP_ImplBase.TEXTVIEW);
		underlyingLAP.addAnnotationOn(aJCas, LAP_ImplBase.HYPOTHESISVIEW);

		// done. aJCas is updated to be used for cache.
		actualCall ++;
	}

	//
	// private data
	private LAPAccess underlyingLAP;
	private Map<String, JCas> textviewCache;
	private Map<String, JCas> hypoviewCache;
	private AnalysisEngine typeAE;

	private int receivedCall;
	private int actualCall;
	private String languageId;
	Logger theLogger;
}