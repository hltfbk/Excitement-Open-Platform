package eu.excitementproject.eop.lap.biu.ae.coreference;

//import java.util.Iterator;
//import java.util.List;
//
//import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
//import org.apache.uima.cas.CAS;
//import org.apache.uima.cas.Type;
//import org.apache.uima.jcas.JCas;
//import org.uimafit.util.JCasUtil;
//
//import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
//import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
//import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
//import eu.excitementproject.eop.lap.biu.ae.SingletonSynchronizedAnnotator;
//import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
//import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolver;
//import eu.excitementproject.eop.lap.biu.postagger.PosTaggedToken;
//import eu.excitementproject.eop.lap.biu.postagger.PosTaggerException;
//
//public abstract class CoreferenceResolverAE<S, T extends CoreferenceResolver<S>> extends SingletonSynchronizedAnnotator<T> {
//	@Override
//	public void process(JCas jcas) throws AnalysisEngineProcessException {
//		try {
//			for (Sentence sentenceAnno : JCasUtil.select(jcas, Sentence.class)) {
//				List<Token> tokens = JCasUtil.selectCovered(jcas, Token.class, sentenceAnno);
//				List<String> tokenStrings = JCasUtil.toText(tokens);
//				List<PosTaggedToken> taggedTokens;
//				
//				synchronized (innerTool) {
//					innerTool.setInput(tokenStrings);
//					innerTool.resolve();
//					taggedTokens = innerTool.getCoreferenceInformation();
//				}
//				
//				if (taggedTokens.size() != tokens.size()) {
//					throw new PosTaggerException("Got pos tagging for " + taggedTokens.size() +
//							" tokens, should have gotten according to the total number of tokens in the sentence: " + tokens.size());
//				}
//				
//				Iterator<Token> tokenIter = tokens.iterator();
//				for (PosTaggedToken taggedToken : taggedTokens) {
//					Token tokenAnno = tokenIter.next();
//					String tagString = taggedToken.getPartOfSpeech().getStringRepresentation();
//					
//					// Get an annotation with the appropriate UIMA type via the mappingProvider
//					Type posTag = mappingProvider.getTagType(tagString);
//					POS posAnnotation = (POS) cas.createAnnotation(posTag, tokenAnno.getBegin(), tokenAnno.getEnd());
//					posAnnotation.setPosValue(tagString);
//					posAnnotation.addToIndexes();
//					
//					tokenAnno.setPos(posAnnotation);
//				}
//			}
//		} catch (CoreferenceResolutionException e) {
//			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
//		}
//	}
//}
