package eu.excitementproject.eop.transformations.uima.ae.truthteller;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.predicatetruth.ClauseTruth;
import eu.excitement.type.predicatetruth.NegationAndUncertainty;
import eu.excitement.type.predicatetruth.PredicateSignature;
import eu.excitement.type.predicatetruth.PredicateTruth;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.lap.biu.uima.CasTreeConverter;
import eu.excitementproject.eop.lap.biu.uima.CasTreeConverterException;
import eu.excitementproject.eop.lap.biu.uima.ae.SingletonSynchronizedAnnotator;
import eu.excitementproject.eop.transformations.biu.en.predicatetruth.PredicateTruthException;
import eu.excitementproject.eop.transformations.biu.en.predicatetruth.SingleTokenTruthAnnotation;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

/**
 * An analysis engine for truth annotations
 *  @author Gabi Stanovsky
 *  @since Aug 2014
 */

public abstract class PredicateTruthAE<T extends eu.excitementproject.eop.transformations.biu.en.predicatetruth.PredicateTruth> extends SingletonSynchronizedAnnotator<T>  {
	
	private CasTreeConverter converter;
	
  	@Override
  	public void initialize(UimaContext aContext) throws ResourceInitializationException{
  		super.initialize(aContext);
  		converter = new CasTreeConverter();
 	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			// Get the raw sentences from the CAS	    	
	    	for (Sentence sentenceAnno : JCasUtil.select(aJCas, Sentence.class)) {
	    		// get a list of all tokens in the current sentence
	    		List<Token> tokens = JCasUtil.selectCovered(aJCas, Token.class, sentenceAnno);
				List<SingleTokenTruthAnnotation> taggedTokens;
				ExtendedNode annotatedSentence;
				annotatedSentence = TreeUtilities.copyFromBasicNode(converter.convertSingleSentenceToTree(aJCas, sentenceAnno));
				
				// run inner tool to obtain truth annotations
				synchronized (innerTool) {
					innerTool.setSentence(annotatedSentence);
					innerTool.annotate();
					taggedTokens = innerTool.getAnnotatedEntities();
				}
				
				// iterate over all tokens and obtain their truth annotations 
				for (ListIterator<Token> it = tokens.listIterator(); it.hasNext();) {
					int curIndex = it.nextIndex();
					Token tokenAnno = it.next();
					SingleTokenTruthAnnotation annotationResult = taggedTokens.get(curIndex);
					
					
					if (annotationResult.getPredicateTruthValue() != null){
						// Predicate Truth
						PredicateTruth ptTag = TruthMapping.mapPredicateTruth(annotationResult.getPredicateTruthValue(),aJCas,tokenAnno.getBegin(), tokenAnno.getEnd());
						ptTag.addToIndexes();
	
					}
					if (annotationResult.getClauseTruthValue() != null){
						// Clause Truth
						//in this case the annotation result must hold a subordinate clause - pass it to the truth mapping
						
						//calculate a Token list from extendedNode list
						List<Token> subtree = new ArrayList<Token>();
						for (ExtendedNode e : annotationResult.getSubtree()){
							subtree.add(tokens.get(e.getInfo().getNodeInfo().getSerial()-1));
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
		catch (CasTreeConverterException
				| UnsupportedPosTagStringException
				| PredicateTruthException e ) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		}
	}
	
}
