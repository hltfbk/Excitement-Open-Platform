/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wiktionary;

/**
 * Represents all the semantic and lexical relations that wiktionary has between word entries and word senses.
 * @author Amnon Lotan
 * @since 21/06/2011
 * @see http://en.wiktionary.org/wiki/Wiktionary:Semantic_relations
 * @see http://en.wiktionary.org/wiki/Wiktionary:Entry_layout_explained
 * 
 */
public enum WiktionaryRelation {
	/**
	 * Each listed antonym denotes the opposite of this entry. 
	 */
	ANTONYM,    
	CHARACTERISTIC_WORD_COMBINATION, 	           
	/**
	 * Each listed coordinate term shares a hypernym with this entry.	
	 */
	COORDINATE_TERM ,	           
	/**
	 * Terms derived from this entry
	 */
	DERIVED_TERM ,	           
	/**
	 * List terms in other languages that have borrowed or inherited the word. The etymology of these terms should then link back to the page. 
	 */
	DESCENDANT ,	           
	ETYMOLOGICALLY_RELATED_TERM,	           
	/**
	 * Each listed holomym has this entrys referent as a part of itself; this entrys referent is part of that of each listed holonym.	
	 */
	HOLONYM ,	           
	/**
	 * Each listed hypernym is superordinate to this entry; This entrys referent is a kind of that denoted by listed hypernym.	
	 */
	HYPERNYM,	           
	/**
	 * Each listed hyponym is subordinate to this entry; Each listed hyponyms referent is a kind of that denoted by this entry.	 
	 */
	HYPONYM ,	           
	/**
	 * Each listed meronym denotes part of this entrys referent.	
	 */
	MERONYM,	           
	/**
	 * Each listed otherwise related term semantically relates to this entry.	
	 */
	SEE_ALSO ,	           
	/**
	 * Each listed synonym denotes the same as this entry.
	 */
	SYNONYM ,	           
	/**
	 * Each listed troponym denotes a particular way to do this entrys referent. Like a verb's hyponym. 
	 * A word that denotes a manner of doing something "`march' is a troponym of `walk'"	
	 */
	TROPONYM , 
	
	/**
	 * words entailed by the sense's gloss
	 */
	GLOSS_TERMS;
	           
	private WiktionaryRelation symmetricType = null;
	static
	{
		setSymmetric(ANTONYM, ANTONYM);
		setSymmetric(SYNONYM, SYNONYM);
		setSymmetric(HYPERNYM , HYPONYM);
		setSymmetric(MERONYM, HOLONYM);
		setSymmetric(COORDINATE_TERM, COORDINATE_TERM);
		setSymmetric(SEE_ALSO, SEE_ALSO);
		setSymmetric(ETYMOLOGICALLY_RELATED_TERM, DESCENDANT);
	}

	/** Set <var>a</var> as <var>b</var>'s symmetric type, and vice versa. */
	private static void setSymmetric(WiktionaryRelation a, WiktionaryRelation b) {
		a.symmetricType = b;
		b.symmetricType = a;
	}
	
	/**
	 * If this WiktionaryRelation has a symmetric relation, its returned. Else, null is returned. 
	 * @return
	 */
	public WiktionaryRelation getSymmetricRelation()
	{
		return symmetricType;
		
	}	
}
