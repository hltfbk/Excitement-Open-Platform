package eu.excitementproject.eop.transformations.generic.truthteller;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.factory.AggregateBuilder;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.uima.BIUFullLAP;

/*
 * A class to extend BIUFullLAP with truth annotations
 * This is implemented within the Transformations package in order to avoid circular dependency between packages
 */


public class BIUFullLAPWithTruthTeller extends BIUFullLAP {
	public BIUFullLAPWithTruthTeller(String taggerModelFile, String nerModelFile,
			String parserHost, Integer parserPort) throws LAPException {
		super(taggerModelFile, nerModelFile,
			parserHost, parserPort);
	}
	
	public BIUFullLAPWithTruthTeller(NameValueTable section) throws LAPException, ConfigurationException {
		super(section);
	}
	
	public BIUFullLAPWithTruthTeller(CommonConfig config) throws LAPException, ConfigurationException {
		super(config);
	}
	
	@Override
	public void addAnnotationOn(JCas aJCas, String viewName) throws LAPException {
		super.addAnnotationOn(aJCas, viewName);
		try {
			//FIXME: where to put this configuration file and how to reference it
			AnalysisEngineDescription truthteller =	createPrimitiveDescription(TruthTellerAnnotatorAE.class,
					TruthTellerAnnotatorAE.PARAM_CONFIG , "C:\\Users\\user\\fromHP\\Shared\\excitement workspace\\eop\\lap\\configuration.xml");
			AggregateBuilder builder = new AggregateBuilder();
			builder.add(truthteller,"_InitialView", viewName);
			AnalysisEngine ae = builder.createAggregate();
			ae.process(aJCas); 
		} catch (ResourceInitializationException e) {
			throw new LAPException("Failed to initilize analysis engine" , e);
		} catch (AnalysisEngineProcessException e) {
			throw new LAPException("An exception while running the Truthteller AE", e);
		} 

	}
	
		
	
		
	
	
}
