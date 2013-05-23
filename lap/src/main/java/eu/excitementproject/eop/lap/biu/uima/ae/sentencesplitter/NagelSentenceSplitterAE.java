package eu.excitementproject.eop.lap.biu.uima.ae.sentencesplitter;

import eu.excitementproject.eop.common.datastructures.Envelope;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.nagel.NagelSentenceSplitter;

public class NagelSentenceSplitterAE extends SentenceSplitterAE<NagelSentenceSplitter> {

	private static Envelope<NagelSentenceSplitter> envelope = new Envelope<NagelSentenceSplitter>();
	
	@Override
	protected final Envelope<NagelSentenceSplitter> getEnvelope(){return envelope;}
	
	@Override
	protected NagelSentenceSplitter buildInnerTool() throws Exception {
		NagelSentenceSplitter sentenceSplitter = new NagelSentenceSplitter();
		return sentenceSplitter;
	}
	
}
