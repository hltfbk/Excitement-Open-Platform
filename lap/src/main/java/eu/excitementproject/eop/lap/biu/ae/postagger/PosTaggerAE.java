package eu.excitementproject.eop.lap.biu.ae.postagger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.lap.biu.ae.SingletonSynchronizedAnnotator;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggedToken;
import eu.excitementproject.eop.lap.biu.postagger.PosTagger;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggerException;


/**
 * A UIMA Analysis Engine tags the document in the CAS for Part Of Speech tags. <BR>
 * This is only a wrapper for an existing non-UIMA <code>eu.excitementproject.eop.lap.biu.postagger.PosTagger</code>
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
			Iterator<List<PosTaggedToken>> iterTaggedtokens = taggedTokens.iterator();
			Iterator<List<Token>> iterTokenAnnotations = tokenAnnotations.iterator();
			while (iterTaggedtokens.hasNext()) {
				List<PosTaggedToken> taggedTokensOnesentence = iterTaggedtokens.next();
				List<Token> tokenAnnotationsOnesentence = iterTokenAnnotations.next();
				
				if (taggedTokensOnesentence.size() != tokenAnnotationsOnesentence.size()) {
					throw new PosTaggerException("Got pos tagging for " + taggedTokensOnesentence.size() +
							" tokens, should have gotten according to the total number of tokens in the sentence: " + tokenAnnotationsOnesentence.size());
				}
								
				// Process each token
				Iterator<PosTaggedToken> iterTaggedtokensOnesentence = taggedTokensOnesentence.iterator();
				Iterator<Token> iterTokenAnnotationsOnesentence = tokenAnnotationsOnesentence.iterator();
				while (iterTaggedtokensOnesentence.hasNext()) {
					PosTaggedToken taggedToken = iterTaggedtokensOnesentence.next();
					String tagString = taggedToken.getPartOfSpeech().getStringRepresentation();
					Token tokenAnnotation = iterTokenAnnotationsOnesentence.next();
					
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
	
	/**
	 * Allow the subclass to provide details regarding its MappingProvider.
	 */
	protected abstract void configureMapping();
}
