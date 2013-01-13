package ac.biu.nlp.nlp.engineml.operations.rules;
import java.io.Serializable;

import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;

/**
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
public class Rule<I, S extends AbstractNode<I, S>> implements Serializable
{
	private static final long serialVersionUID = 2817957141706110612L;

	public Rule(S leftHandSide, S rightHandSide, BidirectionalMap<S, S> mapNodes)
	{
		super();
		this.leftHandSide = leftHandSide;
		this.rightHandSide = rightHandSide;
		this.mapNodes = mapNodes;
	}

	public Rule(S leftHandSide, S rightHandSide, BidirectionalMap<S, S> mapNodes, Boolean isExtraction)
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
