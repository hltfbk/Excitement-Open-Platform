package eu.excitementproject.eop.lap.biu.ae.sentencesplitter;

import java.util.List;
import java.util.SortedMap;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import eu.excitementproject.eop.common.utilities.DockedToken;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.StringUtilException;
import eu.excitementproject.eop.lap.biu.ae.SingletonSynchronizedAnnotator;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;

/**
 * A UIMA Analysis Engine that splits the document in the CAS to sentences. <BR>
 * This is only a wrapper for an existing non-UIMA <code>eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter</code>
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

			// Using the inner tool - smallest "synchronize" block possible
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
}
