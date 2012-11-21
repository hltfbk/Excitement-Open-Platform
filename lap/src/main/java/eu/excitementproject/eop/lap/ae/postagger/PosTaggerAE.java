package eu.excitementproject.eop.lap.ae.postagger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.lap.ae.SingletonSynchronizedAnnotator;

import ac.biu.nlp.nlp.instruments.postagger.PosTaggedToken;
import ac.biu.nlp.nlp.instruments.postagger.PosTagger;
import ac.biu.nlp.nlp.instruments.postagger.PosTaggerException;

/**
 * A UIMA Analysis Engine tags the document in the CAS for Part Of Speech tags. <BR>
 * This is only a wrapper for an existing non-UIMA <code>PosTagger</code>
 * interface.
 * 
 * @author Ofer Bronstein
 * @since Nov 2012
 *
 */
public abstract class PosTaggerAE<T extends PosTagger> extends SingletonSynchronizedAnnotator<T> {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			Collection<Sentence> sentenceAnnotations = JCasUtil.select(aJCas, Sentence.class);
			List<List<Token>> tokenAnnotations = new ArrayList<List<Token>>(sentenceAnnotations.size());
			List<List<String>> tokenStrings = new ArrayList<List<String>>(sentenceAnnotations.size());
			
			for (Sentence sentenceAnnotation : sentenceAnnotations) {
				//TODO isn't it bad to keep getting the tokens for each sentence from the CAS in each
				// of the tools? (PosTagger, Lemmatizer, Parser, etc) Is there no way for this info to be
				// saved, rather than having to search it every time
				List<Token> tokenAnnotationsOnesentence = JCasUtil.selectCovered(aJCas, Token.class, sentenceAnnotation);
				tokenAnnotations.add(tokenAnnotationsOnesentence);
				List<String> tokenStringsOneSentence = JCasUtil.toText(tokenAnnotationsOnesentence);
				tokenStrings.add(tokenStringsOneSentence);
			}

			List<List<PosTaggedToken>> taggedTokens = new ArrayList<List<PosTaggedToken>>(tokenStrings.size());
			
			// Using the inner tool
			synchronized (innerTool) {
				for (List<String> tokenStringsOneSentence : tokenStrings) {
					innerTool.setTokenizedSentence(tokenStringsOneSentence);
					innerTool.process();
					List<PosTaggedToken> taggedTokensOneSentence = innerTool.getPosTaggedTokens();
					taggedTokens.add(taggedTokensOneSentence);
				}
			}
			
			// Process each sentence
			for (int i=0; i<taggedTokens.size(); i++) {
				List<PosTaggedToken> taggedTokensOnesentence = taggedTokens.get(i);
				List<Token> tokenAnnotationsOnesentence = tokenAnnotations.get(i);
				
				if (taggedTokensOnesentence.size() != tokenAnnotationsOnesentence.size()) {
					throw new PosTaggerException("Got pos tagging for " + taggedTokensOnesentence.size() +
							" tokens, should have gotten according to the total number of tokens in the sentence: " + tokenAnnotationsOnesentence.size());
				}
				
				// Process each token
				for (int j=0; j<taggedTokensOnesentence.size(); j++) {
					PosTaggedToken taggedToken = taggedTokensOnesentence.get(j);
					Token tokenAnnotation = tokenAnnotationsOnesentence.get(j);
					
					// TODO build specific POS instance according to the POS string or the CanonicalPosTag
					// in the PartOfspeech object in taggedToken
					POS posAnnotation = new POS(aJCas, tokenAnnotation.getBegin(), tokenAnnotation.getEnd());
					posAnnotation.setPosValue(taggedToken.getPartOfSpeech().getStringRepresentation());
					posAnnotation.addToIndexes();
					
					tokenAnnotation.setPos(posAnnotation);
				}
			}
		} catch (PosTaggerException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		}
	}
	
	// TODO add innerTool.cleanup() in an @Override of destroy()
}
