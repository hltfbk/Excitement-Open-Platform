package eu.excitementproject.eop.lap.biu.uima.ae.postagger;

import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import eu.excitementproject.eop.common.datastructures.Envelope;
import eu.excitementproject.eop.lap.biu.en.postagger.stanford.MaxentPosTagger;

public class MaxentPosTaggerAE extends PosTaggerAE<MaxentPosTagger> {

	/**
	 * Model file of this POS tagger.
	 */
	public static final String PARAM_MODEL_FILE = "model_file";
	@ConfigurationParameter(name = PARAM_MODEL_FILE, mandatory = true)
	private String modelFile;
	
	private static Envelope<MaxentPosTagger> envelope = new Envelope<MaxentPosTagger>();
	
	@Override
	protected final Envelope<MaxentPosTagger> getEnvelope(){return envelope;}
	
	@Override
	protected MaxentPosTagger buildInnerTool() throws Exception {
		MaxentPosTagger tagger = new MaxentPosTagger(modelFile);
		tagger.init();
		return tagger;
	}

	@Override
	protected void configureMapping() {
		mappingProvider.setDefault(MappingProvider.LOCATION, PennPOSMapping.MAPPING_LOCATION);
		mappingProvider.setDefault("tagger.tagset", "default");
	}
	
}
