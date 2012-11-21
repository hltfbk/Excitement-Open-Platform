package eu.excitementproject.eop.lap.ae.tokenizer;

import java.io.File;

import org.uimafit.descriptor.ConfigurationParameter;

import eu.excitementproject.eop.lap.util.Envelope;

import ac.biu.nlp.nlp.instruments.tokenizer.OpenNlpTokenizer;

public class OpenNLPTokenizerAE extends TokenizerAE<OpenNlpTokenizer> {

	// TODO remove  defaultValue?
	/**
	 * Model file of this tokenizer.
	 */
	public static final String PARAM_MODEL_FILE = "model_file";
	@ConfigurationParameter(name = PARAM_MODEL_FILE, mandatory = true, defaultValue="D:/Java/Jars/opennlp-tools-1.3.0/models/english/tokenize/EnglishTok.bin.gz")
	private String modelFile;
	
	private static Envelope<OpenNlpTokenizer> envelope = new Envelope<OpenNlpTokenizer>();
	
	@Override
	protected final Envelope<OpenNlpTokenizer> getEnvelope(){return envelope;}
	
	@Override
	protected OpenNlpTokenizer buildInnerTool() throws Exception {
		//TODO why does modelFile equal null here?
		File mf = new File("D:/Java/Jars/opennlp-tools-1.3.0/models/english/tokenize/EnglishTok.bin.gz");
		OpenNlpTokenizer tokenizer = new OpenNlpTokenizer(mf);
		tokenizer.init();
		return tokenizer;
	}
	
}
