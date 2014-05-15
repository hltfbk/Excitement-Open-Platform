package eu.excitementproject.eop.lap.biu.uima.ae.sentencesplitter;

import eu.excitementproject.eop.common.datastructures.Envelope;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.MorphAdornerSentenceSplitter;

public class MorphAdornerSentenceSplitterAE extends SentenceSplitterAE<MorphAdornerSentenceSplitter> {

	private static Envelope<MorphAdornerSentenceSplitter> envelope = new Envelope<MorphAdornerSentenceSplitter>();
	
	@Override
	protected final Envelope<MorphAdornerSentenceSplitter> getEnvelope(){return envelope;}
	
	@Override
	protected MorphAdornerSentenceSplitter buildInnerTool() throws Exception {
		MorphAdornerSentenceSplitter sentenceSplitter = new MorphAdornerSentenceSplitter();
		return sentenceSplitter;
	}
	
}
