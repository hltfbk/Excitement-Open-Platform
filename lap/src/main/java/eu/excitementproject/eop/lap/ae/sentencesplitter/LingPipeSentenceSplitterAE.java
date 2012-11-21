package eu.excitementproject.eop.lap.ae.sentencesplitter;

import eu.excitementproject.eop.lap.util.Envelope;

import ac.biu.nlp.nlp.instruments.sentencesplit.LingPipeSentenceSplitter;
import ac.biu.nlp.nlp.instruments.tokenizer.MaxentTokenizer;

public class LingPipeSentenceSplitterAE extends SentenceSplitterAE<LingPipeSentenceSplitter> {

	private static Envelope<LingPipeSentenceSplitter> envelope = new Envelope<LingPipeSentenceSplitter>();
	
	@Override
	protected final Envelope<LingPipeSentenceSplitter> getEnvelope(){return envelope;}
	
	@Override
	protected LingPipeSentenceSplitter buildInnerTool() throws Exception {
		LingPipeSentenceSplitter sentenceSplitter = new LingPipeSentenceSplitter();
		return sentenceSplitter;
	}
	
}
