package eu.excitementproject.eop.lap.biu.uima.ae.sentencesplitter;

import eu.excitementproject.eop.common.datastructures.Envelope;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.LingPipeSentenceSplitter;

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
