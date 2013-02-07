package eu.excitementproject.eop.lap.biu.ae.tokenizer;

import java.util.Collection;
import java.util.Iterator;
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
 * This is only a wrapper for an existing non-UIMA <code>eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer</code>
 * abstract class.
 * 
 * @author Ofer Bronstein
 * @since Nov 2012
 *
 */
public abstract class TokenizerAE<T extends Tokenizer> extends SingletonSynchronizedAnnotator<T> {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		try {
			List<List<String>> tokenStrings = null;
			Collection<Sentence> sentenceAnnotations = JCasUtil.select(aJCas, Sentence.class);
			List<String> sentenceStrings = JCasUtil.toText(sentenceAnnotations);

			// Using the inner tool - smallest "synchronize" block possible
			synchronized (innerTool) {
				innerTool.setSentences(sentenceStrings);
				innerTool.tokenize();
				tokenStrings = innerTool.getTokenizedSentences();
			}
			
			if (sentenceStrings.size() != tokenStrings.size()) {
				throw new TokenizerException("Got tokenization for " + tokenStrings.size() +
						" sentences, should have gotten according to the total number of sentences: " + sentenceStrings.size());
			}
			
			
			Iterator<String> iterSentenceStrings = sentenceStrings.iterator();
			Iterator<List<String>> iterTokenStrings = tokenStrings.iterator();
			while (iterSentenceStrings.hasNext())
			{
				String oneSentence = iterSentenceStrings.next();
				List<String> tokensOneSentence = iterTokenStrings.next();
				
				// If you get an exception for an unfound token, you can change
				// the "true" to "false", and tokens unfound in the text will be ignored  
				SortedMap<Integer, DockedToken> dockedTokens = StringUtil.getTokensOffsets(oneSentence, tokensOneSentence, true);
				
				for (DockedToken dockedToken : dockedTokens.values()) {
					Token tokenAnnot = new Token(aJCas);
					tokenAnnot.setBegin(dockedToken.getCharOffsetStart());
					tokenAnnot.setEnd(dockedToken.getCharOffsetEnd());
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
