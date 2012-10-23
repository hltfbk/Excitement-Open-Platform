
package eu.excitementproject.eop.core.component.distance;


//import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
//import org.apache.uima.cas.CASRuntimeException;
//import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
//import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.entailment.EntailmentMetadata;
import eu.excitement.type.entailment.Hypothesis;
import eu.excitement.type.entailment.Pair;
import eu.excitement.type.entailment.Text;

public class CasCreation {

	private String tText = "";
	private String hText = "";
	private String entailment = "";
	
	public CasCreation(String tText, String hText, String entailment) {
		
		this.tText = tText;
		this.hText = hText;
		this.entailment = entailment;
		
	}
	
	public CasCreation() {
		
		this.tText = "The person is hired as a postdoc.";
		this.hText = "The person must have a PhD.";
		this.entailment = "ENTAILMENT";
		
	}
	
	public JCas create() {
	
		//don't do this on your code --- let's ignore
		//exceptions for a moment
		JCas jcas1 = null;
			
		try {
		
			//First, get a CAS.
			XMLInputSource in = new XMLInputSource("/tmp/DummyAE.xml"); // This AE does nothing, but holding all types.
			ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);
			AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier);
			jcas1 = ae.newJCas();
			
			//1) How to create additional Views
			//What is a view? See open platform specification Section 3.
			//and for more info, see UIMA tutorial document section 5 and 6.
			//http://uima.apache.org/d/uimaj-2.4.0/tutorials_and_users_guides.html#ugr.tug.aas
			
			//A view is expressed as a "smaller-CAS". For example, if your CAS has
			//two views, you can treat each of the views just as own CAS.
			//Let's see how it is handled.
			
			JCas viewJCas1 = jcas1.createView("TextView");
			JCas viewJCas2 = jcas1.createView("HypothesisView");
			
			//Note that jcas1 has two views now, one TextView (viewJCas1) and one HypothesisView (viewJCas2)
			//jcas1 (outer CAS container) itself, does not have SOFA text,
			
			//let's set text first.
			viewJCas1.setDocumentText(tText);
			viewJCas1.setDocumentLanguage("EN");
			viewJCas2.setDocumentText(hText);
			viewJCas2.setDocumentLanguage("EN");
			
			//2) Add annotations and see annotations
			//And you can add annotations to views, just as you did in example 1.
			//I've borrowed the WS separation code from example1, in the
			//addAnnotationsToView().
			
			//Go look at addWSTokensToView. it uses "getView()" then
			//add annotations to the view directly.
			addWSTokensToView(jcas1, "TextView");
			addWSTokensToView(jcas1, "HypothesisView");
			
			
			//Let's look at the annotations of each view.
			//PrintAnnotations.printAnnotations(viewJCas1.getCas(), System.out);
			//PrintAnnotations.printAnnotations(viewJCas2.getCas(), System.out);
			
			//3) Let's add some inter-view annotations.
			//
			
			//Note that Entailment.EntailmentMetadata and
			//Entailment.Pair is *not* "Annotation (subtype of uima.tcas.Annotation)
			//Pair and EntailmentMetadata is a subtype of uima.cas.TOP.
			//and they are annotated on "outter most CAS", not on TextView or HypothesisView.
			
			//adding Entailment meta-data
			EntailmentMetadata m = new EntailmentMetadata(jcas1); //textview
			//EntailmentMetadata m = new EntailmentMetadata(viewJCas1); //textview
			m.setChannel("e-mail"); m.setLanguage("EN");
			//m.setBegin(0); m.setEnd(viewJCas1.getDocumentText().length());
			m.setOrigin("Heidelberg Univ.");
			m.addToIndexes();
			
			//mark all of the string on TextView as "entailment.Text"
			Text t = new Text(viewJCas1);
			t.setBegin(0); t.setEnd(viewJCas1.getDocumentText().length());
			t.addToIndexes();
			//mark all of the string on HypothesisView as "entailment.Hypothesis"
			Hypothesis h = new Hypothesis(viewJCas2);
			h.setBegin(0); h.setEnd(viewJCas2.getDocumentText().length());
			h.addToIndexes();
			
