package eu.excitementproject.eop.alignmentedas.p1eda;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.alignmentedas.p1eda.scorers.SimpleWordCoverageCounter;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.EDABinaryClassifierFromWeka;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.FeatureValue;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.core.component.alignment.phraselink.IdenticalLemmaPhraseLinker;

public class SimpleWordCoverageP1EDA extends P1EDATemplate {

	public SimpleWordCoverageP1EDA() throws EDAException
	{	
		// Okay. Two thing you need to provide for the P1 template constructor 
		// are a classifier, and a logger ... 
		// Let's use a Weka based one. 
		super(new EDABinaryClassifierFromWeka(), Logger.getLogger(SimpleWordCoverageP1EDA.class));
		
		// And let's keep the alinger instance and scoring component... 
		// This configuration keeps just one for each. (as-is counter) 
		aligner1 = new IdenticalLemmaPhraseLinker(); 
		scorer1 = new SimpleWordCoverageCounter(null); 
	}

	@Override
	public void addAlignments(JCas input) throws EDAException {

		// Here, just one aligner... (same lemma linker) 
		try {
			aligner1.annotate(input);
		}
		catch (PairAnnotatorComponentException pe)
		{
			throw new EDAException("Underlying aligner raised an exception", pe); 
		}
		
	}

	@Override
	public Vector<FeatureValue> evaluateAlignments(JCas aJCas) throws EDAException {
		
		// The simplest possible method... that works well with simple alignment added 
		// on addAlignments step.  
		// count the "covered" ratio (== H term linked) of words in H. 
		Vector<FeatureValue> fv = new Vector<FeatureValue>(); 
		try {
			Vector<Double> score1 = scorer1.calculateScores(aJCas); 	
			// we know scorer 1 returns 4 numbers. 
			// ( count covered tokens , count all tokens, count covered content-tokens, count all content-tokens)
			// Make two "coverage" ratio now. 
			
			double ratio1 = score1.get(0) / score1.get(1); 
			double ratio2 = score1.get(2) / score1.get(3); 
			
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
	
	final AlignmentComponent aligner1; 
	final ScoringComponent scorer1;  

}
