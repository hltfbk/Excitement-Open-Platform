/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.entailmentrules;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.FlippedBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;
import eu.excitementproject.eop.transformations.generic.rule_compiler.Constants;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.CgxReadingUtils;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.RuleBuildingUtils;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.RuleType;


/**
 * @author Amnon Lotan
 *
 * @since Jul 7, 2012
 */
public class EntailmentRuleBuildingUtils {

	/**
	 * if the file contains the bidirectional flag, add the reverse of the rules
	 * 
	 * @param rulesWithCD
	 * @param cgxText
	 * @throws EntailmentCompilationException 
	 */
	public static	<I extends Info, N extends AbstractNode<I, N>> 
		void addReversedRules(Set<RuleWithConfidenceAndDescription<I, N>> rulesWithCD, String cgxText) throws EntailmentCompilationException 
	{
		if (cgxText.toLowerCase().contains(Constants.BIDIRECTIONAL_LABEL))
		{
			// sanity
			RuleType ruleType ;
			try {
				ruleType = CgxReadingUtils.readCgxRuleType(cgxText);
			} catch (CompilationException e) {
				throw new EntailmentCompilationException("see nested", e);
			}
			if (!RuleType.SUBSTITUTION.equals(ruleType))
				throw new EntailmentCompilationException("Only substitution rules may be bidirectional. This rule is " + ruleType);
			
			List<RuleWithConfidenceAndDescription<I, N>> tmpRulesWithCD = new Vector<RuleWithConfidenceAndDescription<I,N>>();
			for (RuleWithConfidenceAndDescription<I, N> ruleWithCD : rulesWithCD)
			{
				SyntacticRule<I, N> reversedRule = reverseRule(ruleWithCD.getRule());
				sanityCheckRule(reversedRule);
				tmpRulesWithCD.add(new RuleWithConfidenceAndDescription<I, N>(reversedRule, ruleWithCD.getConfidence(), ruleWithCD.getDescription()));
			}
			rulesWithCD.addAll(tmpRulesWithCD);
		}		
	}

	/**
	 * Get an entailment rule and 
	 * <li>check that the RHS has no unmapped variables 
	 * <li>that each node has a relation and POS<br>
	 * <li>ensure all mappings are from left to right
	 * 
	 * @param <I>
	 * @param <S>
	 * @param rule
	 * @throws EntailmentCompilationException 
	 */
	public static <I extends Info, S extends AbstractNode<I,S>> void sanityCheckRule(SyntacticRule<I,S> rule) throws EntailmentCompilationException
	{
		if (rule == null)
			throw new EntailmentCompilationException("Sanity check failed: rule is null for some reason");
	
		S rhs = rule.getRightHandSide();
		S lhs = rule.getLeftHandSide();
		Set<S> lhsNodesSet = null;
		Set<S> rhsNodesSet = null;
		Set<S> nodesSet = null;
		try {
			rhsNodesSet = AbstractNodeUtils.treeToLinkedHashSet(rhs);
			nodesSet = new LinkedHashSet<S>(rhsNodesSet);
			lhsNodesSet = AbstractNodeUtils.treeToLinkedHashSet(lhs);
			nodesSet.addAll(lhsNodesSet);
		} catch (Exception e) {	}	// ignore errors. they're probably because of null lhs's in special function rules
		
		// assure the RHS has no unmapped variables
		if (rhsNodesSet != null)
			for (S node : rhsNodesSet)
				if (node.getInfo().getNodeInfo().isVariable() && !rule.getMapNodes().rightContains(node))
					throw new EntailmentCompilationException("There's an rhs node that is an unmapped variable: " + node);
					
		// ensure all mappings are from left to right (silly, but can't hurt)
		if (rule.getMapNodes() == null)
			throw new EntailmentCompilationException("rule has null mappings map");
		for (S leftNode : rule.getMapNodes().leftSet())
		{
			if (!lhsNodesSet.contains(leftNode))
				throw new EntailmentCompilationException("This node has an outgoing alignment arrow, but it isn't in the LHS: "+leftNode);
			if (!rhsNodesSet.contains(rule.getMapNodes().leftGet(leftNode)))
				throw new EntailmentCompilationException("This node has an incoming alignment arrow, but it isn't in the RHS: "+ rule.getMapNodes().leftGet(leftNode));
		}			
		try {
			RuleBuildingUtils.sanityCheckRuleTree(lhs);
			RuleBuildingUtils.sanityCheckRuleTree(rhs);
		} catch (CompilationException e) {
			throw new EntailmentCompilationException("see nested", e);
		}
	}

	/**
	 * reverse a rule. Replace RHS and LHS labels and flip the mapping
	 * <p>
	 * <b>Remember </b>you should only reverse substitution rules!
	 * 
	 * @param <I>
	 * @param <S>
	 * @param rule
	 * @return
	 */
	private static <I, S extends AbstractNode<I,S>> SyntacticRule<I,S> reverseRule(SyntacticRule<I,S> rule)
	{
		return new SyntacticRule<I, S>(
				rule.getRightHandSide(), rule.getLeftHandSide(), new FlippedBidirectionalMap<S, S>(rule.getMapNodes()), rule.isExtraction());
	}
}
