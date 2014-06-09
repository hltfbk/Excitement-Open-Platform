package eu.excitementproject.eop.lap.biu.uima.ae.postagger;

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
import eu.excitementproject.eop.lap.biu.postagger.PosTaggedToken;
import eu.excitementproject.eop.lap.biu.postagger.PosTagger;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggerException;
import eu.excitementproject.eop.lap.biu.uima.ae.SingletonSynchronizedAnnotator;


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
			
			for (Sentence sentenceAnno : JCasUtil.select(jcas, Sentence.class)) {
				List<Token> tokens = JCasUtil.selectCovered(jcas, Token.class, sentenceAnno);
				List<String> tokenStrings = JCasUtil.toText(tokens);
				List<PosTaggedToken> taggedTokens;
				
				synchronized (innerTool) {
					innerTool.setTokenizedSentence(tokenStrings);
					innerTool.process();
					taggedTokens = innerTool.getPosTaggedTokens();
				}
				
				if (taggedTokens.size() != tokens.size()) {
					throw new PosTaggerException("Got pos tagging for " + taggedTokens.size() +
							" tokens, should have gotten according to the total number of tokens in the sentence: " + tokens.size());
				}
				
				Iterator<Token> tokenIter = tokens.iterator();
				for (PosTaggedToken taggedToken : taggedTokens) {
					Token tokenAnno = tokenIter.next();
					String tagString = taggedToken.getPartOfSpeech().getStringRepresentation();
					
					// Get an annotation with the appropriate UIMA type via the mappingProvider
					Type posTag = mappingProvider.getTagType(tagString);
					POS posAnnotation = (POS) cas.createAnnotation(posTag, tokenAnno.getBegin(), tokenAnno.getEnd());
					posAnnotation.setPosValue(tagString);
					posAnnotation.addToIndexes();
					
					tokenAnno.setPos(posAnnotation);
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
