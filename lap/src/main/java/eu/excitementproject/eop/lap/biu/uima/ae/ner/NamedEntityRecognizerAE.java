package eu.excitementproject.eop.lap.biu.uima.ae.ner;

import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityWord;
import eu.excitementproject.eop.lap.biu.uima.ae.SingletonSynchronizedAnnotator;

/**
 * A UIMA Analysis Engine tags the document in the CAS for NER tags. <BR>
 * This is only a wrapper for an existing non-UIMA
 * {@link eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizer} interface.
 * 
 * @author Ofer Bronstein
 * @since Feb 2013
 *
 */
public abstract class NamedEntityRecognizerAE<T extends NamedEntityRecognizer> extends SingletonSynchronizedAnnotator<T>{

	protected MappingProvider mappingProvider;
	
	@Override
	public void initialize(UimaContext aContext)
		throws ResourceInitializationException
	{
		super.initialize(aContext);

		mappingProvider = new MappingProvider();
		mappingProvider.setDefault(MappingProvider.BASE_TYPE, NamedEntity.class.getName());
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
				List<NamedEntityWord> taggedTokens;
				
				synchronized (innerTool) {
					innerTool.setSentence(tokenStrings);
					innerTool.recognize();
					taggedTokens = innerTool.getAnnotatedSentence();
				}
				
				if (taggedTokens.size() != tokens.size()) {
					throw new NamedEntityRecognizerException("Got NER tagging for " + taggedTokens.size() +
							" tokens, should have gotten according to the total number of tokens in the sentence: " + tokens.size());
				}
				
				Iterator<Token> tokenIter = tokens.iterator();
				for (NamedEntityWord taggedToken : taggedTokens) {
					Token tokenAnno = tokenIter.next();
					
					if (taggedToken.getNamedEntity() != null) {
					
						// We rely on the fact the NamedEntity enum values have the same names as the ones
						// specified in the DKPro mapping (e.g. PERSON, ORGANIZATION)
						String tagString = taggedToken.getNamedEntity().toString();
						
						// Get an annotation with the appropriate UIMA type via the mappingProvider
						Type nerTag = mappingProvider.getTagType(tagString);
						NamedEntity nerAnnotation = (NamedEntity) cas.createAnnotation(nerTag, tokenAnno.getBegin(), tokenAnno.getEnd());
						nerAnnotation.setValue(tagString);
						nerAnnotation.addToIndexes();
					}
				}
			}
		} catch (NamedEntityRecognizerException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		}
	}
	
	/**
	 * Allow the subclass to provide details regarding its MappingProvider.
	 */
	protected abstract void configureMapping();}
