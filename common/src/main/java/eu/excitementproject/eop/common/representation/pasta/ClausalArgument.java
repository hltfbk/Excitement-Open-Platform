package eu.excitementproject.eop.common.representation.pasta;

import java.io.Serializable;
import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * An argument that is a clause for itself. For example "I think that it works": "it works" is a clause, and is an argument of "think".
 * Unlike {@link Argument}, the definition here is not "a set of nodes". Here - the definition of an argument is a subtree.
 * This subtree is the field {@link #clause}. Here - there is no definition of "semantic head". The only information provided about
 * the argument itself is the subtree.
 * In addition, some information about the connection between the predicate and the argument is provided:
 * <UL>
 * <LI>The path from the predicate (the predicate head) and the argument (in the example above, this path might be a node
 * with the word "that", in some parsers).</LI>
 * <LI>The syntactic representative: If the argument is not connected to the predicate, but there is an "antecedent" node that is
 * connected to the predicate, and the argument's subtree's head is the "anaphor" of that "antecedent", then
 * "syntactic representative" field contains that antecedent. Otherwise, it is null.</LI>
 * <LI>The argument type (see {@link ArgumentType} )</LI>  
 * </UL>
 * 
 * @author Asher Stern
 * @since October 8 2012
 *
 */
public class ClausalArgument<I extends Info, S extends AbstractNode<I, S>> implements Serializable
{
	private static final long serialVersionUID = 6964003008048421877L;
	
	public ClausalArgument(List<S> syntacticPathFromPredicateToArgument,
			S clause, S syntacticRepresentative, ArgumentType argumentType)
	{
		super();
		this.syntacticPathFromPredicateToArgument = syntacticPathFromPredicateToArgument;
		this.clause = clause;
		this.syntacticRepresentative = syntacticRepresentative;
		this.argumentType = argumentType;
	}
	
	public ClausalArgument(List<S> syntacticPathFromPredicateToArgument,
			S clause, ArgumentType argumentType)
	{
		super();
		this.syntacticPathFromPredicateToArgument = syntacticPathFromPredicateToArgument;
		this.clause = clause;
		this.syntacticRepresentative = null;
		this.argumentType = argumentType;
	}
	
	
	public List<S> getSyntacticPathFromPredicateToArgument()
	{
		return syntacticPathFromPredicateToArgument;
	}
	public S getClause()
	{
		return clause;
	}
	public S getSyntacticRepresentative()
	{
		return syntacticRepresentative;
	}
	public ArgumentType getArgumentType()
	{
		return argumentType;
	}



	private final List<S> syntacticPathFromPredicateToArgument;
	private final S clause;
	private final S syntacticRepresentative; // null if equals to clause
	private final ArgumentType argumentType;
}
