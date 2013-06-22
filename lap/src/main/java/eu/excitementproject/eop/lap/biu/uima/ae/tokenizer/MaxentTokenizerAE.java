package eu.excitementproject.eop.lap.biu.uima.ae.tokenizer;

import eu.excitementproject.eop.common.datastructures.Envelope;
import eu.excitementproject.eop.lap.biu.en.tokenizer.MaxentTokenizer;

public class MaxentTokenizerAE extends TokenizerAE<MaxentTokenizer> {

	private static Envelope<MaxentTokenizer> envelope = new Envelope<MaxentTokenizer>();
	
	@Override
	protected final Envelope<MaxentTokenizer> getEnvelope(){return envelope;}
	
	@Override
	protected MaxentTokenizer buildInnerTool() throws Exception {
		MaxentTokenizer tokenizer = new MaxentTokenizer();
		tokenizer.init();
		return tokenizer;
	}
	
}
