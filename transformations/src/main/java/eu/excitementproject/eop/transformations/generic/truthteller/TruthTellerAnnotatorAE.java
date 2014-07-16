package eu.excitementproject.eop.transformations.generic.truthteller;

import java.io.File;
import eu.excitementproject.eop.common.datastructures.Envelope;

/*
 * Inherits truth annotations, and makes specific calls for Truth Teller's wrapper
 */

public class TruthTellerAnnotatorAE extends TruthAnnotatorAE<TruthTellerAnnotator> {

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
