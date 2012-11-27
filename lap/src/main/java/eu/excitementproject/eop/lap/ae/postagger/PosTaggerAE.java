package eu.excitementproject.eop.lap.ae.postagger;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import ac.biu.nlp.nlp.instruments.postagger.PosTaggedToken;
import ac.biu.nlp.nlp.instruments.postagger.PosTagger;
import ac.biu.nlp.nlp.instruments.postagger.PosTaggerException;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.lap.ae.SingletonSynchronizedAnnotator;

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
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		for (Sentence sentenceAnnotation : JCasUtil.select(jcas, Sentence.class)) {
			//TODO isn't it bad to keep getting the tokens for each sentence from the CAS in each
			// of the tools? (PosTagger, Lemmatizer, Parser, etc) Is there no way for this info to be
			// saved, rather than having to search it every time
			List<Token> tokens = JCasUtil.selectCovered(jcas, Token.class, sentenceAnnotation);
			List<String> tokenStrings = JCasUtil.toText(tokens);

			List<PosTaggedToken> taggedTokens;
			try {
				// Using the inner tool
				synchronized (innerTool) {
					innerTool.setTokenizedSentence(tokenStrings);
					innerTool.process();
					taggedTokens = innerTool.getPosTaggedTokens();
				}
			} catch (PosTaggerException e) {
				throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
			}
			
			// Process each token
			for (int j=0; j<taggedTokens.size(); j++) {
				PosTaggedToken taggedToken = taggedTokens.get(j);
				Token tokenAnnotation = tokens.get(j);
				
				// TODO build specific POS instance according to the POS string or the CanonicalPosTag
				// in the PartOfspeech object in taggedToken
				POS posAnnotation = new POS(jcas, tokenAnnotation.getBegin(), tokenAnnotation.getEnd());
				posAnnotation.setPosValue(taggedToken.getPartOfSpeech().getStringRepresentation());
				posAnnotation.addToIndexes();
				
				tokenAnnotation.setPos(posAnnotation);
			}
		}
		
	}
	// TODO add innerTool.cleanup() in an @Override of destroy()
}
