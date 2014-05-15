/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;

/**
 * @author Amnon Lotan
 *
 * @since Jun 18, 2012
 */
public class Constants {

	public static final String PHRASAL_VERB_LABEL = "PHRASAL_VERB";
	public static final String PHRASAL_NOUN_LABEL = "PHRASAL_NOUN";
	public static final String PHRASAL_IMPLICATION_SIGNATURE_LABEL = "PHRASAL_SIGNATURE";

	public static final String RHS = "RHS";
	public static final String LHS = "LHS";
	/**
	 * if a rule file contains this, the rule is reversible
	 */
	public static final String BIDIRECTIONAL_LABEL = "<label>bidirectional</label>";
	
	/**
	 * Default confidence score of each entailment {@link SyntacticRule}
	 */
	public static final double RULE_CONFIDENCE = java.lang.Math.exp(-1);
	
	public static final String PREDICATE_LIST_LABEL = "PREDICATE_LIST";
}
