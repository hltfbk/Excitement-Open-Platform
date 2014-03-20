package eu.excitementproject.eop.lap.implbase;

import java.util.StringTokenizer;

import org.apache.uima.analysis_component.CasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * An example AE that uses java.util.StringTokenizer to make whitespace breaking. 
 * 
 * @author UIMA-library example + Gil 
 *
 */
public class WSSeparatorAE extends CasAnnotator_ImplBase {

	@Override
	public void process(CAS aCas) throws AnalysisEngineProcessException {

		JCas jcas1 = null;
		try {
			jcas1 = aCas.getJCas(); 
			
		}
		catch (CASException e)
		{
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.INCORRECT_CAS_INTERFACE, null); 			
		}
				
		// get Text and separate them with whitespace. 
		// annotate each with Token annotation 
		String enText = jcas1.getDocumentText(); 
	    StringTokenizer st = new StringTokenizer(enText); 
	    int pos=0; 
	    while(st.hasMoreTokens())
	    {
	    	String thisTok = st.nextToken(); 
	    	int begin = enText.indexOf(thisTok, pos);
	    	int end = begin + thisTok.length(); 
	    	pos = end; 
	    	
	    	{
	    		Token tokenAnnot = new Token(jcas1);  
	    		tokenAnnot.setBegin(begin);  
	    		tokenAnnot.setEnd(end); 
	    		tokenAnnot.addToIndexes(); 
	    		Lemma lemmaAnnot = new Lemma(jcas1); 
	    		lemmaAnnot.setBegin(begin); 
	    		lemmaAnnot.setEnd(end); 
	    		lemmaAnnot.setValue(thisTok.toLowerCase()); // not really lemma. just lc(token). just as an example.  
	    		lemmaAnnot.addToIndexes(); 

	    		tokenAnnot.setLemma(lemmaAnnot); 
	    	}
	    }
	}
}
