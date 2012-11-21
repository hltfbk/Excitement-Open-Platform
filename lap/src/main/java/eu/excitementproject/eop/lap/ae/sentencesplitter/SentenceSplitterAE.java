package eu.excitementproject.eop.lap.ae.sentencesplitter;

import java.util.List;
import java.util.SortedMap;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import eu.excitementproject.eop.lap.ae.SingletonSynchronizedAnnotator;

import ac.biu.nlp.nlp.general.DockedToken;
import ac.biu.nlp.nlp.general.StringUtil;
import ac.biu.nlp.nlp.general.StringUtilException;
import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter;
import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitterException;

/**
 * A UIMA Analysis Engine that splits the document in the CAS to sentences. <BR>
 * This is only a wrapper for an existing non-UIMA <code>SentenceSplitter</code>
 * interface.
 * 
 * @author Ofer Bronstein
 * @since Nov 2012
 *
 */
public abstract class SentenceSplitterAE<T extends SentenceSplitter> extends SingletonSynchronizedAnnotator<T> {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			List<String> sentenceStrings = null;
			String text = aJCas.getDocumentText();

			// Using the inner tool
			synchronized (innerTool) {
				innerTool.setDocument(text);
				innerTool.split();
				sentenceStrings = innerTool.getSentences();

			}
			
			// If you get an exception for an unfound token, you can change
			// the "true" to "false", and tokens unfound in the text will be ignored  
			SortedMap<Integer, DockedToken> dockedSentences = StringUtil.getTokensOffsets(text, sentenceStrings, true);
			
			for (DockedToken dockedSentence : dockedSentences.values()) {
				Sentence sentenceAnnot = new Sentence(aJCas);
				sentenceAnnot.setBegin(dockedSentence.getCharOffsetStart());
				sentenceAnnot.setEnd(dockedSentence.getCharOffsetEnd());
				sentenceAnnot.addToIndexes();
			}
		} catch (SentenceSplitterException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		} catch (StringUtilException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		}
	}
	
//	protected abstract Tokenizer getInnerTool() throws Exception;
}
