package eu.excitementproject.eop.common.component.distance;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.scoring.ScoringComponent;

/**
 * <P>The ability to calculate the distance or similarity of two textual 
 * units is essential in many textual entailment algorithms. Distance 
 * calculations and similarity calculations are generalized with the distance
 * calculation interface within the EXCITEMENT platform. The interface 
 * generalizes both similarity and distance calculations in a normalized 
 * range of distance 0 (totally identical) to distance 1 (maximally different).</P>
 * 
 * <P>
 * The interface is a subinterface of <code>ScoringComponent</code>. 
 * The new methods it adds is calculation(), which gets a JCas and returns 
 * an object of DistanceValue. 
 * 
 * <P>The main data structure that is returned from a distance component is 
 * DistanceValue, of method calculation(). However, an implementation should 
 * report back a vector correctly when it is called by the inherited method 
 * calculateScores(). The choice of values to report back is, of course, the 
 * implementer's choice. However, it is recommended that the returned set of 
 * vectors should have the distance values (duplicated to the ones in 
 * DistanceValue), and all underlying raw scores (if any) that helped 
 * calculating the DistanceValue. (For example, unnormalized values, 
 * raw-frequency count, length of T/H, denominator of normalizing, etc. They 
 * may be used as additional features by some EDAs).
 */

public interface DistanceCalculation extends ScoringComponent {

	/**
	 * <P>
	 * It delivers the distance calculation result in an Object DistanceValue, which represents 
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
