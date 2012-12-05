package eu.excitementproject.eop.lap.ae.postagger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.lap.ae.SingletonSynchronizedAnnotator;

import ac.biu.nlp.nlp.instruments.postagger.PosTaggedToken;
import ac.biu.nlp.nlp.instruments.postagger.PosTagger;
import ac.biu.nlp.nlp.instruments.postagger.PosTaggerException;


/////////////////
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.ngram.Dictionary;
import opennlp.tools.postag.DefaultPOSContextGenerator;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/////////////
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

	//private CasConfigurableProviderBase<POSTagger> modelProvider;
	private MappingProvider mappingProvider;
	
	@Override
	public void initialize(UimaContext aContext)
		throws ResourceInitializationException
	{
		super.initialize(aContext);

//		modelProvider = new CasConfigurableProviderBase<POSTagger>() {
//			{
//				setDefault(VERSION, "20120616.0");
//				setDefault(GROUP_ID, "de.tudarmstadt.ukp.dkpro.core");
//				setDefault(ARTIFACT_ID,
//						"de.tudarmstadt.ukp.dkpro.core.opennlp-model-tagger-${language}-${variant}");
//				
//				setDefault(LOCATION, "classpath:/de/tudarmstadt/ukp/dkpro/core/opennlp/lib/" +
//						"tagger-${language}-${variant}.bin");
//				setDefault(VARIANT, "maxent");
//				
//				setOverride(LOCATION, null);
//				setOverride(LANGUAGE, null);
//				setOverride(VARIANT, null);
//			}
//			
//			@Override
//			protected POSTagger produceResource(URL aUrl) throws IOException
//			{
//				//////////////////////////////////
//				// TODO copied from infrastructure's OpenNlpPosTagger
//				// TODO consts are the ones used for it
//				MaxentModel posTaggerModel;
//				Dictionary dict;
//				POSDictionary tagDict;
//				File posTaggerModelFile = new File("D:/Java/Jars/opennlp-tools-1.3.0/models/english/parser/tag.bin.gz");
//				String posTaggerTagDictionaryFile = "D:/Java/Jars/opennlp-tools-1.3.0/models/english/parser/tagdict";
//				boolean posTaggerTagDictionaryCaseSensitive = false;
//				String posTaggerDictionaryFile = null;
//				
//				try {
//					posTaggerModel = new SuffixSensitiveGISModelReader(posTaggerModelFile).getModel();
//					tagDict = new POSDictionary(posTaggerTagDictionaryFile, posTaggerTagDictionaryCaseSensitive);
//					dict = posTaggerDictionaryFile != null ? new Dictionary(posTaggerDictionaryFile) : null;
//				} catch (IOException e) {
//					throw new IOException("Error constructing a SuffixSensitiveGISModelReader with " + posTaggerModelFile + 
//							", " + posTaggerTagDictionaryFile + " and " + posTaggerDictionaryFile + ". See nested.", e);
//				}
//				return new POSTaggerME(posTaggerModel, new DefaultPOSContextGenerator(dict), tagDict);
//				/////////////////////////////
//			}
//		};
		
		mappingProvider = new MappingProvider();
		mappingProvider.setDefault(MappingProvider.LOCATION, "classpath:/de/tudarmstadt/ukp/dkpro/" +
				"core/api/lexmorph/tagset/${language}-${tagger.tagset}-tagger.map");
		mappingProvider.setDefault(MappingProvider.BASE_TYPE, POS.class.getName());
		mappingProvider.setDefault("tagger.tagset", "default");
		mappingProvider.setOverride(MappingProvider.LOCATION, null);
		mappingProvider.setOverride(MappingProvider.LANGUAGE, null);
		//mappingProvider.addImport("tagger.tagset", modelProvider);
		
	}
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			CAS cas = jcas.getCas();
			//modelProvider.configure(cas);
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
					Token tokenAnnotation = tokenAnnotationsOnesentence.get(j);
					
					// TODO build specific POS instance according to the POS string or the CanonicalPosTag
					// in the PartOfspeech object in taggedToken
					//////////////////////////////////
					Type posTag = mappingProvider.getTagType(taggedToken.getPartOfSpeech().getStringRepresentation());
					POS posAnnotation = (POS) cas.createAnnotation(posTag, tokenAnnotation.getBegin(), tokenAnnotation.getEnd());
					//////////////////////
					
					/////////POS posAnnotation = new POS(jcas, tokenAnnotation.getBegin(), tokenAnnotation.getEnd());
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
	@Override
	public void destroy() {
		synchronized (innerTool) {
			innerTool.cleanUp();
		}
	}
}