			//now add entailment.Pair
			Pair p = new Pair(jcas1); // (also on TextView)
			//Pair p = new Pair(viewJCas1); // (also on TextView)
			p.setText(t); // t is an annotation on TextView
			p.setHypothesis(h); // h is an annotation on HypothesisView
			//p.setBegin(0); p.setEnd(t.getEnd());
			p.setPairID("M777");
			p.setGoldAnswer(entailment);
			p.addToIndexes();
			
			//Let's pass the top CAS to a function, which will try to
			//find out T and H of the give CAS.
			
			//printTHInfo(jcas1); // note that top CAS is given, not view
			//go see printTHInfo() code.
			
			//This jcas1 is now ready with TextView and Hypothesis Views. It has
			//entailment.Pair (with its entailment.Text and entailment.Hypothesis),
			//and it also has EntailmentMetadata. And the text is annotated with
			//(primitive!) white-space separated tokens.
			
			
			
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	
		return (jcas1);
	
	}
	
	public static void addWSTokensToView(JCas aCas, String viewName)
	{
		try {
			JCas theView = aCas.getView(viewName);
			String enText = theView.getDocumentText();
			
			StringTokenizer st = new StringTokenizer(enText);
			
			int pos=0;
			while(st.hasMoreTokens())
			{
				String thisTok = st.nextToken();
				int begin = enText.indexOf(thisTok, pos);
				int end = begin + thisTok.length();
				pos = end;
				
				Token tokenAnnot = new Token(theView); // note that it is from the "View", not top CAS...
				tokenAnnot.setBegin(begin);
				tokenAnnot.setEnd(end);
				tokenAnnot.addToIndexes(); //.. so this attach itself to "View Index", and it can be itereated from the View.
				
				}
			}
		catch (CASException e)
		{
			//if no such view name exist in the CAS, JCAS.getView() raises an exception.
			e.printStackTrace();
		}
	
	}
	
	@SuppressWarnings("unused")
	public static void printTHInfo(JCas aCas)
	{
		try {
			JCas tView = aCas.getView("TextView");
			JCas hView = aCas.getView("HypothesisView"); // again, if the view names are not there, it will raise exceptions
			
			//Now we need to get Entailment.Pair, to find out about
			//the entailment problem.
			
			//If the goal type is an annotation that has begin / end (most of them. Tokens, POSes, NERs ..)
			//1) use getAnnotationsIndex and iterate over them. It returns an ordered iterator.
			
			//If the target type is *not* annotations (i.e. EntailmentMetadata, Pair)
			//2) getJFSIndexRepository().getAllIndexedFS()
			//This can be used to fetch any type instances. (including annotation and non annotation)
			//Unlike getAnnotationsIndex, the returned data has no orders.
			//(Only a few ECXITEMENT types are non-annotation: including Entailment.EntailmentMetadata, and Entailment.Pair.)
			
			//Since we need to get Entailment.Pair, use getAllIndexedFS
			FSIterator<TOP> pairIter = aCas.getJFSIndexRepository().getAllIndexedFS(Pair.type);
			//note that we get it from outside "wrapping" CAS, not from the view CAS.
			
			Pair p=null;
			int i=0;
			System.out.println("====");
			while(pairIter.hasNext())
			{
				p = (Pair) pairIter.next();
				i++;
				System.out.printf("PairID: %s\n", p.getPairID());
				System.out.printf("Text of the pair: %s\n", p.getText().getCoveredText());
				//note that Text annotation is actually on TextView.
				System.out.printf("Hypothesis of the pair: %s\n", p.getHypothesis().getCoveredText());
				//note that Hypothesis annotation is actually on HypothesisView. You can access it from pair.
				System.out.printf("GoldAnswer of the pair: %s\n", p.getGoldAnswer());
			}
			System.out.printf("----\nThe CAS had %d pairs.\n====\n", i);
		
		}
		catch (CASException e)
		{
			e.printStackTrace();
		}
	
	}


}