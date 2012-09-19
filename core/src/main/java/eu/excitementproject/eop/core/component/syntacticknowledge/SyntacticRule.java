package eu.excitementproject.eop.core.component.syntacticknowledge;

import java.io.Serializable;

import eu.excitementproject.eop.core.component.lexicalknowledge.TERuleRelation;
import eu.excitementproject.eop.core.representation.parsetree.AbstractNode;
import eu.excitementproject.eop.core.representation.parsetree.BasicNode;
import eu.excitementproject.eop.core.representation.parsetree.Info;
import eu.excitementproject.eop.core.utilities.BidirectionalMap;

/**
 * [DELETEME_LATER: imported and extended from BIUTEE 2.4.1 "rule", but somewhat different. 1) "relation" added. 2) introduction removed.(NEED2TALK?)] 
 * 
 * Some entailment relationships cannot be described well at the lexical level, 
 * but require access to the syntactic level. Examples include changes of verb 
 * voice, changes in a predicate's argument structure, or proper paraphrase.
 * In the EXCITEMENT platform, we call syntactic level knowledge syntactic rules, 
 * and a collection of such rules with standard access methods a syntactic rulebase. 
 * Similar to lexical rules, syntactic rules also have two sides (LHS and RHS) and 
 * define the relationship between the them (for example, LHS entails RHS, or LHS 
 * does not entail RHS). Unlike in the case of lexical knowledge, each side (LHS / 
 * RHS) is defined as a partial parse tree. (e.g. that of BasicNode) 
 *
 * * <P>
 * A Rule is:
 * <OL>
 * <LI>left hand side</LI>
 * <LI>right hand side</LI>
 * <LI>a mapping from (part of) the left hand nodes to
 * the right hand side nodes.</LI>
 * </OL>
 * <P>
 * Note that Rule does <B>not</B> implement equals() and hashCode(). I.e. each rule is
 * unique in the system. 
 * 
 * @param <I> The information type of the tree-nodes, e.g. {@link Info}.  See {@link AbstractNode}.
 * @param <S> The rule's nodes, e.g. {@link BasicNode}. See {@link AbstractNode}.
 * 
 */

public class SyntacticRule<I,S extends AbstractNode<I,S>> implements Serializable {

	private static final long serialVersionUID = 1804205599883182009L;

	public SyntacticRule(S leftHandSide, S rightHandSide, BidirectionalMap<S, S> mapNodes, TERuleRelation relation)
	{
		super();
		this.leftHandSide = leftHandSide;
		this.rightHandSide = rightHandSide;
		this.mapNodes = mapNodes;
		this.relation = relation; 
	}

	public S getLeftHandSide()
	{
		return leftHandSide;
	}
	
	public S getRightHandSide()
	{
		return rightHandSide;
	}
	
	public BidirectionalMap<S, S> getMapNodes()
	{
		return mapNodes;
	}

	public TERuleRelation getRelation()
	{
		return relation; 
	}

	// protected
	
	protected S leftHandSide;
	protected S rightHandSide;
	
	
	/**
	 * relation from LHS to RHS. (entailment, or nonentailment) 
	 */
	protected TERuleRelation relation; 
	
	/**
	 * map from LHS to RHS
	 */
	protected BidirectionalMap<S,S> mapNodes;
		
}
