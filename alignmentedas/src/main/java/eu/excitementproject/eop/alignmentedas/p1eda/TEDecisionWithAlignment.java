package eu.excitementproject.eop.alignmentedas.p1eda;

import java.util.Vector;

import org.apache.uima.jcas.JCas;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.FeatureValue;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.TEDecision;

/**
 * A TEDecision implementation that holds the additional information for 
 * alignment. The alignment data can be accessed by get the underlying JCas 
 * that holds the alignments, which were used by the (alignment based) EDA 
 * to make the entailment decision.  
 * 
 * The method that has been added for this alignment data is getJCasWithAlignment(). 
 * It returns the underlying JCas, and all its data (including alignment data) can be 
 * accessed as normal JCas data.  
 * 
 * @author Tae-Gil Noh
 * @since July 2014 
 */
public class TEDecisionWithAlignment implements TEDecision {
	
	public TEDecisionWithAlignment(DecisionLabel label, Double confidence, String pairID, JCas casWithAlignment, Vector<FeatureValue> featureVector)
	{
		this.label = label; 
		this.confidence = confidence; 
		this.pairID = pairID; 
		this.theJCas = casWithAlignment; 
		this.featureVector = featureVector; 
	}
	
	@Override
	public DecisionLabel getDecision() {
		return label;
	}

	@Override
	public double getConfidence() {
		return confidence;
	}

	@Override
	public String getPairID() {
		return pairID; 
	}
	
	/**
	 * Call this method to access the underlying JCas, which includes the alignment data, and all annotations. 
	 * 
	 * @return JCas the JCas that holds alignments and all other annotations that were used for EDA decision.
	 */
	public JCas getJCasWithAlignment() {
		return theJCas; 
	}
	
	/**
	 * Call this method to access the "features", as they were used in the EDA. 
	 * 
	 * @return
	 */
	public Vector<FeatureValue> getFeatureVector()
	{
		return featureVector; 
	}
	
	private final DecisionLabel label; 
	private final double confidence; 
	private final String pairID; 
	private final JCas theJCas; 
	private final Vector<FeatureValue> featureVector; 

}
