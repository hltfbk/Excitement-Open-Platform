package eu.excitementproject.eop.common.component.lexicalknowledge;

/**
 * <P>This enum value represents a canonical mapping of lexical/syntactic rule relations.
 * Knowledge resources have different "originalRelation".  
 * However, all of them share this common TERuleRelation which describes the 
 * relation in terms of textual entailment. It is one of the two values: Entailment 
 * which means that the lexical relationship from LHS to RHS is entailment. 
 * NonEntailment means that the lexical relationship from LHS to RHS cannot be 
 * entailment. Note that NonEntailment means the knowledge resource is confident 
 * that the relationship is not entailment (e.g. contradiction, etc). </P> 
 * 
 * @author Gil
 * @since
 * 
 */
public enum TERuleRelation {
	Entailment,
	NonEntailment,
	;
}
