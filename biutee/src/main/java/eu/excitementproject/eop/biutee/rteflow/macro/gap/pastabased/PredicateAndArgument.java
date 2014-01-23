package eu.excitementproject.eop.biutee.rteflow.macro.gap.pastabased;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
import eu.excitementproject.eop.common.representation.pasta.TypedArgument;

/**
 * 
 * @author Asher Stern
 * @since Aug 8, 2013
 *
 * @param <I>
 * @param <S>
 */
public class PredicateAndArgument<I extends Info, S extends AbstractNode<I, S>>
{
	public PredicateAndArgument(PredicateArgumentStructure<I, S> predicate,
			TypedArgument<I, S> argument)
	{
		super();
		this.predicate = predicate;
		this.argument = argument;
	}
	
	
	
	public PredicateArgumentStructure<I, S> getPredicate()
	{
		return predicate;
	}
	public TypedArgument<I, S> getArgument()
	{
		return argument;
	}



	private final PredicateArgumentStructure<I, S> predicate;
	private final TypedArgument<I, S> argument;
}
