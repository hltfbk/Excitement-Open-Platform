package eu.excitementproject.eop.common.representation.pasta;

/**
 * The relation between the predicate to the argument. It might be a subject, an object, a modifier, or unknown.
 * Note that the boundary between object and modifier is vague, and many times an argument appears as object,
 * though it is actually a modifier.
 *  
 * @author Asher Stern
 * @since Oct 4, 2012
 *
 */
public enum ArgumentType
{
	SUBJECT,
	OBJECT,
	MODIFIER,
	UNKNOWN;
}
