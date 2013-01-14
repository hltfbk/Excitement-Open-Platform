package eu.excitementproject.eop.common.component.syntacticknowledge;
import java.io.Serializable;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

/**
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
 * <P>
 * 
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
 * <P>
 * It is optional to specify that the rule is <tt>introduction</tt> rule or
 * <tt>substitution</tt> rule, but it is also possible to omit that specification
 * (and make the type of the rule unspecified).
 * 
 * 
 * 
 * @author Asher Stern
 * @since Feb 5, 2011
 *
 * @param <I> The information type of the tree-nodes, e.g. {@link Info}.  See {@link AbstractNode}.
 * @param <S> The rule's nodes, e.g. {@link BasicNode}. See {@link AbstractNode}.
 */
public class SyntacticRule<I, S extends AbstractNode<I, S>> implements Serializable
{
	private static final long serialVersionUID = 2817957141706110612L;

	public SyntacticRule(S leftHandSide, S rightHandSide, BidirectionalMap<S, S> mapNodes)
	{
		super();
		this.leftHandSide = leftHandSide;
		this.rightHandSide = rightHandSide;
		this.mapNodes = mapNodes;
	}

	public SyntacticRule(S leftHandSide, S rightHandSide, BidirectionalMap<S, S> mapNodes, Boolean isExtraction)
	{
		super();
		this.leftHandSide = leftHandSide;
		this.rightHandSide = rightHandSide;
		this.mapNodes = mapNodes;
		this.isExtraction = isExtraction;
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
	
	public Boolean isExtraction()
	{
		return this.isExtraction;
	}
	
	protected S leftHandSide;
	protected S rightHandSide;
	
	/**
	 * map from LHS to RHS
	 */
	protected BidirectionalMap<S,S> mapNodes;
	
	/**
	 * There are two types of rules: introduction and substitution.
	 * This field:<BR>
	 * <tt>true</tt> - the rule is introduction.
	 * <tt>false</tt> - the rule is substitution.
	 * <tt>null</tt> - the type is not specified.
	 * <BR>
	 * This field is <tt>null</tt> by default.
	 */
	protected Boolean isExtraction = null;
}
