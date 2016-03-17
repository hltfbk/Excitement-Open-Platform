package eu.excitementproject.eop.alignmentedas.p1eda.instances;

import java.util.Vector;

import org.apache.uima.jcas.JCas;
import weka.classifiers.functions.Logistic;
import eu.excitementproject.eop.alignmentedas.p1eda.P1EDATemplate;
import eu.excitementproject.eop.alignmentedas.p1eda.classifiers.EDABinaryClassifierFromWeka;
import eu.excitementproject.eop.alignmentedas.p1eda.scorers.SimpleProperNounCoverageCounter;
import eu.excitementproject.eop.alignmentedas.p1eda.scorers.SimpleVerbCoverageCounter;
import eu.excitementproject.eop.alignmentedas.p1eda.scorers.SimpleWordCoverageCounter;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.ClassifierException;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.EDAClassifierAbstraction;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.FeatureValue;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.ParameterValue;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.core.component.alignment.phraselink.IdenticalLemmaPhraseLinker;

/**
 * This is a minimally configured instance for P1EDA. It uses nothing but "same 
 * lemma" aligner, and uses 3 basic features (word coverage, proper noun coverage 
 * and verb coverage). 
 * 
 * The EDA instance requires no argument on the constructor. 
 * The EDA requires Token, Lemma and POS annotated on the input T-H pair (JCas). 
 * 
 * @author Tae-Gil Noh 
 *
 */
public class MinimalP1EDA extends P1EDATemplate {

	public MinimalP1EDA() throws EDAException
	{	
		try {
			aligner = new IdenticalLemmaPhraseLinker(); 
		}
		catch (AlignmentComponentException ae)
		{
			throw new EDAException("Initializing Alignment components failed: " + ae.getMessage(), ae); 
		}
		
		wordCoverageScorer = new SimpleWordCoverageCounter(null); 
		nerCoverageScorer = new SimpleProperNounCoverageCounter(); 
		verbCoverageScorer = new SimpleVerbCoverageCounter(); 
	}

	@Override
	public void addAlignments(JCas input) throws EDAException {

		// Here, just one aligner... (same lemma linker) 
		try {
			aligner.annotate(input);
		}
		catch (PairAnnotatorComponentException pe)
		{
			throw new EDAException("Underlying aligner raised an exception", pe); 
		}
		
	}
	
	@Override
	public Vector<FeatureValue> evaluateAlignments(JCas aJCas, Vector<ParameterValue> param) throws EDAException {
				
		// The simplest possible method... that works well with simple alignment added 
		// on addAlignments step.  
		// count the "covered" ratio (== H term linked) of words in H. 
		// Note that this instance does not utilize param at all. 

		// the feature vector that will be filled in
		Vector<FeatureValue> fv = new Vector<FeatureValue>(); 
		try {
			Vector<Double> score1 = wordCoverageScorer.calculateScores(aJCas); 	
			// we know word Coverage scorer returns 4 numbers. 
			// ( count covered tokens , count all tokens, count covered content-tokens, count all content-tokens)
			// Make two "coverage" ratio now. 
			
			double ratio1 = score1.get(0) / score1.get(1); 
			double ratio2 = score1.get(2) / score1.get(3); 
			
			logger.debug("Adding feature as: " + score1.get(0) + "/" + score1.get(1)); 
			logger.debug("Adding feature as: " + score1.get(2) + "/" + score1.get(3)); 
			fv.add(new FeatureValue("TokenCoverageRatio", ratio1)); 
			fv.add(new FeatureValue("ContentTokenCoverageRatio", ratio2)); 
			
			Vector<Double> score2 = nerCoverageScorer.calculateScores(aJCas); 
			// we know NER Coverage scorer  returns 2 numbers. 
			// (number of ner words covered in H, number of all NER words in H) 
			// let's make one coverage ratio. 

			// ratio of Proper noun coverage ... 
			double ratio_ner = 0; 
			// special case first ... 
			if (score2.get(1) == 0)
				ratio_ner = 1.0;
			else
			{
				ratio_ner = score2.get(0) / score2.get(1); 
			}
			fv.add(new FeatureValue("NERCoverageRatio", ratio_ner)); 		
			
			
			Vector<Double> score3 = verbCoverageScorer.calculateScores(aJCas); 
			// we know Verb Coverage counter returns 2 numbers. 
			// (number of covered Vs in H, number of all Vs in H) 
			double ratio_V = 0; 
			// special case first... (hmm would be rare but)
			if(score3.get(1) ==0)
				ratio_V = 1.0; 
			else
			{
				ratio_V = score3.get(0) / score3.get(1); 
			}
			fv.add(new FeatureValue("VerbCoverageRatio", ratio_V)); 		
			
		}
		catch (ScoringComponentException se)
		{
			throw new EDAException("Scoring component raised an exception", se); 
		}
		catch (ArrayIndexOutOfBoundsException obe)
		{
			throw new EDAException("Integrity failure - this simply shouldn't happen", obe); 
		}
		
		return fv; 
	}
	
	@Override
	protected EDAClassifierAbstraction prepareClassifier() throws EDAException
	{
		try {
			return new EDABinaryClassifierFromWeka(new Logistic(), null); 
		}
		catch (ClassifierException ce)
		{
			throw new EDAException("Preparing an instance of Classifier for EDA failed: underlying Classifier raised an exception: ", ce); 
		}
	}
	
	
	AlignmentComponent aligner; 

	ScoringComponent wordCoverageScorer;  
	ScoringComponent nerCoverageScorer;  
	ScoringComponent verbCoverageScorer;  



}
