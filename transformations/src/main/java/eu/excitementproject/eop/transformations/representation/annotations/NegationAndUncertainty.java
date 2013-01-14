/**
 * 
 */
package eu.excitementproject.eop.transformations.representation.annotations;


/**
 * @author Amnon Lotan
 * @since 18/06/2011
 * 
 */
public enum NegationAndUncertainty 
{
	/**
	 * "Positive"
	 */
	P,
	/**
	 * "Negative"
	 */
	N,
	/**
	 * "Unknown" 
	 */
	U;

	/**
	 * @return
	 * @throws AnnotationValueException 
	 */
	public NegationAndUncertainty addNegation() throws AnnotationValueException {
		switch(this)
		{
			case P:
				return N;
			case N:
				return P;
			case U:
				return U;
		}
		throw new AnnotationValueException("This method is missing a switch clause for the NegationAndUncertainty value: " + this);
	}
}
