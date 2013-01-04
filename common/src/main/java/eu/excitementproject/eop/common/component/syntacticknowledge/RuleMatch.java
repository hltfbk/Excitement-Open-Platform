package eu.excitementproject.eop.common.component.syntacticknowledge;

import eu.excitementproject.eop.common.representation.parsetree.AbstractNode;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.utilities.BidirectionalMap;


/**
 * [Spec 1.1 Section 4.7.2.2] 
 * This simple object represents a match of a syntactic rule on a parse tree. 
 * It has two variables. Member variable "rule" is a SyntacticRule, which holds 
 * the rewriting rule. The other variable "mapLHStoTree" is a bidirectional 
 * mapping of BasicNode. The variable holds mapping info between each LHS node 
 * of the rule and the corresponding node of the input parse tree.
 * 
 * Type argument I and S follow that of SyntacticRule (thus, I=Info and S=BasicNode, for BasicNode based syntactic Rule)
 * @author Gil 
 *
 */
public class RuleMatch<I,S extends AbstractNode<I,S>> {

	public RuleMatch(SyntacticRule<I,S> rule, BidirectionalMap<S,S> mapLHStoTree)
	{
		this.rule = rule;
		this.mapLHStoTree = mapLHStoTree; 	
	}
	
	public SyntacticRule<I,S> getRule()
	{
		return rule; 
	}
	
	public BidirectionalMap<S,S> getMapLHStoTree()
	{
		return mapLHStoTree; 
	}
	
	// protected 	
	private SyntacticRule<I,S> rule;
	private BidirectionalMap<S,S> mapLHStoTree; 
	
}
