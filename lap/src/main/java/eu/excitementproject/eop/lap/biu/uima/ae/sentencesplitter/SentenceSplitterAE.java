package eu.excitementproject.eop.lap.biu.uima.ae.sentencesplitter;

import java.util.List;
import java.util.SortedMap;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import eu.excitementproject.eop.common.utilities.DockedToken;
import eu.excitementproject.eop.common.utilities.DockedTokenFinder;
import eu.excitementproject.eop.common.utilities.DockedTokenFinderException;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;
import eu.excitementproject.eop.lap.biu.uima.ae.SingletonSynchronizedAnnotator;

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
			
			// dock the tokens on text, with heuristic recovery attempts, but not strict -
			// tokens that are not found in text will be silently ignored!
			SortedMap<Integer, DockedToken> dockedSentences = DockedTokenFinder.find(text, sentenceStrings, false, true);
			
			for (DockedToken dockedSentence : dockedSentences.values()) {
				Sentence sentenceAnnot = new Sentence(aJCas);
				sentenceAnnot.setBegin(dockedSentence.getCharOffsetStart());
				sentenceAnnot.setEnd(dockedSentence.getCharOffsetEnd());
				sentenceAnnot.addToIndexes();
			}
		} catch (SentenceSplitterException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		} catch (DockedTokenFinderException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		}
	}
}
