package eu.excitementproject.eop.lap.ae.tokenizer;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.lap.ae.SingletonSynchronizedAnnotator;

import ac.biu.nlp.nlp.general.DockedToken;
import ac.biu.nlp.nlp.general.StringUtil;
import ac.biu.nlp.nlp.general.StringUtilException;
import ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer;
import ac.biu.nlp.nlp.instruments.tokenizer.TokenizerException;

/**
 * A UIMA Analysis Engine that tokenizes the document in the CAS. <BR>
 * This is only a wrapper for an existing non-UIMA <code>Tokenizer</code>
 * abstract class.
 * 
 * @author Ofer Bronstein
 * @since Nov 2012
 *
 */
public abstract class TokenizerAE<T extends Tokenizer> extends SingletonSynchronizedAnnotator<T> {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		/*************************************************/
		// TODO REMOVE testing only - builds Sentence annotations just by "."
//		String text = aJCas.getDocumentText();
//		text = text.trim();
//		if (!text.endsWith(".")) {
//			text = text + ".";
//		}
//		Matcher m = Pattern.compile("([^\\.]+)").matcher(text);
//		while(m.find()) {
//			Sentence sentence = new Sentence(aJCas, m.start(), m.end());
//			sentence.addToIndexes();
//		}
		/*************************************************/
		
		try {
			
			List<List<String>> tokenStrings = null;
			// TODO is there no way to get the annotations BY ORDER? not critical, but it seems better
			// to have the process sequential, and not in random order
			Collection<Sentence> sentenceAnnotations = JCasUtil.select(aJCas, Sentence.class);
			List<String> sentenceStrings = JCasUtil.toText(sentenceAnnotations);

			// Using the inner tool
			synchronized (innerTool) {
				innerTool.setSentences(sentenceStrings);
				innerTool.tokenize();
				tokenStrings = innerTool.getTokenizedSentences();

			}
			
			if (sentenceStrings.size() != tokenStrings.size()) {
				throw new TokenizerException("Got tokenization for " + tokenStrings.size() +
						" sentences, should have gotten according to the total number of sentences: " + sentenceStrings.size());
			}
			
			for (int i=0; i<sentenceStrings.size(); i++) {
			
				String oneSentence = sentenceStrings.get(i);
				List<String> tokensOneSentence = tokenStrings.get(i);
				
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
	
//	protected abstract Tokenizer getInnerTool() throws Exception;
}
