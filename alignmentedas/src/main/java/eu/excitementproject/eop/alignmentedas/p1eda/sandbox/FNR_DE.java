package eu.excitementproject.eop.alignmentedas.p1eda.sandbox;

import java.util.Vector;

import org.apache.uima.jcas.JCas;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.functions.VotedPerceptron;
import weka.classifiers.lazy.KStar;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
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
import eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped.DerivBaseDELinker;
import eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped.GermaNetDELinker;
import eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped.GermanTransDMDELinker;
import eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped.VerbOceanENLinker;
import eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped.WordNetENLinker;
import eu.excitementproject.eop.core.component.alignment.phraselink.IdenticalLemmaPhraseLinker;
import eu.excitementproject.eop.core.component.alignment.phraselink.MeteorPhraseLinkerDE;
import eu.excitementproject.eop.core.component.alignment.phraselink.MeteorPhraseLinkerEN;

/**
 * Fast aNd Reliable, word-coverage based German configuration of P1EDA.
 * (This setting will get you around 63-64% accuracy on German RTE3. 
 * Not the best setting, but more reliable, and fast to be used on "any" text. )
 *  
 * @author Tae-Gil Noh
 *
 */
@SuppressWarnings("unused")
public class FNR_DE extends P1EDATemplate {

	public FNR_DE() throws EDAException
	{	
		// And let's prepare the aligner instances and scoring components... 
		try {
			identicalLemmaLinker = new IdenticalLemmaPhraseLinker(); 
			meteorParaphraseLinker = new MeteorPhraseLinkerDE(); 
//			derivBaseLinker = new DerivBaseDELinker(); 
//			distSimLinker = new GermanTransDMDELinker();
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
		
		try {
			identicalLemmaLinker.annotate(input);
			meteorParaphraseLinker.annotate(input); 
//			derivBaseLinker.annotate(input); 
//			distSimLinker.annotate(input); 
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
			// you can use other classifiers from Weka, such as ... 
			//return new EDABinaryClassifierFromWeka(new NaiveBayes(), null); 
			//return new EDABinaryClassifierFromWeka(new VotedPerceptron(), null); 
			//return new EDABinaryClassifierFromWeka(new J48(), null); 
			//return new EDABinaryClassifierFromWeka(new MultilayerPerceptron(), null); 
			//return new EDABinaryClassifierFromWeka(new KStar(), null);
			//return new EDABinaryClassifierFromWeka(new SimpleLogistic(), null); 
			//return new EDABinaryClassifierFromWeka(new RandomForest(), null); 

		}
		catch (ClassifierException ce)
		{
			throw new EDAException("Preparing an instance of Classifier for EDA failed: underlying Classifier raised an exception: ", ce); 
		}
	}
	
	
	AlignmentComponent identicalLemmaLinker; 
	AlignmentComponent meteorParaphraseLinker; 
	AlignmentComponent derivBaseLinker; 
	AlignmentComponent distSimLinker; 
	AlignmentComponent germaNetLinker; 

	ScoringComponent wordCoverageScorer;  
	ScoringComponent nerCoverageScorer;  
	ScoringComponent verbCoverageScorer;  



}
