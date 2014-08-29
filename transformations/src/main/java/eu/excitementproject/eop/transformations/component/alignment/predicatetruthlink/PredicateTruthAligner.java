package eu.excitementproject.eop.transformations.component.alignment.predicatetruthlink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.Link.Direction;
import eu.excitement.type.alignment.Target;
import eu.excitement.type.predicatetruth.PredicateTruth;
import eu.excitement.type.predicatetruth.PredicateTruthNegative;
import eu.excitement.type.predicatetruth.PredicateTruthPositive;
import eu.excitement.type.predicatetruth.PredicateTruthUncertain;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;



/**
 * Produces alignment links between the text and the hypothesis,
 * based on the predicate truth annotations
 * <P>
 * Usage: align a sentence pair by calling the annotate method.
 * When the {@linkplain Aligner} object is no longer to be used, the
 * {@link #cleanUp()} method should be called.
 * 
 * @author Gabi Stanovsky
 * @since Aug 2014
 */


public class PredicateTruthAligner implements AlignmentComponent {

	private JCas textView, hypoView;
	
	//constant values used for aligner description
	public static final String ALIGNER_ID = "PredicateTruth";
	public static final String ALIGNER_VERSION = "TruthTeller_1.0"; 
	public static final String ALIGNEMNT_TYPE_AGREEING_POSITIVE = "Agreeing_Positive_Predicate_Truth";
	public static final String ALIGNEMNT_TYPE_AGREEING_NEGATIVE = "Agreeing_Negative_Predicate_Truth";
	public static final String ALIGNEMNT_TYPE_DISAGREEING = "Disagreeing_Predicate_Truth";
	public static final String ALIGNEMNT_TYPE_NON_MATCHING = "Non_Matching_Predicate_Truth";

	//(currently) constant values used for alignment links
	private static final double ALIGNER_CONFIDENCE = 1.0;
	private static final Direction ALIGNER_DIRECTION = Direction.Bidirection;

	//store the annotations of predicate truth, for memoization
	private Map<Class<? extends PredicateTruth>,Collection<? extends Annotation>> memoTextAnnots;
	private Map<Class<? extends PredicateTruth>,Collection<? extends Annotation>> memoHypoAnnots;
	private static final List<Class<? extends PredicateTruth>> ptTypes = new ArrayList<Class<? extends PredicateTruth>>(){
		private static final long serialVersionUID = 8489900798036315449L;

	{
		add(PredicateTruthPositive.class);
		add(PredicateTruthNegative.class);
		add(PredicateTruthUncertain.class);
	}};
	
	
	
	/** 
	 * default constructor
	 * set all members to null 
	 */
	public PredicateTruthAligner(){
		textView = null;
		hypoView =  null;
	}
	
	@Override
	public void annotate(JCas aJCas) throws PairAnnotatorComponentException {
		try {
			// Get the text and hypothesis views
			textView = aJCas.getView(LAP_ImplBase.TEXTVIEW);
			hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
			
			// Record annotations
			memoTextAnnots = new HashMap<Class<? extends PredicateTruth>,Collection<? extends Annotation>>();
			memoHypoAnnots = new HashMap<Class<? extends PredicateTruth>,Collection<? extends Annotation>>();
			
			for (Class<? extends PredicateTruth> ptType : ptTypes){
				memoTextAnnots.put(ptType, JCasUtil.select(textView, ptType));
				memoHypoAnnots.put(ptType, JCasUtil.select(hypoView, ptType));
			}
			

			// add alignment links
			// Agreeing Positive Predicate Truth
			// PT+ <-> PT+
			createPredicateTruthLinks(PredicateTruthPositive.class,PredicateTruthPositive.class, ALIGNER_CONFIDENCE, ALIGNER_DIRECTION,ALIGNEMNT_TYPE_AGREEING_POSITIVE);
			
			// Agreeing Negative Predicate Truth
			// PT- <-> PT-
			createPredicateTruthLinks(PredicateTruthNegative.class,PredicateTruthNegative.class, ALIGNER_CONFIDENCE, ALIGNER_DIRECTION,ALIGNEMNT_TYPE_AGREEING_NEGATIVE);
			
			// Disagreeing Predicate Truth
			// PT+ <-> PT-
			createPredicateTruthLinks(PredicateTruthPositive.class,PredicateTruthNegative.class, ALIGNER_CONFIDENCE, ALIGNER_DIRECTION,ALIGNEMNT_TYPE_DISAGREEING);
			// PT- <-> PT+
			createPredicateTruthLinks(PredicateTruthNegative.class,PredicateTruthPositive.class, ALIGNER_CONFIDENCE, ALIGNER_DIRECTION,ALIGNEMNT_TYPE_DISAGREEING);
			
			// Non Matching Predicate Truth
			// PT+ <-> PT?
			createPredicateTruthLinks(PredicateTruthPositive.class,PredicateTruthUncertain.class, ALIGNER_CONFIDENCE, ALIGNER_DIRECTION,ALIGNEMNT_TYPE_NON_MATCHING);
			// PT- <-> PT?
			createPredicateTruthLinks(PredicateTruthNegative.class,PredicateTruthUncertain.class, ALIGNER_CONFIDENCE, ALIGNER_DIRECTION,ALIGNEMNT_TYPE_NON_MATCHING);
			// PT? <-> PT+
			createPredicateTruthLinks(PredicateTruthUncertain.class,PredicateTruthPositive.class, ALIGNER_CONFIDENCE, ALIGNER_DIRECTION,ALIGNEMNT_TYPE_NON_MATCHING);
			// PT? <-> PT-
			createPredicateTruthLinks(PredicateTruthUncertain.class,PredicateTruthNegative.class, ALIGNER_CONFIDENCE, ALIGNER_DIRECTION,ALIGNEMNT_TYPE_NON_MATCHING);
			
		}
		catch (CASException e) {
			throw new PairAnnotatorComponentException(e);
		}
	}
	
	

