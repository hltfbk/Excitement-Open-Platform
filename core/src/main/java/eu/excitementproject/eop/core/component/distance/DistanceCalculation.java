package eu.excitementproject.eop.core.component.distance;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.Components;

/**
 * <P>The ability to calculate the distance or similarity of two textual 
 * units is essential in many textual entailment algorithms. Distance 
 * calculations and similarity calculations are generalized with the distance
 * calculation interface within the EXCITEMENT platform. The interface 
 * generalizes both similarity and distance calculations in a normalized 
 * range of distance 0 (totally identical) to distance 1 (maximally different).</P>
 * 
 * The interface is a subinterface of <code>Components</code>. 
 * The new methods it adds is calculation(), which gets a JCas and returns 
 * an object of DistanceValue. [Spec Section 4.5] 
 */
public interface DistanceCalculation extends Components {

	/**
	 * <P>This method may only be called after a successful call to initialize(). It 
	 * delivers the distance calculation result in an Object DistanceValue, which represents 
	 * the distance between two textual objects in the JCas. </P>
	 * The calculation is done between the two views of the JCas: TextView and 
	 * HypothesisView. The calculation() method knows nothing about other entailment 
	 * annotations (like entailment.Pair or entailment.Text) and it should not check 
	 * those annotations. The implementation may choose to check the validity of the 
	 * input. For example, if the provided annotations are not compatible for calculations 
	 * (e.g. if the parse tree is missing for a tree edit distance component), it can 
	 * raise an exception accordingly. However, unlike in the case of the EDA process()
	 * method, this check is not mandatory.
	 */
	public DistanceValue calculation(JCas aCas) throws DistanceComponentException;
	
	
}
