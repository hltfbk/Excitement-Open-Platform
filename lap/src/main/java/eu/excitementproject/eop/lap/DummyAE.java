package eu.excitementproject.eop.lap;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

public class DummyAE extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas arg0) throws AnalysisEngineProcessException {
		// This AE does nothing;  
		// it is provided here so user-level codes can make a new JCas (or CAS) 
		// with all EXCITEMENT types, by using this AE. 
		
		// See the descriptor XML (//desc/DummyAE.xml); it imports all 12 type 
		// files currently used by EXCITEMENT within it. 
	}
}
