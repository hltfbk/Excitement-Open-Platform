package eu.excitementproject.eop.transformations.generic.truthteller;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.predicatetruth.ClauseTruth;
import eu.excitement.type.predicatetruth.NegationAndUncertainty;
import eu.excitement.type.predicatetruth.PredicateSignature;
import eu.excitement.type.predicatetruth.PredicateTruth;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.uima.ae.SingletonSynchronizedAnnotator;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/*
 * An analysis engine for truth annotations
 */

public abstract class TruthAnnotatorAE<T extends TruthAnnotator> extends SingletonSynchronizedAnnotator<T>  {
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException{
		super.initialize(aContext);
		
		
		//debug
		/*
		mappingProvider = new MappingProvider();
		configureMapping();
		
		Type nerTag = mappingProvider.getTagType("eu.excitement.type.predicatetruth.PredicateTruthNegative");
		int stam =0;
		stam++;*/
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Get the raw sentences from the CAS	    	
    	for (Sentence sentenceAnno : JCasUtil.select(aJCas, Sentence.class)) {
    		String rawSentence = sentenceAnno.getCoveredText();
    		// get a list of all tokens in the current sentence
    		List<Token> tokens = JCasUtil.selectCovered(aJCas, Token.class, sentenceAnno);
			Map<Integer, SingleTokenTruthAnnotation> taggedTokens;
			
			// run inner tool to obtain truth annotations
			synchronized (innerTool) {
				innerTool.setSentence(rawSentence);
				try{
					innerTool.annotate();
				}
				catch(Exception e){
					throw new AnalysisEngineProcessException(e);
				}
				taggedTokens = innerTool.getAnnotatedEntities();
			}
			
			// iterate over all tokens and obtain their truth annotations
			ListIterator<Token> it = tokens.listIterator(); 
			for (Token tokenAnno : tokens) {
				int curIndex = it.nextIndex();
				it.next();
				SingleTokenTruthAnnotation annotationResult = taggedTokens.get(curIndex);
				
				
				if (annotationResult.getPredicateTruthValue() != null){
					// Predicate Truth
					PredicateTruth ptTag = TruthMapping.mapPredicateTruth(annotationResult.getPredicateTruthValue(),aJCas,tokenAnno.getBegin(), tokenAnno.getEnd());
					ptTag.addToIndexes();

				}
				if (annotationResult.getClauseTruthValue() != null){
					// Clause Truth
					//in this case the annotation result must hold a subordinate clause - pass it to the truth mapping
					
					//calculate a Token set from extendedNode set
					Set<Token> subtree = new HashSet<Token>();
					for (ExtendedNode e : annotationResult.getSubtree()){
						subtree.add(tokens.get(Integer.parseInt(e.getInfo().getId())-1));
					}
					
					// get boundaries from annotationResult and get them from the token's begin and and 
					int begin = tokens.get(annotationResult.getSubtreeMinimalIndex()).getBegin(),
					    end   = tokens.get(annotationResult.getSubtreeMaximalIndex()).getEnd();
					ClauseTruth ctTag = TruthMapping.mapClauseTruth(annotationResult.getClauseTruthValue(), aJCas, subtree,begin,end);
					ctTag.addToIndexes();
					     
					
				}
				
				if (annotationResult.getNuValue() != null){
					// Negation and Uncertainty
					NegationAndUncertainty nuTag = TruthMapping.mapNegationAndUncertainty(annotationResult.getNuValue(),aJCas,tokenAnno.getBegin(), tokenAnno.getEnd());
					nuTag.addToIndexes();
				}
				
				if (annotationResult.getPredicateSignatureValue() != null){
					// Predicate Signature
					PredicateSignature sigTag = TruthMapping.mapPredicateSignature(annotationResult.getPredicateSignatureValue(),aJCas,tokenAnno.getBegin(), tokenAnno.getEnd());
					sigTag.addToIndexes();
				}    				

				
			}
		}
	}
	
	/**
	 * Allow the subclass to provide details regarding its MappingProvider.
	 
	protected abstract void configureMapping();
	*/

	// get the configuration parameter 
	public static final String PARAM_CONFIG = "config";
	@ConfigurationParameter(name = PARAM_CONFIG, mandatory = true)
	protected String config;
	
	
	
	public static void main(String[] args) throws LAPException {
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		LAPAccess lap = new BIUFullLAPWithTruthTeller(
				"src/main/resources/model/left3words-wsj-0-18.tagger",
				"src/main/resources/model/ner-eng-ie.crf-3-all2008-distsim.ser.gz",
				"localhost",
				8080);
		
		File devSet = new File("src/main/resources/dataset/RTE3_dev_few.xml"); 
		File trainDir = new File("target/xmi/RTE5_dev"); // as 
		trainDir.mkdirs();
		lap.processRawInputFormat(devSet, trainDir); 
		System.out.println("THE END");
	
	}
}
