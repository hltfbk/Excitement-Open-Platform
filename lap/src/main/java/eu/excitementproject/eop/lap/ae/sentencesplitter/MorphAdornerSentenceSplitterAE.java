package eu.excitementproject.eop.lap.ae.sentencesplitter;

import eu.excitementproject.eop.lap.util.Envelope;

import ac.biu.nlp.nlp.instruments.sentencesplit.LingPipeSentenceSplitter;
import ac.biu.nlp.nlp.instruments.sentencesplit.MorphAdornerSentenceSplitter;
import ac.biu.nlp.nlp.instruments.tokenizer.MaxentTokenizer;

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
