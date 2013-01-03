package ac.biu.nlp.nlp.instruments.inflector;


/**
 * Represents a word and its inflection
 * <p>
 * Immutable
 * 
 * @author Amnon Lotan
 *
 * @since 26/02/2011
 */
public class Inflection 
{
	/**
	 * English verbal inflection types
	 * 
	 * @author Amnon Lotan
	 * @since 26/02/2011
	 */
	public static enum InflectionType 
	{
		GERUND,				// singing
		PRETERITE,	 		// sang
		PAST_PARTICIPLE, 	// sung
		PRESENT_3SG, 		// sings
		NOUN_PLURAL			// songs
	}
	
	/**
	 * @param type
	 * @param word
	 */
	public Inflection(InflectionType type, String word)
	{
		this.type = type;
		this.word = word;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return word;
	}
	
	public final InflectionType type;
	public final String word;
}