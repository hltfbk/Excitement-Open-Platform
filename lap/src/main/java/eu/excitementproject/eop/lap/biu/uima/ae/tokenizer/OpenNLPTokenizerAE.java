package eu.excitementproject.eop.lap.biu.uima.ae.tokenizer;

/**
 * A UIMA wrapper for BIU's OpenNlpTokenizer.
 * This class is commented-out, since during BIU's migration to EXCITEMENT (January 2013)
 * the OpenNlpTokenizer class was removed, due to a jar conflict.
 * If this class is ever needed in BIU, it should be implemented over the jar
 * used in EOP, and then this wrapper class can be used (assuming the constructor and
 * interface methods have the same input).
 */

//import java.io.File;
//
//import org.uimafit.descriptor.ConfigurationParameter;
//
//import eu.excitementproject.eop.lap.util.Envelope;
//
//public class OpenNLPTokenizerAE extends TokenizerAE<OpenNlpTokenizer> {
//
//	/**
//	 * Model file of this tokenizer.
//	 */
//	public static final String PARAM_MODEL_FILE = "model_file";
//	@ConfigurationParameter(name = PARAM_MODEL_FILE, mandatory = true)
//	private String modelFile;
//	
//	private static Envelope<OpenNlpTokenizer> envelope = new Envelope<OpenNlpTokenizer>();
//	
//	@Override
//	protected final Envelope<OpenNlpTokenizer> getEnvelope(){return envelope;}
//	
//	@Override
//	protected OpenNlpTokenizer buildInnerTool() throws Exception {
//		File mf = new File(modelFile);
//		OpenNlpTokenizer tokenizer = new OpenNlpTokenizer(mf);
//		tokenizer.init();
//		return tokenizer;
//	}
//	
//}
