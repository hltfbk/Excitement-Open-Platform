/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.representation;
import java.util.LinkedHashSet;

import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.transformations.generic.truthteller.application.AnnotationRuleApplierFactory;
import eu.excitementproject.eop.transformations.generic.truthteller.application.ct.ClauseTruthAnnotationRuleApplier;

/**
 * This enums all the types and subtypes of entailment rules and annotation rules that the engine may use vis a vis
 * the SYNTACTIC resource, and the annotator.
 * 
 * 
 * @author Amnon Lotan
 *
 * @since Jun 19, 2012
 */
public enum RuleType {
	
	SUBSTITUTION(false),
	EXTRACTION(false),
	ANNOTATION(true),
	/**
	 * This is the value the user needs to write in the "ruleType" parameter of an {@link AnnotationRule} xml, for it to cause the 
	 * {@link AnnotationRuleApplierFactory} to select the {@link ClauseTruthAnnotationRuleApplier}. 
	 */
	COMPUTE_RECURSIVE_CT(true), 
	COMPUTE_PT(true),		// computes PT in every node as the product of CT and NU
	;
	
	private final boolean isAnnotation;

	private RuleType(boolean isAnnotation)
	{
		this.isAnnotation = isAnnotation;
	}
	
	/**
	 * @return the isAnnotation
	 */
	public boolean isAnnotation() {
		return isAnnotation;
	}
	
	/**
	 * Return the matching {@link RuleType}. If no one exists, return null.
	 * @param ruleTypeStr
	 * @return
	 */
	public static RuleType fromString(String ruleTypeStr)
	{
		for (RuleType ruleType : values())
			if (ruleType.name().equals(ruleTypeStr))
				return ruleType;
		return null;
	}
	
	public static final String PRINTED_VALUES = Utils.arrayToCollection(RuleType.values(), new LinkedHashSet<RuleType>()).toString();

}
