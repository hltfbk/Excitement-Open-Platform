package eu.excitementproject.eop.lap.biu.uima.ae.ner;

import java.io.File;

import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import eu.excitementproject.eop.common.datastructures.Envelope;
import eu.excitementproject.eop.lap.biu.en.ner.stanford.StanfordNamedEntityRecognizer;

public class StanfordNamedEntityRecognizerAE extends NamedEntityRecognizerAE<StanfordNamedEntityRecognizer> {

	/**
	 * Model file of this NER tagger.
	 */
	public static final String PARAM_MODEL_FILE = "model_file";
	@ConfigurationParameter(name = PARAM_MODEL_FILE, mandatory = true)
	private File modelFile;
	
	private static Envelope<StanfordNamedEntityRecognizer> envelope = new Envelope<StanfordNamedEntityRecognizer>();
	
	@Override
	protected final Envelope<StanfordNamedEntityRecognizer> getEnvelope(){return envelope;}
	
	@Override
	protected StanfordNamedEntityRecognizer buildInnerTool() throws Exception {
		StanfordNamedEntityRecognizer ner = new StanfordNamedEntityRecognizer(modelFile);
		ner.init();
		return ner;
	}

	@Override
	protected void configureMapping() {
		mappingProvider.setDefault(MappingProvider.LOCATION, StanfordNERMapping.MAPPING_LOCATION);
		mappingProvider.setDefault("variant", "all.3class.distsim.crf");
	}
	
}
