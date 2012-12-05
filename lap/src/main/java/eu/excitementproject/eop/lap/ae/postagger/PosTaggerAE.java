package eu.excitementproject.eop.lap.ae.postagger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.util.JCasUtil;

import ac.biu.nlp.nlp.instruments.postagger.PosTaggedToken;
import ac.biu.nlp.nlp.instruments.postagger.PosTagger;
import ac.biu.nlp.nlp.instruments.postagger.PosTaggerException;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
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

	protected MappingProvider mappingProvider;
	
	@Override
	public void initialize(UimaContext aContext)
		throws ResourceInitializationException
	{
		super.initialize(aContext);

		mappingProvider = new MappingProvider();
		mappingProvider.setDefault(MappingProvider.BASE_TYPE, POS.class.getName());
		configureMapping();
	}
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			CAS cas = jcas.getCas();
			mappingProvider.configure(cas);
			
			Collection<Sentence> sentenceAnnotations = JCasUtil.select(jcas, Sentence.class);
			List<List<Token>> tokenAnnotations = new ArrayList<List<Token>>(sentenceAnnotations.size());
			List<List<String>> tokenStrings = new ArrayList<List<String>>(sentenceAnnotations.size());
			
			for (Sentence sentenceAnnotation : sentenceAnnotations) {
				List<Token> tokenAnnotationsOnesentence = JCasUtil.selectCovered(jcas, Token.class, sentenceAnnotation);
				tokenAnnotations.add(tokenAnnotationsOnesentence);
				List<String> tokenStringsOneSentence = JCasUtil.toText(tokenAnnotationsOnesentence);
				tokenStrings.add(tokenStringsOneSentence);
			}

			List<List<PosTaggedToken>> taggedTokens = new ArrayList<List<PosTaggedToken>>(tokenStrings.size());
			
			// Using the inner tool
			// This is done in a different for-loop, to avoid entering the synchornized() block
			// many times (once per Sentence)
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
					String tagString = taggedToken.getPartOfSpeech().getStringRepresentation();
					Token tokenAnnotation = tokenAnnotationsOnesentence.get(j);
					
					// Get an annotation with the appropriate UIMA type via the mappingProvider
					Type posTag = mappingProvider.getTagType(tagString);
					POS posAnnotation = (POS) cas.createAnnotation(posTag, tokenAnnotation.getBegin(), tokenAnnotation.getEnd());
					posAnnotation.setPosValue(tagString);
					posAnnotation.addToIndexes();
					
					tokenAnnotation.setPos(posAnnotation);
				}
			}
		} catch (PosTaggerException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		}
	}
	
	@Override
	public void destroy() {
		synchronized (innerTool) {
			innerTool.cleanUp();
		}
	}
	
	/**
	 * Allow the subclass to provide details regarding its MappingProvider.
	 */
	protected abstract void configureMapping();
}
