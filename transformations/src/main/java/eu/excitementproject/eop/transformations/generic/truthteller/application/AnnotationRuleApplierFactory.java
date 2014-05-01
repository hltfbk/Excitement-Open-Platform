package eu.excitementproject.eop.transformations.generic.truthteller.application;
import java.io.Serializable;
import java.util.List;

import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.generic.truthteller.application.ct.ClauseTruthAnnotationRuleApplier;
import eu.excitementproject.eop.transformations.generic.truthteller.application.ct.PredTruthAnnotationRuleApplier;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRule;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRuleWithDescription;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.BasicRuleAnnotations;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.RuleType;
import eu.excitementproject.eop.transformations.representation.ExtendedConstructionNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;


/**
 *  This class performs a few special functions on {@link AnnotationNode} sentences, like recursive {@link ClauseTruth} and
 *  {@link PredTruth} computation.
 *  <p>
 *  More significantly, for rule compilation time, it exports {@link #getSpecialSubstitutionRule(String)}, which  identifies String labels (supposedly read from 
 *  "ruleType" parameters in CGX files) and 
 *  maps them to special {@link SyntacticRule}s that carry the same labels.<br>
 *  For engine preprocess time, it exports {@link #getAnnotationRuleApplier(AnnotationRule)}, which  takes a rule and returns the {@link AnnotationRuleApplier} meant to 
 *  apply it.
 *  
 * @author Amnon Lotan
 * @since 11/06/2011
 * 
 */
public class AnnotationRuleApplierFactory implements Serializable
{
	private static final long serialVersionUID = -7372070421613134527L;
	private final List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> recursiveCtCalcAnnotationRules;

	/**
	 * Ctor
	 * @param recursiveCtCalcAnnotationRules may be null
	 */
	public AnnotationRuleApplierFactory(
			List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> recursiveCtCalcAnnotationRules) {
		this.recursiveCtCalcAnnotationRules = recursiveCtCalcAnnotationRules;
	}

	/**
	 * If the {@link AnnotationRule} matches against a special {@link AnnotationRuleApplier}, return the applier. Else return an instance of 
	 * {@link DefaultAnnotationRuleApplier}
	 * @param rule
	 * @return
	 * @throws AnnotatorException 
	 */
	public  AnnotationRuleApplier<ExtendedConstructionNode> getAnnotationRuleApplier(AnnotationRule<ExtendedNode, BasicRuleAnnotations> rule) throws AnnotatorException {
		
		RuleType ruleType =	rule.getRuleType();
		if (ruleType == null)
			return new DefaultAnnotationRuleApplier(rule);		// default
		
		switch (ruleType)
		{
		case COMPUTE_RECURSIVE_CT:
			return  new ClauseTruthAnnotationRuleApplier( recursiveCtCalcAnnotationRules );
		case COMPUTE_PT:
			return PredTruthAnnotationRuleApplier.getInstance();
		case ANNOTATION:
			return new DefaultAnnotationRuleApplier(rule);		// default
		default:
			throw new AnnotatorException("This rule type is not permitted in this method: "+ ruleType);
		}
	}
}
