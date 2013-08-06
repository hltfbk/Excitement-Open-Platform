package eu.excitementproject.eop.common.representation.pasta;

import java.io.Serializable;
import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * An argument plus information about the connection between the predicate to the argument.
 * This class contains:
 * <UL>
 * <LI>The {@linkplain Argument} itself.</LI>
 * <LI>The {@linkplain ArgumentType}</LI>
 * <LI>The path from the predicate head to the argument (e.g. "I put it on the table" - the path from the predicate to
 * "the table" is the node with the word "on")</LI>
 * <LI>An indicator whether the argument is <B>syntactically</B> a child, or a descendant of the predicate head or not.
 * Though in most cases it is a descendant, there are some cases in which the connection between the predicate and
 * the argument are less direct, and the argument might be somewhere else in the parse-tree.</LI>
 * </UL>
 * @author Asher Stern
 * @since Oct 4, 2012
 *
 */
public class TypedArgument<I extends Info, S extends AbstractNode<I, S>> implements Serializable
{
	private static final long serialVersionUID = 5351676289842989417L;
	
	public TypedArgument(Argument<I, S> argument, ArgumentType argumentType, List<S> syntacticPathFromPredicateToArgument, boolean descendantOfPredicateHead)
	{
		super();
		this.argument = argument;
		this.argumentType = argumentType;
		this.syntacticPathFromPredicateToArgument = syntacticPathFromPredicateToArgument;
		this.descendantOfPredicateHead = descendantOfPredicateHead;
	}

	public TypedArgument(Argument<I, S> argument, ArgumentType argumentType, List<S> syntacticPathFromPredicateToArgument)
	{
		super();
		this.argument = argument;
		this.argumentType = argumentType;
		this.syntacticPathFromPredicateToArgument = syntacticPathFromPredicateToArgument;
		this.descendantOfPredicateHead = true;
	}

	public TypedArgument(Argument<I, S> argument, ArgumentType argumentType)
	{
		super();
		this.argument = argument;
		this.argumentType = argumentType;
		this.syntacticPathFromPredicateToArgument = null;
		this.descendantOfPredicateHead = true;
	}
	
	
	
	public Argument<I, S> getArgument()
	{
		return argument;
	}
	public ArgumentType getArgumentType()
	{
		return argumentType;
	}
	public List<S> getSyntacticPathFromPredicateToArgument()
	{
		return syntacticPathFromPredicateToArgument;
	}
	public boolean isDescendantOfPredicateHead()
	{
		return descendantOfPredicateHead;
	}


	
	@Override
	public String toString()
	{
		return ((getArgumentType()!=null)?getArgumentType().name():"")+getArgument().toString();
	}



	private final Argument<I, S> argument;
	private final ArgumentType argumentType;
	private final List<S> syntacticPathFromPredicateToArgument;
	private final boolean descendantOfPredicateHead;
}