	@Override
	public String getComponentName() {
		// Name of this component that is used to identify the related configuration section
		return this.getClass().getName();
	}

	@Override
	public String getInstanceName() {
		// This component does not support instance configuration
		return null;
	}
	
	/**
	 * Draw bidirectional links between all predicate truth annotation of type (TextType) in text and truth annotation of type (hypoType) in Hypothesis
	 * @param textType
	 * @param hypoType
	 * @param confidence
	 * @param linkDirection
	 * @param linkInfo
	 * @throws CASException
	 */
	private void createPredicateTruthLinks(Class<? extends PredicateTruth> textType, Class<? extends Annotation> hypoType, double confidence,Direction linkDirection,String linkInfo) throws CASException{
		
		// get relevant annotations from text and hypothesis - use pre-recorded annotations
		Collection<? extends Annotation> textAnnotations = memoTextAnnots.get(textType);
		Collection<? extends Annotation> hypoAnnotations = memoHypoAnnots.get(hypoType);
		
		// mark links between all of the found types
		for (Annotation tAnno : textAnnotations){
			for (Annotation hAnno : hypoAnnotations){
				Token tToken = UimaUtils.selectCoveredSingle(textView, Token.class, tAnno);
				Token hToken = UimaUtils.selectCoveredSingle(hypoView, Token.class, hAnno);
				addAlignmentAnnotations(tToken,hToken, confidence, linkDirection, linkInfo);				
			}
		}
		
	}
	
	
	/**
	 * Add an alignment link from T to H, based on the rule t->h
	 * in which t is a phrase in T from index textStart to textEnd of the tokens,
	 * and h is a phrase in H from index hypoStart to hypoEnd of the tokens,
	 * @param textToken Token in TextView to annotate
	 * @param hypoToken Token in HypoView to annotate
	 * @param confidence The confidence of the rule
	 * @param linkDirection The direction of the link (t to h, h to t or bidirectional). 
	 * @param linkInfo The relation of the rule (Wordnet synonym, Wikipedia redirect etc).
	 * @throws CASException 
	 */
	private void addAlignmentAnnotations(Token textToken, Token hypoToken,
														double confidence,
														Direction linkDirection,
														String linkInfo) 
																throws CASException {
		
	
		// Prepare the Target instances
		Target textTarget = new Target(textView);
		Target hypoTarget = new Target(hypoView);
		
		
		// Prepare an FSArray instance and put the target annotations in it
		FSArray textAnnots = new FSArray(textView, 1);
		FSArray hypoAnnots = new FSArray(hypoView, 1);
		
		textAnnots.set(0, textToken);
		hypoAnnots.set(0, hypoToken);
		
		textTarget.setTargetAnnotations(textAnnots);
		hypoTarget.setTargetAnnotations(hypoAnnots);
		
		// Set begin and end value of the Target annotations
		textTarget.setBegin(textToken.getBegin());
		textTarget.setEnd(textToken.getEnd());
		hypoTarget.setBegin(hypoToken.getBegin());
		hypoTarget.setEnd(hypoToken.getEnd());
		
		// Add the targets to the indices 
		textTarget.addToIndexes(); 
		hypoTarget.addToIndexes();
		
		// Mark an alignment.Link and add it to the hypothesis view
		Link link = new Link(hypoView); 
		link.setTSideTarget(textTarget); 
		link.setHSideTarget(hypoTarget); 

		// Set the link direction
		link.setDirection(linkDirection); 
		
		// Set strength according to the rule data
		link.setStrength(confidence); 
		
		// Add the link information
		link.setAlignerID(ALIGNER_ID);  
		link.setAlignerVersion(ALIGNER_VERSION); 
		link.setLinkInfo(linkInfo);
		
		// Mark begin and end according to the hypothesis target
		link.setBegin(hypoTarget.getBegin()); 
		link.setEnd(hypoTarget.getEnd());
		
		// Add to index 
		link.addToIndexes(); 
	}


}
