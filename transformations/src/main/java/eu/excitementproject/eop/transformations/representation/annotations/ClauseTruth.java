package eu.excitementproject.eop.transformations.representation.annotations;


/**
 * This annotation to predicate nodes says whether the predicate's clause/sentence is literally entailed
 * from the text (POSITIVE), is not (NEGATIVE), or whether the text is unclear about it (UNKNOWN)
 * 
 * @author Amnon Lotan
 * @since 01/06/2011
 * 
 */
public enum ClauseTruth 
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
	U, 
	
	/**
	 * "Not identified" - used exclusively for complements of {@link PredicateSignature#NOT_IN_LEXICON} 
	 */
	O	
	;
	
	public ClauseTruth flip() throws AnnotationValueException
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
			throw new AnnotationValueException("Internal error, this CT value is not recognized: " + this);
				
		}
		
	}
}
