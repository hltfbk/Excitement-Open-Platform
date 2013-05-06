package eu.excitementproject.eop.lap.biu.ae.tokenizer;

import java.util.List;
import java.util.SortedMap;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.common.utilities.DockedToken;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.StringUtilException;
import eu.excitementproject.eop.lap.biu.ae.SingletonSynchronizedAnnotator;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.TokenizerException;

/**
 * A UIMA Analysis Engine that tokenizes the document in the CAS. <BR>
 * This is only a wrapper for an existing non-UIMA {@link eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer}
 * abstract class. <BR>
 * <BR>
 * <b>NOTE:</b> If the underlying tokenizer returns some tokens that don't exist in the
 * original sentence (e.g. transforming a '£' to a '#'), these tokens will be <b>silently
 * ignored</b>, and effectively removed from further processing in the LAP.
 * 
 * @author Ofer Bronstein
 * @since Nov 2012
 *
 */
public abstract class TokenizerAE<T extends Tokenizer> extends SingletonSynchronizedAnnotator<T> {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			for (Sentence sentenceAnno : JCasUtil.select(aJCas, Sentence.class)) {
				List<String> tokenStrings;
				
				synchronized (innerTool) {
					innerTool.setSentence(sentenceAnno.getCoveredText());
					innerTool.tokenize();
					tokenStrings = innerTool.getTokenizedSentence();
				}

				// NOTE: The method is called in non-strict mode, which means that tokens from the
				// tokenizer output that are not found in the text are ignored, instead of an exception
				// being thrown. This is done since, for example, MaxentTokenizer transformed
				// a '£' to a '#', and we don't have any way to deal with it. Some some tokens may be
				// discarded.
				SortedMap<Integer, DockedToken> dockedTokens = StringUtil.getTokensOffsets(sentenceAnno.getCoveredText(), tokenStrings, false);
				
				for (DockedToken dockedToken : dockedTokens.values()) {
					Token tokenAnnot = new Token(aJCas);
					tokenAnnot.setBegin(dockedToken.getCharOffsetStart() + sentenceAnno.getBegin());
					tokenAnnot.setEnd(dockedToken.getCharOffsetEnd() + sentenceAnno.getBegin());
					tokenAnnot.addToIndexes();
				}
			}		
		} catch (TokenizerException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		} catch (StringUtilException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		}
	}
	
}
