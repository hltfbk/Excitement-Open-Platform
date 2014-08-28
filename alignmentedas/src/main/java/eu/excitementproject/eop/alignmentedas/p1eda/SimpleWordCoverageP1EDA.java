package eu.excitementproject.eop.alignmentedas.p1eda;

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
import eu.excitementproject.eop.alignmentedas.p1eda.classifiers.EDABinaryClassifierFromWeka;
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
import eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped.VerbOceanENLinker;
import eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped.WordNetENLinker;
import eu.excitementproject.eop.core.component.alignment.phraselink.IdenticalLemmaPhraseLinker;
import eu.excitementproject.eop.core.component.alignment.phraselink.MeteorPhraseLinkerDE;
import eu.excitementproject.eop.core.component.alignment.phraselink.MeteorPhraseLinkerEN;

@SuppressWarnings("unused")
public class SimpleWordCoverageP1EDA extends P1EDATemplate {

	public SimpleWordCoverageP1EDA() throws EDAException
	{	
		// And let's keep the alinger instance and scoring component... 
		// This configuration keeps just one for each. (as-is counter) 
		try {
			aligner1 = new IdenticalLemmaPhraseLinker(); 
			aligner2 = new MeteorPhraseLinkerEN(); 
			aligner3 = new WordNetENLinker(null); 
			aligner4 = new VerbOceanENLinker(null); 
		}
		catch (AlignmentComponentException ae)
		{
			throw new EDAException("Initializing Alignment components failed: " + ae.getMessage(), ae); 
		}
		
		scorer1 = new SimpleWordCoverageCounter(null); 
	}

	@Override
	public void addAlignments(JCas input) throws EDAException {

		// Here, just one aligner... (same lemma linker) 
		try {
			aligner1.annotate(input);
			aligner2.annotate(input); 
			aligner3.annotate(input);
//			aligner4.annotate(input); 

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
			Vector<Double> score1 = scorer1.calculateScores(aJCas); 	
			// we know scorer 1 returns 4 numbers. 
			// ( count covered tokens , count all tokens, count covered content-tokens, count all content-tokens)
			// Make two "coverage" ratio now. 
			
			double ratio1 = score1.get(0) / score1.get(1); 
			double ratio2 = score1.get(2) / score1.get(3); 
			
			logger.debug("Adding feature as: " + score1.get(0) + "/" + score1.get(1)); 
			logger.debug("Adding feature as: " + score1.get(2) + "/" + score1.get(3)); 
			fv.add(new FeatureValue(ratio1)); 
			fv.add(new FeatureValue(ratio2)); 
			
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
			//return new EDABinaryClassifierFromWeka(new NaiveBayes(), null); 
			//return new EDABinaryClassifierFromWeka(new VotedPerceptron(), null); 
			//return new EDABinaryClassifierFromWeka(new J48(), null); 
			//return new EDABinaryClassifierFromWeka(new MultilayerPerceptron(), null); 
			//return new EDABinaryClassifierFromWeka(new KStar(), null);
			//return new EDABinaryClassifierFromWeka(new SimpleLogistic(), null); 

		}
		catch (ClassifierException ce)
		{
			throw new EDAException("Preparing an instance of Classifier for EDA failed: underlying Classifier raised an exception: ", ce); 
		}
	}
	
	
	final AlignmentComponent aligner1; 
	final AlignmentComponent aligner2; 
	final AlignmentComponent aligner3; 
	final AlignmentComponent aligner4; 

	final ScoringComponent scorer1;  

}
