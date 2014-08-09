package eu.excitementproject.eop.transformations.uima.ae.truthteller;

import java.io.File;

import org.uimafit.descriptor.ConfigurationParameter;

import eu.excitementproject.eop.common.datastructures.Envelope;

/**
 * Inherits truth annotations, and makes specific calls for Truth Teller's wrapper
 * @author Gabi Stanovsky
 * @since Aug 2014
 */

public class TruthTellerAnnotatorAE extends PredicateTruthAE<TruthTellerAnnotator> {

	
	// get the configuration parameter 
	public static final String PARAM_CONFIG = "config";
	@ConfigurationParameter(name = PARAM_CONFIG, mandatory = true)
	private String config;
	
	
	@Override
	protected TruthTellerAnnotator buildInnerTool() throws Exception {
		TruthTellerAnnotator ret = new TruthTellerAnnotator(new File(config));
		ret.init();
		return ret;
	}

	@Override
	protected final Envelope<TruthTellerAnnotator> getEnvelope(){return envelope;}
	
	
	private static Envelope<TruthTellerAnnotator> envelope = new Envelope<TruthTellerAnnotator>();

}
