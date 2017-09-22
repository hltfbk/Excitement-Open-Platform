package eu.excitementproject.eop.transformations.biu.en.predicatetruth;

import java.io.File;

import org.apache.uima.fit.descriptor.ConfigurationParameter;

import eu.excitementproject.eop.common.datastructures.Envelope;
import eu.excitementproject.eop.transformations.uima.ae.truthteller.PredicateTruthAE;

/**
 * Inherits truth annotations, and makes specific calls for Truth Teller's wrapper
 * @author Gabi Stanovsky
 * @since Aug 2014
 */

public class TruthTellerAnnotatorAE extends PredicateTruthAE<TruthTellerAnnotator> {

	
	// get the configuration parameter 
	public static final String PARAM_CONFIG = "annotationRulesFile";
	@ConfigurationParameter(name = PARAM_CONFIG, mandatory = true)
	private File annotationRulesFile;
	
	
	@Override
	protected TruthTellerAnnotator buildInnerTool() throws Exception {
		TruthTellerAnnotator ret = new TruthTellerAnnotator(annotationRulesFile);
		ret.init();
		return ret;
	}

	@Override
	protected final Envelope<TruthTellerAnnotator> getEnvelope(){return envelope;}
	
	
	private static Envelope<TruthTellerAnnotator> envelope = new Envelope<TruthTellerAnnotator>();

}
