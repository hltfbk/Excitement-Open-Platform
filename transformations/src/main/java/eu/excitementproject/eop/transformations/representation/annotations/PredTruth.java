/**
 * 
 */
package eu.excitementproject.eop.transformations.representation.annotations;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;

/**
 * This is an annotation for {@link AdditionalNodeInformation}s pertaining to predicate nodes that says
 * whether the predicate's action is taken to be entailed from the text (POSITIVE), not (NEGATIVE), whether the text is unclear about it 
 * (UNKNOWN) or whether the system did not identify anything about it (UNSPECIFIED). For non predicate nodes it is undefined. 
 * <br>For example: 
 * <li>{-} He did not look --> has {-} cos he actually didn't look
 * <li>He refused [{-} (he) to look]  --> has {-} cos he actually didn't look
 * <li>He refused [{+} (he) not to look] --> has {+} cos he actually looked 
 * 
 * @author Amnon Lotan
 * @since 31/05/2011
 * 
 */
public enum PredTruth {
	/**
	 * "Positive"
	 */
	P,	//("P"),
	/**
	 * "Negative"
	 */
	N,	//("N"),
	/**
	 * "Unknown" 
	 */
	U,	//("U");
	/**
	 * "Not identified" - used exclusively for complements of {@link PredicateSignature#NOT_IN_LEXICON} 
	 */
	O	
;

	/**
	 * @return
	 */
	public PredTruth flip() throws AnnotationValueException
	{
		switch (this)
		{
		case P:
			return N;
		case N:
			return P;
		case U:
			return U;
		case O:
			return O;
		default:
			throw new AnnotationValueException("Internal error, this PT value is not recognized: " + this);

		}
	}
}
