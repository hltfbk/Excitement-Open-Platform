package eu.excitementproject.eop.lap.ae.postagger;

import java.io.File;

import org.uimafit.descriptor.ConfigurationParameter;

import eu.excitementproject.eop.lap.util.Envelope;

import ac.biu.nlp.nlp.instruments.postagger.OpenNlpPosTagger;
import ac.biu.nlp.nlp.instruments.tokenizer.OpenNlpTokenizer;

public class OpenNlpPosTaggerAE extends PosTaggerAE<OpenNlpPosTagger> {

	// TODO remove  defaultValue?
	/**
	 * Model file of this tokenizer.
	 */
	public static final String PARAM_MODEL_FILE = "model_file";
	@ConfigurationParameter(name = PARAM_MODEL_FILE, mandatory = true, defaultValue="D:/Java/Jars/opennlp-tools-1.3.0/models/english/parser/tag.bin.gz")
	private String modelFile;
	
	private static Envelope<OpenNlpPosTagger> envelope = new Envelope<OpenNlpPosTagger>();
	
	@Override
	protected final Envelope<OpenNlpPosTagger> getEnvelope(){return envelope;}
	
	@Override
	protected OpenNlpPosTagger buildInnerTool() throws Exception {
		//TODO why does modelFile equal null here?
		File mf = new File("D:/Java/Jars/opennlp-tools-1.3.0/models/english/parser/tag.bin.gz");
		String tdp = "D:/Java/Jars/opennlp-tools-1.3.0/models/english/parser/tagdict";
		OpenNlpPosTagger tagger = new OpenNlpPosTagger(mf, tdp);
		tagger.init();
		return tagger;
	}
	
}
