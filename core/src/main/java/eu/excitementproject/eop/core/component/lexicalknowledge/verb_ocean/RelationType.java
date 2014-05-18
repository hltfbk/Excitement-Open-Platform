/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;

/**
 * This enum lists all the relation types between verbs in VerbOcean. Each relation type has an {@link EntailmentDirection} i.e. indicates leftToRight entialment, 
 * rigthToLeft, bidirectional, or none.
 * @author Amnon Lotan
 *
 * @since 25 Dec 2011
 */
public enum RelationType{
	UNKNOWN(EntailmentDirection.NONE),
	HAPPENS_BEFORE(EntailmentDirection.ENTAILED_BY),
	SIMILAR(EntailmentDirection.BIDIRECTIONAL),
	STRONGER_THAN(EntailmentDirection.ENTAILING),
	OPPOSITE_OF(EntailmentDirection.BIDIRECTIONAL),
	CAN_RESULT_IN(EntailmentDirection.ENTAILING),
	LOW_VOL(EntailmentDirection.NONE);
	
	
	
	private RelationType(EntailmentDirection direction)
	{
		entailmentDirection = direction;
	}
	
	/**
	 *	is left entailed right (ENTAILING), vice versa (ENTAILED_BY) or symmetric (BIDRECTIONAL) or NONE 
	 */
	private EntailmentDirection entailmentDirection;
	
	
	public static RelationType parse(String relationDescription) throws LexicalResourceException
	{
		relationDescription = relationDescription.toLowerCase();
		
		if(relationDescription.contains("similar"))
			return SIMILAR;
		else if(relationDescription.contains("before"))
			return HAPPENS_BEFORE;
		else if(relationDescription.contains("stronger"))
			return STRONGER_THAN;
		else if(relationDescription.contains("result"))
			return CAN_RESULT_IN;
		else if(relationDescription.contains("opposite"))
			return OPPOSITE_OF;
		else if(relationDescription.contains("low"))
			return LOW_VOL;
		else if(relationDescription.contains("unk"))
			return UNKNOWN;
			
			
		throw new LexicalResourceException("unknown relation " + relationDescription);	
	}
	
	
	public boolean isEntailing()
	{
		return (entailmentDirection.equals(EntailmentDirection.ENTAILING) || entailmentDirection.equals(EntailmentDirection.BIDIRECTIONAL));
	}
			
	public boolean isEntailed()
	{
		return (entailmentDirection.equals(EntailmentDirection.ENTAILED_BY) || entailmentDirection.equals(EntailmentDirection.BIDIRECTIONAL));
	}
	
	public boolean isBidirectional() {
		return entailmentDirection.equals(EntailmentDirection.BIDIRECTIONAL);
	} 
}

